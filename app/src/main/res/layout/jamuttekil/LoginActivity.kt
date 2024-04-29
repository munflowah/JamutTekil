package layout.jamuttekil
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import mx.edu.itesca.jamuttekil.R

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.edtUsername)
        val passwordEditText = findViewById<EditText>(R.id.edtPassword)
        val loginButton = findViewById<Button>(R.id.btnAccessLogin)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Hay campos vacios", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in successful
                    Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                    // Aquí puedes redirigir a otra actividad si el inicio de sesión es exitoso
                    val intent = Intent(this, MenuActivity::class.java)
                    startActivity(intent)
                } else {
                    // Sign-in failed
                    val errorMessage = task.exception?.message ?: "Error de autenticación"
                            Toast.makeText(this, "Error: Datos Invalidos", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
