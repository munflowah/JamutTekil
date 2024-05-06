package mx.edu.itesca.jamuttekil

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuOrders : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        val btnSOrders = findViewById<Button>(R.id.btnSeeOrder)
        btnSOrders.setOnClickListener {
            val intent = Intent(this, Pedidos::class.java)
            startActivity(intent)
        }
        val btnDoOrder = findViewById<Button>(R.id.btnDoOrder)
        btnDoOrder.setOnClickListener {
            val intent = Intent(this, MakeOrder::class.java)
            startActivity(intent)
        }
    }
}