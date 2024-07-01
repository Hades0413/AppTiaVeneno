package com.example.apptiaveneno

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class MainNuevoProducto : AppCompatActivity(), View.OnClickListener {

    private lateinit var idCrearProductoCodigo: EditText
    private lateinit var idCrearProductoIdCategoria: Spinner
    private lateinit var idCrearProductoDescripcion: EditText
    private lateinit var idCrearProductoPrecioCompra: EditText
    private lateinit var idCrearProductoPrecioVenta: EditText
    private lateinit var idCrearProductoStock: EditText
    private lateinit var idCrearProductoRutaImagen: ImageView
    private lateinit var btnGrabarProducto: Button
    private lateinit var btnCrearProductoVolver: Button

    private val PICK_IMAGE_REQUEST = 1
    private val REQUEST_STORAGE_PERMISSION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nuevo_producto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        idCrearProductoCodigo = findViewById(R.id.idCrearProductoCodigo)
        idCrearProductoIdCategoria = findViewById(R.id.idCrearProductoIdCategoria)
        idCrearProductoDescripcion = findViewById(R.id.idCrearProductoDescripcion)
        idCrearProductoPrecioCompra = findViewById(R.id.idCrearProductoPrecioCompra)
        idCrearProductoPrecioVenta = findViewById(R.id.idCrearProductoPrecioVenta)
        idCrearProductoStock = findViewById(R.id.idCrearProductoStock)
        idCrearProductoRutaImagen = findViewById(R.id.idCrearProductoRutaImagen)
        btnGrabarProducto = findViewById(R.id.btnGrabarProducto)
        btnCrearProductoVolver = findViewById(R.id.btnCrearProductoVolver)

        btnGrabarProducto.setOnClickListener(this)
        btnCrearProductoVolver.setOnClickListener(this)

        // Asociar método para seleccionar imagen al ImageView
        idCrearProductoRutaImagen.setOnClickListener {
            seleccionarImagen()
        }

        // Cargar categorías al iniciar la actividad
        cargarCategoriasProducto(-1)
    }

    override fun onClick(v: View) {
        when (v) {
            btnGrabarProducto -> {
                val codigo = idCrearProductoCodigo.text.toString()
                val idCategoria = idCrearProductoIdCategoria.selectedItemPosition + 1
                val descripcionCategoria = idCrearProductoIdCategoria.selectedItem.toString() // Obtener la descripción de la categoría seleccionada
                val descripcion = idCrearProductoDescripcion.text.toString()
                val precioCompra = idCrearProductoPrecioCompra.text.toString()
                val precioVenta = idCrearProductoPrecioVenta.text.toString()
                val stock = idCrearProductoStock.text.toString()
                val rutaImagen = idCrearProductoRutaImagen.tag?.toString() ?: ""

                // Verificar todos los campos
                val camposFaltantes = mutableListOf<String>()

                if (codigo.isEmpty()) {
                    camposFaltantes.add("Código")
                }
                if (descripcion.isEmpty()) {
                    camposFaltantes.add("Descripción")
                }
                if (precioCompra.isEmpty()) {
                    camposFaltantes.add("Precio de Compra")
                }
                if (precioVenta.isEmpty()) {
                    camposFaltantes.add("Precio de Venta")
                }
                if (stock.isEmpty()) {
                    camposFaltantes.add("Stock")
                }
                if (rutaImagen.isNullOrEmpty()) {
                    camposFaltantes.add("Imagen")
                }

                // Mostrar mensaje de alerta si hay campos faltantes
                if (camposFaltantes.isNotEmpty()) {
                    val mensaje = "Por favor completa los siguientes campos:\n\n${camposFaltantes.joinToString("\n")}"
                    mostrarAlerta(mensaje)
                    return
                }

                // Si todos los campos están completos, proceder con la grabación del producto
                grabarProducto(codigo, idCategoria, descripcionCategoria, descripcion, precioCompra.toDouble(), precioVenta.toDouble(), stock.toInt(), rutaImagen)
            }
            btnCrearProductoVolver -> {
                val intent = Intent(this, MainProducto::class.java)
                startActivity(intent)
            }
        }
    }

    private fun mostrarAlerta(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Campos Incompletos")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar", null)
        val dialog = builder.create()
        dialog.show()
    }


    // Método para seleccionar una imagen desde la galería
    private fun seleccionarImagen() {
        if (checkStoragePermission()) {
            abrirSelectorImagen()
        } else {
            requestStoragePermission()
        }
    }

    private fun abrirSelectorImagen() {
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

            try {
                val inputStream = contentResolver.openInputStream(uri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Obtener el nombre de la imagen original sin la extensión
                val originalFileName = getFileName(uri).substringBeforeLast('.')

                // Guardar la imagen en el directorio específico
                val savedFile = saveImageToDirectory(bitmap, originalFileName)

                if (savedFile != null) {
                    // Actualizar la tag del ImageView con el nombre del archivo guardado sin extensión
                    idCrearProductoRutaImagen.tag = originalFileName

                    // Mostrar la imagen en ImageView
                    idCrearProductoRutaImagen.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveImageToDirectory(bitmap: Bitmap, fileName: String): File? {
        // Obtener el directorio donde se almacenará la imagen
        val directory = getExternalFilesDir(null)

        try {
            // Crear el archivo donde se guardará la imagen
            val file = File(directory, "$fileName.jpg") // Agregar la extensión .jpg

            // Guardar la imagen en el archivo
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.close()

            return file
        } catch (e: IOException) {
            Log.e("saveImageToDirectory", "Error al guardar la imagen: ${e.message}")
            e.printStackTrace()
            return null
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result!!
    }

    private fun grabarProducto(codigo: String, idCategoria: Int, descripcionCategoria: String, descripcion: String, precioCompra: Double?, precioVenta: Double?, stock: Int?, rutaImagen: String?) {
        val mensajesError = StringBuilder()

        if (codigo.isEmpty()) {
            mensajesError.append("Falta completar el campo Código\n")
        }
        if (descripcion.isEmpty()) {
            mensajesError.append("Falta completar el campo Descripción\n")
        }
        if (precioCompra == null) {
            mensajesError.append("Falta completar el campo Precio de Compra\n")
        }
        if (precioVenta == null) {
            mensajesError.append("Falta completar el campo Precio de Venta\n")
        }
        if (stock == null) {
            mensajesError.append("Falta completar el campo Stock\n")
        }
        if (rutaImagen.isNullOrEmpty()) {
            mensajesError.append("No has seleccionado una imagen\n")
        }

        if (mensajesError.isNotEmpty()) {
            mostrarAlerta(mensajesError.toString())
            return
        }

        // Aquí procedes con el código para enviar los datos a la API
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://tiaveneno.somee.com/api/Inventario/productos")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json; utf-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.doOutput = true

                val productoJson = JSONObject().apply {
                    put("codigo", codigo)
                    put("oCategoria", JSONObject().apply {
                        put("idCategoria", idCategoria)
                        put("descripcion", descripcionCategoria)
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

                if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Producto creado exitosamente", Toast.LENGTH_SHORT).show()

                        // Redirigir a la actividad MainProducto
                        val intent = Intent(this@MainNuevoProducto, MainProducto::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val errorStream = conn.errorStream
                    val errorMessage = errorStream?.bufferedReader()?.use { it.readText() } ?: "Error desconocido"
                    Log.e("Error", "Código de respuesta: $responseCode. Mensaje: $errorMessage")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Error al crear el producto", Toast.LENGTH_SHORT).show()
                    }
                }

                conn.disconnect()
            } catch (e: Exception) {
                Log.e("Error", "Error al crear el producto", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "Error al crear el producto", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




    private fun checkStoragePermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
    }

    private fun cargarCategoriasProducto(selectedIdCategoria: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://tiaveneno.somee.com/api/Inventario/categorias")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Accept", "application/json")
                val responseCode = conn.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                    val categoriasJsonArray = JSONArray(responseText)
                    val categoriasList = mutableListOf<String>()

                    for (i in 0 until categoriasJsonArray.length()) {
                        val categoriaJson = categoriasJsonArray.getJSONObject(i)
                        val descripcion = categoriaJson.getString("descripcion")
                        categoriasList.add(descripcion)
                    }

                    withContext(Dispatchers.Main) {
                        val adapter = ArrayAdapter(this@MainNuevoProducto, android.R.layout.simple_spinner_item, categoriasList)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        idCrearProductoIdCategoria.adapter = adapter

                        if (selectedIdCategoria != -1) {
                            idCrearProductoIdCategoria.setSelection(selectedIdCategoria - 1)
                        }
                    }
                } else {
                    Log.e("Error de carga", "No se pudo cargar las categorías. Código de respuesta: $responseCode")
                }

                conn.disconnect()
            } catch (e: Exception) {
                Log.e("Error de carga", "No se pudo cargar las categorías", e)
            }
        }
    }
}
