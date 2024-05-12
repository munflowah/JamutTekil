package mx.edu.itesca.jamuttekil

data class Producto(
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

