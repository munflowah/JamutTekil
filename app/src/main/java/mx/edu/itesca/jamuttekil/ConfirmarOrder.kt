package mx.edu.itesca.jamuttekil

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ConfirmarOrder: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mostrarMensaje()

    }
    fun mostrarMensaje() {
        val intent = intent
        // Si CarroCompras es Serializable
        val carroCompras = intent.getSerializableExtra("carro_compras") as? ArrayList<Producto>

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
        builder.setMessage("¿Estás seguro de que quieres continuar?")

        builder.setPositiveButton("SÍ") { dialog, which ->
            // Acciones a realizar cuando el usuario presiona el botón "SÍ"
            val intent = Intent(this, Pedidos::class.java)
            intent.putExtra("carro_compras", carroCompras)
            startActivity(intent)
        }

        builder.setNegativeButton("NO") { dialog, which ->
            // Acciones a realizar cuando el usuario presiona el botón "NO"
        }

        builder.show()
    }
}