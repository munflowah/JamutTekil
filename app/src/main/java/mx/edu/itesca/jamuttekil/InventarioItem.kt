package mx.edu.itesca.jamuttekil

data class InventarioItem(
    var nombre: String,
    var cantidad: Double,
    var unidadMedida: String,
    val idProd: String,
    var precioProd: Double,
    var imagenUrl: String? = null
)
