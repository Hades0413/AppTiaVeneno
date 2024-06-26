package com.example.apptiaveneno.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.apptiaveneno.Entity.Colaborador
import com.example.apptiaveneno.R

class ColaboradorAdapter(private val context: Context, private val dataSource: List<Colaborador>) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = convertView ?: inflater.inflate(R.layout.activity_data_colaboradores, parent, false)

        val colaborador = getItem(position) as Colaborador

        val nombreTextView = rowView.findViewById<TextView>(R.id.idNombreColaboradores)
        val apellidoTextView = rowView.findViewById<TextView>(R.id.idApellidoColaboradores)
        val codigoTextView = rowView.findViewById<TextView>(R.id.idcodigoestudiantilColaboradores)
        val imagenImageView = rowView.findViewById<ImageView>(R.id.idrutaimagenColaboradores)

        nombreTextView.text = colaborador.nombre
        apellidoTextView.text = colaborador.apellido
        codigoTextView.text = colaborador.codigoEstudiantil

        val rutaImagen = colaborador.rutaImagen
        if (rutaImagen.isNotEmpty()) {
            val resourceId = context.resources.getIdentifier(rutaImagen, "drawable", context.packageName)
            if (resourceId != 0) {
                imagenImageView.setImageResource(resourceId)
            } else {
                imagenImageView.setImageResource(R.drawable.discord) // Imagen por defecto si no se encuentra
            }
        } else {
            imagenImageView.setImageResource(R.drawable.discord) // Imagen por defecto si no hay ruta especificada
        }

        return rowView
    }
}
