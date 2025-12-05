package com.example.practica8_contactos

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class RegistroContactoActivity : AppCompatActivity() {

    private lateinit var imgContacto: ImageView
    private lateinit var edCodigo: EditText
    private lateinit var edNombre: EditText
    private lateinit var edDireccion: EditText
    private lateinit var edTelefono: EditText
    private lateinit var edCorreo: EditText
    private lateinit var btnSeleccionarImagen: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnConsultar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnLimpiar: Button

    private var imagenBase64: String = ""
    private var usuario: String = ""
    private val url = "http://192.168.15.185/contactos/" // Cambiar por tu IP

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_contacto)

        usuario = intent.getStringExtra("usuario") ?: ""

        initViews()
        setupListeners()
    }

    private fun initViews() {
        imgContacto = findViewById(R.id.imgContacto)
        edCodigo = findViewById(R.id.edCodigo)
        edNombre = findViewById(R.id.edNombre)
        edDireccion = findViewById(R.id.edDireccion)
        edTelefono = findViewById(R.id.edTelefono)
        edCorreo = findViewById(R.id.edCorreo)
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnConsultar = findViewById(R.id.btnConsultar)
        btnActualizar = findViewById(R.id.btnActualizar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnLimpiar = findViewById(R.id.btnLimpiar)
    }

    private fun setupListeners() {
        btnSeleccionarImagen.setOnClickListener {
            seleccionarImagen()
        }

        btnGuardar.setOnClickListener {
            if (validarCamposObligatorios()) {
                guardarContacto()
            }
        }

        btnConsultar.setOnClickListener {
            if (edCodigo.text.isEmpty()) {
                Toast.makeText(this, "Ingrese el código del contacto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            consultarContacto()
        }

        btnActualizar.setOnClickListener {
            if (validarCamposObligatorios()) {
                actualizarContacto()
            }
        }

        btnEliminar.setOnClickListener {
            if (edCodigo.text.isEmpty()) {
                Toast.makeText(this, "Ingrese el código del contacto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            eliminarContacto()
        }

        btnLimpiar.setOnClickListener {
            limpiarCampos()
        }
    }

    private fun seleccionarImagen() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val uri: Uri = data.data!!
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                imgContacto.setImageBitmap(bitmap)
                imagenBase64 = convertirImagenABase64(bitmap)
            } catch (e: Exception) {
                Toast.makeText(this, "Error al cargar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun convertirImagenABase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun validarCamposObligatorios(): Boolean {
        if (edCodigo.text.isEmpty()) {
            Toast.makeText(this, "El código es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (edNombre.text.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (edTelefono.text.isEmpty()) {
            Toast.makeText(this, "El teléfono es obligatorio", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun guardarContacto() {
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, "${url}guardar_contacto.php",
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getBoolean("success")) {
                        Toast.makeText(this, "Contacto guardado exitosamente", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
            }) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["codigo"] = edCodigo.text.toString()
                params["nombre"] = edNombre.text.toString()
                params["direccion"] = edDireccion.text.toString()
                params["telefono"] = edTelefono.text.toString()
                params["correo"] = edCorreo.text.toString()
                params["imagen"] = imagenBase64
                params["usuario"] = usuario
                return params
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun consultarContacto() {
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, "${url}consultar_contacto.php",
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getBoolean("success")) {
                        val contacto = jsonResponse.getJSONObject("contacto")
                        edNombre.setText(contacto.getString("nombre"))
                        edDireccion.setText(contacto.getString("direccion"))
                        edTelefono.setText(contacto.getString("telefono"))
                        edCorreo.setText(contacto.getString("correo"))

                        val imagenUrl = contacto.getString("imagen")
                        if (imagenUrl.isNotEmpty()) {
                            com.squareup.picasso.Picasso.get()
                                .load(imagenUrl)
                                .placeholder(R.drawable.ic_person)
                                .error(R.drawable.ic_person)
                                .into(imgContacto)
                        }

                        Toast.makeText(this, "Contacto encontrado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
            }) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["codigo"] = edCodigo.text.toString()
                params["usuario"] = usuario
                return params
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun actualizarContacto() {
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, "${url}actualizar_contacto.php",
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getBoolean("success")) {
                        Toast.makeText(this, "Contacto actualizado exitosamente", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
            }) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["codigo"] = edCodigo.text.toString()
                params["nombre"] = edNombre.text.toString()
                params["direccion"] = edDireccion.text.toString()
                params["telefono"] = edTelefono.text.toString()
                params["correo"] = edCorreo.text.toString()
                params["imagen"] = imagenBase64
                params["usuario"] = usuario
                return params
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun eliminarContacto() {
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, "${url}eliminar_contacto.php",
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getBoolean("success")) {
                        Toast.makeText(this, "Contacto eliminado exitosamente", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(this, jsonResponse.getString("message"), Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
            }) {

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["codigo"] = edCodigo.text.toString()
                params["usuario"] = usuario
                return params
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun limpiarCampos() {
        edCodigo.text.clear()
        edNombre.text.clear()
        edDireccion.text.clear()
        edTelefono.text.clear()
        edCorreo.text.clear()
        imgContacto.setImageResource(R.drawable.ic_person)
        imagenBase64 = ""
    }
}