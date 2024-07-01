package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainNuevaCategoria : AppCompatActivity(), View.OnClickListener {

    private lateinit var idCrearCategoriaDescripcion: EditText
    private lateinit var btnGrabarCategoria: Button
    private lateinit var btnVolverCategoria: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nueva_categoria)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        idCrearCategoriaDescripcion = findViewById(R.id.idCrearCategoriaDescripcion)
        btnGrabarCategoria = findViewById(R.id.btnGrabarCategoria)
        btnVolverCategoria = findViewById(R.id.btnVolverCategoria)

        btnGrabarCategoria.setOnClickListener(this)
        btnVolverCategoria.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v) {
            btnGrabarCategoria -> {
                val descripcion = idCrearCategoriaDescripcion.text.toString()

                if (descripcion.isNotEmpty()) {
                    grabarCategoria(descripcion)
                } else {
                    mostrarAlerta("Por favor completa todos los campos")
                }
            }
            btnVolverCategoria -> {
                val intent = Intent(this, MainCategoria::class.java)
                startActivity(intent)
            }
        }
    }

    private fun grabarCategoria(descripcion: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://tiaveneno.somee.com/api/Inventario/categorias")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val jsonObject = JSONObject().apply {
                    put("descripcion", descripcion)
                }

                conn.outputStream.use { os ->
                    val input = jsonObject.toString().toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                val responseCode = conn.responseCode
                val responseBody = conn.inputStream.bufferedReader().use { it.readText() }

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Registro exitoso
                    launch(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Se registró correctamente", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainNuevaCategoria, MainCategoria::class.java)
                        startActivity(intent)
                    }
                } else if (responseCode == HttpURLConnection.HTTP_CONFLICT) {
                    // Verificar el contenido de la respuesta para determinar si ya existe
                    val jsonResponse = JSONObject(responseBody)
                    if (jsonResponse.optBoolean("existe", false)) {
                        launch(Dispatchers.Main) {
                            mostrarAlertaCategoriaExistente()
                        }
                    } else {
                        launch(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "Error en el servidor", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Error del servidor
                    launch(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Error en el servidor", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Error al conectar con la API", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun mostrarAlertaCategoriaExistente() {
        AlertDialog.Builder(this@MainNuevaCategoria)
            .setTitle("Error")
            .setMessage("La categoría ya existe en la base de datos.")
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun mostrarAlerta(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alerta")
            .setMessage(mensaje)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }
}
