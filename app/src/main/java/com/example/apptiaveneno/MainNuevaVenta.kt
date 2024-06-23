package com.example.apptiaveneno

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.math.BigDecimal
import java.math.RoundingMode

class MainNuevaVenta : AppCompatActivity(), View.OnClickListener {

    private lateinit var edtRegistrarVentaTipoPago: AutoCompleteTextView
    private lateinit var edtRegistarVentaDocCliente: EditText
    private lateinit var edtRegistrarVentaNomCliente: EditText
    private lateinit var edtRegistrarVentaMontoPago: EditText
    private lateinit var edtRegistrarVentaMontoCambio: EditText
    private lateinit var edtRegistrarVentaMontoSubTotal: EditText
    private lateinit var edtRegistrarVentaMontoIGV: EditText
    private lateinit var edtRegistrarVentaMontoTotal: EditText
    private lateinit var spnRegistrarVentaProducto: Spinner
    private lateinit var edtRegistrarVentaPrecioVenta: EditText
    private lateinit var edtRegistrarVentaCantidad: EditText
    private lateinit var edtRegistrarVentaTotal: EditText
    private lateinit var btnRegistrarVentaAgregar: Button
    private lateinit var btnRegistrarVentaVolver: Button

    private lateinit var productosJsonArray: JSONArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_venta)

        // Inicializar vistas
        edtRegistrarVentaTipoPago = findViewById(R.id.edtRegistrarVentaTipoPago)
        edtRegistarVentaDocCliente = findViewById(R.id.edtRegistarVentaDocCliente)
        edtRegistrarVentaNomCliente = findViewById(R.id.edtRegistrarVentaNomCliente)
        edtRegistrarVentaMontoPago = findViewById(R.id.edtRegistrarVentaMontoPago)
        edtRegistrarVentaMontoCambio = findViewById(R.id.edtRegistrarVentaMontoCambio)
        edtRegistrarVentaMontoSubTotal = findViewById(R.id.edtRegistrarVentaMontoSubTotal)
        edtRegistrarVentaMontoIGV = findViewById(R.id.edtRegistrarVentaMontoIGV)
        edtRegistrarVentaMontoTotal = findViewById(R.id.edtRegistrarVentaMontoTotal)
        spnRegistrarVentaProducto = findViewById(R.id.spnRegistrarVentaProducto)
        edtRegistrarVentaPrecioVenta = findViewById(R.id.edtRegistrarVentaPrecioVenta)
        edtRegistrarVentaCantidad = findViewById(R.id.edtRegistrarVentaCantidad)
        edtRegistrarVentaTotal = findViewById(R.id.edtRegistrarVentaTotal)
        btnRegistrarVentaAgregar = findViewById(R.id.btnRegistrarVentaAgregar)
        btnRegistrarVentaVolver = findViewById(R.id.btnRegistrarVentaVolver)

        // Configurar eventos de clic
        btnRegistrarVentaAgregar.setOnClickListener(this)
        btnRegistrarVentaVolver.setOnClickListener { volver() }

        // Cargar productos en el Spinner
        cargarProductos(-1)

        // Configurar el evento de selección del Spinner
        spnRegistrarVentaProducto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (::productosJsonArray.isInitialized) {
                    val selectedProducto = productosJsonArray.getJSONObject(position)
                    val precioVenta = selectedProducto.getDouble("precioVenta")
                    edtRegistrarVentaPrecioVenta.setText(precioVenta.toString())
                    calcularTotal()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }

        // Añadir TextWatcher al campo de cantidad
        edtRegistrarVentaCantidad.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calcularTotal()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Añadir TextWatcher al campo de Total para recalcular el monto IGV cuando cambie
        edtRegistrarVentaTotal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calcularIGV()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Añadir TextWatcher al campo de MontoPago para recalcular el cambio cuando cambie
        edtRegistrarVentaMontoPago.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calcularCambio()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun volver() {
        val intent = Intent(this, MenuPrincipal::class.java)
        startActivity(intent)
    }

    override fun onClick(v: View) {
        when (v) {
            btnRegistrarVentaAgregar -> {
                // Obtener datos de los campos de texto
                val tipoPago = edtRegistrarVentaTipoPago.text.toString()
                val documentoCliente = edtRegistarVentaDocCliente.text.toString()
                val nombreCliente = edtRegistrarVentaNomCliente.text.toString()
                val montoPago = edtRegistrarVentaMontoPago.text.toString()
                val montoCambio = edtRegistrarVentaMontoCambio.text.toString()
                val montoSubTotal = edtRegistrarVentaMontoSubTotal.text.toString()
                val montoIGV = edtRegistrarVentaMontoIGV.text.toString()
                val montoTotal = edtRegistrarVentaMontoTotal.text.toString()
                val idProducto = spnRegistrarVentaProducto.selectedItemPosition + 1
                val descripcionProducto = spnRegistrarVentaProducto.selectedItem.toString()
                val precioVenta = edtRegistrarVentaPrecioVenta.text.toString()
                val cantidad = edtRegistrarVentaCantidad.text.toString()
                val total = edtRegistrarVentaTotal.text.toString()

                // Validar datos
                if (tipoPago.isNotEmpty() && documentoCliente.isNotEmpty() && nombreCliente.isNotEmpty() && montoPago.isNotEmpty() && montoCambio.isNotEmpty() && montoSubTotal.isNotEmpty() && montoIGV.isNotEmpty() && montoTotal.isNotEmpty() && descripcionProducto.isNotEmpty()) {
                    agregarVenta(
                        tipoPago,
                        documentoCliente,
                        nombreCliente,
                        montoPago.toDouble(),
                        montoCambio.toDouble(),
                        montoSubTotal.toDouble(),
                        montoIGV.toDouble(),
                        montoTotal.toDouble(),
                        idProducto,
                        descripcionProducto,
                        precioVenta.toDouble(),
                        cantidad.toInt(),
                        total.toDouble()
                    )
                } else {
                    Toast.makeText(applicationContext, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun agregarVenta(
        tipoPago: String,
        documentoCliente: String,
        nombreCliente: String,
        montoPago: Double,
        montoCambio: Double,
        montoSubTotal: Double,
        montoIGV: Double,
        montoTotal: Double,
        idProducto: Int,
        descripcionProducto: String,
        precioVenta: Double,
        cantidad: Int,
        total: Double
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://tiaveneno.somee.com/api/Venta/RegistrarVenta")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                // Crear objeto JSON para la venta
                val ventaObject = JSONObject().apply {
                    put("idVenta", 0)
                    put("tipoPago", tipoPago)
                    put("numeroDocumento", "")
                    put("documentoCliente", documentoCliente)
                    put("nombreCliente", nombreCliente)
                    put("montoPagoCon", montoPago)
                    put("montoCambio", montoCambio)
                    put("montoSubTotal", montoSubTotal)
                    put("montoIGV", montoIGV)
                    put("montoTotal", montoTotal)
                    put("fechaRegistro", "2024-06-20") // Ajustar según el formato de tu API
                    put("oDetalleVenta", JSONArray().apply {
                        put(JSONObject().apply {
                            put("idDetalleVenta", 0)
                            put("oProducto", JSONObject().apply {
                                put("idProducto", idProducto)
                                put("codigo", "")
                                put("oCategoria", JSONObject().apply {
                                    put("idCategoria", 0)
                                    put("descripcion", "")
                                })
                                put("descripcion", descripcionProducto)
                                put("unidadMedida", "")
                                put("precioCompra", 0.0)
                                put("precioVenta", precioVenta)
                                put("stock", 0)
                                put("rutaImagen", "")
                            })
                            put("precioVenta", precioVenta)
                            put("cantidad", cantidad)
                            put("total", total)
                        })
                    })
                }

                // Enviar la solicitud
                conn.outputStream.use { os ->
                    val input = ventaObject.toString().toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Registro exitoso
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Se registró correctamente", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainNuevaVenta, MainCategoria::class.java)
                        startActivity(intent)
                    }
                } else {
                    // Otro estado de respuesta, por ejemplo, error del servidor
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Error en el servidor", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Error al conectar con la API", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun cargarProductos(selectedCategoryId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://tiaveneno.somee.com/api/Inventario/productos")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = conn.inputStream
                    val responseText = inputStream.bufferedReader().use { it.readText() }
                    productosJsonArray = JSONArray(responseText)

                    val productosList = mutableListOf<String>()
                    for (i in 0 until productosJsonArray.length()) {
                        val producto = productosJsonArray.getJSONObject(i)
                        val descripcion = producto.getString("descripcion")
                        productosList.add(descripcion)
                    }

                    withContext(Dispatchers.Main) {
                        val adapter = ArrayAdapter(
                            this@MainNuevaVenta,
                            android.R.layout.simple_spinner_item,
                            productosList
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spnRegistrarVentaProducto.adapter = adapter

                        // Seleccionar la categoría si el ID está especificado
                        if (selectedCategoryId >= 0) {
                            val selectedIndex = productosList.indexOfFirst {
                                it.contains(selectedCategoryId.toString())
                            }
                            if (selectedIndex >= 0) {
                                spnRegistrarVentaProducto.setSelection(selectedIndex)
                            }
                        }
                    }
                } else {
                    Log.e("Error de carga", "No se pudo cargar los productos. Código de respuesta: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("Error de conexión", "Error al conectar con la API: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun calcularTotal() {
        val precioVentaStr = edtRegistrarVentaPrecioVenta.text.toString()
        val cantidadStr = edtRegistrarVentaCantidad.text.toString()

        if (precioVentaStr.isNotEmpty() && cantidadStr.isNotEmpty()) {
            val precioVenta = precioVentaStr.toDoubleOrNull() ?: 0.0
            val cantidad = cantidadStr.toIntOrNull() ?: 0
            val total = precioVenta * cantidad
            edtRegistrarVentaTotal.setText(total.toString())
            edtRegistrarVentaMontoSubTotal.setText(total.toString())
            calcularMontoTotal()
        } else if (cantidadStr.isEmpty()) {
            edtRegistrarVentaTotal.setText("")
            edtRegistrarVentaMontoSubTotal.setText("")
            calcularMontoTotal()
        } else {
            edtRegistrarVentaTotal.setText("0.0")
            edtRegistrarVentaMontoSubTotal.setText("")
            calcularMontoTotal()
        }
    }

    private fun calcularMontoTotal() {
        val montoIGVStr = edtRegistrarVentaMontoIGV.text.toString()
        val totalStr = edtRegistrarVentaTotal.text.toString()

        if (totalStr.isNotEmpty() && montoIGVStr.isNotEmpty()) {
            val montoIGV = montoIGVStr.toDoubleOrNull() ?: 0.0
            val total = totalStr.toDoubleOrNull() ?: 0.0
            val montoTotal = montoIGV + total
            edtRegistrarVentaMontoTotal.setText(montoTotal.toString())
            calcularCambio()
        } else {
            edtRegistrarVentaMontoTotal.setText("")
            calcularCambio()
        }
    }

    private fun calcularCambio() {
        val montoPagoStr = edtRegistrarVentaMontoPago.text.toString()
        val montoTotalStr = edtRegistrarVentaMontoTotal.text.toString()

        if (montoPagoStr.isNotEmpty() && montoTotalStr.isNotEmpty()) {
            val montoPago = montoPagoStr.toDoubleOrNull() ?: 0.0
            val montoTotal = montoTotalStr.toDoubleOrNull() ?: 0.0
            val montoCambio = montoPago - montoTotal
            val montoCambioDecimal = BigDecimal(montoCambio).setScale(1, RoundingMode.HALF_EVEN)
            edtRegistrarVentaMontoCambio.setText(montoCambioDecimal.toString())
        } else {
            edtRegistrarVentaMontoCambio.setText("")
        }
    }

    private fun calcularIGV() {
        val totalStr = edtRegistrarVentaTotal.text.toString()

        if (totalStr.isNotEmpty()) {
            val total = totalStr.toDoubleOrNull() ?: 0.0
            val montoIGV = (total * 0.18).toBigDecimal().setScale(1, RoundingMode.HALF_EVEN)
            edtRegistrarVentaMontoIGV.setText(montoIGV.toString())
            calcularMontoTotal()
        } else {
            edtRegistrarVentaMontoIGV.setText("")
            calcularMontoTotal()
        }
    }
}
