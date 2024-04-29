package mx.edu.itesca.jamuttekil
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<Button>(R.id.btnAccess)
        val btnRegistro = findViewById<Button>(R.id.btnRegister)

        // Cuando se hace clic en "Iniciar Sesión", se navega a la actividad de inicio de sesión
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
// Cuando se hace clic en "Registrar", se navega a la actividad de registrar
        btnRegistro.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
}
