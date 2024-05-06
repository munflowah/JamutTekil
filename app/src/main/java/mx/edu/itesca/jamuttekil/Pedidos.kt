package mx.edu.itesca.jamuttekil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Pedidos  : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        val intent = intent
        val carroCompras = intent.getSerializableExtra("carro_compras") as? ArrayList<Producto>

    }


}