package mx.edu.itesca.jamuttekil

import InventarioAdapter
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
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
    private lateinit var btnEditar: Button

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
            inventarioAdapter.setSelectedItem(selectedItem) // Establecer elemento seleccionado en el adaptador
            btnEditar.isEnabled = true // Habilitar el botón de editar
            inventarioAdapter.notifyDataSetChanged() // Notificar al adaptador sobre el cambio
        }

        btnEditar = findViewById(R.id.btnEditar)
        btnEditar.isEnabled = false // Deshabilitar el botón de editar inicialmente

        inventarioAdapter.setOnEditClickListener { selectedItem ->
            mostrarDialogoEditarElemento(selectedItem)
        }

        // Botón Agregar
        val btnAgregar: Button = findViewById(R.id.btnAgregar)
        btnAgregar.setOnClickListener {
            mostrarDialogoAgregarElemento()
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

        manejarSeleccionElemento()
    }

    private fun manejarSeleccionElemento() {
        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = inventarioAdapter.getItem(position) as InventarioItem
            inventarioAdapter.setSelectedItem(selectedItem)
            btnEditar.isEnabled =
                true // Habilitar el botón de editar cuando se selecciona un elemento

        }

        // Al hacer clic en el botón Editar se activa y ya es clickeable
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
        val btnEliminarElemento: Button = dialogView.findViewById(R.id.btnEliminarElemento) // Nuevo botón para eliminar



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
        //aqui se usa el listener para llamar a las funciones de eliminar
        btnEliminarElemento.setOnClickListener {
            mostrarConfirmacionEliminar(item.idProd)
            dialogBuilder.dismiss() // Cerrar el diálogo de editar después de eliminar
        }

        dialogBuilder.show()
    }

    private fun guardarElementoEnBaseDeDatos(
        nombre: String,
        cantidad: Double,
        unidadMedida: String,
        precioProd: Double
    ) {
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
                inventarioAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Elemento agregado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al actualizar el elemento", Toast.LENGTH_SHORT).show()
            }
    }


    private fun actualizarElementoEnBaseDeDatos(
        idProd: String,
        nombre: String,
        cantidad: Double,
        unidadMedida: String,
        precioProd: Double
    ) {
        val db = FirebaseFirestore.getInstance()
        val inventarioRef = db.collection("Inventario").document(idProd)

        val actualizacion = hashMapOf(
            "nombre" to nombre,
            "cantidad" to cantidad,
            "unidadMedida" to unidadMedida,
            "precioProd" to precioProd
        )

        inventarioRef.update(actualizacion as Map<String, Any>)
            .addOnSuccessListener {
                // Actualizar elemento en la lista local
                val itemActualizado = inventarioList.find { it.idProd == idProd }
                itemActualizado?.apply {
                    this.nombre = nombre
                    this.cantidad = cantidad
                    this.unidadMedida = unidadMedida
                    this.precioProd = precioProd
                }

                // Notificar al adaptador de la actualización
                inventarioAdapter.notifyDataSetChanged()

                // Mostrar mensaje o realizar cualquier otra acción
                Toast.makeText(this, "Elemento actualizado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al actualizar el elemento", Toast.LENGTH_SHORT).show()
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
                    val idProd = document.id // Obtener el ID del documento
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
                Log.e(TAG, "Error al obtener datos del inventario: $exception")
            }
    }
    private fun mostrarConfirmacionEliminar(idProd: String) {
        val confirmacionDialog = AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar este elemento?")
            .setPositiveButton("Sí") { _, _ ->
                eliminarElemento(idProd)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        confirmacionDialog.show()
    }

    private fun eliminarElemento(idProd: String) {
        val db = FirebaseFirestore.getInstance()
        val inventarioRef = db.collection("Inventario").document(idProd)

        inventarioRef.delete()
            .addOnSuccessListener {
                // Eliminar el elemento de la lista local y actualizar el adaptador
                val itemEliminado = inventarioList.find { it.idProd == idProd }
                itemEliminado?.let {
                    inventarioList.remove(it)
                    inventarioAdapter.actualizarLista(inventarioList)
                }
                Toast.makeText(this, "Elemento eliminado exitosamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al eliminar el elemento", Toast.LENGTH_SHORT).show()
            }
    }

}