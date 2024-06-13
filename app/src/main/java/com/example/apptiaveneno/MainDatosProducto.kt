package com.example.apptiaveneno

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainDatosProducto : AppCompatActivity(), View.OnClickListener {

    private lateinit var idCrudIdProducto: TextView
    private lateinit var idCrudCodigoProducto: TextView
    private lateinit var idCrudIdCategoriaProducto: EditText
    private lateinit var idCrudDescripcionProducto: EditText
    private lateinit var idCrudPrecioCompraProducto: EditText
    private lateinit var idCrudPrecioVentaProducto: EditText
    private lateinit var idCrudStockProducto: EditText
    private lateinit var idCrudProductoRutaImagen: ImageView
    private lateinit var btnActualizarProducto: Button
    private lateinit var btnEliminarProducto: Button
    private lateinit var btnCrudVolverProducto: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_datos_producto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        idCrudIdProducto = findViewById(R.id.idCrudIdProducto)
        idCrudCodigoProducto = findViewById(R.id.idCrudCodigoProducto)
        idCrudIdCategoriaProducto = findViewById(R.id.idCrudIdCategoriaProducto)
        idCrudDescripcionProducto = findViewById(R.id.idCrudDescripcionProducto)
        idCrudPrecioCompraProducto = findViewById(R.id.idCrudPrecioCompraProducto)
        idCrudPrecioVentaProducto = findViewById(R.id.idCrudPrecioVentaProducto)
        idCrudStockProducto = findViewById(R.id.idCrudStockProducto)
        idCrudProductoRutaImagen = findViewById(R.id.idCrudProductoRutaImagen)
        btnActualizarProducto = findViewById(R.id.btnActualizarProducto)
        btnEliminarProducto = findViewById(R.id.btnEliminarProducto)
        btnCrudVolverProducto = findViewById(R.id.btnCrudVolverProducto)



        val idProducto = intent.getIntExtra("idProducto", -1)
        val codigo = intent.getStringExtra("codigo")
        val idCategoria = intent.getIntExtra("idCategoria", -1)
        val descripcion = intent.getStringExtra("descripcion")
        val precioCompra = intent.getDoubleExtra("precioCompra", 0.0)
        val precioVenta = intent.getDoubleExtra("precioVenta", 0.0)
        val stock = intent.getIntExtra("stock", 0)
        val rutaImagen = intent.getStringExtra("rutaImagen")

        // Verifica que los datos se recibieron correctamente
        if (idProducto == -1 || codigo.isNullOrEmpty() || idCategoria == -1 || descripcion.isNullOrEmpty()) {
            // Aquí puedes manejar el caso donde los datos no se recibieron correctamente
            Toast.makeText(this, "Error al recibir los datos del producto", Toast.LENGTH_SHORT).show()
            // Puedes finalizar la actividad si los datos son esenciales
            finish()
        }

    // A partir de aquí, puedes utilizar los datos recibidos como idProducto, codigo, idCategoria, etc.

        rutaImagen?.let {
            cargarImagenDesdeUrl(it)
        }

        idCrudIdProducto.setText(idProducto.toString())
        idCrudCodigoProducto.setText(codigo)
        idCrudIdCategoriaProducto.setText(idCategoria.toString())
        idCrudDescripcionProducto.setText(descripcion)
        idCrudPrecioCompraProducto.setText(precioCompra.toString())
        idCrudPrecioVentaProducto.setText(precioVenta.toString())
        idCrudStockProducto.setText(stock.toString())

        btnActualizarProducto.setOnClickListener(this)
        btnEliminarProducto.setOnClickListener(this)
        btnCrudVolverProducto.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnActualizarProducto -> {
                val idProducto = idCrudIdProducto.text.toString().toIntOrNull()
                val codigo = idCrudCodigoProducto.text.toString()
                val idCategoria = idCrudIdCategoriaProducto.text.toString().toIntOrNull()
                val descripcion = idCrudDescripcionProducto.text.toString()
                val precioCompra = idCrudPrecioCompraProducto.text.toString().toDoubleOrNull()
                val precioVenta = idCrudPrecioVentaProducto.text.toString().toDoubleOrNull()
                val stock = idCrudStockProducto.text.toString().toIntOrNull()

                if (idProducto != null && codigo.isNotEmpty() && idCategoria != null && descripcion.isNotEmpty() && precioCompra != null && precioVenta != null && stock != null) {
                    actualizarProducto(idProducto, codigo, idCategoria, descripcion, precioCompra, precioVenta, stock)
                } else {
                    Toast.makeText(applicationContext, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btnEliminarProducto -> {
                val idProducto = idCrudIdProducto.text.toString().toIntOrNull()

                if (idProducto != null) {
                    eliminarProducto(idProducto)
                } else {
                    Toast.makeText(applicationContext, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btnCrudVolverProducto -> {
                volverAProductos()
            }
        }
    }

    private fun cargarImagenDesdeUrl(url: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL(url)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)
                launch(Dispatchers.Main) {
                    idCrudProductoRutaImagen.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                Log.e("Error", "Error al cargar la imagen desde la URL: ${e.message}")
            }
        }
    }


    private fun actualizarProducto(idProducto: Int, codigo: String, idCategoria: Int, descripcion: String, precioCompra: Double, precioVenta: Double, stock: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://tiaveneno.somee.com/api/Inventario/productos/$idProducto")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val jsonObject = JSONObject().apply {
                    put("codigo", codigo)
                    put("idCategoria", idCategoria)
                    put("descripcion", descripcion)
                    put("precioCompra", precioCompra)
                    put("precioVenta", precioVenta)
                    put("stock", stock)
                }

                conn.outputStream.use { os ->
                    val input = jsonObject.toString().toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    launch(Dispatchers.Main) {
                        Log.e("Error de actualización", "No se pudo actualizar el producto. Código de respuesta: $responseCode")
                        Toast.makeText(applicationContext, "No se pudo actualizar el producto", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Log.e("Error de conexión", "Error al conectar con la API: ${e.message}")
                    Toast.makeText(applicationContext, "Error al conectar con la API", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun eliminarProducto(idProducto: Int) {
        val alertDialog = AlertDialog.Builder(this).create()
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_alert_dialog_producto, null)

        dialogView.findViewById<Button>(R.id.buttonCancel).setOnClickListener {
            alertDialog.dismiss() // Cerrar el diálogo si se hace clic en "Cancelar"
        }

        dialogView.findViewById<Button>(R.id.buttonAccept).setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val url = URL("https://tiaveneno.somee.com/api/Inventario/productos/$idProducto")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "DELETE"
                    conn.setRequestProperty("Content-Type", "application/json")

                    val responseCode = conn.responseCode
                    Log.d("EliminarProducto", "Código de respuesta de la API: $responseCode") // Mensaje de registro agregado

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        launch(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        launch(Dispatchers.Main) {
                            Toast.makeText(applicationContext, "No se pudo eliminar el producto", Toast.LENGTH_SHORT).show()
                            Log.e("EliminarProducto", "No se pudo eliminar el producto. Código de respuesta: $responseCode")
                        }
                    }
                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Error al conectar con la API", Toast.LENGTH_SHORT).show()
                        Log.e("EliminarProducto", "Error al conectar con la API: ${e.message}")
                    }
                    e.printStackTrace()
                }
            }
            alertDialog.dismiss() // Cerrar el diálogo después de eliminar el producto
        }

        alertDialog.setView(dialogView)
        alertDialog.setCancelable(false) // Evitar que el diálogo se cierre al tocar fuera de él
        alertDialog.show()
    }

    private fun volverAProductos() {
        val intent = Intent(this, MainProducto::class.java)
        startActivity(intent)
    }
}

