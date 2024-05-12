package mx.edu.itesca.jamuttekil

interface ProductoListener {
    fun onProductoAgregado(producto: Producto)
    fun onProductoQuitado(producto: Producto)
}