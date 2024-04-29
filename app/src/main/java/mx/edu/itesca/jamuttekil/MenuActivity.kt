package mx.edu.itesca.jamuttekil
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val buttonOrders = findViewById<Button>(R.id.btnInventory)
        buttonOrders.setOnClickListener {
            val intent = Intent(this, Pedidos::class.java)
            startActivity(intent)
        }
        val buttonInventory = findViewById<Button>(R.id.btnOrder)
        buttonInventory.setOnClickListener {
            val intent = Intent(this, Inventario::class.java)
            startActivity(intent)

        }


    }
}


