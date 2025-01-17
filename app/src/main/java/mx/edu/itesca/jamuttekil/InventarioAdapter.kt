import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import mx.edu.itesca.jamuttekil.InventarioItem
import mx.edu.itesca.jamuttekil.R

class InventarioAdapter(
    private val context: Context,
    private var inventarioList: List<InventarioItem>,
    private var onEditClickListener: ((InventarioItem) -> Unit)? = null,
    private var selectedItem: InventarioItem? = null
) : BaseAdapter() {

    override fun getCount(): Int {
        return inventarioList.size
    }

    override fun getItem(position: Int): Any {
        return inventarioList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_inventario, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val item = inventarioList[position]
        holder.nombreTextView.text = item.nombre
        holder.cantidadTextView.text = "${item.cantidad} ${item.unidadMedida}"
        holder.idProdTextView.text = "ID: ${item.idProd}"
        holder.precioProdTextView.text = "Precio: $${item.precioProd}"

        // Cargar la imagen en el ImageView
        Glide.with(context)
            .load(item.imagenUrl) // Aquí usamos la URL de la imagen del InventarioItem
            .placeholder(R.drawable.app) // Placeholder mientras se carga la imagen
            .error(R.drawable.caja) // Imagen de error si no se puede cargar la imagen
            .centerCrop() // Escala centrada para el ImageView
            .into(holder.imagenProducto) // Cargar la imagen en el ImageView

        return view!!
    }

    fun actualizarLista(nuevaLista: List<InventarioItem>) {
        inventarioList = nuevaLista
        notifyDataSetChanged()
    }

    fun setOnEditClickListener(listener: (InventarioItem) -> Unit) {
        onEditClickListener = listener
    }

    fun getSelectedItem(): InventarioItem? {
        return selectedItem
    }

    fun setSelectedItem(item: InventarioItem?) {
        selectedItem = item
    }

    private class ViewHolder(view: View) {
        val nombreTextView: TextView = view.findViewById(R.id.nombreTextView)
        val cantidadTextView: TextView = view.findViewById(R.id.cantidadTextView)
        val idProdTextView: TextView = view.findViewById(R.id.idProdTextView)
        val precioProdTextView: TextView = view.findViewById(R.id.precioProdTextView)
        val imagenProducto: ImageView = view.findViewById(R.id.imagenProducto) // ImageView para la imagen del producto
    }
}
