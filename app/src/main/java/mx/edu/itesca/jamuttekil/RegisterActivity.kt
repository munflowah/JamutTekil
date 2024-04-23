package mx.edu.itesca.jamuttekil
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val edtFirstName = findViewById<EditText>(R.id.edtName)
        val edtLastName = findViewById<EditText>(R.id.edtLastName)
        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtCellphone = findViewById<EditText>(R.id.edtNumberPhone)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val edtConfirmPassword = findViewById<EditText>(R.id.edtPasswordConfirm)
        val btnRegister = findViewById<Button>(R.id.btnRegistrar)

        btnRegister.setOnClickListener {
            val firstName = edtFirstName.text.toString()
            val lastName = edtLastName.text.toString()
            val username = edtUsername.text.toString()
            val email = edtCellphone.text.toString()
            val password = edtPassword.text.toString()
            val confirmPassword = edtConfirmPassword.text.toString()

            // Verifica que las contraseñas coincidan
            if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Registra al usuario con Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user: FirebaseUser? = auth.currentUser
                        val userId = user?.uid ?: ""

                        // Guarda datos adicionales en Firebase Realtime Database
                        val userData = mapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "username" to username
                        )

                        // Guarda los datos del usuario en la base de datos
                        database.child("users").child(userId).setValue(userData)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                    // Aquí puedes redirigir a otra actividad
                                } else {
                                    Toast.makeText(this, "Error al guardar datos adicionales", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}


