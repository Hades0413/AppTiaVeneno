package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.apptiaveneno.Entity.Usuario
import kotlinx.coroutines.*

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

            // Verifica si se ingresaron datos en los campos de correo y contraseña
            if (correo.isEmpty() || clave.isEmpty()) {
                // Muestra un mensaje de error si no se ingresaron datos en los campos
                Toast.makeText(
                    applicationContext,
                    "Debes de ingresar datos, no seas imbécil :v",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                // Intenta iniciar sesión si se ingresaron datos en los campos
                login(correo, clave)
            }
        }

    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun login(correo: String, clave: String) {
        GlobalScope.launch(Dispatchers.IO) {
            // Intenta realizar el inicio de sesión con la API
            val successWithAPI = try {
                performLoginWithAPI(correo, clave)
            } catch (e: Exception) {
                // Si hay un error de conexión con la API, registra el error y muestra un mensaje
                Log.e("MainActivity", "Error en la conexión con la API: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        applicationContext,
                        "Error de conexión con la API",
                        Toast.LENGTH_LONG
                    ).show()
                }
                false
            }

            // Si el inicio de sesión con la API fue exitoso, inicia la actividad HomeActivity
            if (successWithAPI) {
                withContext(Dispatchers.Main) {
                    Log.d("MainActivity", "Inicio de sesión exitoso")
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    finish()
                }
            } else {
                // Si el inicio de sesión con la API falló, muestra un mensaje de error
                withContext(Dispatchers.Main) {
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
    /*
    @OptIn(DelicateCoroutinesApi::class)
private fun login(correo: String, clave: String) {
    GlobalScope.launch(Dispatchers.IO) {
        // Intenta realizar el inicio de sesión con la API
        val successWithAPI = try {
            performLoginWithAPI(correo, clave)
        } catch (e: Exception) {
            // Si hay un error de conexión con la API, registra el error y muestra un mensaje
            Log.e("MainActivity", "Error en la conexión con la API: ${e.message}", e)
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    applicationContext,
                    "Error de conexión con la API",
                    Toast.LENGTH_LONG
                ).show()
            }
            false
        }

        // Si el inicio de sesión con la API falló o si la API no está disponible, intenta con usuarios estáticos
        if (!successWithAPI) {
            val successWithStaticUsers = performLoginWithStaticUsers(correo, clave)
            if (successWithStaticUsers) {
                // Si el inicio de sesión con usuarios estáticos fue exitoso, inicia la actividad HomeActivity
                withContext(Dispatchers.Main) {
                    Log.d("MainActivity", "Inicio de sesión exitoso con usuarios estáticos")
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    finish()
                }
            } else {
                // Si el inicio de sesión con usuarios estáticos falló, muestra un mensaje de error
                withContext(Dispatchers.Main) {
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
}

     */


    private fun performLoginWithAPI(correo: String, clave: String): Boolean {
        return try {
            // Aquí iría la lógica para conectarse a la API y verificar las credenciales
            // En esta implementación simularemos una conexión exitosa
            // Tengamos fe para que no falle :,v
            /*
             val url = URL("https://localhost:7034/api/Usuario")

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
             */
            true
        } catch (e: Exception) {
            // Si hay un error de conexión, registra el error y devuelve falso
            Log.e("MainActivity", "Error de conexión: ${e.message}", e)
            false
        }
    }

    private fun performLoginWithStaticUsers(correo: String, clave: String): Boolean {
        // Lista de usuarios en caso falle la conexión con la API :v
        val usuarios = listOf(
            Usuario(1, "Jorge Fabrizio Olano Farfan 26", "leder@hotmail.com", "asd"),
            Usuario(2, "Claudia Sifuentes", "autista@hotmail.com", "asd"),
            Usuario(3, "Miguel Jaime", "hades@hotmail.com", "asd"),
            Usuario(4, "Nicolas Perez", "nicolas@hotmail.com", "asd")
        )

        // Busca el usuario por correo electrónico
        val usuarioValido = usuarios.find { it.correo == correo }

        // Verifica si se encontró un usuario con el correo proporcionado
        if (usuarioValido != null) {
            // Si se encontró un usuario, verifica si la contraseña coincide
            if (usuarioValido.clave == clave) {
                // Contraseña correcta
                return true
            } else {
                // Contraseña incorrecta
                return false
            }
        } else {
            // No se encontró el correo en la lista de usuarios
            return false
        }
    }


}
