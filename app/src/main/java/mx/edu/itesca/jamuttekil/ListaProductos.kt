package mx.edu.itesca.jamuttekil

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.itesca.jamuttekil.Producto
import mx.edu.itesca.jamuttekil.ProductoAdapter
import mx.edu.itesca.jamuttekil.R

class ListaProductos : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var productoAdapter: ProductoAdapter
    private lateinit var productoList: MutableList<Producto>
    private lateinit var btnEditar: Button

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

        manejarSeleccionElemento()
    }

    private fun manejarSeleccionElemento() {
        listView.setOnItemClickListener { parent, view, position, id ->
            productoAdapter.getSelectedItem()?.let { selectedItem ->
                // Habilitar el botón de editar cuando se selecciona un elemento
                btnEditar.isEnabled = true
                productoAdapter.notifyDataSetChanged()
            }
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
        val etImg: EditText = dialogView.findViewById(R.id.ivImagenSeleccionada)
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
            val img = etImg.text.toString().toIntOrNull() ?: 0
            val precio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0

            // Guardar el producto en Firestore y actualizar la lista
            guardarProductoEnBaseDeDatos(nombre, cantidadG, descrip, img, precio)
            dialogBuilder.dismiss()
        }

        dialogBuilder.show()
    }
    private fun guardarProductoEnBaseDeDatos(
        nombre: String,
        cantidadG: String,
        descrip: String,
        img: Int,
        precio: Double
    ) {
        val db = FirebaseFirestore.getInstance()
        val productosRef = db.collection("Productos")

        val nuevoProducto = hashMapOf(
            "nombre" to nombre,
            "cantidadG" to cantidadG,
            "descrip" to descrip,
            "img" to img,
            "precio" to precio
        )

        productosRef.add(nuevoProducto)
            .addOnSuccessListener { documentReference ->
                // Obtener ID del nuevo producto creado en la base de datos
                val idProd = documentReference.id

                // Actualizar lista local y adaptador con el nuevo producto
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

    private fun mostrarDialogoEditarElemento(item: Producto) {
        // Implementa la lógica para editar un producto existente
        // Puedes usar un AlertDialog similar al utilizado en la edición
    }

    private fun obtenerDatosProductos() {
        val db = FirebaseFirestore.getInstance()
        val productosRef = db.collection("Productos")

        productosRef.get()
            .addOnSuccessListener { result ->
                val listaProductos = mutableListOf<Producto>()
                for (document in result) {
                    val nombre = document.getString("nombre") ?: ""
                    val cantidadG = document.getString("cantidadG") ?: ""
                    val descrip = document.getString("descrip") ?: ""
                    val img = document.getLong("img")?.toInt() ?: 0
                    val precio = document.getDouble("precio") ?: 0.0
                    val idProd = document.id // Obtener el ID del documento

                    val item = Producto(cantidadG, descrip, img, nombre, precio, idProd)
                    listaProductos.add(item)
                }
                // Asignar la lista de productos obtenida a productoList y actualizar el adaptador
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
