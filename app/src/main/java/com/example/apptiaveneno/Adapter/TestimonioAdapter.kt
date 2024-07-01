package com.example.apptiaveneno.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apptiaveneno.Entity.Testimonio
import com.example.apptiaveneno.R

class TestimonioAdapter(
    private val context: Context,
    private val testimonios: List<Testimonio>
) : RecyclerView.Adapter<TestimonioAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idcodigotestimonio: TextView = itemView.findViewById(R.id.idcodigotestimonio_value)
        val nomusu: TextView = itemView.findViewById(R.id.nomusu)
        val testimonioText: TextView = itemView.findViewById(R.id.testimonio)
        val fotousu: ImageView = itemView.findViewById(R.id.fotousu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.activity_data_testimonio, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val testimonio = testimonios[position]
        holder.idcodigotestimonio.text = testimonio.idcodigotestimonio.toString()
        holder.nomusu.text = testimonio.nomusu
        holder.testimonioText.text = testimonio.testimonio

        // Manejar la carga de imágenes
        val rutaImagen = testimonio.fotousu
        if (rutaImagen.isNotEmpty()) {
            val resourceId = context.resources.getIdentifier(rutaImagen, "drawable", context.packageName)
            if (resourceId != 0) {
                holder.fotousu.setImageResource(resourceId)
            } else {
                holder.fotousu.setImageResource(R.drawable.discord) // Imagen predeterminada si no se encuentra la imagen
            }
        } else {
            holder.fotousu.setImageResource(R.drawable.discord) // Imagen predeterminada si no hay ruta de imagen
        }
    }

    override fun getItemCount(): Int {
        return testimonios.size
    }
}
