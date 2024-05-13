package mx.edu.itesca.jamuttekil

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ConfirmarCompraActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var etNombreCliente: EditText
    private lateinit var etTelefonoCliente: EditText
    private lateinit var etHoraRecogida: EditText
    private lateinit var btnFinalizarPedido: Button
    private lateinit var tvPrecioTotal: TextView
    private lateinit var etFechaRecogida: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmar_compra)

        db = FirebaseFirestore.getInstance()
        etNombreCliente = findViewById(R.id.editTextNombre)
        etTelefonoCliente = findViewById(R.id.editTextCelular)
        etHoraRecogida = findViewById(R.id.editTextHoraRecoger)
        btnFinalizarPedido = findViewById(R.id.btnFinalizarPedido)
        tvPrecioTotal = findViewById(R.id.textViewPrecioTotal)
        etFechaRecogida = findViewById(R.id.editTextFechaRecoger)

        val rvCompra: RecyclerView = findViewById(R.id.rvCompra)
        rvCompra.layoutManager = LinearLayoutManager(this)

        val productosCarrito = intent.getSerializableExtra("productosCarrito") as? ArrayList<Producto>
        val total = intent.getDoubleExtra("total", 0.0)

        val adapter = CompraAdapter(productosCarrito ?: emptyList(), this)
        rvCompra.adapter = adapter

        val totalCarrito = intent.getStringExtra("totalCarrito")
        tvPrecioTotal.text = "$total MXN "

        btnFinalizarPedido.setOnClickListener {
            val horaRecogida = etHoraRecogida.text.toString().trim()
            finalizarPedido(total, horaRecogida)
        }

    }

    private fun finalizarPedido(total: Double, horaRecogida: String) {
        val nombreCliente = etNombreCliente.text.toString().trim()
        val telefonoCliente = etTelefonoCliente.text.toString().trim()
        val horaRecogidaInput = etHoraRecogida.text.toString().trim()
        val fechaRecogida = etFechaRecogida.text.toString().trim()

        if (nombreCliente.isEmpty() || telefonoCliente.isEmpty() || horaRecogidaInput.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val productosCarrito = intent.getSerializableExtra("productosCarrito") as? ArrayList<Producto>
        if (productosCarrito != null) {
            val pedido = Pedido(nombreCliente, telefonoCliente, horaRecogidaInput, productosCarrito,
                total.toString(), fechaRecogida)

            // Guardar el pedido en Firestore
            db.collection("Pedido")
                .add(pedido)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this, "Pedido guardado correctamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar el pedido: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Error: No se recibieron los datos del carrito", Toast.LENGTH_SHORT).show()
        }
    }




}
