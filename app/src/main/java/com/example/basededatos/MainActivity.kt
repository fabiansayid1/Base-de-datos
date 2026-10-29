package com.example.basededatos

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnIngresar: Button
    private lateinit var tvCrearCuenta: TextView

    private lateinit var dbHelper: DatabaseHelper

    // Lanzador para recibir los datos devueltos desde RegisterActivity
    private val registerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val correo = result.data?.getStringExtra("EXTRA_EMAIL")
            val password = result.data?.getStringExtra("EXTRA_PASSWORD")

            // Auto-rellenar las casillas del Login
            correo?.let { etCorreo.setText(it) }
            password?.let { etPassword.setText(it) }

            Toast.makeText(this, "Datos cargados. ¡Presiona Ingresar!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPassword)
        btnIngresar = findViewById(R.id.btnIngresar)
        tvCrearCuenta = findViewById(R.id.tvCrearCuenta)

        dbHelper = DatabaseHelper(this)

        tvCrearCuenta.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            registerLauncher.launch(intent)
        }

        btnIngresar.setOnClickListener {
            loginUsuario()
        }
    }

    private fun loginUsuario() {
        val correo = etCorreo.text.toString().trim().lowercase()
        val password = etPassword.text.toString().trim()

        if (correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa tu correo y contraseña", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.error = "Ingresa un correo válido"
            etCorreo.requestFocus()
            return
        }

        val passwordHasheada = hashPassword(password)

        val esValido = dbHelper.validarUsuario(correo, passwordHasheada)

        if (esValido) {
            Toast.makeText(this, "¡Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, HomeActivity::class.java).apply {
                putExtra("USER_EMAIL", correo)
            }
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show()
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}