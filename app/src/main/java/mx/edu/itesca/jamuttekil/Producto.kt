package mx.edu.itesca.jamuttekil

import android.widget.ImageView
import java.io.Serializable

data class Producto(
    var cantidadG: String = "",
    var descrip: String = "",
    var img: Int = 0,
    var nombre: String = "",
    var precio: Double = 0.0,
    val idProd: String
)
