package mx.edu.itesca.jamuttekil

data class Producto(
    var cantidadG: String,
    var descrip: String,
    var img: String?, // Ahora img puede ser nulo
    var nombre: String,
    var precio: Double,
    val idProd: String
)

