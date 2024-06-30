package com.example.apptiaveneno

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainPerfilUsuario : AppCompatActivity() {

    private lateinit var textViewNombre: TextView
    private lateinit var textViewCorreo: TextView
    private lateinit var textViewId: TextView
    private lateinit var btnVolver: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)

        // Inicializar las vistas
        textViewNombre = findViewById(R.id.txtNombreCompleto)
        textViewCorreo = findViewById(R.id.txtCorreo)
        textViewId = findViewById(R.id.txtIdUsuario)
        btnVolver = findViewById(R.id.btnVolver)
        btnVolver.setOnClickListener { volver() }

        // Cargar datos del usuario
        cargarDatosUsuario()
    }

    private fun cargarDatosUsuario() {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val idUsuario = sharedPreferences.getInt("idUsuario", -1)
        val nombreCompleto = sharedPreferences.getString("nombreCompleto", "N/A")
        val correo = sharedPreferences.getString("correo", "N/A")

        if (idUsuario != -1) {
            textViewNombre.text = nombreCompleto
            textViewCorreo.text = correo
            textViewId.text = idUsuario.toString()
        } else {
            // Opcional: Manejar el caso cuando no hay datos disponibles
            textViewNombre.text = "No disponible"
            textViewCorreo.text = "No disponible"
            textViewId.text = "No disponible"
        }
    }

    private fun volver() {
        // Volver al MenuPrincipal
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
        finish() // Finalizar la actividad actual
    }
    private fun cerrarSesion() {
        // Limpiar los datos del usuario en SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            clear() // Limpia todos los datos guardados
            apply()
        }

        // Redirigir al Login o pantalla de inicio
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish() // Finalizar la actividad actual
    }


}
