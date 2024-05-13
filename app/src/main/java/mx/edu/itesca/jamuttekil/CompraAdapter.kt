package mx.edu.itesca.jamuttekil

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class CompraAdapter(private val productos: List<Producto>, private val context: Context) :
    RecyclerView.Adapter<CompraAdapter.CompraViewHolder>() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    inner class CompraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreProducto: TextView = itemView.findViewById(R.id.nombreTextView)
        val cantidadProducto: TextView = itemView.findViewById(R.id.cantidadTextView)
        val precioProducto: TextView = itemView.findViewById(R.id.precioProdTextView)
        val idProducto: TextView = itemView.findViewById(R.id.idProdTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompraViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_compra, parent, false)
        return CompraViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompraViewHolder, position: Int) {
        val producto = productos[position]

        holder.nombreProducto.text = producto.nombre
        holder.cantidadProducto.text = "Cantidad: ${producto.cantidadG}"
        holder.precioProducto.text = "Precio: $${producto.precio}"
        holder.idProducto.text = "ID: ${producto.idProd}"

        holder.itemView.setOnClickListener {
            guardarPedido(producto)
        }
    }

    override fun getItemCount(): Int {
        return productos.size
    }

    private fun calcularPrecioTotal(): Double {
        var total = 0.0
        for (producto in productos) {
            val precio = producto.precio
            total += precio
        }
        return total
    }

    private fun guardarPedido(producto: Producto) {
        val nombreCliente = "Nombre del cliente"
        val telefonoCliente = "Teléfono del cliente"
        val horaRecogida = "Hora de recogida"
        val total = calcularPrecioTotal()
        val pedido = Pedido(nombreCliente, telefonoCliente, horaRecogida, listOf(producto), total)

        // Guardar el pedido en Firestore
        db.collection("Pedido")
            .add(pedido)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(context, "Pedido guardado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al guardar el pedido: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
