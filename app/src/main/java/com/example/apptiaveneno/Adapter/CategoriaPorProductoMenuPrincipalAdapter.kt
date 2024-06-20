package com.example.apptiaveneno.Adapter

import android.content.Context
import android.graphics.Paint
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.apptiaveneno.R
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import kotlin.random.Random

class CategoriaPorProductoMenuPrincipalAdapter(
    private val context: Context,
    private val dataSource: JSONArray
) : RecyclerView.Adapter<CategoriaPorProductoMenuPrincipalAdapter.ViewHolder>() {


    // Definir los patrones de calificación
    private val pattern1 = intArrayOf(5, 3, 5, 4, 3)  // Primera orden
    private val pattern2 = intArrayOf(4, 5, 3, 5, 4)  // Segunda orden


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_item_producto_menu_principal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = dataSource.getJSONObject(position)

        // Elegir el patrón según la posición
        val pattern = if (position % 2 == 0) pattern1 else pattern2

        // Obtener la calificación para este elemento según el patrón
        val randomIndex = Random.nextInt(pattern.size)
        val rating = pattern[randomIndex].toFloat()

        holder.bind(producto, rating)
    }

    override fun getItemCount(): Int {
        return dataSource.length()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val idDataProductoIdCategoria: TextView = itemView.findViewById(R.id.idDataProductoIdCategoria)
        private val idDataProductoDescripcion: TextView = itemView.findViewById(R.id.idDataProductoDescripcion)
        private val idDataProductoPrecioVenta: TextView = itemView.findViewById(R.id.idDataProductoPrecioVenta)
        private val idDataProductoPrecioVentaDescuento: TextView = itemView.findViewById(R.id.idDataProductoPrecioVentaDescuento)
        private val idDataProductoRutaImagen: ImageView = itemView.findViewById(R.id.idDataProductoRutaImagen)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBarComidas)

        fun bind(producto: JSONObject, rating: Float) {
            idDataProductoIdCategoria.text = producto.getJSONObject("oCategoria").getString("descripcion")
            idDataProductoDescripcion.text = producto.getString("descripcion")
            idDataProductoPrecioVenta.text = formatPrice(producto.getDouble("precioVenta"))

            val precioCompra = producto.getDouble("precioCompra")
            val porcentajeAumento = 1.25
            val precioConDescuento = precioCompra * (1 + porcentajeAumento)


            // Formatear el texto con símbolo y precio tachado
            val formattedText = formatPriceWithStrikeThrough(precioConDescuento)

            // Mostrar el texto formateado en idDataProductoPrecioVentaDescuento
            idDataProductoPrecioVentaDescuento.text = formattedText

            // Aplicar efecto de tachado al precio con descuento
            idDataProductoPrecioVentaDescuento.paintFlags = idDataProductoPrecioVentaDescuento.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG



            val rutaImagen = producto.optString("rutaImagen", "")
            if (rutaImagen.isNotEmpty()) {
                val resourceId = context.resources.getIdentifier(rutaImagen, "drawable", context.packageName)
                if (resourceId != 0) {
                    idDataProductoRutaImagen.setImageResource(resourceId)
                } else {
                    idDataProductoRutaImagen.setImageResource(R.drawable.intro_alitas)
                }
            } else {
                idDataProductoRutaImagen.setImageResource(R.drawable.intro_alitas)
            }// Determinar el marginStart del RatingBar según la longitud de la descripción de la categoría
            val categoriaDescripcion = producto.getJSONObject("oCategoria").getString("descripcion")
            val params = ratingBar.layoutParams as ViewGroup.MarginLayoutParams
            params.marginStart = if (categoriaDescripcion.length > 8) {
                context.resources.getDimensionPixelSize(R.dimen.rating_bar_margin_start_large)  // Dimensión para categorías largas
            } else {
                context.resources.getDimensionPixelSize(R.dimen.rating_bar_margin_start_small)  // Dimensión para categorías cortas
            }
            ratingBar.layoutParams = params

            // Configurar el RatingBar con la calificación obtenida
            ratingBar.rating = rating
        }

        private fun formatPrice(price: Double): String {
            val df = DecimalFormat("#.00")
            return "S/. ${df.format(price)}"
        }

        private fun formatPriceWithStrikeThrough(price: Double): SpannableString {
            val formattedPrice = formatPrice(price)
            val spannableString = SpannableString(formattedPrice)

            // Aplicar estilo al texto
            spannableString.setSpan(StrikethroughSpan(), 0, formattedPrice.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            return spannableString
        }

    }
}
