package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainRegistrar : AppCompatActivity(), View.OnClickListener {

    private lateinit var edtNombreCompleto: EditText
    private lateinit var edtCorreo: EditText
    private lateinit var edtContrasenia: EditText
    private lateinit var btnRegistrarUsuario: ConstraintLayout
    private lateinit var idYaTengoCuenta: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)

        edtNombreCompleto = findViewById(R.id.idRegistrarNombreCompleto)
        edtCorreo = findViewById(R.id.idRegistrarCorreo)
        edtContrasenia = findViewById(R.id.idRegistrarContrasenia)
        btnRegistrarUsuario = findViewById(R.id.btnRegistrarUsuario)
        idYaTengoCuenta = findViewById(R.id.idYaTengoCuenta)

        btnRegistrarUsuario.setOnClickListener(this)
        idYaTengoCuenta.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v) {
            btnRegistrarUsuario -> {
                val NombreCompleto = edtNombreCompleto.text.toString()
                val correo = edtCorreo.text.toString()
                val clave = edtContrasenia.text.toString()

                if (NombreCompleto.isNotEmpty() && correo.isNotEmpty() && clave.isNotEmpty()) {
                    registrarUsuarioAPI(NombreCompleto, correo, clave)
                } else {
                    Toast.makeText(applicationContext, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            idYaTengoCuenta -> {
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }
        }
    }

    private fun registrarUsuarioAPI(NombreCompleto: String, correo: String, clave: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://gamarraplus.somee.com/api/Usuario")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val jsonObject = JSONObject().apply {
                    put("NombreCompleto", NombreCompleto)
                    put("correo", correo)
                    put("clave", clave)
                }

                conn.outputStream.use { os ->
                    val input = jsonObject.toString().toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Registro exitoso
                    launch(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Se registró correctamente", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainRegistrar, Login::class.java)
                        startActivity(intent)
                    }
                } else {
                    // Error en el registro
                    launch(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "No se pudo registrar", Toast.LENGTH_SHORT).show()
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
}
