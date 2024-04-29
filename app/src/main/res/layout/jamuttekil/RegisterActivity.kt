package layout.jamuttekil
// RegisterActivity.kt
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import mx.edu.itesca.jamuttekil.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etCelular: EditText
    private lateinit var etContrasena: EditText
    private lateinit var etConfirmarContrasena: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Instanciar Firebase Auth y Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Vincular las vistas
        etNombre = findViewById(R.id.edtName)
        etApellido = findViewById(R.id.edtLastName)
        etCorreo = findViewById(R.id.edtUsername)
        etCelular = findViewById(R.id.edtNumberPhone)
        etContrasena = findViewById(R.id.edtPassword)
        etConfirmarContrasena = findViewById(R.id.edtPasswordConfirm)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener {
            registrarUsuario()
        }
    }

    private fun registrarUsuario() {
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val celular = etCelular.text.toString().trim()
        val contrasena = etContrasena.text.toString().trim()
        val confirmarContrasena = etConfirmarContrasena.text.toString().trim()

        // Validar campos
        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(apellido) ||
            TextUtils.isEmpty(correo) || TextUtils.isEmpty(celular) ||
            TextUtils.isEmpty(contrasena) || TextUtils.isEmpty(confirmarContrasena)) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        if (contrasena != confirmarContrasena) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear usuario en Firebase Authentication
        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val User: FirebaseUser? = auth.currentUser

                    // Guardar información adicional en Firestore
                    val userInfo = hashMapOf(
                        "nombre" to nombre,
                        "apellido" to apellido,
                        "celular" to celular,
                        "correo" to correo
                    )

                    firestore.collection("User").document(User?.uid ?: "").set(userInfo)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registro exitoso.", Toast.LENGTH_SHORT).show()
                            // Aquí puedes redirigir al usuario a otra actividad
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al guardar datos adicionales.", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Error al registrar usuario.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}