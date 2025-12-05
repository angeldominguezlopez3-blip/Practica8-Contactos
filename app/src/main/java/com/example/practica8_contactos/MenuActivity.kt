package com.example.practica8_contactos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    private lateinit var tvBienvenida: TextView
    private lateinit var btnRegistroContacto: Button
    private lateinit var btnVerContactos: Button
    private lateinit var btnCerrarSesion: Button
    private var usuario: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Obtener usuario de la sesión o del intent
        usuario = intent.getStringExtra("usuario") ?: ""
        if (usuario.isEmpty()) {
            val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
            usuario = prefs.getString("usuario", "") ?: ""
        }

        initViews()
        setupListeners()
    }

    private fun initViews() {
        tvBienvenida = findViewById(R.id.tvBienvenida)
        btnRegistroContacto = findViewById(R.id.btnRegistroContacto)
        btnVerContactos = findViewById(R.id.btnVerContactos)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        tvBienvenida.text = "Bienvenido, $usuario"
    }

    private fun setupListeners() {
        btnRegistroContacto.setOnClickListener {
            val intent = Intent(this, RegistroContactoActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        btnVerContactos.setOnClickListener {
            val intent = Intent(this, VisorContactosActivity::class.java)
            intent.putExtra("usuario", usuario)
            startActivity(intent)
        }

        btnCerrarSesion.setOnClickListener {
            // Limpiar sesión
            val prefs = getSharedPreferences("sesion", MODE_PRIVATE)
            prefs.edit().clear().apply()

            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()

            // Volver al login
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}