package com.sv.udb.registronotasapp.ui.navigation

sealed class AppRoutes(val route: String) {
    data object Login : AppRoutes("login")
    data object Subjects : AppRoutes("subjects")
    data object SubjectDetail : AppRoutes("subject_detail/{subjectId}") {
        fun createRoute(subjectId: Long): String = "subject_detail/$subjectId"
    }
}