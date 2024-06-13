package com.example.apptiaveneno

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainDatosProducto : AppCompatActivity(), View.OnClickListener {

    private lateinit var idCrudIdProducto: TextView
    private lateinit var idCrudCodigoProducto: TextView
    private lateinit var idCrudIdCategoriaProducto: Spinner
    private lateinit var idCrudDescripcionProducto: EditText
    private lateinit var idCrudPrecioCompraProducto: EditText
    private lateinit var idCrudPrecioVentaProducto: EditText
    private lateinit var idCrudStockProducto: EditText
    private lateinit var idCrudProductoRutaImagen: ImageView
    private lateinit var btnActualizarProducto: Button
    private lateinit var btnEliminarProducto: Button
    private lateinit var btnCrudVolverProducto: Button

    private val PICK_IMAGE_REQUEST = 1

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

        btnActualizarProducto.setOnClickListener(this)
        btnEliminarProducto.setOnClickListener(this)
        btnCrudVolverProducto.setOnClickListener(this)

        // Obtener datos del Intent
        val idProducto = intent.getIntExtra("idProducto", -1)
        val codigo = intent.getStringExtra("codigo")
        val idCategoria = intent.getIntExtra("idCategoria", -1)
        val descripcion = intent.getStringExtra("descripcion")
        val precioCompra = intent.getDoubleExtra("precioCompra", 0.0)
        val precioVenta = intent.getDoubleExtra("precioVenta", 0.0)
        val stock = intent.getIntExtra("stock", 0)
        val rutaImagen = intent.getStringExtra("rutaImagen")

        // Verificar que los datos se recibieron correctamente
        if (idProducto == -1 || codigo.isNullOrEmpty() || idCategoria == -1 || descripcion.isNullOrEmpty()) {
            Toast.makeText(this, "Error al recibir los datos del producto", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Log para verificar los datos recibidos
        Log.d("DatosProducto", "idProducto: $idProducto, codigo: $codigo, idCategoria: $idCategoria, descripcion: $descripcion, precioCompra: $precioCompra, precioVenta: $precioVenta, stock: $stock, rutaImagen: $rutaImagen")

        // Cargar los datos en los componentes de la interfaz
        idCrudIdProducto.text = idProducto.toString()
        idCrudCodigoProducto.text = codigo
        idCrudDescripcionProducto.setText(descripcion)
        idCrudPrecioCompraProducto.setText(precioCompra.toString())
        idCrudPrecioVentaProducto.setText(precioVenta.toString())
        idCrudStockProducto.setText(stock.toString())

        // Cargar la imagen si existe una URL válida
        rutaImagen?.let {
            cargarImagenDesdeRuta(rutaImagen)
        }

        // Cargar las categorías y seleccionar la categoría correspondiente
        cargarCategoriasProducto(idCategoria)
    }

    // Método llamado cuando se hace clic en la imagen
    fun seleccionarImagen(view: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST)
    }

    // Método para manejar el resultado de la selección de imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val uri: Uri? = data.data

            // Aquí puedes cargar la imagen desde la URI seleccionada
            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                idCrudProductoRutaImagen.setImageBitmap(bitmap)
                idCrudProductoRutaImagen.tag = uri.toString()  // Guardar la ruta de la imagen en la tag
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun cargarCategoriasProducto(idCategoriaSeleccionada: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://tiaveneno.somee.com/api/Inventario/categorias")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Content-Type", "application/json")

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    val jsonArray = JSONArray(response.toString())
                    val categoriasList = mutableListOf<String>()
                    val idCategoriasList = mutableListOf<Int>()

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val idCategoria = jsonObject.getInt("idCategoria")
                        val descripcion = jsonObject.getString("descripcion")

                        categoriasList.add(descripcion)
                        idCategoriasList.add(idCategoria)
                    }

                    launch(Dispatchers.Main) {
                        val adapter = ArrayAdapter<String>(this@MainDatosProducto, android.R.layout.simple_spinner_item, categoriasList)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        idCrudIdCategoriaProducto.adapter = adapter

                        val posicion = idCategoriasList.indexOf(idCategoriaSeleccionada)
                        if (posicion != -1) {
                            idCrudIdCategoriaProducto.setSelection(posicion)
                        }
                    }
                } else {
                    launch(Dispatchers.Main) {
                        Toast.makeText(this@MainDatosProducto, "Error al obtener las categorías", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(this@MainDatosProducto, "Error al conectar con la API", Toast.LENGTH_SHORT).show()
                    Log.e("Error", "Error al conectar con la API: ${e.message}")
                }
                e.printStackTrace()
            }
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnActualizarProducto -> {
                val idProducto = idCrudIdProducto.text.toString().toIntOrNull()
                val codigo = idCrudCodigoProducto.text.toString()
                val idCategoria = idCrudIdCategoriaProducto.selectedItemPosition + 1
                val descripcion = idCrudDescripcionProducto.text.toString()
                val precioCompra = idCrudPrecioCompraProducto.text.toString().toDoubleOrNull()
                val precioVenta = idCrudPrecioVentaProducto.text.toString().toDoubleOrNull()
                val stock = idCrudStockProducto.text.toString().toIntOrNull()
                val rutaImagen = idCrudProductoRutaImagen.tag?.toString() ?: ""  // Obtener la ruta de la imagen desde la tag del ImageView

                if (idProducto != null && codigo.isNotEmpty() && idCategoria != null && descripcion.isNotEmpty() && precioCompra != null && precioVenta != null && stock != null) {
                    actualizarProducto(idProducto, codigo, idCategoria, descripcion, precioCompra, precioVenta, stock, rutaImagen)
                } else {
                    Toast.makeText(applicationContext, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                }

            }
            R.id.btnEliminarProducto -> {
                val idProducto = idCrudIdProducto.text.toString().toIntOrNull()

                if (idProducto != null) {
                    eliminarProducto(idProducto)
                } else {
                    Toast.makeText(applicationContext, "Error al obtener el ID del producto", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btnCrudVolverProducto -> {
                volverAProductos()
            }
        }
    }

    private fun cargarImagenDesdeRuta(rutaImagen: String) {
        val resourceId = resources.getIdentifier(rutaImagen, "drawable", packageName)
        if (resourceId != 0) {
            idCrudProductoRutaImagen.setImageResource(resourceId)
        } else {
            idCrudProductoRutaImagen.setImageResource(R.drawable.discord) // Imagen predeterminada si no se encuentra la imagen
        }
    }

    private fun actualizarProducto(idProducto: Int, codigo: String, idCategoria: Int, descripcion: String, precioCompra: Double, precioVenta: Double, stock: Int, rutaImagen: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://tiaveneno.somee.com/api/Inventario/productos/$idProducto")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val productoJson = JSONObject().apply {
                    put("idProducto", idProducto)
                    put("codigo", codigo)
                    put("oCategoria", JSONObject().apply {
                        put("idCategoria", idCategoria)
                        put("descripcion", descripcion)
                    })
                    put("descripcion", descripcion)
                    put("precioCompra", precioCompra)
                    put("precioVenta", precioVenta)
                    put("stock", stock)
                    put("rutaImagen", rutaImagen)
                }

                conn.outputStream.use { os ->
                    val input = productoJson.toString().toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                val responseCode = conn.responseCode
                val responseMessage = conn.responseMessage

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorStream = conn.errorStream
                    val errorMessage = errorStream?.bufferedReader()?.use { it.readText() } ?: "Sin mensaje de error"
                    Log.e("Error de actualización", "No se pudo actualizar el producto. Código de respuesta: $responseCode. Mensaje de error: $errorMessage")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "No se pudo actualizar el producto: $responseMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
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
