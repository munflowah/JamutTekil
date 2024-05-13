package mx.edu.itesca.jamuttekil

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import mx.edu.itesca.jamuttekil.CarritoAdapter
import mx.edu.itesca.jamuttekil.Producto
import mx.edu.itesca.jamuttekil.R

class Carrito : AppCompatActivity(), CarritoAdapter.OnDeleteItemClickListener {

    private lateinit var btnPagar: Button
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var productosCarrito: ArrayList<Producto> // Obtén esta lista del intent
    private lateinit var rvProductosCarrito: RecyclerView
    private lateinit var adapter: CarritoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        productosCarrito = intent.getSerializableExtra("productosCarrito") as ArrayList<Producto>

        btnPagar = findViewById(R.id.btnPagar)
        btnPagar.setOnClickListener {
            // Aquí deberías abrir la actividad para ingresar los datos de pago
        }
        rvProductosCarrito = findViewById(R.id.rvProductosCarrito)
        rvProductosCarrito.layoutManager = LinearLayoutManager(this)

        // Utiliza el adaptador global en lugar de crear uno nuevo
        adapter = CarritoAdapter(productosCarrito)
        adapter.setOnDeleteItemClickListener(this)
        rvProductosCarrito.adapter = adapter

        mostrarProductosCarrito()
    }


    private fun mostrarProductosCarrito() {
        val adapter = CarritoAdapter(productosCarrito)

        rvProductosCarrito.layoutManager = LinearLayoutManager(this)

        rvProductosCarrito.adapter = adapter    }

    override fun onDeleteItemClick(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("¿Estás seguro?")
            .setMessage("¿Quieres eliminar este producto del carrito?")
            .setPositiveButton("Sí") { _, _ ->
                adapter.eliminarProducto(position)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
