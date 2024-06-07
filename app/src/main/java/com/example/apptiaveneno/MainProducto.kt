package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainProducto : AppCompatActivity() {

    private lateinit var btnRegresarProducto:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_producto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnRegresarProducto=findViewById(R.id.btnRegresarProducto)

        btnRegresarProducto.setOnClickListener{volvermenuprincipal()}
    }

    private fun volvermenuprincipal() {
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
    }
}