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

    private val productos: MutableList<Producto>

) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {


    inner class CarritoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
        val nombreProducto: TextView = itemView.findViewById(R.id.nombreTextView)
        val precioProducto: TextView = itemView.findViewById(R.id.precioProdTextView)
        val descripProducto: TextView = itemView.findViewById(R.id.descripTextView)
        val cantidadProducto: TextView = itemView.findViewById(R.id.cantidadTextView)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val producto = productos[position]

        holder.nombreProducto.text = producto.nombre
        holder.cantidadProducto.text = "Cantidad: ${producto.cantidadG}"
        holder.precioProducto.text = "Precio: $${producto.precio}"
        holder.descripProducto.text = "Descripción: ${producto.descrip}"

        holder.btnEliminar.setOnClickListener {
            onDeleteItemClickListener?.onDeleteItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return productos.size
    }
    fun eliminarProducto(position: Int) {
        productos.removeAt(position)
        notifyItemRemoved(position)
    }
    //BOTON ELIMINAR
    interface OnDeleteItemClickListener {
        fun onDeleteItemClick(position: Int)
    }
    private var onDeleteItemClickListener: OnDeleteItemClickListener? = null

    fun setOnDeleteItemClickListener(listener: OnDeleteItemClickListener) {
        this.onDeleteItemClickListener = listener
    }

}

