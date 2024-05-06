package mx.edu.itesca.jamuttekil
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

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

                    // Obtener el tipo de usuario del usuario actualmente autenticado
                    val currentUser = mAuth.currentUser
                    currentUser?.let {
                        val tipoUsuario = obtenerTipoUsuario(currentUser.uid)
                        // Aquí puedes redirigir a otra actividad según el tipo de usuario
                        if (tipoUsuario == "admin") {
                            val intent = Intent(this, MenuActivity::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this, MenuActivityUser::class.java)
                            startActivity(intent)
                        }
                        finish() // Cerrar esta actividad para que no pueda volver atrás
                    }
                } else {
                    // Sign-in failed
                    val errorMessage = task.exception?.message ?: "Error de autenticación"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun obtenerTipoUsuario(userId: String): String {
        // Verificar si el userId coincide con algún ID de usuario administrador predefinido
        // Si coincide, retornar "admin", de lo contrario, retornar "normal"
        return if (userId in listOf("B68D4oO74TbzXfXlokkhnkYQ5z62")) {
            "admin"
        } else {
            "normal"
        }
    }
}
