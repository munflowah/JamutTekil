package mx.edu.itesca.jamuttekil

import InventarioAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class Inventario : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var inventarioAdapter: InventarioAdapter
    private lateinit var inventarioList: MutableList<InventarioItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        // Inicializar ListView y adaptador
        listView = findViewById(R.id.lvInventory)
        inventarioList = mutableListOf() // Inicializamos la lista vacía
        inventarioAdapter = InventarioAdapter(this, inventarioList)
        listView.adapter = inventarioAdapter

        // Obtener datos del inventario desde Firestore y mostrarlos en el ListView
        obtenerDatosInventario()

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = inventarioAdapter.getItem(position) as InventarioItem
        }

        // Botón Agregar
        val btnAgregar: Button = findViewById(R.id.btnAgregar)
        btnAgregar.setOnClickListener {
            mostrarDialogoAgregarElemento()
        }

        val btnEditar: Button = findViewById(R.id.btnEditar)
        btnEditar.isEnabled = false // Deshabilitar inicialmente el botón hasta que se seleccione un elemento

        inventarioAdapter.setOnEditClickListener { selectedItem ->
            mostrarDialogoEditarElemento(selectedItem)
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            inventarioAdapter.getSelectedItem()?.let { selectedItem ->
                // Habilitar el botón de editar cuando se selecciona un elemento
                btnEditar.isEnabled = true
                inventarioAdapter.notifyDataSetChanged()
            }
        }

        btnEditar.setOnClickListener {
            inventarioAdapter.getSelectedItem()?.let { selectedItem ->
                mostrarDialogoEditarElemento(selectedItem)
            }
        }
    }
    private fun mostrarDialogoAgregarElemento() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_elemento, null)
        val etNombre: EditText = dialogView.findViewById(R.id.etNombre)
        val etCantidad: EditText = dialogView.findViewById(R.id.etCantidad)
        val etUnidadMedida: EditText = dialogView.findViewById(R.id.etUnidadMedida)
        val etPrecioProd: EditText = dialogView.findViewById(R.id.etPrecioProd)
        val btnGuardarElemento: Button = dialogView.findViewById(R.id.btnGuardarElemento)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Agregar Elemento")
            .setCancelable(true)
            .create()

        btnGuardarElemento.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val cantidad = etCantidad.text.toString().toDoubleOrNull() ?: 0.0
            val unidadMedida = etUnidadMedida.text.toString().trim()
            val precioProd = etPrecioProd.text.toString().toDoubleOrNull() ?: 0.0

            // Guardar elemento en la base de datos y actualizar la lista
            guardarElementoEnBaseDeDatos(nombre, cantidad, unidadMedida, precioProd)
            dialogBuilder.dismiss()
        }

        dialogBuilder.show()
    }

    private fun mostrarDialogoEditarElemento(item: InventarioItem) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_elemento, null)
        val etNombre: EditText = dialogView.findViewById(R.id.etNombre)
        val etCantidad: EditText = dialogView.findViewById(R.id.etCantidad)
        val etUnidadMedida: EditText = dialogView.findViewById(R.id.etUnidadMedida)
        val etPrecioProd: EditText = dialogView.findViewById(R.id.etPrecioProd)
        val btnGuardarElemento: Button = dialogView.findViewById(R.id.btnGuardarElemento)

        // Rellenar los EditTexts con los datos del elemento seleccionado
        etNombre.setText(item.nombre)
        etCantidad.setText(item.cantidad.toString())
        etUnidadMedida.setText(item.unidadMedida)
        etPrecioProd.setText(item.precioProd.toString())

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Editar Elemento")
            .setCancelable(true)
            .create()

        btnGuardarElemento.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val cantidad = etCantidad.text.toString().toDoubleOrNull() ?: 0.0
            val unidadMedida = etUnidadMedida.text.toString().trim()
            val precioProd = etPrecioProd.text.toString().toDoubleOrNull() ?: 0.0

            // Actualizar elemento en la base de datos y en la lista local
            actualizarElementoEnBaseDeDatos(item.idProd, nombre, cantidad, unidadMedida, precioProd)
            dialogBuilder.dismiss()
        }

        dialogBuilder.show()
    }

    private fun guardarElementoEnBaseDeDatos(nombre: String, cantidad: Double, unidadMedida: String, precioProd: Double) {
        val db = FirebaseFirestore.getInstance()
        val inventarioRef = db.collection("Inventario")

        val nuevoElemento = hashMapOf(
            "nombre" to nombre,
            "cantidad" to cantidad,
            "unidadMedida" to unidadMedida,
            "precioProd" to precioProd
        )

        inventarioRef.add(nuevoElemento)
            .addOnSuccessListener { documentReference ->
                // Obtener ID del nuevo elemento creado en la base de datos
                val idProd = documentReference.id

                // Actualizar lista local y adaptador con el nuevo elemento
                val nuevoItem = InventarioItem(nombre, cantidad, unidadMedida, idProd, precioProd)
                inventarioList.add(nuevoItem)
                inventarioAdapter.actualizarLista(inventarioList)

                Toast.makeText(this, "Elemento agregado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                // Manejar errores al guardar en la base de datos
            }
    }


    private fun actualizarElementoEnBaseDeDatos(idProd: String, nombre: String, cantidad: Double, unidadMedida: String, precioProd: Double) {
        val db = FirebaseFirestore.getInstance()
        val inventarioRef = db.collection("Inventario").document(idProd)

        val actualizacion = hashMapOf(
            "nombre" to nombre,
            "cantidad" to cantidad,
            "unidadMedida" to unidadMedida,
            "precioProd" to precioProd
            // No actualizamos el precio aquí, puedes agregar lógica adicional si necesitas actualizar el precio
        )

        inventarioRef.update(actualizacion as Map<String, Any>)
            .addOnSuccessListener {
                // Actualizar elemento en la lista local y en el adaptador
                val itemActualizado = inventarioList.find { it.idProd == idProd }
                itemActualizado?.let {
                    it.nombre = nombre
                    it.cantidad = cantidad
                    it.unidadMedida = unidadMedida
                    it.precioProd = precioProd
                    inventarioAdapter.actualizarLista(inventarioList)
                }
            }
            .addOnFailureListener { exception ->
                // Manejar errores al actualizar en la base de datos
            }
    }

    private fun obtenerDatosInventario() {
        val db = FirebaseFirestore.getInstance()
        val inventarioRef = db.collection("Inventario")

        inventarioRef.get()
            .addOnSuccessListener { result ->
                val listaInventario = mutableListOf<InventarioItem>()
                for (document in result) {
                    val nombre = document.getString("nombre") ?: ""
                    val cantidad = document.getDouble("cantidad") ?: 0.0
                    val unidadMedida = document.getString("unidadMedida") ?: ""
                    val idProd = document.getString("idProd") ?: ""
                    val precioProd = document.getDouble("precioProd") ?: 0.0

                    val item = InventarioItem(nombre, cantidad, unidadMedida, idProd, precioProd)
                    listaInventario.add(item)
                }
                // Asignar la lista de inventario obtenida a inventarioList y actualizar el adaptador
                inventarioList = listaInventario
                inventarioAdapter.actualizarLista(inventarioList)
            }
            .addOnFailureListener { exception ->
                // Manejar errores al obtener datos del inventario desde Firestore
            }
    }}
