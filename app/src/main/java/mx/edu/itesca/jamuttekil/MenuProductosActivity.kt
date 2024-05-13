package mx.edu.itesca.jamuttekil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MenuProductosActivity : AppCompatActivity(), ProductoListener {

    private lateinit var lvMenuProductos: ListView
    private lateinit var menuProductoAdapter: MenuProductoAdapter
    private lateinit var productoList: MutableList<Producto> // Lista de productos del menú
    private var contadorCarrito: Int = 0
    private lateinit var cantidadCarritoTextView: TextView
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val productosCarrito: ArrayList<Producto> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_productos)

        lvMenuProductos = findViewById(R.id.lvMenuProductos)
        productoList = mutableListOf() // Inicializamos la lista vacía

        cantidadCarritoTextView = findViewById(R.id.tVCantidadCarro)
        contadorCarrito = 0
        actualizarContadorCarrito()
        menuProductoAdapter = MenuProductoAdapter(this, productoList, this)
        lvMenuProductos.adapter = menuProductoAdapter


        val imgCar = findViewById<ImageView>(R.id.imgCar)
        imgCar.setOnClickListener { abrirCarrito() }


        obtenerDatosProductos()
    }

    interface ProductoListener {
        fun onProductoAgregado(producto: Producto)
        fun onProductoQuitado(producto: Producto)
    }
    private fun abrirCarrito() {
        val intent = Intent(this, Carrito::class.java)
        intent.putExtra("productosCarrito", productosCarrito)
        startActivity(intent)
    }


    private fun actualizarContadorCarrito() {
        cantidadCarritoTextView.text = contadorCarrito.toString()
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
                menuProductoAdapter.actualizarLista(productoList)
            }
            .addOnFailureListener { exception ->
                // Manejar errores al obtener datos de productos desde Firestore
                Log.e("MenuProductosActivity", "Error al obtener datos de productos: $exception")
            }
    }


    override fun onProductoAgregado(producto: Producto) {
        contadorCarrito++
        actualizarContadorCarrito()
        Toast.makeText(this, "Producto ${producto.nombre} agregado al carrito", Toast.LENGTH_SHORT).show()
        productosCarrito.add(producto)
    }

    override fun onProductoQuitado(producto: Producto) {
        if (contadorCarrito > 0) {
            contadorCarrito--
            actualizarContadorCarrito()
            productosCarrito.remove(producto)
        }
    }
}
