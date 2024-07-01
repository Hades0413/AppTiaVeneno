package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apptiaveneno.Adapter.ProductoAdapter
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainProducto : AppCompatActivity() {

    private lateinit var idDescripcionProducto: EditText
    private lateinit var btnRegresarProducto: Button
    private lateinit var btnIrCrearNuevoProducto: Button
    private lateinit var btnConsultarProducto: Button
    private lateinit var listarProducto: ListView

    //bottom_app_bar
    private lateinit var homeBtn: LinearLayout
    private lateinit var comidasBtn: LinearLayout
    private lateinit var categoriasBtn: LinearLayout
    private lateinit var ventasBtn: LinearLayout
    private lateinit var modoBtn: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_producto)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        idDescripcionProducto = findViewById(R.id.idDescripcionProducto)
        btnRegresarProducto = findViewById(R.id.btnRegresarProducto)
        btnIrCrearNuevoProducto = findViewById(R.id.btnIrCrearNuevaProducto)
        btnConsultarProducto = findViewById(R.id.btnConsultarProducto)
        listarProducto = findViewById(R.id.listarProducto)

        btnRegresarProducto.setOnClickListener { volverMenuPrincipal() }
        btnIrCrearNuevoProducto.setOnClickListener { irCrearNuevoProducto() }
        btnConsultarProducto.setOnClickListener { consultarProducto() }

        listarProducto.setOnItemClickListener { parent, view, position, id ->
            try {
                val productoSeleccionado = parent.getItemAtPosition(position) as JSONObject
                val idProducto = productoSeleccionado.getInt("idProducto")
                val codigo = productoSeleccionado.getString("codigo")
                val idCategoria = if (productoSeleccionado.has("oCategoria")) {
                    val oCategoria = productoSeleccionado.getJSONObject("oCategoria")
                    oCategoria.getInt("idCategoria")
                } else {
                    -1 // Establecer un valor por defecto si no hay categoría definida
                }
                val descripcion = productoSeleccionado.getString("descripcion")
                val precioCompra = productoSeleccionado.getDouble("precioCompra")
                val precioVenta = productoSeleccionado.getDouble("precioVenta")
                val stock = productoSeleccionado.getInt("stock")
                val rutaImagen = productoSeleccionado.getString("rutaImagen")

                val intent = Intent(this@MainProducto, MainDatosProducto::class.java).apply {
                    putExtra("idProducto", idProducto)
                    putExtra("codigo", codigo)
                    putExtra("idCategoria", idCategoria)
                    putExtra("descripcion", descripcion)
                    putExtra("precioCompra", precioCompra)
                    putExtra("precioVenta", precioVenta)
                    putExtra("stock", stock)
                    putExtra("rutaImagen", rutaImagen)
                }
                startActivity(intent)

            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(this@MainProducto, "Error al obtener los datos del producto", Toast.LENGTH_SHORT).show()
            }
        }

        //bottom_app_bar
        homeBtn = findViewById(R.id.homeBtn)
        comidasBtn = findViewById(R.id.comidasBtn)
        categoriasBtn = findViewById(R.id.categoriasBtn)
        ventasBtn = findViewById(R.id.ventasBtn)
        modoBtn = findViewById(R.id.modoBtn)
        homeBtn.setOnClickListener { home() }
        categoriasBtn.setOnClickListener { vercategoria() }
        comidasBtn.setOnClickListener { verproducto() }
        ventasBtn.setOnClickListener { verventa() }
        modoBtn.setOnClickListener { vermodo() }

        cargarProductos()
    }

    //bottom_app_bar
    private fun home() {
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
    }
    private fun verproducto() {
        val intent = Intent(this, MainProducto::class.java)
        startActivity(intent)
    }
    private fun verventa() {
        val intent = Intent(this, MainVenta::class.java)
        startActivity(intent)
    }
    private fun vercategoria() {
        val intent = Intent(this, MainCategoria::class.java)
        startActivity(intent)
    }
    private fun vermodo() {
        val intent = Intent(this, MainColaborador::class.java)
        startActivity(intent)
    }

    //Actualizar automáticamente la vista
    override fun onResume() {
        super.onResume()
        cargarProductos()
    }


    private fun cargarProductos() {
        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("https://tiaveneno.somee.com/api/Inventario/productos")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Content-Type", "application/json")

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                // Procesar la respuesta aquí, como crear un adaptador y asignarlo al ListView
                val jsonArray = JSONArray(response.toString())

                // Crear un adaptador personalizado para mostrar los productos en un ListView
                val adapter = ProductoAdapter(this@MainProducto, jsonArray)

                // Asignar el adaptador al ListView en el layout
                runOnUiThread {
                    listarProducto.adapter = adapter
                }
            }
        }
    }

    private fun consultarProducto() {
        val searchQuery = idDescripcionProducto.text.toString().trim()

        if (searchQuery.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val url = URL("https://tiaveneno.somee.com/api/Inventario/productos")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.setRequestProperty("Content-Type", "application/json")

                    val responseCode = conn.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val reader = BufferedReader(InputStreamReader(conn.inputStream))
                        val response = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }
                        reader.close()

                        val jsonArray = JSONArray(response.toString())
                        val filteredList = JSONArray()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val descripcion = jsonObject.getString("descripcion")
                            val idProducto = jsonObject.getString("idProducto")
                            val codigo = jsonObject.getString("codigo")
                            val idCategoria = jsonObject.optInt("idCategoria", -1).toString()

                            if (descripcion.contains(searchQuery, ignoreCase = true) ||
                                idProducto.equals(searchQuery, ignoreCase = true) ||
                                codigo.equals(searchQuery, ignoreCase = true) ||
                                idCategoria.equals(searchQuery, ignoreCase = true)
                            ) {
                                filteredList.put(jsonObject)
                            } else if (codigo.contains(searchQuery, ignoreCase = true)) {
                                filteredList.put(jsonObject)
                            }
                        }

                        runOnUiThread {
                            if (filteredList.length() > 0) {
                                val adapter = ProductoAdapter(this@MainProducto, filteredList)
                                listarProducto.adapter = adapter
                            } else {
                                mostrarAlertaProductoNoEncontrado()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            mostrarAlertaProductoVacio()
        }
    }


    private fun mostrarAlertaProductoVacio() {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("No has ingresado ninguna descripción.")
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun mostrarAlertaProductoNoEncontrado() {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("El producto ingresado no se encuentra en la base de datos.")
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun volverMenuPrincipal() {
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
    }

    private fun irCrearNuevoProducto() {
        val intent = Intent(this, MainNuevoProducto::class.java)
        startActivity(intent)
    }
}
