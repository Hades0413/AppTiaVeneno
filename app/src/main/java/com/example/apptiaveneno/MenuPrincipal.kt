package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apptiaveneno.Adapter.CategoriaMenuPrincipalAdapter
import com.example.apptiaveneno.Adapter.ProductoMenuPrincipalAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MenuPrincipal : AppCompatActivity() {
    private lateinit var comidasBtn: LinearLayout
    private lateinit var categoriasBtn: LinearLayout
    private lateinit var listaCategoriaMenuPrincipal: RecyclerView
    private lateinit var listaProductoMenuPrincipal: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        comidasBtn = findViewById(R.id.comidasBtn)
        categoriasBtn = findViewById(R.id.categoriasBtn)
        listaCategoriaMenuPrincipal = findViewById(R.id.listaCategoriaMenuPrincipal)
        listaProductoMenuPrincipal = findViewById(R.id.listaProductoMenuPrincipal)

        val layoutManager = GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)
        listaCategoriaMenuPrincipal.layoutManager = layoutManager

        categoriasBtn.setOnClickListener { vercategoria() }
        comidasBtn.setOnClickListener { verproducto() }
    }

    private fun vercategoria() {
        val intent = Intent(this, MainCategoria::class.java)
        startActivity(intent)
    }

    private fun verproducto() {
        val intent = Intent(this, MainProducto::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        fetchCategorias()
        cargarProductos()
    }

    private fun fetchCategorias() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://tiaveneno.somee.com/api/Inventario/categorias")
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

                    runOnUiThread {
                        val adapter = CategoriaMenuPrincipalAdapter(this@MenuPrincipal, jsonArray)
                        listaCategoriaMenuPrincipal.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun cargarProductos() {
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

                    runOnUiThread {
                        val adapter = ProductoMenuPrincipalAdapter(this@MenuPrincipal, jsonArray)
                        listaProductoMenuPrincipal.layoutManager = LinearLayoutManager(this@MenuPrincipal)
                        listaProductoMenuPrincipal.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
