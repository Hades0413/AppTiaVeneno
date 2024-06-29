package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apptiaveneno.Adapter.CategoriaMenuPrincipalAdapter
import com.example.apptiaveneno.Adapter.CategoriaPorProductoMenuPrincipalAdapter
import com.example.apptiaveneno.Adapter.ProductoMenuPrincipalAdapter
import com.example.apptiaveneno.Adapter.ProductoPorCategoriaAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
class MenuPrincipal : AppCompatActivity() {
    private lateinit var categoriaAdapter: CategoriaMenuPrincipalAdapter
    private lateinit var productoAdapter: ProductoMenuPrincipalAdapter
    private lateinit var comidasBtn: LinearLayout
    private lateinit var categoriasBtn: LinearLayout
    private lateinit var ventasBtn: LinearLayout
    private lateinit var modoBtn: LinearLayout
    private lateinit var listaCategoriaMenuPrincipal: RecyclerView
    private lateinit var listaProductoMenuPrincipal: RecyclerView
    private lateinit var idDescripcionProducto: EditText
    private lateinit var allProducts: JSONArray



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
        ventasBtn = findViewById(R.id.ventasBtn)
        modoBtn = findViewById(R.id.modoBtn)
        listaCategoriaMenuPrincipal = findViewById(R.id.listaCategoriaMenuPrincipal)
        listaProductoMenuPrincipal = findViewById(R.id.listaProductoMenuPrincipal)
        idDescripcionProducto = findViewById(R.id.idDescripcionProducto)

        val layoutManager = GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)
        listaCategoriaMenuPrincipal.layoutManager = layoutManager

        categoriasBtn.setOnClickListener { vercategoria() }
        comidasBtn.setOnClickListener { verproducto() }
        ventasBtn.setOnClickListener { verventa() }
        modoBtn.setOnClickListener { vermodo() }

        idDescripcionProducto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                consultarProducto()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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
                        categoriaAdapter = CategoriaMenuPrincipalAdapter(
                            this@MenuPrincipal,
                            jsonArray
                        ) { categoria ->
                            if (categoria.has("idCategoria")) {
                                val intent = Intent(
                                    this@MenuPrincipal,
                                    MainCategoriaPorProducto::class.java
                                ).apply {
                                    putExtra("categoriaId", categoria.getInt("idCategoria"))
                                    putExtra(
                                        "categoriaDescripcion",
                                        categoria.getString("descripcion")
                                    )
                                }
                                startActivity(intent)
                            }
                        }
                        listaCategoriaMenuPrincipal.adapter = categoriaAdapter
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

                    allProducts = JSONArray(response.toString())

                    runOnUiThread {
                        productoAdapter = ProductoMenuPrincipalAdapter(this@MenuPrincipal, allProducts)
                        listaProductoMenuPrincipal.layoutManager =
                            LinearLayoutManager(this@MenuPrincipal)
                        listaProductoMenuPrincipal.adapter = productoAdapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun consultarProducto() {
        val searchQuery = idDescripcionProducto.text.toString().trim()
        val filteredList = JSONArray()

        for (i in 0 until allProducts.length()) {
            val jsonObject = allProducts.getJSONObject(i)
            if (jsonObject.getString("descripcion").contains(searchQuery, true)) {
                filteredList.put(jsonObject)
            }
        }

        // Update the adapter with the new filtered list
        productoAdapter = ProductoMenuPrincipalAdapter(this, filteredList)
        listaProductoMenuPrincipal.adapter = productoAdapter

        // Notify the adapter of the data change
        productoAdapter.notifyDataSetChanged()
    }
}