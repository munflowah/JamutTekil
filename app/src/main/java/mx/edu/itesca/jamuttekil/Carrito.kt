package mx.edu.itesca.jamuttekil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import mx.edu.itesca.jamuttekil.databinding.ActivityCarritoBinding

class Carrito : AppCompatActivity() {

    private lateinit var binding: ActivityCarritoBinding
    private lateinit var adapter: AdaptadorCarrito

    var carroCompras = ArrayList<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCarritoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        carroCompras = intent.getSerializableExtra("carro_compras") as ArrayList<Producto>
        val btnComprar = findViewById<Button>(R.id.btnConfirmarOrder)
        val btnEliminar = findViewById<Button>(R.id.btnEliminar)

        setupRecyclerView()

        btnComprar.setOnClickListener {
            val intent = Intent(this, ConfirmarOrder::class.java)
            intent.putExtra("carro_compras", carroCompras)
            startActivity(intent)
        }
    }

    fun setupRecyclerView() {
        binding.LsCarro.layoutManager = LinearLayoutManager(this)
        adapter = AdaptadorCarrito(binding.tvTotal, carroCompras)
        binding.LsCarro.adapter = adapter
    }
}