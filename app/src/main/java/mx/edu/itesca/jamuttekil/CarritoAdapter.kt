package mx.edu.itesca.jamuttekil

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CarritoAdapter(

    private val context: Context,
    private var listaCarrito: List<Producto>

) : RecyclerView.Adapter<CarritoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProductoCarrito: ImageView = itemView.findViewById(R.id.imgProducto)
        val nombreProductoCarrito: TextView = itemView.findViewById(R.id.nombreTextView)
        val cantidadProductoCarrito: TextView = itemView.findViewById(R.id.cantidadTextView)
        val precioProductoCarrito: TextView = itemView.findViewById(R.id.precioProdTextView)
        val descripProductoCarrito: TextView = itemView.findViewById(R.id.descripTextView)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(context).inflate(R.layout.item_carrito, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = listaCarrito[position]

        // Asignar los datos del producto al ViewHolder
        Glide.with(context)
            .load(producto.img)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.icchorizo)
            .centerCrop()
            .into(holder.imgProductoCarrito)

        holder.nombreProductoCarrito.text = producto.nombre
        holder.cantidadProductoCarrito.text = "Cantidad: ${producto.cantidadG}"
        holder.precioProductoCarrito.text = "Precio: $${producto.precio}"
        holder.descripProductoCarrito.text = "Descripción: ${producto.descrip}"

        holder.btnEliminar.setOnClickListener {

            onItemClickListener?.onItemClick(position)
        }

    }

    override fun getItemCount(): Int {
        return listaCarrito.size
    }
    fun actualizarLista(nuevaLista: List<Producto>) {
        listaCarrito = nuevaLista
        notifyDataSetChanged() // Notificar al RecyclerView que los datos han cambiado
    }
}

