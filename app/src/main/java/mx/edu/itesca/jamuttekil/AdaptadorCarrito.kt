package mx.edu.itesca.jamuttekil

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdaptadorCarrito(var tvTotal: TextView,
                       var carroCompras: ArrayList<Producto>): RecyclerView.Adapter<AdaptadorCarrito.ViewHolder>(){

                           var total:Double = 0.0

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imgProducto = itemView.findViewById<ImageView>(R.id.imgChorizo)
        val tvNombre = itemView.findViewById<TextView>(R.id.tVNameProduct)
        val tvCantidad = itemView.findViewById<TextView>(R.id.tVKilos)
        val tvPrecio = itemView.findViewById<TextView>(R.id.tVPrecio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_carrito, parent, false)
        total = 0.0

        carroCompras.forEach {
            total += it.precio
        }

        tvTotal.text = "$$total"

        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = carroCompras[position]

        holder.imgProducto.setImageResource(producto.img)
        holder.tvNombre.text = producto.nombre
        holder.tvCantidad.text = producto.cantidadG
        holder.tvPrecio.text = "$${producto.precio}"
    }

    override fun getItemCount(): Int {
        return carroCompras.size
    }
}