
package mx.edu.itesca.jamuttekil

import InventarioAdapter
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.HashMap

class Inventario : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var inventarioAdapter: InventarioAdapter
    private lateinit var inventarioList: MutableList<InventarioItem>
    private lateinit var btnEditar: Button
    private val File = 1
    private val database = Firebase.database
    val myRef = database.getReference("Imagenes")
    val REQUEST_CODE_GALLERY = 0
    private var idProdActual: String? = null
    private var imagenUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        // Inicializar ListView y adaptador
        listView = findViewById(R.id.lvInventory)
        inventarioList = mutableListOf() // Inicializamos la lista vacía
        inventarioAdapter = InventarioAdapter(this, inventarioList)
        listView.adapter = inventarioAdapter

        // Obtener datos del inventario desde Firestore y mostrarlos en el ListView
        obtenerDatosInventario()

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = inventarioAdapter.getItem(position) as InventarioItem
            inventarioAdapter.setSelectedItem(selectedItem) // Establecer elemento seleccionado en el adaptador
            btnEditar.isEnabled = true // Habilitar el botón de editar
            inventarioAdapter.notifyDataSetChanged() // Notificar al adaptador sobre el cambio
        }

        btnEditar = findViewById(R.id.btnEditar)
        btnEditar.isEnabled = false // Deshabilitar el botón de editar inicialmente

        inventarioAdapter.setOnEditClickListener { selectedItem ->
            mostrarDialogoEditarElemento(selectedItem)
        }

        // Botón Agregar
        val btnAgregar: Button = findViewById(R.id.btnAgregar)
        btnAgregar.setOnClickListener {
            mostrarDialogoAgregarElemento()
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            inventarioAdapter.getSelectedItem()?.let { selectedItem ->
                // Habilitar el botón de editar cuando se selecciona un elemento
                btnEditar.isEnabled = true
                inventarioAdapter.notifyDataSetChanged()
            }
        }

        btnEditar.setOnClickListener {
            inventarioAdapter.getSelectedItem()?.let { selectedItem ->
                mostrarDialogoEditarElemento(selectedItem)
            }
        }
        val etFiltro: EditText = findViewById(R.id.etFiltro)
        etFiltro.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarLista(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        mostrarListaCompleta()

        manejarSeleccionElemento()
    }

    //AQUI SON METODOS DEL FILTRO
    // Función para filtrar la lista por nombre del elemento
    private fun filtrarLista(nombreFiltro: String) {
        val listaFiltrada = if (nombreFiltro.isNotBlank()) {
            obtenerListaCompleta().filter { item ->
                item.nombre.contains(nombreFiltro, ignoreCase = true)
            }
        } else {
            obtenerListaCompleta()
        }
        actualizarLista(listaFiltrada)
    }



    private fun mostrarListaCompleta() {
        val listaCompleta: List<InventarioItem> = obtenerListaCompleta()
        actualizarLista(listaCompleta)
    }

    private fun obtenerListaCompleta(): List<InventarioItem> {
        return inventarioList
    }

    private fun actualizarLista(nuevaLista: List<InventarioItem>) {
        inventarioList = nuevaLista.toMutableList()
        // Actualizar el adaptador de la lista
        inventarioAdapter.actualizarLista(inventarioList)
    }
    //FIN DE FILTRO
    private fun manejarSeleccionElemento() {
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = inventarioAdapter.getItem(position) as InventarioItem
            inventarioAdapter.setSelectedItem(selectedItem)
            btnEditar.isEnabled =
                true // Habilitar el botón de editar cuando se selecciona un elemento

        }

        // Al hacer clic en el botón Editar se activa y ya es clickeable
        btnEditar.setOnClickListener {
            inventarioAdapter.getSelectedItem()?.let { selectedItem ->
                mostrarDialogoEditarElemento(selectedItem)
            }
        }
    }


    private fun mostrarDialogoAgregarElemento() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_elemento, null)
        val etNombre: EditText = dialogView.findViewById(R.id.etNombre)
        val etCantidad: EditText = dialogView.findViewById(R.id.etCantidad)
        val etUnidadMedida: EditText = dialogView.findViewById(R.id.etUnidadMedida)
        val etPrecioProd: EditText = dialogView.findViewById(R.id.etPrecioProd)
        val btnGuardarElemento: Button = dialogView.findViewById(R.id.btnGuardarElemento)
        val uploadImageView: ImageView = dialogView.findViewById(R.id.uploadImageView)


        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Agregar Elemento")
            .setCancelable(true)
            .create()
        uploadImageView.setOnClickListener {
            fileUpload()
        }
        btnGuardarElemento.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val cantidad = etCantidad.text.toString().toDoubleOrNull() ?: 0.0
            val unidadMedida = etUnidadMedida.text.toString().trim()
            val precioProd = etPrecioProd.text.toString().toDoubleOrNull() ?: 0.0

            if (imagenUri != null) {
                subirImagenFirebase() // Llamar a la función de subir imagen

            } else {
                guardarElementoEnBaseDeDatos(nombre, cantidad, unidadMedida, precioProd, null)
            }
            dialogBuilder.dismiss()
        }

        dialogBuilder.show()
    }

    private fun mostrarDialogoEditarElemento(item: InventarioItem) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_elemento, null)
        val etNombre: EditText = dialogView.findViewById(R.id.etNombre)
        val etCantidad: EditText = dialogView.findViewById(R.id.etCantidad)
        val etUnidadMedida: EditText = dialogView.findViewById(R.id.etUnidadMedida)
        val etPrecioProd: EditText = dialogView.findViewById(R.id.etPrecioProd)
        val btnGuardarElemento: Button = dialogView.findViewById(R.id.btnGuardarElemento)
        val btnEliminarElemento: Button = dialogView.findViewById(R.id.btnEliminarElemento) // Nuevo botón para eliminar
        val uploadImageView: ImageView = dialogView.findViewById(R.id.uploadImageView)



        // Rellenar los EditTexts con los datos del elemento seleccionado
        etNombre.setText(item.nombre)
        etCantidad.setText(item.cantidad.toString())
        etUnidadMedida.setText(item.unidadMedida)
        etPrecioProd.setText(item.precioProd.toString())

        Glide.with(this).load(item.imagenUrl).into(uploadImageView)


        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Editar Elemento")
            .setCancelable(true)
            .create()

        btnGuardarElemento.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val cantidad = etCantidad.text.toString().toDoubleOrNull() ?: 0.0
            val unidadMedida = etUnidadMedida.text.toString().trim()
            val precioProd = etPrecioProd.text.toString().toDoubleOrNull() ?: 0.0

            // Actualizar elemento en la base de datos y en la lista local
            actualizarElementoEnBaseDeDatos(item.idProd, nombre, cantidad, unidadMedida, precioProd, null)
            dialogBuilder.dismiss()
        }
        //aqui se usa el listener para llamar a las funciones de eliminar
        btnEliminarElemento.setOnClickListener {
            mostrarConfirmacionEliminar(item.idProd)
            dialogBuilder.dismiss() // Cerrar el diálogo de editar después de eliminar
        }
        uploadImageView.setOnClickListener {
            seleccionarNuevaImagenParaElemento(uploadImageView)
        }

        dialogBuilder.show()
    }

    private fun seleccionarNuevaImagenParaElemento(imageView: ImageView) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    private fun guardarElementoEnBaseDeDatos(
        nombre: String,
        cantidad: Double,
        unidadMedida: String,
        precioProd: Double,
        imagenUri: Uri?
    ) {
        val db = FirebaseFirestore.getInstance()
        val inventarioRef = db.collection("Inventario")

        val nuevoElemento = hashMapOf(
            "nombre" to nombre,
            "cantidad" to cantidad,
            "unidadMedida" to unidadMedida,
            "precioProd" to precioProd,
            "imagenUrl" to ""
        )

        inventarioRef.add(nuevoElemento)
            .addOnSuccessListener { documentReference ->
                // Obtener ID del nuevo elemento creado en la base de datos
                val idProd = documentReference.id
                idProdActual = idProd

                // Actualizar lista local y adaptador con el nuevo elemento
                val nuevoItem = InventarioItem(nombre, cantidad, unidadMedida, idProd, precioProd)
                inventarioList.add(nuevoItem)
                inventarioAdapter.actualizarLista(inventarioList)
                inventarioAdapter.notifyDataSetChanged()

                // Subir la imagen si se seleccionó una
                if (imagenUri != null) {
                    subirImagenFirebase()
                }

                Toast.makeText(this, "Elemento agregado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al actualizar el elemento", Toast.LENGTH_SHORT).show()
            }
    }

    private fun subirImagenAServer(imagenUri: Uri, idProd: String) {
        // Obtener una referencia al Firebase Storage
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        // Crear una referencia al archivo en Firebase Storage
        val fileRef: StorageReference = storageRef.child("images/$idProd.jpg") // Cambiar la extensión según el formato de tu imagen

        // Subir la imagen al Firebase Storage
        fileRef.putFile(imagenUri)
            .addOnSuccessListener { taskSnapshot ->
                // Imagen subida exitosamente
                // Obtener la URL de descarga de la imagen
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    // Aquí puedes guardar la URL de la imagen en la base de datos
                    val imageUrl = uri.toString()
                    // Por ejemplo, actualizando la URL en el documento del producto
                    actualizarUrlImagenEnInventario(idProd, imageUrl)
                }.addOnFailureListener { exception ->
                    // Manejar errores al obtener la URL de descarga de la imagen
                    Log.e(TAG, "Error al obtener la URL de descarga de la imagen: $exception")
                }
            }
            .addOnFailureListener { exception ->
                // Manejar errores al subir la imagen al Firebase Storage
                Log.e(TAG, "Error al subir la imagen al Firebase Storage: $exception")
            }
    }



    private fun actualizarElementoEnBaseDeDatos(
        idProd: String,
        nombre: String,
        cantidad: Double,
        unidadMedida: String,
        precioProd: Double,
        imagenUri: Uri?
    ) {
        val db = FirebaseFirestore.getInstance()
        val inventarioRef = db.collection("Inventario").document(idProd)

        val actualizacion = hashMapOf(
            "nombre" to nombre,
            "cantidad" to cantidad,
            "unidadMedida" to unidadMedida,
            "precioProd" to precioProd
        )

        inventarioRef.update(actualizacion as Map<String, Any>)
            .addOnSuccessListener {
                // Actualizar elemento en la lista local
                val itemActualizado = inventarioList.find { it.idProd == idProd }
                itemActualizado?.apply {
                    this.nombre = nombre
                    this.cantidad = cantidad
                    this.unidadMedida = unidadMedida
                    this.precioProd = precioProd
                }

                // Notificar al adaptador de la actualización
                inventarioAdapter.notifyDataSetChanged()

                // Mostrar mensaje o realizar cualquier otra acción
                Toast.makeText(this, "Elemento actualizado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al actualizar el elemento", Toast.LENGTH_SHORT).show()
            }
    }


    private fun obtenerDatosInventario() {
        val db = FirebaseFirestore.getInstance()
        val inventarioRef = db.collection("Inventario")

        inventarioRef.get()
            .addOnSuccessListener { result ->
                val listaInventario = mutableListOf<InventarioItem>()
                for (document in result) {
                    val nombre = document.getString("nombre") ?: ""
                    val cantidad = document.getDouble("cantidad") ?: 0.0
                    val unidadMedida = document.getString("unidadMedida") ?: ""
                    val idProd = document.id // Obtener el ID del documento
                    val precioProd = document.getDouble("precioProd") ?: 0.0

                    val item = InventarioItem(nombre, cantidad, unidadMedida, idProd, precioProd)
                    listaInventario.add(item)
                }
                // Asignar la lista de inventario obtenida a inventarioList y actualizar el adaptador
                inventarioList = listaInventario
                inventarioAdapter.actualizarLista(inventarioList)
            }
            .addOnFailureListener { exception ->
                // Manejar errores al obtener datos del inventario desde Firestore
                Log.e(TAG, "Error al obtener datos del inventario: $exception")
            }
    }
    private fun mostrarConfirmacionEliminar(idProd: String) {
        val confirmacionDialog = AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar este elemento?")
            .setPositiveButton("Sí") { _, _ ->
                eliminarElemento(idProd)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        confirmacionDialog.show()
    }

    private fun eliminarElemento(idProd: String) {
        val db = FirebaseFirestore.getInstance()
        val inventarioRef = db.collection("Inventario").document(idProd)

        inventarioRef.delete()
            .addOnSuccessListener {
                // Eliminar el elemento de la lista local y actualizar el adaptador
                val itemEliminado = inventarioList.find { it.idProd == idProd }
                itemEliminado?.let {
                    inventarioList.remove(it)
                    inventarioAdapter.actualizarLista(inventarioList)
                }
                Toast.makeText(this, "Elemento eliminado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al eliminar el elemento", Toast.LENGTH_SHORT).show()
            }
    }



    private fun subirImagenFirebase() {
        val uploadImageView: ImageView=findViewById(R.id.uploadImageView)
        uploadImageView.setOnClickListener {
            fileUpload()
        }
    }
    fun fileUpload() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, File)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == File) {
            if (resultCode == RESULT_OK) {
                val FileUri = data!!.data
                val Folder: StorageReference =
                    FirebaseStorage.getInstance().getReference().child("Inventario")
                val file_name: StorageReference = Folder.child("file" + FileUri!!.lastPathSegment)
                file_name.putFile(FileUri).addOnSuccessListener { taskSnapshot ->
                    file_name.getDownloadUrl().addOnSuccessListener { uri ->
                        val hashMap =
                            HashMap<String, String>()
                        hashMap["link"] = java.lang.String.valueOf(uri)
                        myRef.setValue(hashMap)
                        Log.d("Mensaje", "Se subió correctamente")
                    }
                }
            }
        }
    }



    private fun actualizarUrlImagenEnInventario(idProd: String, imageUrl: String) {
        // Buscar el InventarioItem correspondiente y actualizar su URL de imagen
        val item = inventarioList.find { it.idProd == idProd }
        item?.let {
            it.imagenUrl = imageUrl // Actualizar la URL de la imagen
            inventarioAdapter.notifyDataSetChanged() // Notificar al adaptador sobre el cambio
        }
    }




}
