package com.sv.udb.registronotasapp.data.model

data class ActivityItem(
    val id: Long = 0,
    val subjectId: Long,
    val name: String,
    val percentage: Double,
    val score: Double,
    val createdAt: String
)