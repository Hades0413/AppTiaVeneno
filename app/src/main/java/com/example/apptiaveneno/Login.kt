package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.*
import org.json.JSONArray
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
        idRegistrar = findViewById(R.id.idDataCategoriaDescripcion)
        btnLogin = findViewById(R.id.login_btn)

        btnLogin.setOnClickListener {
            val correo = edtCorreo.text.toString()
            val clave = edtClave.text.toString()

            if (correo.isEmpty() && clave.isEmpty()) {
                showSweetAlertDialog("Debes ingresar usuario y contraseña.")
            } else if (correo.isEmpty()) {
                showSweetAlertDialog("Debes ingresar un usuario.")
            } else if (clave.isEmpty()) {
                showSweetAlertDialog("Debes ingresar una contraseña.")
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
            if (correo.isEmpty() && clave.isEmpty()) {
                runOnUiThread {
                    showSweetAlertDialog("Debes de ingresar usuario y contraseña.")
                }
            } else if (correo.isEmpty()) {
                runOnUiThread {
                    showSweetAlertDialog("Debes de ingresar un usuario.")
                }
            } else if (clave.isEmpty()) {
                runOnUiThread {
                    showSweetAlertDialog("Debes de ingresar una contraseña.")
                }
            } else {
                val successWithAPI = try {
                    performLoginWithAPI(correo, clave)
                } catch (e: Exception) {
                    runOnUiThread {
                        showSweetAlertDialog("Error de conexión con la API")
                    }
                    false
                }

                if (successWithAPI) {
                    runOnUiThread {
                        startActivity(Intent(this@Login, MenuPrincipal::class.java))
                        finish()
                    }
                } else {
                    runOnUiThread {
                        showSweetAlertDialog("Usuario y/o Contraseña incorrectos")
                    }
                }
            }
        }
    }


    private fun performLoginWithAPI(correo: String, clave: String): Boolean {
        return try {
            val url = URL("https://tiaveneno.somee.com/api/Usuario")

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
                for (i in 0 until jsonArray.length()) {
                    val usuario = jsonArray.getJSONObject(i)
                    val correoUsuario = usuario.getString("correo")
                    val claveUsuario = usuario.getString("clave")
                    if (correo == correoUsuario && clave == claveUsuario) {
                        return true
                    }
                }
            } else {
                Log.e("Login", "Error en la respuesta de la API: $responseCode")
            }
            false
        } catch (e: Exception) {
            Log.e("Login", "Error al realizar la solicitud a la API", e)
            false
        }
    }


    private fun showSweetAlertDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        val view: View = LayoutInflater.from(this).inflate(R.layout.custom_alert_dialog, null)

        val dialogText: TextView = view.findViewById(R.id.dialog_text)
        dialogText.text = message

        builder.setView(view)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()

        val positiveButton = view.findViewById<TextView>(R.id.dialog_button)
        positiveButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun registrar() {
        val intent = Intent(this, MainRegistrar::class.java)
        startActivity(intent)
    }
}
