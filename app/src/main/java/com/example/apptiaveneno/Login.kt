package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.apptiaveneno.Entity.Usuario
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Login : AppCompatActivity() {
    private lateinit var edtCorreo: EditText
    private lateinit var edtClave: EditText
    private lateinit var idRegistrar: TextView
    private lateinit var btnLogin: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtCorreo = findViewById(R.id.Correo)
        edtClave = findViewById(R.id.editTextContrasenia)
        idRegistrar = findViewById(R.id.idRegistrar)
        btnLogin = findViewById(R.id.login_btn)

        btnLogin.setOnClickListener {
            val correo = edtCorreo.text.toString()
            val clave = edtClave.text.toString()

            if (correo.isEmpty() || clave.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Debes de ingresar datos, no seas imbécil :v",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                login(correo, clave)
            }
        }

        idRegistrar.setOnClickListener {
            registrar()
        }
    }

    private fun login(correo: String, clave: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val successWithAPI = try {
                performLoginWithAPI(correo, clave)
            } catch (e: Exception) {
                Log.e("Login", "Error en la conexión con la API: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        "Error de conexión con la API",
                        Toast.LENGTH_LONG
                    ).show()
                }
                false
            }

            if (successWithAPI) {
                withContext(Dispatchers.Main) {
                    Log.d("Login", "Inicio de sesión exitoso")
                    startActivity(Intent(this@Login, MenuPrincipal::class.java))
                    finish()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Log.d("Login", "Credenciales incorrectas")
                    Toast.makeText(
                        applicationContext,
                        "Usuario y/o Contraseña incorrectos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun performLoginWithAPI(correo: String, clave: String): Boolean {
        return try {
            val url = URL("https://gamarraplus.somee.com/api/Usuario") // Cambia a HTTPS

            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET" // Cambia a GET
            conn.setRequestProperty("Content-Type", "application/json")

            val reader = BufferedReader(InputStreamReader(conn.inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()

            val jsonArray = JSONArray(response.toString())
            for (i in 0 until jsonArray.length()) {
                val usuario = jsonArray.getJSONObject(i)
                val correoUsuario = usuario.getString("correo")
                val claveUsuario = usuario.getString("clave")
                if (correo == correoUsuario && clave == claveUsuario) {
                    // Credenciales correctas
                    return true
                }
            }
            // Si no se encuentra el usuario o las credenciales son incorrectas, devuelve false
            false
        } catch (e: Exception) {
            // Si hay un error de conexión o procesamiento, registra el error y devuelve false
            Log.e("Login", "Error de conexión: ${e.message}", e)
            false
        }
    }


    private fun registrar() {
        val intent = Intent(this, MainRegistrar::class.java)
        startActivity(intent)
    }
}
