package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apptiaveneno.Adapter.ProductoPorCategoriaAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainCategoriaPorProducto : AppCompatActivity() {
    private lateinit var productoAdapter: ProductoPorCategoriaAdapter
    private lateinit var listaProductoPorCategoria: RecyclerView
    private var categoriaId: Int = 0
    private var categoriaDescripcion: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_categoria_por_producto)

        val backButton = findViewById<ImageButton>(R.id.regresarMenuPrincipal)
        backButton.setOnClickListener {
            val intent = Intent(this, MenuPrincipal::class.java)
            startActivity(intent)
            finish() // Opcional: cerrar la actividad actual si no se espera regresar
        }

        val homeButton = findViewById<LinearLayout>(R.id.homeBtn)
        homeButton.setOnClickListener {
            val intent = Intent(this, MenuPrincipal::class.java)
            startActivity(intent)
        }

        val comidasButton = findViewById<LinearLayout>(R.id.comidasBtn)
        comidasButton.setOnClickListener {
            val intent = Intent(this, MainProducto::class.java)
            startActivity(intent)
        }

        val ventasButton = findViewById<LinearLayout>(R.id.ventasBtn)
        ventasButton.setOnClickListener {
            val intent = Intent(this, MainVenta::class.java)
            startActivity(intent)
        }

        val categoriasButton = findViewById<LinearLayout>(R.id.categoriasBtn)
        categoriasButton.setOnClickListener {
            val intent = Intent(this, MainCategoria::class.java)
            startActivity(intent)
        }

        val goatsButton = findViewById<LinearLayout>(R.id.modoBtn)
        goatsButton.setOnClickListener {
            val intent = Intent(this, MainColaborador::class.java)
            startActivity(intent)
        }

        categoriaId = intent.getIntExtra("categoriaId", 0)
        categoriaDescripcion = intent.getStringExtra("categoriaDescripcion") ?: ""

        setupViews()
        cargarProductosPorCategoria()
    }

    private fun setupViews() {
        listaProductoPorCategoria = findViewById(R.id.listaProductoSegunCategoria)
        listaProductoPorCategoria.layoutManager = LinearLayoutManager(this)

        val categoriaElegidaTextView = findViewById<TextView>(R.id.CategoriaElegida)
        categoriaElegidaTextView.text = categoriaDescripcion
    }

    private fun cargarProductosPorCategoria() {
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

                    val allProducts = JSONArray(response.toString())
                    val filteredProducts = JSONArray()

                    for (i in 0 until allProducts.length()) {
                        val product = allProducts.getJSONObject(i)
                        val categoria = product.getJSONObject("oCategoria")
                        if (categoria.getInt("idCategoria") == categoriaId) {
                            filteredProducts.put(product)
                        }
                    }

                    runOnUiThread {
                        productoAdapter = ProductoPorCategoriaAdapter(this@MainCategoriaPorProducto, filteredProducts)
                        listaProductoPorCategoria.adapter = productoAdapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}