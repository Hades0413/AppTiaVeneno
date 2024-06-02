package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var edtCorreo: EditText
    private lateinit var edtClave: EditText
    private lateinit var btnLogin: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtCorreo = findViewById(R.id.editTextUsuario)
        edtClave = findViewById(R.id.editTextContrasenia)
        btnLogin = findViewById(R.id.login_btn)

        btnLogin.setOnClickListener {
            val correo = edtCorreo.text.toString()
            val clave = edtClave.text.toString()
            login(correo, clave)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun login(correo: String, clave: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val success = performLogin(correo, clave)
            withContext(Dispatchers.Main) {
                if (success) {
                    Log.d("MainActivity", "Inicio de sesión exitoso")
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    finish()
                } else {
                    Log.d("MainActivity", "Credenciales incorrectas")
                    Toast.makeText(
                        applicationContext,
                        "Usuario y/o Contraseña incorrectos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun performLogin(correo: String, clave: String): Boolean {
        return try {
            val url = URL("https://192.168.18.6:7034/api/Usuario")

            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val jsonObject = JSONObject().apply {
                put("correo", correo)
                put("clave", clave)
            }

            conn.outputStream.use { os ->
                val input = jsonObject.toString().toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val reader = BufferedReader(InputStreamReader(conn.inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()

            val jsonResponse = JSONObject(response.toString())
            jsonResponse.getBoolean("success")
        } catch (e: java.net.ConnectException) {
            Log.e("MainActivity", "Error de conexión: ${e.message}", e)
            false
        } catch (e: Exception) {
            Log.e("MainActivity", "Error en la conexión con la API", e)
            false
        }
    }
}
