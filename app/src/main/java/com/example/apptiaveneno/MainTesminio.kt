package com.example.apptiaveneno

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apptiaveneno.Adapter.TestimonioAdapter
import com.example.apptiaveneno.Data.InitBD
import com.example.apptiaveneno.Entity.Testimonio
import com.example.apptiaveneno.utils.appConfig

class MainTestimonio : AppCompatActivity() {

    private lateinit var listView: RecyclerView
    private lateinit var testimonioAdapter: TestimonioAdapter
    private var testimonios: MutableList<Testimonio> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testimonio)

        // Configuración de padding para ventanas con bordes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar RecyclerView y Adapter
        listView = findViewById(R.id.listaTestimonios)
        testimonioAdapter = TestimonioAdapter(this, testimonios)
        listView.adapter = testimonioAdapter

        // Establecer el LayoutManager para el RecyclerView
        listView.layoutManager = LinearLayoutManager(this)

        // Obtener la base de datos de appConfig y cargar testimonios
        val db = appConfig.BD
        cargarTestimonios(db)
    }

    private fun cargarTestimonios(db: InitBD) {
        // Ejecutar consulta SQL para obtener testimonios
        val cursor = db.readableDatabase.rawQuery("SELECT cod, nomusu, testimonio, fotousu FROM tb_testimonios", null)

        // Limpiar lista de testimonios antes de agregar nuevos
        testimonios.clear()

        // Procesar resultados de la consulta y actualizar la lista
        if (cursor.moveToFirst()) {
            do {
                val idcodigotestimonio = cursor.getInt(cursor.getColumnIndexOrThrow("cod"))
                val nomusu = cursor.getString(cursor.getColumnIndexOrThrow("nomusu"))
                val testimonio = cursor.getString(cursor.getColumnIndexOrThrow("testimonio"))
                val fotousu = cursor.getString(cursor.getColumnIndexOrThrow("fotousu"))
                testimonios.add(Testimonio(idcodigotestimonio, nomusu, testimonio, fotousu))
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Notificar al adaptador que los datos han cambiado
        testimonioAdapter.notifyDataSetChanged()
    }
}
