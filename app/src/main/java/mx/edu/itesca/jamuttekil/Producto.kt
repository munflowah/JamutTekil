package mx.edu.itesca.jamuttekil

/*data class Producto(
    var cantidadG: String="",
    var descrip: String="",
    var img: String? ="", // Ahora img puede ser nulo
    var nombre: String="",
    var precio: Double=0.0,
    val idProd: String=""
){
    // Constructor vacío necesario para Firebase Firestore
    constructor() : this("", "", "", "", 0.0,"")
}
*/
import com.google.firebase.firestore.PropertyName
import java.io.Serializable

data class Producto(

    @get:PropertyName("cantidad") var cantidadG: String = "",
    @get:PropertyName("descrip") var descrip: String = "",
    @get:PropertyName("img") var img: String = "",
    @get:PropertyName("nombre") var nombre: String = "",
    @get:PropertyName("precio") var precio: Double = 0.0,
    // Asegúrate de tener un constructor sin argumentos aquí
    var idProd: String = ""
) : Serializable


