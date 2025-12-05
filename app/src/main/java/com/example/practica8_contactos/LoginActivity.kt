package com.example.practica8_contactos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var edUsuario: EditText
    private lateinit var edPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegistro: Button

    private val url = "http://192.168.15.185/contactos/" // Cambiar por tu IP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edUsuario = findViewById(R.id.edUsuario)
        edPassword = findViewById(R.id.edPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegistro = findViewById(R.id.btnRegistro)

        btnLogin.setOnClickListener {
            if (validarCampos()) {
                iniciarSesion()
            }
        }

        btnRegistro.setOnClickListener {
            if (validarCampos()) {
                registrarUsuario()
            }
        }
    }

    private fun validarCampos(): Boolean {
        if (edUsuario.text.isEmpty()) {
            Toast.makeText(this, "Ingrese usuario", Toast.LENGTH_SHORT).show()
            return false
        }
        if (edPassword.text.isEmpty()) {
            Toast.makeText(this, "Ingrese contraseña", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun iniciarSesion() {
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, "${url}login.php",
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getBoolean("success")) {
                        val usuario = edUsuario.text.toString()

                        // Guardar sesión
                        val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
                        prefs.edit().putString("usuario", usuario).apply()

                        Toast.makeText(this, "Bienvenido $usuario", Toast.LENGTH_SHORT).show()

                        // Ir al menú principal
                        val intent = Intent(this, `MenuActivity.kt`::class.java)
                        intent.putExtra("usuario", usuario)
                        startActivity(intent)
                        finish()
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
                params["usuario"] = edUsuario.text.toString()
                params["password"] = edPassword.text.toString()
                return params
            }
        }

        requestQueue.add(stringRequest)
    }

    private fun registrarUsuario() {
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Request.Method.POST, "${url}registro.php",
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    if (jsonResponse.getBoolean("success")) {
                        Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                        edUsuario.text.clear()
                        edPassword.text.clear()
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
                params["usuario"] = edUsuario.text.toString()
                params["password"] = edPassword.text.toString()
                return params
            }
        }

        requestQueue.add(stringRequest)
    }
}