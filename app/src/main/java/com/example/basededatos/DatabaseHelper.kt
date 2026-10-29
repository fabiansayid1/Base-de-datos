package com.example.basededatos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "studypulse.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_UNIVERSIDAD = "universidad"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "contrasena"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val crearTablaUsuarios = """
            CREATE TABLE $TABLE_USUARIOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_UNIVERSIDAD TEXT NOT NULL,
                $COL_CORREO TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(crearTablaUsuarios)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    fun registrarUsuario(nombre: String, universidad: String, correo: String, password: String): Boolean {
        val db = writableDatabase
        val datos = ContentValues().apply {
            put(COL_NOMBRE, nombre.trim())
            put(COL_UNIVERSIDAD, universidad.trim())
            put(COL_CORREO, correo.trim().lowercase())
            put(COL_PASSWORD, password)
        }

        val resultado = db.insert(TABLE_USUARIOS, null, datos)
        return resultado != -1L
    }

    fun validarUsuario(correo: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT 1 FROM $TABLE_USUARIOS WHERE $COL_CORREO = ? AND $COL_PASSWORD = ? LIMIT 1"

        return db.rawQuery(query, arrayOf(correo.trim().lowercase(), password)).use { cursor ->
            cursor.moveToFirst()
        }
    }

    fun obtenerNombreYUniversidad(correo: String): Pair<String, String>? {
        val db = readableDatabase
        val query = "SELECT $COL_NOMBRE, $COL_UNIVERSIDAD FROM $TABLE_USUARIOS WHERE $COL_CORREO = ? LIMIT 1"

        db.rawQuery(query, arrayOf(correo.trim().lowercase())).use { cursor ->
            if (cursor.moveToFirst()) {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE))
                val universidad = cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIVERSIDAD))
                return Pair(nombre, universidad)
            }
        }
        return null
    }
}