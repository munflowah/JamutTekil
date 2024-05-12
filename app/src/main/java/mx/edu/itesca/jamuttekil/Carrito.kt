package mx.edu.itesca.jamuttekil

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Carrito : AppCompatActivity() {

    private lateinit var rvCarrito: RecyclerView
    private lateinit var adaptadorCarrito: CarritoAdapter
    private var listaCarrito: MutableList<Producto> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        val productosCarrito = intent.getSerializableExtra("productosCarrito") as? ArrayList<Producto>
        Log.d("Carrito", "Productos recibidos: ${productosCarrito}")

        productosCarrito?.let {

            listaCarrito.addAll(it)
            adaptadorCarrito.notifyDataSetChanged()
        }

        // Inicializar adaptadorCarrito antes de utilizarlo
        adaptadorCarrito = CarritoAdapter(this, listaCarrito)

        // Configurar RecyclerView y adaptador con la lista de productos en el carrito
        rvCarrito = findViewById(R.id.rvCarrito)
        rvCarrito.adapter = adaptadorCarrito
        rvCarrito.layoutManager = LinearLayoutManager(this)

        adaptadorCarrito.setOnItemClickListener(object : CarritoAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // Eliminar el producto de la lista y notificar al adaptador
                listaCarrito.removeAt(position)
                adaptadorCarrito.notifyDataSetChanged()
            }
        })
    }
}
