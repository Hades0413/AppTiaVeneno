package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class CambioContrasenaActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var edtNombreCompleto: EditText
    private lateinit var edtCorreo: EditText
    private lateinit var idVolverLoginCambio: TextView
    private lateinit var btnRegistrarUsuarioCambio: ConstraintLayout

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambio_contrasena)

        edtNombreCompleto = findViewById(R.id.idRegistrarNombreCompletoCambio)
        edtCorreo = findViewById(R.id.idRegistrarCorreoCambio)
        idVolverLoginCambio = findViewById(R.id.idVolverLoginCambio)
        btnRegistrarUsuarioCambio = findViewById(R.id.btnRegistrarUsuarioCambio)

        btnRegistrarUsuarioCambio.setOnClickListener(this)
        idVolverLoginCambio.setOnClickListener{volver()}
    }

    override fun onClick(v: View) {
        val nombreCompleto = edtNombreCompleto.text.toString()
        val correo = edtCorreo.text.toString()

        if (nombreCompleto.isNotEmpty() && correo.isNotEmpty()) {
            coroutineScope.launch {
                buscarUsuarioPorNombreYCorreo(nombreCompleto, correo)
            }
        } else {
            val mensaje = "Por favor completa todos los campos."
            mostrarAlertaCamposFaltantes(mensaje)
        }
    }

    private fun mostrarAlertaCamposFaltantes(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("Campos incompletos")
            .setMessage(mensaje)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private suspend fun buscarUsuarioPorNombreYCorreo(nombreCompleto: String, correo: String) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://tiaveneno.somee.com/api/Usuario")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 10000
                conn.readTimeout = 10000

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = conn.inputStream
                    val response = inputStream.bufferedReader().use { it.readText() }
                    val usuarios = JSONArray(response)

                    var usuario: JSONObject? = null
                    for (i in 0 until usuarios.length()) {
                        val u = usuarios.getJSONObject(i)
                        if (u.optString("nombreCompleto") == nombreCompleto && u.optString("correo") == correo) {
                            usuario = u
                            break
                        }
                    }

                    if (usuario != null) {
                        // Mostrar alerta con los datos del usuario
                        withContext(Dispatchers.Main) {
                            mostrarAlertaActualizarContrasena(usuario)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Error al buscar el usuario: ${conn.responseMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Error al conectar con la API: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun mostrarAlertaActualizarContrasena(usuario: JSONObject) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_actualizar_contrasena, null)
        builder.setView(dialogView)

        val txtUsuarioDatos = dialogView.findViewById<TextView>(R.id.txtUsuarioDatos)
        val edtNuevaContrasena = dialogView.findViewById<EditText>(R.id.edtNuevaContrasena)
        val btnActualizarContrasena = dialogView.findViewById<Button>(R.id.btnActualizarContrasena)

        val usuarioDatos = "ID: ${usuario.optInt("idUsuario")}\nNombre: ${usuario.optString("nombreCompleto")}\nCorreo: ${usuario.optString("correo")}"
        txtUsuarioDatos.text = usuarioDatos

        val alertDialog = builder.create()

        btnActualizarContrasena.setOnClickListener {
            val nuevaContrasena = edtNuevaContrasena.text.toString()
            if (nuevaContrasena.isNotEmpty()) {
                alertDialog.dismiss()
                val idUsuario = usuario.optInt("idUsuario")
                val nombreCompleto = usuario.optString("nombreCompleto")
                val correo = usuario.optString("correo")
                coroutineScope.launch {
                    actualizarContrasena(idUsuario, nombreCompleto, correo, nuevaContrasena)
                }
            } else {
                Toast.makeText(applicationContext, "Por favor ingrese una nueva contraseña.", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.show()
    }


    private suspend fun actualizarContrasena(idUsuario: Int, nombreCompleto: String, correo: String, nuevaContrasena: String) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://tiaveneno.somee.com/api/Usuario/$idUsuario")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true
                conn.connectTimeout = 10000
                conn.readTimeout = 10000

                val jsonObject = JSONObject().apply {
                    put("idUsuario", idUsuario)
                    put("nombreCompleto", nombreCompleto)
                    put("correo", correo)
                    put("clave", nuevaContrasena)
                }

                conn.outputStream.use { os ->
                    val input = jsonObject.toString().toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                val responseCode = conn.responseCode
                val responseMessage = conn.responseMessage
                val responseBody = conn.inputStream.bufferedReader().use { it.readText() }

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Contraseña cambiada exitosamente", Toast.LENGTH_SHORT).show()
                        // Redirigir al Login
                        val intent = Intent(this@CambioContrasenaActivity, Login::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Log.e("API_ERROR", "Error al cambiar la contraseña: $responseCode $responseMessage $responseBody")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error al conectar con la API: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Error al conectar con la API", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun volver(){
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }

}
