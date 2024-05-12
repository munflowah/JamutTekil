package mx.edu.itesca.jamuttekil

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
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
import com.google.firebase.storage.ktx.storage
import java.util.HashMap

class ListaProductos : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var productoAdapter: ProductoAdapter
    private lateinit var productoList: MutableList<Producto>
    private lateinit var btnEditar: Button
    private val File = 1
    private val database = Firebase.database
    val myRef = database.getReference("Imagenes")
    private var imgURL : String? = null
    private var callback: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_producto)

        // Inicializar ListView y adaptador
        listView = findViewById(R.id.lvProducto)
        productoList = mutableListOf() // Inicializamos la lista vacía
        productoAdapter = ProductoAdapter(this, productoList)
        listView.adapter = productoAdapter

        // Obtener datos de productos desde Firestore y mostrarlos en el ListView
        obtenerDatosProductos()

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = productoAdapter.getItem(position) as Producto
            productoAdapter.setSelectedItem(selectedItem) // Establecer elemento seleccionado en el adaptador
            btnEditar.isEnabled = true // Habilitar el botón de editar
            productoAdapter.notifyDataSetChanged() // Notificar al adaptador sobre el cambio
        }

        btnEditar = findViewById(R.id.btnEditar)
        btnEditar.isEnabled = false // Deshabilitar el botón de editar inicialmente

        productoAdapter.setOnEditClickListener { selectedItem ->
            mostrarDialogoEditarElemento(selectedItem)
        }

        // Botón Agregar
        val btnAgregar: Button = findViewById(R.id.btnAgregar)
        btnAgregar.setOnClickListener {
            mostrarDialogoAgregarElemento()
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            productoAdapter.getSelectedItem()?.let { selectedItem ->
                // Habilitar el botón de editar cuando se selecciona un elemento
                btnEditar.isEnabled = true
                productoAdapter.notifyDataSetChanged()
            }
        }

        btnEditar.setOnClickListener {
            productoAdapter.getSelectedItem()?.let { selectedItem ->
                mostrarDialogoEditarElemento(selectedItem)
            }
        }

        val etFiltro: EditText = findViewById(R.id.etFiltroP)

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
    // Lista para almacenar los productos filtrados
    private var productoListFiltrada: MutableList<Producto> = mutableListOf()

    private fun filtrarLista(nombreFiltro: String) {
        productoListFiltrada = if (nombreFiltro.isNotBlank()) {
            productoList.filter { item ->
                item.nombre.contains(nombreFiltro, ignoreCase = true)
            }.toMutableList()
        } else {
            productoList
        }
        actualizarLista(productoListFiltrada)
    }
    private fun mostrarListaCompleta() {
        val listaCompleta: List<Producto> = obtenerListaCompleta()
        actualizarLista(listaCompleta)
    }

    private fun obtenerListaCompleta(): MutableList<Producto> {
        return productoList
    }

    private fun actualizarLista(nuevaLista: List<Producto>) {
        // Actualizar el adaptador de la lista
        productoAdapter.actualizarLista(nuevaLista)
    }
//FIN DE FILTRO

    private fun manejarSeleccionElemento() {
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = productoAdapter.getItem(position) as Producto
            productoAdapter.setSelectedItem(selectedItem)
            btnEditar.isEnabled =
                true // Habilitar el botón de editar cuando se selecciona un elemento

        }

        // Al hacer clic en el botón Editar se activa y ya es clickeable
        btnEditar.setOnClickListener {
            productoAdapter.getSelectedItem()?.let { selectedItem ->
                mostrarDialogoEditarElemento(selectedItem)
            }
        }
    }

    private fun mostrarDialogoAgregarElemento() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_producto, null)
        val etNombre: EditText = dialogView.findViewById(R.id.etNombre)
        val etCantidadG: EditText = dialogView.findViewById(R.id.etCantidadG)
        val etDescrip: EditText = dialogView.findViewById(R.id.etDescrip)
        val etPrecio: EditText = dialogView.findViewById(R.id.etPrecio)
        val btnGuardar: Button = dialogView.findViewById(R.id.btnGuardar)
        val agregarImagen: ImageView = dialogView.findViewById(R.id.uploadImageView)

        agregarImagen.setOnClickListener {
            btnGuardar.isEnabled = false

            fileUpload{url ->
                imgURL = url
                btnGuardar.isEnabled = true
            }
        }

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Agregar Producto")
            .setCancelable(true)
            .create()

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val cantidadG = etCantidadG.text.toString().trim()
            val descrip = etDescrip.text.toString().trim()
            val precio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0

            val localImgURL = imgURL

            if (localImgURL != null) {
                guardarProductoEnBaseDeDatos(nombre, cantidadG, descrip, localImgURL, precio)
            } else {
                guardarProductoSinImagenEnBaseDeDatos(nombre, cantidadG, descrip, null, precio)
            }

            dialogBuilder.dismiss()
        }

        dialogBuilder.show()
    }

    fun fileUpload(callback: (String) -> Unit) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, File)
        this.callback = callback
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == File) {
            if (resultCode == RESULT_OK) {
                val FileUri = data!!.data
                val Folder: StorageReference =
                    FirebaseStorage.getInstance().getReference().child("Productos")
                val file_name: StorageReference = Folder.child("file" + FileUri!!.lastPathSegment)
                file_name.putFile(FileUri).addOnSuccessListener { taskSnapshot ->
                    file_name.getDownloadUrl().addOnSuccessListener { uri ->
                        val hashMap =
                            HashMap<String, String>()
                        hashMap["link"] = java.lang.String.valueOf(uri)
                        myRef.setValue(hashMap)
                        imgURL = uri.toString()
                        Log.d("Mensaje", "Se subió correctamente")
                        callback?.invoke(uri.toString())
                    }
                }
            }
        }
    }

    private fun mostrarDialogoEditarElemento(item: Producto) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_producto, null)
        val etNombre: EditText = dialogView.findViewById(R.id.etNombre)
        val etCantidadG: EditText = dialogView.findViewById(R.id.etCantidadG)
        val etDescrip: EditText = dialogView.findViewById(R.id.etDescrip)
        val etPrecio: EditText = dialogView.findViewById(R.id.etPrecio)
        val btnGuardar: Button = dialogView.findViewById(R.id.btnGuardar)
        val btnEliminarElemento: Button = dialogView.findViewById(R.id.btnEliminarElemento)
        val uploadImageView: ImageView = dialogView.findViewById(R.id.uploadImageView)

        // Rellenar los EditTexts con los datos del elemento seleccionado
        etNombre.setText(item.nombre)
        etCantidadG.setText(item.cantidadG.toString())
        etDescrip.setText(item.descrip)
        etPrecio.setText(item.precio.toString())
        Glide.with(this).load(item.img).into(uploadImageView)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Editar Elemento")
            .setCancelable(true)
            .create()

        btnGuardar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val cantidadG = etCantidadG.text.toString().trim()
            val descrip = etDescrip.text.toString().trim()
            val precio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0

            // Actualizar elemento en la base de datos y en la lista local
            actualizarProductoEnBaseDeDatos(nombre, cantidadG, descrip, null, precio, item.idProd)
            dialogBuilder.dismiss()
        }
        //aqui se usa el listener para llamar a las funciones de eliminar
        btnEliminarElemento.setOnClickListener {
            mostrarConfirmacionEliminar(item.idProd)
            dialogBuilder.dismiss() // Cerrar el diálogo de editar después de eliminar
        }
        dialogBuilder.show()
    }


    private fun guardarProductoSinImagenEnBaseDeDatos(
        nombre: String,
        cantidadG: String,
        descrip: String,
        img: Uri?,
        precio: Double
    ) {
        val db = FirebaseFirestore.getInstance()
        val productosRef = db.collection("Productos")

        val nuevoProducto = hashMapOf(
            "nombre" to nombre,
            "cantidadG" to cantidadG,
            "descrip" to descrip,
            "precio" to precio,
            "img" to ""
        )

        productosRef.add(nuevoProducto)
            .addOnSuccessListener { documentReference ->
                // Obtener ID del nuevo producto creado en la base de datos
                val idProd = documentReference.id

                val nuevoItem = Producto(cantidadG, descrip, null, nombre, precio, idProd)
                productoList.add(nuevoItem)
                productoAdapter.actualizarLista(productoList)
                productoAdapter.notifyDataSetChanged()

                Toast.makeText(this, "Producto agregado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al guardar el producto", Toast.LENGTH_SHORT).show()
            }
    }

    private fun guardarProductoEnBaseDeDatos(
        nombre: String,
        cantidadG: String,
        descrip: String,
        img: String,
        precio: Double
    ) {
        val db = FirebaseFirestore.getInstance()
        val productosRef = db.collection("Productos")

        val nuevoProducto = hashMapOf(
            "nombre" to nombre,
            "cantidadG" to cantidadG,
            "descrip" to descrip,
            "precio" to precio,
            "img" to img
        )

        productosRef.add(nuevoProducto)
            .addOnSuccessListener { documentReference ->
                // Obtener ID del nuevo producto creado en la base de datos
                val idProd = documentReference.id

                val nuevoItem = Producto(cantidadG, descrip, img, nombre, precio, idProd)
                productoList.add(nuevoItem)
                productoAdapter.actualizarLista(productoList)
                productoAdapter.notifyDataSetChanged()

                Toast.makeText(this, "Producto agregado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al guardar el producto", Toast.LENGTH_SHORT).show()
            }
    }

    private fun actualizarProductoEnBaseDeDatos(
        nombre: String,
        cantidadG: String,
        descrip: String,
        img: String?, // Cambiar el tipo de dato a String nullable
        precio: Double,
        idProd: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val productoRef = db.collection("Productos").document(idProd)

        val actualizacion = hashMapOf(
            "nombre" to nombre,
            "cantidad" to cantidadG,
            "precio" to precio,
            "descrip" to descrip
        )

        productoRef.update(actualizacion as Map<String, Any>)
            .addOnSuccessListener {
                // Actualizar elemento en la lista local
                val itemActualizado = productoList.find { it.idProd == idProd }
                itemActualizado?.apply {
                    this.nombre = nombre
                    this.cantidadG = cantidadG
                    this.precio = precio
                    this.descrip = descrip
                }

                // Notificar al adaptador de la actualización
                productoAdapter.notifyDataSetChanged()

                // Mostrar mensaje o realizar cualquier otra acción
                Toast.makeText(this, "Elemento actualizado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al actualizar el elemento", Toast.LENGTH_SHORT).show()
            }
    }
    private fun mostrarConfirmacionEliminar(idProd: String) {
        val confirmacionDialog = AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar este elemento?")
            .setPositiveButton("Sí") { _, _ ->
                eliminarProducto(idProd)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        confirmacionDialog.show()
    }

    private fun obtenerDatosProductos() {
        val db = FirebaseFirestore.getInstance()
        val productosRef = db.collection("Productos")

        productosRef.get()
            .addOnSuccessListener { result ->
                val listaProductos = mutableListOf<Producto>()
                for (document in result) {
                    val idProd = document.id
                    val cantidadG = document.getString("cantidadG") ?: ""
                    val descrip = document.getString("descrip") ?: ""
                    val nombre = document.getString("nombre") ?: ""
                    val precio = document.getDouble("precio") ?: 0.0
                    val img = document.getString("img") ?: ""

                    val item = Producto(cantidadG, descrip, img, nombre, precio, idProd) // Usar el ID como idProd
                    listaProductos.add(item)
                }
                productoList = listaProductos
                productoAdapter.actualizarLista(productoList)
            }
            .addOnFailureListener { exception ->
                // Manejar errores al obtener datos de productos desde Firestore
                Log.e(TAG, "Error al obtener datos de productos: $exception")
            }
    }
    private fun eliminarProducto(idProd: String) {
        val db = FirebaseFirestore.getInstance()
        val productosRef = db.collection("Productos").document(idProd)

        productosRef.delete()
            .addOnSuccessListener {
                // Eliminar el producto de la lista local y actualizar el adaptador
                val productoEliminado = productoList.find { it.idProd == idProd }
                productoEliminado?.let {
                    productoList.remove(it)
                    productoAdapter.actualizarLista(productoList)
                }
                Toast.makeText(this, "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al eliminar el producto", Toast.LENGTH_SHORT).show()
            }
    }

}
