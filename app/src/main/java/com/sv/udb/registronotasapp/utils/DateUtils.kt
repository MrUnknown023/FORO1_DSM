package com.sv.udb.registronotasapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun currentDateTime(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return formatter.format(Date())
}