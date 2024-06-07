package com.example.apptiaveneno.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.apptiaveneno.R
import org.json.JSONArray
import org.json.JSONObject

class ProductoAdapter(private val context: Context, private val dataSource: JSONArray) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.length()
    }

    override fun getItem(position: Int): JSONObject {
        return dataSource.getJSONObject(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: inflater.inflate(R.layout.activity_data_producto, parent, false)

        val idDataProductoId = view.findViewById<TextView>(R.id.idDataProductoId)
        val idDataProductoCodigo = view.findViewById<TextView>(R.id.idDataProductoCodigo)
        val idDataProductoIdCategoria = view.findViewById<TextView>(R.id.idDataProductoIdCategoria)
        val idDataProductoDescripcion = view.findViewById<TextView>(R.id.idDataProductoDescripcion)
        val idDataProductoPrecioCompra = view.findViewById<TextView>(R.id.idDataProductoPrecioCompra)
        val idDataProductoPrecioVenta = view.findViewById<TextView>(R.id.idDataProductoPrecioVenta)
        val idDataProductoStock = view.findViewById<TextView>(R.id.idDataProductoStock)
        val idDataProductoRutaImagen = view.findViewById<ImageView>(R.id.idDataProductoRutaImagen)

        val producto = getItem(position)
        idDataProductoId.text = producto.getString("idProducto")
        idDataProductoCodigo.text = producto.getString("codigo")
        idDataProductoDescripcion.text = producto.getString("descripcion")
        idDataProductoPrecioCompra.text = producto.getDouble("precioCompra").toString()
        idDataProductoPrecioVenta.text = producto.getDouble("precioVenta").toString()
        idDataProductoStock.text = producto.getInt("stock").toString()

        val oCategoria = producto.getJSONObject("oCategoria")
        val descripcionCategoria = oCategoria.getString("descripcion")

        // Asigna solo la descripción de la categoría
        idDataProductoIdCategoria.text = descripcionCategoria

        val rutaImagen = producto.optString("rutaImagen", "")

        if (rutaImagen.isNotEmpty()) {
            val resourceId = context.resources.getIdentifier(rutaImagen, "drawable", context.packageName)
            if (resourceId != 0) {
                idDataProductoRutaImagen.setImageResource(resourceId)
            } else {
                idDataProductoRutaImagen.setImageResource(R.drawable.discord)
            }
        } else {
            idDataProductoRutaImagen.setImageResource(R.drawable.discord)
        }

        return view
    }
}
