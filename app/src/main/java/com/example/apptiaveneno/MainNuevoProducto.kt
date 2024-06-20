package com.example.apptiaveneno

import android.Manifest
import android.app.Activity
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
import java.text.SimpleDateFormat
import java.util.*

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
        cargarCategoriasProducto(-1) // Aquí puedes pasar el ID de la categoría seleccionada si lo deseas
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

                if (codigo.isNotEmpty() && descripcionCategoria.isNotEmpty() && descripcion.isNotEmpty() && precioCompra.isNotEmpty() && precioVenta.isNotEmpty() && stock.isNotEmpty()) {
                    grabarProducto(codigo, idCategoria, descripcionCategoria, descripcion, precioCompra.toDouble(), precioVenta.toDouble(), stock.toInt(), rutaImagen)
                } else {
                    Toast.makeText(applicationContext, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            btnCrearProductoVolver -> {
                val intent = Intent(this, MainProducto::class.java)
                startActivity(intent)
            }
        }
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

                // Generar nombre de archivo único
                val imageName = generateUniqueFileName()

                // Guardar la imagen en el directorio específico
                val savedFile = saveImageToDirectory(bitmap, imageName)

                if (savedFile != null) {
                    // Actualizar la tag del ImageView con la ruta del archivo guardado
                    idCrearProductoRutaImagen.tag = savedFile.name // Aquí se guarda el nombre del archivo

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

    private fun generateUniqueFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "imagen_$timeStamp.jpg"
    }

    private fun saveImageToDirectory(bitmap: Bitmap, fileName: String): File? {
        // Obtener el directorio donde se almacenará la imagen
        val directory = File(getExternalFilesDir(null), "ImgTiaVeneno")

        try {
            // Crear el directorio si no existe
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    Log.e("saveImageToDirectory", "Error: No se pudo crear el directorio")
                    return null
                }
            }

            // Crear el archivo donde se guardará la imagen
            val file = File(directory, fileName)

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

    private fun grabarProducto(codigo: String, idCategoria: Int, descripcionCategoria: String, descripcion: String, precioCompra: Double, precioVenta: Double, stock: Int, rutaImagen: String) {
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
                        put("descripcion", descripcionCategoria) // Asegúrate de que este valor sea la descripción correcta
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
                    }
                } else {
                    val errorStream = conn.errorStream
                    val errorMessage = errorStream?.bufferedReader()?.use { it.readText() } ?: "Sin mensaje de error"
                    Log.e("Error de creación", "No se pudo crear el producto. Código de respuesta: $responseCode. Mensaje de error: $errorMessage")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Error al crear el producto: $responseMessage", Toast.LENGTH_SHORT).show()
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



    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_STORAGE_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirSelectorImagen()
            } else {
                Toast.makeText(this, "Permiso denegado para acceder al almacenamiento", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarCategoriasProducto(selectedCategoryId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://tiaveneno.somee.com/api/Inventario/categorias")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"

                val responseCode = conn.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = conn.inputStream
                    val responseText = inputStream.bufferedReader().use { it.readText() }
                    val categoriasJsonArray = JSONArray(responseText) // Aquí cambiamos a JSONArray

                    val categoriasList = mutableListOf<String>()
                    for (i in 0 until categoriasJsonArray.length()) {
                        val categoria = categoriasJsonArray.getJSONObject(i)
                        val idCategoria = categoria.getInt("idCategoria")
                        val descripcion = categoria.getString("descripcion")
                        categoriasList.add(descripcion)
                    }

                    withContext(Dispatchers.Main) {
                        val adapter = ArrayAdapter(
                            this@MainNuevoProducto,
                            android.R.layout.simple_spinner_item,
                            categoriasList
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        idCrearProductoIdCategoria.adapter = adapter

                        // Seleccionar la categoría si el ID está especificado
                        if (selectedCategoryId >= 0) {
                            val selectedIndex = categoriasList.indexOfFirst {
                                it.contains(selectedCategoryId.toString())
                            }
                            if (selectedIndex >= 0) {
                                idCrearProductoIdCategoria.setSelection(selectedIndex)
                            }
                        }
                    }
                } else {
                    Log.e("Error de carga", "No se pudo cargar las categorías. Código de respuesta: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("Error de conexión", "Error al conectar con la API: ${e.message}")
                e.printStackTrace()
            }
        }
    }

}
