package com.example.apptiaveneno.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apptiaveneno.R
import org.json.JSONArray
import org.json.JSONObject
class CategoriaMenuPrincipalAdapter(
    private val context: Context,
    private val dataSource: JSONArray,
    private val onCategoriaClickListener: (JSONObject) -> Unit
) : RecyclerView.Adapter<CategoriaMenuPrincipalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.rv_item_categoria_menu_principal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoria = dataSource.getJSONObject(position)
        holder.bind(categoria)
    }

    override fun getItemCount(): Int {
        return dataSource.length()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val cat_background: LinearLayout = itemView.findViewById(R.id.cat_background)
        private val categoriaImagen: ImageView = itemView.findViewById(R.id.categoriaImagen)

        fun bind(categoria: JSONObject) {
            tvDescripcion.text = categoria.optString("descripcion", "No Description")

            // Selección dinámica del fondo basado en la posición (ejemplo)
            val backgroundResource = when (adapterPosition % 5) {
                0 -> R.drawable.cat_background
                1 -> R.drawable.cat_background2
                2 -> R.drawable.cat_background3
                3 -> R.drawable.cat_background4
                4 -> R.drawable.cat_background5
                else -> R.drawable.cat_background // Por si acaso
            }
            cat_background.setBackgroundResource(backgroundResource)

            // Obtener la descripción de la categoría y formatearla para la imagen
            val descripcion = categoria.optString("descripcion", "").lowercase().replace(" ", "")

            // Asignar imagen según la descripción
            val imageResourceId = when (descripcion) {
                "marina" -> R.drawable.marina
                "criolla" -> R.drawable.criolla
                "chifa" -> R.drawable.chifa
                "andina" -> R.drawable.andina
                "amazonica" -> R.drawable.amazonica
                "nikkei" -> R.drawable.nikkei
                "vegetariana" -> R.drawable.vegetariana
                "fusión" -> R.drawable.fusion
                "pastelería" -> R.drawable.pasteleria
                "sandwichería" -> R.drawable.sandwicheria
                "parrilla" -> R.drawable.parrilla
                "cevichería" -> R.drawable.cevicheria
                "pollería" -> R.drawable.polleria
                "cafetería" -> R.drawable.cafeteria
                "juguería" -> R.drawable.jugueria
                "picantería" -> R.drawable.picanteria
                "rosticería" -> R.drawable.rosticeria
                "anticuchería" -> R.drawable.anticucheria
                "cocinanovoandina" -> R.drawable.cocina_novoandina
                "comidarapida" -> R.drawable.comida_rapida
                else -> R.drawable.comida_rapida
            }

            categoriaImagen.setImageResource(imageResourceId)

            // Manejar clic en el elemento del RecyclerView
            itemView.setOnClickListener {
                onCategoriaClickListener(categoria)
            }
        }
    }
}