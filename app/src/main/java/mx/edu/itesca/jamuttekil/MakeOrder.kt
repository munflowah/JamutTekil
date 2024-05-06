package mx.edu.itesca.jamuttekil

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import mx.edu.itesca.jamuttekil.databinding.ActivityMakeorderBinding
import org.json.JSONArray

class MakeOrder : AppCompatActivity() {
    private lateinit var binding: ActivityMakeorderBinding
    private lateinit var adapter: AdaptadorProducto

    var listaProductos = ArrayList<Producto>()
    var carroCompras = ArrayList<Producto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMakeorderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        agregarProductos()
        setupRecyclerView()
    }

    fun setupRecyclerView() {
        binding.lsProductos.layoutManager = LinearLayoutManager(this)
        adapter = AdaptadorProducto(this,binding.tVCantidadCarro,binding.imgCar,listaProductos, carroCompras )
        binding.lsProductos.adapter = adapter
    }

    private fun agregarProductos() {
        listaProductos.add(Producto("001",R.drawable.chorizopaquete,"Chorizo de Puerco", "1Kg",60.0))
        listaProductos.add(Producto("002",R.drawable.chorizopaquete,"Chorizo de Puerco", "500g",30.0))
        listaProductos.add(Producto("003",R.drawable.chorizopaquete, "Chorizo de Puerco", "250g",15.0))
    }
    fun leerSharedPreferences(){
        val sp = this.getSharedPreferences("carro_compras", MODE_PRIVATE)
        val jsonString = sp.getString("productos", "")
        val jsonArray = JSONArray(jsonString)

        if (jsonArray != null) {
            for (i in 0 until jsonArray.length()) {
                val productoJson = jsonArray.getJSONObject(i)

                carroCompras.add(
                    Producto(
                        productoJson.getString("id"),
                        productoJson.getInt("imagen"),
                        productoJson.getString("nombre"),
                        productoJson.getString("cantidad"),
                        productoJson.getDouble("precio")
                    )
                )
            }
        }
    }
}