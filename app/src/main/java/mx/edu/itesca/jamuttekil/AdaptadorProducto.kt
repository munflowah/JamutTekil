package mx.edu.itesca.jamuttekil
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson

class AdaptadorProducto(
    var context: Context,
    var cantCarro: TextView,
    var btnVerCarrito: ImageView,
    var lsProductos: ArrayList<Producto>,
    var lsCarrito: ArrayList<Producto>
) : RecyclerView.Adapter<AdaptadorProducto.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivFoto = itemView.findViewById<ImageView>(R.id.imgChorizo)
        var tvNombre = itemView.findViewById<TextView>(R.id.tVNameProduct)
        var tvCantidad = itemView.findViewById<TextView>(R.id.tVKilos)
        var tvPrecio = itemView.findViewById<TextView>(R.id.tVPrecio)
        var tvCntProducto = itemView.findViewById<TextView>(R.id.tVCantidadProducto)
        var btnMas = itemView.findViewById<Button>(R.id.sumar)
        var btnMenos = itemView.findViewById<Button>(R.id.restarProducto)
        var btnAgregar = itemView.findViewById<Button>(R.id.btnAgregar)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = lsProductos[position]

        // Carga de la imagen con Glide
        Glide.with(context)
            .load(producto.img)
            .into(holder.ivFoto)

        // Establecimiento de los valores del producto en el ViewHolder
        holder.tvNombre.text = producto.nombre
        holder.tvCantidad.text = producto.cantidadG
        holder.tvPrecio.text = producto.precio.toString()
        holder.tvCntProducto.text = "0"

        cantCarro.text = lsCarrito.size.toString()

        // Control de cantidad de productos
        holder.btnMas.setOnClickListener {
            val cantidad = Integer.parseInt(holder.tvCntProducto.text.toString().trim())
            holder.tvCntProducto.text = "${cantidad + 1}"
        }

        holder.btnMenos.setOnClickListener {
            val cantidad = Integer.parseInt(holder.tvCntProducto.text.toString().trim())
            if (cantidad > 0) {
                holder.tvCntProducto.text = "${cantidad - 1}"
            }
        }

        holder.btnAgregar.setOnClickListener {
            val cantidad = Integer.parseInt(holder.tvCntProducto.text.toString().trim())

            if (cantidad > 0) {
                // Agregar productos al carrito
                for (i in 1..cantidad) {
                    lsCarrito.add(producto)
                }

                // Actualizar contador de productos en el carrito
                cantCarro.text = "${Integer.parseInt(cantCarro.text.toString().trim()) + cantidad}"

                // Guardar en SharedPreferences
                guardarSharedPreferences()

                // Calcular el total del precio del carrito
                val totalPrecio = calcularTotalPrecio()

                // Aquí puedes agregar lógica para mostrar o usar el total del precio
            }
        }

        btnVerCarrito.setOnClickListener {
            val intent = Intent(context, Carrito::class.java)
            intent.putExtra("carro_compras", lsCarrito)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return lsProductos.size
    }

    fun guardarSharedPreferences() {
        val sp = context.getSharedPreferences("carro_compras", MODE_PRIVATE)
        val editor = sp.edit()

        val jsonString = Gson().toJson(lsCarrito)

        editor.putString("productos", jsonString)

        editor.apply()
    }

    fun calcularTotalPrecio(): Double {
        var total = 0.0
        for (producto in lsCarrito) {
            total += producto.precio
        }
        return total
    }
}