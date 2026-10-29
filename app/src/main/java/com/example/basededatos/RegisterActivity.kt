package com.example.basededatos

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.basededatos.network.RetrofitClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.MessageDigest

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText
    private lateinit var actvUniversidad: AutoCompleteTextView
    private lateinit var btnRegistrar: Button
    private lateinit var tvYaTengoCuenta: TextView

    private lateinit var dbHelper: DatabaseHelper
    private var busquedaJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPassword)
        actvUniversidad = findViewById(R.id.actvUniversidad)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        tvYaTengoCuenta = findViewById(R.id.tvYaTengoCuenta)

        dbHelper = DatabaseHelper(this)

        configurarBuscadorUniversidad()

        tvYaTengoCuenta.setOnClickListener {
            finish()
        }

        btnRegistrar.setOnClickListener {
            registrarUsuario()
        }
    }

    private fun configurarBuscadorUniversidad() {
        actvUniversidad.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""

                busquedaJob?.cancel()

                if (query.length >= 3) {
                    busquedaJob = lifecycleScope.launch {
                        delay(300)
                        buscarUniversidadesAPI(query)
                    }
                }
            }
        })
    }

    private fun buscarUniversidadesAPI(query: String) {
        lifecycleScope.launch {
            try {
                val resultados = RetrofitClient.apiService.getUniversities(name = query)
                val nombresUni = resultados.map { it.name }

                if (nombresUni.isNotEmpty()) {
                    mostrarListaDesplegable(nombresUni)
                } else {
                    usarRespaldoLocal(query)
                }
            } catch (e: Exception) {
                usarRespaldoLocal(query)
            }
        }
    }

    private fun usarRespaldoLocal(query: String) {
        val listaRespaldo = listOf(
            "Universidad Politécnica del Estado de Morelos (Upemor)",
            "Universidad Nacional Autónoma de México (UNAM)",
            "Instituto Politécnico Nacional (IPN)",
            "Instituto Tecnológico y de Estudios Superiores de Monterrey (ITESM)",
            "Universidad Autónoma del Estado de Morelos (UAEM)",
            "Universidad Autónoma Metropolitana (UAM)",
            "Universidad Tecnológica de México (UNITEC)",
            "Universidad Anáhuac",
            "Universidad Autónoma de Guadalajara",
            "Universidad Autónoma de Nuevo León"
        )

        val coincidencias = listaRespaldo.filter { it.contains(query, ignoreCase = true) }

        if (coincidencias.isNotEmpty()) {
            mostrarListaDesplegable(coincidencias)
        } else {
            mostrarListaDesplegable(listOf(query))
        }
    }

    private fun mostrarListaDesplegable(lista: List<String>) {
        // Usa el diseño personalizado con texto oscuro
        val adapter = ArrayAdapter(
            this@RegisterActivity,
            R.layout.item_dropdown_university,
            lista
        )
        actvUniversidad.setAdapter(adapter)
        adapter.notifyDataSetChanged()

        if (actvUniversidad.hasFocus()) {
            actvUniversidad.showDropDown()
        }
    }

    private fun registrarUsuario() {
        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim().lowercase()
        val password = etPassword.text.toString().trim()
        val universidad = actvUniversidad.text.toString().trim()

        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty() || universidad.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos, incluida la universidad", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.error = "Ingresa un correo válido"
            etCorreo.requestFocus()
            return
        }

        if (password.length < 4) {
            etPassword.error = "La contraseña debe tener al menos 4 caracteres"
            etPassword.requestFocus()
            return
        }

        val passwordHasheada = hashPassword(password)

        val resultado = dbHelper.registrarUsuario(nombre, universidad, correo, passwordHasheada)

        if (resultado) {
            Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()

            val resultIntent = Intent().apply {
                putExtra("EXTRA_EMAIL", correo)
                putExtra("EXTRA_PASSWORD", password)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        } else {
            Toast.makeText(
                this,
                "El correo ya está registrado o ocurrió un error.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}