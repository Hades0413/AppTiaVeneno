package com.example.apptiaveneno.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.apptiaveneno.R
import org.json.JSONArray
import org.json.JSONObject

class CategoriaAdapter(private val context: Context, private val dataSource: JSONArray) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.length()
    }

    override fun getItem(position: Int): JSONObject {
        return dataSource.getJSONObject(position)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).getLong("idCategoria")
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: inflater.inflate(R.layout.activity_data_categoria, parent, false)

        val idDataCategoriaCodigo = view.findViewById<TextView>(R.id.idDataCategoriaCodigo)
        val tvDescripcion = view.findViewById<TextView>(R.id.tvDescripcion)

        val categoria = getItem(position)
        idDataCategoriaCodigo.text = categoria.getString("idCategoria")
        tvDescripcion.text = categoria.getString("descripcion")

        return view
    }

}
