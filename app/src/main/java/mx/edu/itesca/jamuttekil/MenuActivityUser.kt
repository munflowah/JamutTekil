package mx.edu.itesca.jamuttekil
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MenuActivityUser : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_user)


        val buttonInventory = findViewById<Button>(R.id.btnProductoss)
        buttonInventory.setOnClickListener {
            val intent = Intent(this, Pedidos::class.java)
            startActivity(intent)

        }
        val buttonProductoss = findViewById<Button>(R.id.btnOrder)
        buttonProductoss.setOnClickListener {
            val intent = Intent(this, Ordenes::class.java)
            startActivity(intent)

        }

    }
}


