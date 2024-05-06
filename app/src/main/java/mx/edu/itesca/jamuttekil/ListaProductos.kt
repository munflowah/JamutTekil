package mx.edu.itesca.jamuttekil

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
        // Implementa la lógica para agregar un nuevo producto
        // Puedes usar un AlertDialog similar al utilizado en la edición
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
}
