package com.sv.udb.registronotasapp.data.model

data class User(
    val id: Long = 0,
    val email: String,
    val password: String,
    val displayName: String
)