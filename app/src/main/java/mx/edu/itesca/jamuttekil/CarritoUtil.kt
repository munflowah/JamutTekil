package mx.edu.itesca.jamuttekil

object CarritoUtil {
    val listaCarrito: MutableList<Producto> = mutableListOf()

    fun agregarProducto(producto: Producto) {
        listaCarrito.add(producto)
    }

    fun quitarProducto(producto: Producto) {
        listaCarrito.remove(producto)
    }

    fun limpiarCarrito() {
        listaCarrito.clear()
    }
}
