package com.example.apptiaveneno

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.apptiaveneno.Entity.Usuario

class MainPerfilUsuario : AppCompatActivity() {

    // Propiedades privadas para los TextViews
    private lateinit var textViewNombre: TextView
    private lateinit var textViewCorreo: TextView
    private lateinit var textViewId: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)

        // Inicializa las propiedades privadas
        textViewNombre = findViewById(R.id.txtNombreCompleto)
        textViewCorreo = findViewById(R.id.txtCorreo)
        textViewId = findViewById(R.id.txtIdUsuario)

        // Obtén el objeto Usuario del Intent
        val usuario = intent.getParcelableExtra<Usuario>("usuario")

        // Muestra los datos del usuario
        usuario?.let {
            textViewNombre.text = it.nombreCompleto
            textViewCorreo.text = it.correo
            textViewId.text = it.idUsuario.toString()
        }
    }
}
