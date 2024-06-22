package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebView.FindListener
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringReader
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class MainVenta : AppCompatActivity() {

    private lateinit var edtConsultarNroDocumento: EditText
    private lateinit var btnConsultarDetalle: Button
    private lateinit var txtTipoPagoDetalle: TextView
    private lateinit var txtNroDocumentoDetalle: TextView
    private lateinit var txtDocClienteDetalle: TextView
    private lateinit var txtNombreClienteDetalle: TextView
    private lateinit var txtMontoPagoConDetalle: TextView
    private lateinit var txtMontoCambioDetalle: TextView
    private lateinit var txtMontoSubTotalDetalle: TextView
    private lateinit var txtMontoIGVDetalle: TextView
    private lateinit var txtMontoTotalDetalle: TextView
    private lateinit var txtFechaRegistroDetalle: TextView
    private lateinit var txtProductoDetalle: TextView
    private lateinit var txtCantidadDetalle: TextView
    private lateinit var txtPrecioProductoDetalle: TextView
    private lateinit var txtPrecioVentaDetalle: TextView
    private lateinit var btnNuevaVentaDetalle : Button

    //bottom_app_bar
    private lateinit var homeBtn: LinearLayout
    private lateinit var comidasBtn: LinearLayout
    private lateinit var categoriasBtn: LinearLayout
    private lateinit var ventasBtn: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detallesventa)

        edtConsultarNroDocumento = findViewById(R.id.edtConsultarNroDocumento)
        btnConsultarDetalle = findViewById(R.id.btnConsultarDetalle)
        txtTipoPagoDetalle = findViewById(R.id.txtTipoPagoDetalle)
        txtNroDocumentoDetalle = findViewById(R.id.txtNroDocumentoDetalle)
        txtDocClienteDetalle = findViewById(R.id.txtDocClienteDetalle)
        txtNombreClienteDetalle = findViewById(R.id.txtNombreClienteDetalle)
        txtMontoPagoConDetalle = findViewById(R.id.txtMontoPagoConDetalle)
        txtMontoCambioDetalle = findViewById(R.id.txtMontoCambioDetalle)
        txtMontoSubTotalDetalle = findViewById(R.id.txtMontoSubTotalDetalle)
        txtMontoIGVDetalle = findViewById(R.id.txtMontoIGVDetalle)
        txtMontoTotalDetalle = findViewById(R.id.txtMontoTotalDetalle)
        txtFechaRegistroDetalle = findViewById(R.id.txtFechaRegistroDetalle)
        txtProductoDetalle = findViewById(R.id.txtProductoDetalle)
        txtCantidadDetalle = findViewById(R.id.txtCantidadDetalle)
        txtPrecioProductoDetalle = findViewById(R.id.txtPrecioProductoDetalle)
        txtPrecioVentaDetalle = findViewById(R.id.txtPrecioVentaDetalle)
        btnNuevaVentaDetalle = findViewById(R.id.btnNuevaVentaDetalle)

        btnConsultarDetalle.setOnClickListener { consultarDetalle() }
        btnNuevaVentaDetalle.setOnClickListener { NuevaVenta() }

        //bottom_app_bar
        homeBtn = findViewById(R.id.homeBtn)
        comidasBtn = findViewById(R.id.comidasBtn)
        categoriasBtn = findViewById(R.id.categoriasBtn)
        ventasBtn = findViewById(R.id.ventasBtn)
        homeBtn.setOnClickListener { home() }
        categoriasBtn.setOnClickListener { vercategoria() }
        comidasBtn.setOnClickListener { verproducto() }
        ventasBtn.setOnClickListener { verventa() }

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

    private fun consultarDetalle() {
        val nroDocumento = edtConsultarNroDocumento.text.toString().trim()

        if (nroDocumento.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val url = URL("https://www.tiaveneno.somee.com/api/Venta/VerDetalleVenta/$nroDocumento")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.setRequestProperty("Content-Type", "application/xml")

                    val responseCode = conn.responseCode
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val reader = BufferedReader(InputStreamReader(conn.inputStream))
                        val response = StringBuilder()
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }
                        reader.close()

                        Log.d("MainVenta", "Response: $response")

                        val venta = parseXML(response.toString())

                        withContext(Dispatchers.Main) {
                            txtTipoPagoDetalle.text = venta["TipoPago"]
                            txtNroDocumentoDetalle.text = venta["NumeroDocumento"]
                            txtDocClienteDetalle.text = venta["DocumentoCliente"]
                            txtNombreClienteDetalle.text = venta["NombreCliente"]
                            txtMontoPagoConDetalle.text = venta["MontoPagoCon"]
                            txtMontoCambioDetalle.text = venta["MontoCambio"]
                            txtMontoSubTotalDetalle.text = venta["MontoSubTotal"]
                            txtMontoIGVDetalle.text = venta["MontoIGV"]
                            txtMontoTotalDetalle.text = venta["MontoTotal"]
                            txtFechaRegistroDetalle.text = venta["FechaRegistro"]

                            // Ajuste para los detalles de venta
                            txtProductoDetalle.text = venta["Descripcion"]
                            txtCantidadDetalle.text = venta["Cantidad"]
                            txtPrecioProductoDetalle.text = venta["PrecioVenta"]
                            txtPrecioVentaDetalle.text = venta["Total"]
                        }
                    } else {
                        showError("Error en la conexión: $responseCode")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("MainVenta", "Error al consultar los detalles de la venta: ${e.message}")
                    showError("Error al consultar los detalles de la venta")
                }
            }
        } else {
            mostrarAlertaCategoriaVacia()
        }
    }

    private fun parseXML(xml: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document: Document = builder.parse(InputSource(StringReader(xml)))
            document.documentElement.normalize()

            val elements = document.documentElement.childNodes
            for (i in 0 until elements.length) {
                val node = elements.item(i)
                if (node is Element) {
                    if (node.tagName == "DetalleVenta") {
                        val items = node.getElementsByTagName("Item")
                        if (items.length > 0) {
                            val item = items.item(0) as Element
                            map["Descripcion"] = item.getElementsByTagName("Descripcion").item(0).textContent
                            map["Cantidad"] = item.getElementsByTagName("Cantidad").item(0).textContent
                            map["PrecioVenta"] = item.getElementsByTagName("PrecioVenta").item(0).textContent
                            map["Total"] = item.getElementsByTagName("Total").item(0).textContent
                        }
                    } else {
                        map[node.tagName] = node.textContent
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return map
    }

    private fun mostrarAlertaCategoriaVacia() {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage("No has ingresado un número de documento.")
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private suspend fun showError(message: String) {
        withContext(Dispatchers.Main) {
            AlertDialog.Builder(this@MainVenta)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("Aceptar", null)
                .show()
        }
    }


    fun NuevaVenta(){
        val intent = Intent(this, MainNuevaVenta :: class.java)
        startActivity(intent)
    }
}
