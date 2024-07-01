package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apptiaveneno.Adapter.ColaboradorAdapter
import com.example.apptiaveneno.Entity.Colaborador
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class MainColaborador : AppCompatActivity() {
    private val TAG = "MainColaborador"
    private lateinit var listView: ListView
    private lateinit var adapter: ColaboradorAdapter
    private val colaboradoresList = mutableListOf<Colaborador>()
    private lateinit var database: DatabaseReference


    //bottom_app_bar
    private lateinit var homeBtn: LinearLayout
    private lateinit var comidasBtn: LinearLayout
    private lateinit var categoriasBtn: LinearLayout
    private lateinit var ventasBtn: LinearLayout
    private lateinit var modoBtn: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_colaboradores)

        // Configurar márgenes según los sistemas de barras
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar la lista y el adaptador
        listView = findViewById(R.id.listarColaboradores)
        adapter = ColaboradorAdapter(this, colaboradoresList)
        listView.adapter = adapter

        // Verificar e inicializar Firebase si no está inicializado
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }

        // Conectar a Firebase y obtener datos de colaboradores
        conectarFirebase()

        // guardar datos
        guardarDatos()

        //bottom_app_bar
        homeBtn = findViewById(R.id.homeBtn)
        comidasBtn = findViewById(R.id.comidasBtn)
        categoriasBtn = findViewById(R.id.categoriasBtn)
        ventasBtn = findViewById(R.id.ventasBtn)
        modoBtn = findViewById(R.id.modoBtn)
        homeBtn.setOnClickListener { home() }
        categoriasBtn.setOnClickListener { vercategoria() }
        comidasBtn.setOnClickListener { verproducto() }
        ventasBtn.setOnClickListener { verventa() }
        modoBtn.setOnClickListener { vermodo() }
    }


    //bottom_app_bar
    private fun home() {
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
    }
    private fun verproducto() {
        val intent = Intent(this, MainProducto::class.java)
        startActivity(intent)
    }
    private fun verventa() {
        val intent = Intent(this, MainVenta::class.java)
        startActivity(intent)
    }
    private fun vercategoria() {
        val intent = Intent(this, MainCategoria::class.java)
        startActivity(intent)
    }
    private fun vermodo() {
        val intent = Intent(this, MainColaborador::class.java)
        startActivity(intent)
    }

    private fun conectarFirebase() {
        try {
            // Referencia a la base de datos de Firebase
            database = FirebaseDatabase.getInstance().reference.child("colaboradores")

            // obtener datos de colaboradores
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        colaboradoresList.clear() // Limpiar la lista actual
                        // Iterar sobre los datos obtenidos de Firebase
                        for (data in snapshot.children) {
                            val colaborador = data.getValue(Colaborador::class.java)
                            colaborador?.let {
                                colaboradoresList.add(it) // Agregar colaborador a la lista
                            }
                        }
                        adapter.notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
                        Log.d(TAG, "Colaboradores cargados desde Firebase")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al procesar datos de colaboradores: ${e.message}")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error al cargar colaboradores: ${error.message}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error inicializando Firebase: ${e.message}")
        }
    }

    private fun guardarDatos() {
        try {
            // Crear lista de colaboradores
            val colaboradores = listOf(
                Colaborador("OLANO FARFAN", "I202211470", "JORGE FABRIZIO", "olano"),
                Colaborador("SIFUENTES ZEVALLOS", "I202212046", "CLAUDIA YADIRA", "sifuentes"),
                Colaborador("JAIME GOMERO", "I202214869", "EDUARDO MIGUEL", "hades"),
                Colaborador("PEREZ SINCHITULLO", "I202021282", "NICOLAS CHRISTOPPER", "perez")
            )

            // Verificar si cada colaborador ya existe antes de agregarlo
            for (colaborador in colaboradores) {
                val query = database.orderByChild("codigoEstudiantil").equalTo(colaborador.codigoEstudiantil)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            // Si no existe, agregar el colaborador
                            val colaboradorRef = database.push()
                            colaboradorRef.setValue(colaborador)
                                .addOnSuccessListener {
                                    Log.d(TAG, "Datos guardados correctamente para ${colaborador.nombre}")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error al guardar datos: ${e.message}")
                                }
                        } else {
                            Log.d(TAG, "Colaborador con codigoEstudiantil ${colaborador.codigoEstudiantil} ya existe.")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "Error al verificar datos: ${error.message}")
                    }
                })
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar datos en Firebase: ${e.message}")
        }
    }
}
