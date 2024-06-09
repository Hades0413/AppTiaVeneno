package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

class MainDatosCategoria : AppCompatActivity(), View.OnClickListener {

    private lateinit var idCrudCodigoCategoria: TextView
    private lateinit var idCrudDescripcionCategoria: EditText
    private lateinit var btnActualizarCategoria: Button
    private lateinit var btnEliminarCategoria: Button
    private lateinit var btnCrudVolverCategoria: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_datos_categoria)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        idCrudCodigoCategoria = findViewById(R.id.idCrudCodigoCategoria)
        idCrudDescripcionCategoria = findViewById(R.id.idCrudDescripcionCategoria)
        btnActualizarCategoria = findViewById(R.id.btnActualizarCategoria)
        btnEliminarCategoria = findViewById(R.id.btnEliminarCategoria)
        btnCrudVolverCategoria = findViewById(R.id.btnCrudVolverCategoria)

        val idCategoria = intent.getIntExtra("idCategoria", -1)
        val descripcion = intent.getStringExtra("descripcion")

        idCrudCodigoCategoria.text = idCategoria.toString()
        idCrudDescripcionCategoria.setText(descripcion)

        btnActualizarCategoria.setOnClickListener(this)
        btnEliminarCategoria.setOnClickListener(this)
        btnCrudVolverCategoria.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnActualizarCategoria -> {
                val idCategoria = idCrudCodigoCategoria.text.toString().toIntOrNull()
                val descripcion = idCrudDescripcionCategoria.text.toString()

                if (idCategoria != null && descripcion.isNotEmpty()) {
                    actualizarCategoria(idCategoria, descripcion)
                } else {
                    Toast.makeText(applicationContext, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btnEliminarCategoria -> {
                val idCategoria = idCrudCodigoCategoria.text.toString().toIntOrNull()

                if (idCategoria != null) {
                    eliminarCategoria(idCategoria)
                } else {
                    Toast.makeText(applicationContext, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btnCrudVolverCategoria -> {
                val intent = Intent(this, MainCategoria::class.java)
                startActivity(intent)
            }
        }
    }

    private fun actualizarCategoria(idCategoria: Int, descripcion: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://tiaveneno.somee.com/api/Inventario/categorias/$idCategoria")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val jsonObject = JSONObject().apply {
                    put("idCategoria", idCategoria)
                    put("descripcion", descripcion)
                }

                conn.outputStream.use { os ->
                    val input = jsonObject.toString().toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Se actualizó correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    launch(Dispatchers.Main) {
                        Log.e("Error de actualización", "No se pudo actualizar. Código de respuesta: $responseCode")
                        Toast.makeText(applicationContext, "No se pudo actualizar", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Log.e("Error de conexión", "Error al conectar con la API: ${e.message}")
                    Toast.makeText(applicationContext, "Error al conectar con la API", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        }
    }


    private fun eliminarCategoria(idCategoria: Int) {
        val alertDialog = AlertDialog.Builder(this).create()
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_alert_dialog_categoria, null)

        dialogView.findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            alertDialog.dismiss() // Cerrar el diálogo si se hace clic en "Cancelar"
        }

        dialogView.findViewById<Button>(R.id.buttonAccept).setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val url = URL("https://tiaveneno.somee.com/api/Inventario/categorias/$idCategoria")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "DELETE"
                    conn.setRequestProperty("Content-Type", "application/json")

                    val responseCode = conn.responseCode
                    Log.d("EliminarCategoria", "Código de respuesta de la API: $responseCode") // Mensaje de registro agregado

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        launch(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "Se eliminó correctamente", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        launch(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "No se pudo eliminar", Toast.LENGTH_SHORT).show()
                            Log.e("EliminarCategoria", "No se pudo eliminar. Código de respuesta: $responseCode")
                        }
                    }
                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Error al conectar con la API", Toast.LENGTH_SHORT).show()
                        Log.e("EliminarCategoria", "Error al conectar con la API: ${e.message}")
                    }
                    e.printStackTrace()
                }
            }
            alertDialog.dismiss() // Cerrar el diálogo después de eliminar la categoría
        }

        alertDialog.setView(dialogView)
        alertDialog.setCancelable(false) // Evitar que el diálogo se cierre al tocar fuera de él
        alertDialog.show()
    }



}
