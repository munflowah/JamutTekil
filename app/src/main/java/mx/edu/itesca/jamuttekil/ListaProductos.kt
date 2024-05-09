package mx.edu.itesca.jamuttekil

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

class ListaProductos : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var productoAdapter: ProductoAdapter
    private lateinit var productoList: MutableList<Producto>
    private lateinit var btnEditar: Button
    private val File = 1
    private val database = Firebase.database
    val myRef = database.getReference("Imagenes")
    val REQUEST_CODE_GALLERY = 0
    private var idProdActual: String? = null
    private var imagenUri: Uri? = null

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
        val listaCompleta: List<Producto> = obtenerListaCompleta()
        actualizarLista(listaCompleta)
    }

    private fun obtenerListaCompleta(): MutableList<Producto> {
        return productoList
    }

    private fun actualizarLista(nuevaLista: List<Producto>) {
        productoList = nuevaLista.toMutableList()
        // Actualizar el adaptador de la lista
        productoAdapter.actualizarLista(productoList)
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

            if (imagenUri != null) {
                subirImagenFirebase()
            } else {
                guardarProductoEnBaseDeDatos(nombre, cantidadG, descrip, null, precio)
            }

            dialogBuilder.dismiss()
        }

        dialogBuilder.show()
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
            actualizarElementoEnBaseDeDatos(nombre, cantidadG, descrip, null, precio, item.idProd)
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
    private fun guardarProductoEnBaseDeDatos(
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
                    val img = uri.toString()
                    // Por ejemplo, actualizando la URL en el documento del producto
                    actualizarUrlImagenEnInventario(idProd, img)
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
        nombre: String,
        cantidadG: String,
        descrip: String,
        img: String?, // Cambiar el tipo de dato a String nullable
        precio: Double,
        idProd: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val productoRef = db.collection("Producto").document(idProd)

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
        val productoRef = db.collection("Producto").document(idProd)

        productoRef.delete()
            .addOnSuccessListener {
                // Eliminar el elemento de la lista local y actualizar el adaptador
                val itemEliminado = productoList.find { it.idProd == idProd }
                itemEliminado?.let {
                    productoList.remove(it)
                    productoAdapter.actualizarLista(productoList)
                }
                Toast.makeText(this, "Elemento eliminado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al eliminar el elemento", Toast.LENGTH_SHORT).show()
            }
    }
    fun fileUpload() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, File)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            val imagenUri: Uri? = data.data
            if (imagenUri != null) {
                // Obtener el idProd actualizado después de agregar el elemento en la base de datos
                val idProd = idProdActual


                if (idProd != null) {
                    subirImagenFirebase() // Pasar el idProd correcto
                } else {
                    Toast.makeText(this, "Error: idProd es nulo", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Error al obtener la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun subirImagenFirebase() {
        val imagenUri = this.imagenUri // Obtener la URI de la imagen desde la variable de clase

        if (imagenUri != null) {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val idProd = idProdActual ?: return // Obtener el idProd actual o salir si es nulo

            // Crear una referencia al archivo en Firebase Storage
            val fileRef: StorageReference = storageRef.child("Imagenes/$idProd.jpg") // Cambiar la extensión según el formato de tu imagen

            // Subir la imagen al Firebase Storage
            fileRef.putFile(imagenUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Imagen subida exitosamente
                    // Obtener la URL de descarga de la imagen
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        // Actualizar la URL de la imagen en la base de datos
                        val imageUrl = uri.toString()
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
        } else {
            Toast.makeText(this, "Error: La URI de la imagen es nula", Toast.LENGTH_SHORT).show()
        }
    }
    private fun actualizarUrlImagenEnInventario(idProd: String, imageUrl: String) {
        // Buscar el InventarioItem correspondiente y actualizar su URL de imagen
        val item = productoList.find { it.idProd == idProd }
        item?.let {
            it.img = imageUrl // Actualizar la URL de la imagen
            productoAdapter.notifyDataSetChanged() // Notificar al adaptador sobre el cambio
        }
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


                    val item = Producto(cantidadG, descrip, null, nombre, precio, idProd) // Usar el ID como idProd
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
