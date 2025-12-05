package com.example.practica8_contactos

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class VisorContactosActivity : AppCompatActivity() {

    private lateinit var lvContactos: ListView
    private lateinit var tvSinContactos: TextView
    private var usuario: String = ""
    private val contactos = ArrayList<Contacto>()
    private val url = "http://192.168.15.185/contactos/" // Cambiar por tu IP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visor_contactos)

        usuario = intent.getStringExtra("usuario") ?: ""

        lvContactos = findViewById(R.id.lvContactos)
        tvSinContactos = findViewById(R.id.tvSinContactos)

        cargarContactos()
    }

    private fun cargarContactos() {
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, "${url}listar_contactos.php",
            { response ->
                try {
                    val jsonArray = JSONArray(response)
                    contactos.clear()

                    if (jsonArray.length() == 0) {
                        tvSinContactos.visibility = TextView.VISIBLE
                        lvContactos.visibility = ListView.GONE
                    } else {
                        tvSinContactos.visibility = TextView.GONE
                        lvContactos.visibility = ListView.VISIBLE

                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val contacto = Contacto(
                                codigo = obj.getString("codigo"),
                                nombre = obj.getString("nombre"),
                                direccion = obj.getString("direccion"),
                                telefono = obj.getString("telefono"),
                                correo = obj.getString("correo"),
                                imagen = obj.getString("imagen"),
                                usuario = obj.getString("usuario")
                            )
                            contactos.add(contacto)
                        }

                        val adapter = ContactoAdapter(this, contactos)
                        lvContactos.adapter = adapter
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
                params["usuario"] = usuario
                return params
            }
        }

        requestQueue.add(stringRequest)
    }
}