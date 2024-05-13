package mx.edu.itesca.jamuttekil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.Toast

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

        rvProductosCarrito = findViewById(R.id.rvProductosCarrito)
        rvProductosCarrito.layoutManager = LinearLayoutManager(this)

        // Inicializa el adaptador y configura el listener
        adapter = CarritoAdapter(productosCarrito)
        adapter.setOnDeleteItemClickListener(this)
        rvProductosCarrito.adapter = adapter

        btnPagar = findViewById(R.id.btnHacerPedido)
        btnPagar.setOnClickListener {
            val tvTotal: TextView = findViewById(R.id.tvTotal)
            btnPagar.setOnClickListener {
                val totalCarrito = tvTotal.text.toString().substringAfter(": ").replace(" MXN", "")
                val intent = Intent(this, ConfirmarCompraActivity::class.java)
                intent.putExtra("total", totalCarrito.toDouble())  // Convertir el String a Double
                intent.putExtra("productosCarrito", ArrayList(productosCarrito))
                startActivity(intent)
            }

        }

        mostrarPrecioTotalSafely()
    }

    override fun onDeleteItemClick(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("¿Estás seguro?")
            .setMessage("¿Quieres eliminar este producto del carrito?")
            .setPositiveButton("Sí") { _, _ ->
                adapter.eliminarProducto(position)  // Llama al método eliminarProducto del adaptador
                mostrarPrecioTotalSafely()  // Actualiza el precio total después de eliminar el producto
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun mostrarPrecioTotalSafely() {
        val tvTotal: TextView? = findViewById(R.id.tvTotal)
        tvTotal?.let {
            val total = calcularPrecioTotal()
            val textoTotal = "Precio total: $total MXN"  // Agrega " MXN" para pesos mexicanos

            it.text = textoTotal  // Actualiza el texto del TextView con el nuevo total
        }
    }

    private fun calcularPrecioTotal(): Double {
        var total = 0.0
        for (producto in productosCarrito) {
            val precio = producto.precio
            total += precio
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
