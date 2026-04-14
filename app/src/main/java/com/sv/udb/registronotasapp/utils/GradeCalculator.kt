package com.sv.udb.registronotasapp.utils

import com.sv.udb.registronotasapp.data.model.ActivityItem

fun calculateTotalPercentage(activities: List<ActivityItem>): Double {
    return activities.sumOf { it.percentage }
}

fun calculateRemainingPercentage(activities: List<ActivityItem>): Double {
    return 100.0 - calculateTotalPercentage(activities)
}

fun calculateFinalGrade(activities: List<ActivityItem>): Double {
    return activities.sumOf { (it.score * it.percentage) / 100.0 }
}

fun getStatus(finalGrade: Double): String {
    return if (finalGrade >= 6.0) "Aprobado" else "Reprobado"
}