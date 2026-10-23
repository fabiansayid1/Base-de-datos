package com.example.basededatos

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val ivMenu = findViewById<ImageView>(R.id.ivMenu)
        val tvVerTodo = findViewById<TextView>(R.id.tvVerTodo)
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregar)

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