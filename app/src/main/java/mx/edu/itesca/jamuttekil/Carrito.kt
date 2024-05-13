package mx.edu.itesca.jamuttekil

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.Toast

import kotlin.time.times

class Carrito : AppCompatActivity(), CarritoAdapter.OnDeleteItemClickListener {

    private lateinit var btnPagar: Button
    private lateinit var rvProductosCarrito: RecyclerView
    private lateinit var adapter: CarritoAdapter
    private val productosCarrito: MutableList<Producto> = mutableListOf()  // Inicializa la lista

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        // Obtén la lista de productos del intent
        productosCarrito.addAll(intent.getSerializableExtra("productosCarrito") as ArrayList<Producto>)

        btnPagar = findViewById(R.id.btnPagar)
        rvProductosCarrito = findViewById(R.id.rvProductosCarrito)
        rvProductosCarrito.layoutManager = LinearLayoutManager(this)

        // Inicializa el adaptador y configura el listener
        adapter = CarritoAdapter(productosCarrito)
        adapter.setOnDeleteItemClickListener(this)
        rvProductosCarrito.adapter = adapter
        mostrarPrecioTotalSafely()
    }

    override fun onDeleteItemClick(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("¿Estás seguro?")
            .setMessage("¿Quieres eliminar este producto del carrito?")
            .setPositiveButton("Sí") { _, _ ->
                eliminarProducto(position)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun eliminarProducto(position: Int) {
        productosCarrito.removeAt(position)
        adapter.notifyItemRemoved(position)
    }
    private fun mostrarPrecioTotalSafely() {
        val tvTotal: TextView? = findViewById(R.id.tvTotal)
        tvTotal?.let {
            val total = calcularPrecioTotal()
            val textoTotal = "Precio total: $total"
            it.text = textoTotal
        }
    }
    private fun calcularPrecioTotal(): Double {
        var total = 0.0
        for (producto in productosCarrito) {
            val precioDouble = producto.precio // precioDouble es Double
            val cantidadString = producto.cantidadG // cantidadString es String

            println("Precio: $precioDouble, Cantidad: $cantidadString")

            // Convierte cantidadString a Double para verificar si no está vacía
            if (!cantidadString.isNullOrEmpty()) {
                val cantidadDouble = cantidadString.toDouble() // Convierte la cantidad a Double
                total += precioDouble * cantidadDouble
            } else {
                // Mostrar un Toast de error cuando la cantidad esté vacía
                Toast.makeText(this, "Error: Valor de cantidad vacío", Toast.LENGTH_SHORT).show()
            }
        }
        return total
    }
    fun agregarProductoAlCarrito(producto: Producto) {
        // Verifica si la cantidad del producto es válida antes de agregarlo
        if (producto.cantidadG.isNotEmpty()) {
            productosCarrito.add(producto)
            adapter.notifyDataSetChanged()
            mostrarPrecioTotalSafely()
        } else {
            Toast.makeText(this, "Error: La cantidad del producto es inválida", Toast.LENGTH_SHORT).show()
        }
    }






}
