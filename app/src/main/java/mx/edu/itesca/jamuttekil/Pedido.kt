package mx.edu.itesca.jamuttekil

data class Pedido(
    val nombreCliente: String,
    val telefonoCliente: String,
    val horaRecogida: String,
    val productos: List<Producto>,
    val total: Double,
    val fechaRecogida: String
)
