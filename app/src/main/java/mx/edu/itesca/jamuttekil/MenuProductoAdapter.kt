package mx.edu.itesca.jamuttekil

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide

class MenuProductoAdapter(
    private val context: Context,
    private val productoList: List<Producto>,
    private val productoListener: MenuProductosActivity
) : BaseAdapter() {

    interface ProductoListener {
        fun onProductoAgregado(producto: Producto)
        fun onProductoQuitado(producto: Producto)
    }

    override fun getCount(): Int {
        return productoList.size
    }

    override fun getItem(position: Int): Any {
        return productoList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_menu_producto, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val item = productoList[position]
        holder.nombreTextView.text = item.nombre
        holder.idProdTextView.text = "ID: ${item.idProd}"
        holder.precioProdTextView.text = "Precio: $${item.precio}"
        holder.descrip.text = "Descripción: ${item.descrip}"

        Glide.with(context)
            .load(item.img)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.icchorizo)
            .centerCrop()
            .into(holder.imgProducto)

        val cantidadTextView = holder.cantidadTextView


        holder.btnAgregar.setOnClickListener {
            productoListener?.onProductoAgregado(item)
            Toast.makeText(context, "Producto ${item.nombre} agregado al carrito", Toast.LENGTH_SHORT).show()
        }

        return view!!
    }

    class ViewHolder(view: View) {
        val nombreTextView: TextView = view.findViewById(R.id.nombreTextView)
        val imgProducto: ImageView = view.findViewById(R.id.imgProducto)
        val cantidadTextView: TextView = view.findViewById(R.id.cantidadTextView)
        val idProdTextView: TextView = view.findViewById(R.id.idProdTextView)
        val precioProdTextView: TextView = view.findViewById(R.id.precioProdTextView)
        val descrip: TextView = view.findViewById(R.id.descripTextView)

        val btnAgregar: Button = view.findViewById(R.id.btnAgregar)

    }


}
