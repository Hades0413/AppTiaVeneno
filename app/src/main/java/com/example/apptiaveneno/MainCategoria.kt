package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apptiaveneno.Adapter.CategoriaAdapter
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainCategoria : AppCompatActivity() {
    private lateinit var idDescripcionCategoria: EditText
    private lateinit var btnRegresarCategoria: Button
    private lateinit var btnIrCrearNuevaCategoria: Button
    private lateinit var btnConsultarCategoria: Button
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_categoria)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        idDescripcionCategoria = findViewById(R.id.idDescripcionCategoria)
        btnRegresarCategoria = findViewById(R.id.btnRegresarCategoria)
        btnIrCrearNuevaCategoria = findViewById(R.id.btnIrCrearNuevaCategoria)
        btnConsultarCategoria = findViewById(R.id.btnConsultarCategoria)
        listView = findViewById(R.id.listarCategoria)

        btnRegresarCategoria.setOnClickListener { volverMenuPrincipal() }
        btnIrCrearNuevaCategoria.setOnClickListener { irCrearNuevaCategoria() }
        btnConsultarCategoria.setOnClickListener { consultarCategoria() }

        listView.setOnItemClickListener { parent, view, position, id ->
            val categoriaSeleccionada = parent.getItemAtPosition(position) as JSONObject
            val idCategoria = categoriaSeleccionada.getInt("idCategoria")
            val descripcion = categoriaSeleccionada.getString("descripcion")
            val intent = Intent(this@MainCategoria, MainDatosCategoria::class.java).apply {
                putExtra("idCategoria", idCategoria)
                putExtra("descripcion", descripcion)
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        fetchCategorias()
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
                        val adapter = CategoriaAdapter(this@MainCategoria, jsonArray)
                        listView.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun consultarCategoria() {
        val descripcion = idDescripcionCategoria.text.toString().trim()

        if (descripcion.isNotEmpty()) {
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
                        val filteredList = JSONArray()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            if (jsonObject.getString("descripcion") == descripcion) {
                                filteredList.put(jsonObject)
                                break // Si deseas mostrar solo el primer elemento que coincida, de lo contrario, quita el break
                            }
                        }

                        runOnUiThread {
                            if (filteredList.length() > 0) {
                                val adapter = CategoriaAdapter(this@MainCategoria, filteredList)
                                listView.adapter = adapter
                            } else {
                                mostrarAlertaCategoriaNoEncontrada()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            mostrarAlertaCategoriaVacia()
        }
    }

    private fun mostrarAlertaCategoriaVacia() {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("No seas imbécil no has puesto nada :v.")
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun mostrarAlertaCategoriaNoEncontrada() {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("La categoría ingresada no se encuentra en la base de datos.")
            .setPositiveButton("Aceptar", null)
            .show()
    }



    private fun volverMenuPrincipal() {
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
    }

    private fun irCrearNuevaCategoria() {
        val intent = Intent(this, MainNuevaCategoria::class.java)
        startActivity(intent)
    }
}
