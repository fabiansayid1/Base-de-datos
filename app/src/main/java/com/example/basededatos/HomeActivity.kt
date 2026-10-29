package com.example.basededatos

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        dbHelper = DatabaseHelper(this)

        val ivMenu = findViewById<ImageView>(R.id.ivMenu)
        val tvVerTodo = findViewById<TextView>(R.id.tvVerTodo)
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregar)
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        val tvInicialAvatar = findViewById<TextView>(R.id.tvInicialAvatar)

        val userEmail = intent.getStringExtra("USER_EMAIL")
        if (!userEmail.isNullOrEmpty()) {
            val datosUsuario = dbHelper.obtenerNombreYUniversidad(userEmail)
            if (datosUsuario != null) {
                val (nombre, universidad) = datosUsuario
                tvBienvenida.text = "Hola, $nombre"

                if (nombre.isNotEmpty()) {
                    tvInicialAvatar.text = nombre.first().uppercase()
                }

                Toast.makeText(this, "Estudiante de: $universidad", Toast.LENGTH_LONG).show()
            }
        }

        ivMenu.setOnClickListener {
            Toast.makeText(this, "Menú de opciones", Toast.LENGTH_SHORT).show()
        }

        tvVerTodo.setOnClickListener {
            Toast.makeText(this, "Mostrando todas las materias...", Toast.LENGTH_SHORT).show()
        }

        fabAgregar.setOnClickListener {
            Toast.makeText(this, "Agregar nueva tarea o actividad", Toast.LENGTH_SHORT).show()
        }
    }
}