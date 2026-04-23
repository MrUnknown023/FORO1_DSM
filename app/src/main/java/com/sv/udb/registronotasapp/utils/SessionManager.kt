package com.sv.udb.registronotasapp.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    // Crea un archivo XML interno para guardar datos
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Función para GUARDAR la sesión cuando el usuario hace login o se registra
    fun saveSession(userId: Long, userName: String) {
        prefs.edit().apply {
            putLong("USER_ID", userId)
            putString("USER_NAME", userName)
            apply() // apply() guarda de forma asíncrona (más rápido)
        }
    }

    // Función para LEER el ID del usuario dinámicamente
    fun getUserId(): Long {
        return prefs.getLong("USER_ID", -1L) // Devuelve -1 si no hay sesión activa
    }

    // Función para LEER el nombre del usuario dinámicamente
    fun getUserName(): String {
        return prefs.getString("USER_NAME", "Usuario") ?: "Usuario"
    }

    // Función para CERRAR sesión
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}