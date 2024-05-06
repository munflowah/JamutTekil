package mx.edu.itesca.jamuttekil

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import mx.edu.itesca.jamuttekil.Producto
import mx.edu.itesca.jamuttekil.R

class ProductoAdapter(
    private val context: Context,
    private var productoList: List<Producto>,
    private var onEditClickListener: ((Producto) -> Unit)? = null,
    private var selectedItem: Producto? = null
) : BaseAdapter() {

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
            view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val item = productoList[position]
        holder.nombreTextView.text = item.nombre
        holder.cantidadTextView.text = "${item.cantidadG}"
        holder.idProdTextView.text = "ID: ${item.idProd}"
        holder.precioProdTextView.text = "Precio: $${item.precio}"
        // Aquí configura la imagen según la lógica de tu aplicación
        holder.imgProducto.setImageResource(item.img)

        return view!!
    }

    fun actualizarLista(nuevaLista: List<Producto>) {
        productoList = nuevaLista
        notifyDataSetChanged()
    }

    fun setOnEditClickListener(listener: (Producto) -> Unit) {
        onEditClickListener = listener
    }

    fun getSelectedItem(): Producto? {
        return selectedItem
    }

    fun setSelectedItem(item: Producto?) {
        selectedItem = item
    }

    private class ViewHolder(view: View) {
        val nombreTextView: TextView = view.findViewById(R.id.nombreTextView)
        val cantidadTextView: TextView = view.findViewById(R.id.cantidadTextView)
        val idProdTextView: TextView = view.findViewById(R.id.idProdTextView)
        val precioProdTextView: TextView = view.findViewById(R.id.precioProdTextView)
        val imgProducto: ImageView = view.findViewById(R.id.imgProducto)
    }
}
