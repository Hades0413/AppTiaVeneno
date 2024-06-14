package com.example.apptiaveneno.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apptiaveneno.R
import org.json.JSONArray
import org.json.JSONObject

class CategoriaMenuPrincipalAdapter(private val context: Context, private val dataSource: JSONArray) :
    RecyclerView.Adapter<CategoriaMenuPrincipalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_item_categoria_menu_principal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoria = dataSource.getJSONObject(position)
        holder.bind(categoria, position)
    }

    override fun getItemCount(): Int {
        return dataSource.length()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val cat_background: LinearLayout = itemView.findViewById(R.id.cat_background)

        fun bind(categoria: JSONObject, position: Int) {
            tvDescripcion.text = categoria.getString("descripcion")

            // Selección dinámica del fondo basado en la posición
            val backgroundResource = when (position % 5) {
                0 -> R.drawable.cat_background
                1 -> R.drawable.cat_background2
                2 -> R.drawable.cat_background3
                3 -> R.drawable.cat_background4
                4 -> R.drawable.cat_background5
                else -> R.drawable.cat_background // Por si acaso
            }
            cat_background.setBackgroundResource(backgroundResource)
        }
    }
}
