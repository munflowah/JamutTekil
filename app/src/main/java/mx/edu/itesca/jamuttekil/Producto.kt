package mx.edu.itesca.jamuttekil

import android.widget.ImageView
import java.io.Serializable

data class Producto(val id:String, val img:Int,val nombre:String, val cantidadG:String,val precio:Double):Serializable
