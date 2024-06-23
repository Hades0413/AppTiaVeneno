package com.example.apptiaveneno.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apptiaveneno.R
import org.json.JSONArray
import org.json.JSONObject
class ProductoPorCategoriaAdapter(
    private val context: Context,
    private var dataSource: JSONArray
) : RecyclerView.Adapter<ProductoPorCategoriaAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_item_producto_menu_principal_por_categoria, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = dataSource.getJSONObject(position)
        holder.bind(producto)
    }

    override fun getItemCount(): Int {
        return dataSource.length()
    }

    fun updateData(newDataSource: JSONArray) {
        dataSource = newDataSource
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val idDataProductoIdCategoria: TextView = itemView.findViewById(R.id.idDataProductoIdCategoria)
        private val idDataProductoDescripcion: TextView = itemView.findViewById(R.id.idDataProductoDescripcion)
        private val idDataProductoId: TextView = itemView.findViewById(R.id.idDataProductoId)
        private val idDataProductoCodigo: TextView = itemView.findViewById(R.id.idDataProductoCodigo)
        private val idDataProductoStock: TextView = itemView.findViewById(R.id.idDataProductoStock)
        private val idDataProductoPrecioCompra: TextView = itemView.findViewById(R.id.idDataProductoPrecioCompra)
        private val idDataProductoPrecioVenta: TextView = itemView.findViewById(R.id.idDataProductoPrecioVenta)
        private val idDataProductoRutaImagen: ImageView = itemView.findViewById(R.id.idDataProductoRutaImagen)

        fun bind(producto: JSONObject) {
            val oCategoria = producto.getJSONObject("oCategoria")
            idDataProductoIdCategoria.text = oCategoria.getString("descripcion")
            idDataProductoDescripcion.text = producto.getString("descripcion")
            idDataProductoId.text = producto.getString("idProducto")
            idDataProductoCodigo.text = producto.getString("codigo")
            idDataProductoStock.text = producto.getString("stock")
            idDataProductoPrecioCompra.text = producto.getString("precioCompra")
            idDataProductoPrecioVenta.text = producto.getString("precioVenta")

            val rutaImagen = producto.optString("rutaImagen", "")
            if (rutaImagen.isNotEmpty()) {
                val resourceId = context.resources.getIdentifier(rutaImagen, "drawable", context.packageName)
                if (resourceId != 0) {
                    idDataProductoRutaImagen.setImageResource(resourceId)
                } else {
                    idDataProductoRutaImagen.setImageResource(R.drawable.intro_alitas) // Imagen predeterminada si no se encuentra la imagen
                }
            } else {
                idDataProductoRutaImagen.setImageResource(R.drawable.intro_alitas) // Imagen predeterminada si no hay ruta de imagen
            }
        }
    }
}