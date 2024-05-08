package mx.edu.itesca.jamuttekil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val buttonInventory = findViewById<Button>(R.id.btnInventory)
        buttonInventory.setOnClickListener {
            val intent = Intent(this, Inventario::class.java)
            startActivity(intent)
        }

        val buttonOrders = findViewById<Button>(R.id.btnOrder)
        buttonOrders.setOnClickListener {
            val intent = Intent(this, Pedidos::class.java)
            startActivity(intent)
        }


        val buttonProductos = findViewById<Button>(R.id.btnProductos)
        buttonProductos.setOnClickListener {
            val intent = Intent(this, ListaProductos::class.java)
            startActivity(intent)
        }
    }
}
