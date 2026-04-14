package com.sv.udb.registronotasapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sv.udb.registronotasapp.ui.screens.LoginScreen
import com.sv.udb.registronotasapp.ui.screens.SubjectDetailScreen
import com.sv.udb.registronotasapp.ui.screens.SubjectsScreen

//TODO:
//	•	Agregar navegación segura pasando subjectId
//	•	Agregar navegación de regreso entre pantallas
//	•	Definir flujo completo:
//	•	Login
//	•	Materias
//	•	Detalle de materia
//	•	Preparar rutas futuras para editar actividades
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Login.route
    ) {
        composable(AppRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppRoutes.Subjects.route) {
                        popUpTo(AppRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(AppRoutes.Subjects.route) {
            SubjectsScreen(
                onSubjectClick = { subjectId ->
                    navController.navigate(AppRoutes.SubjectDetail.createRoute(subjectId))
                }
            )
        }

        composable(
            route = AppRoutes.SubjectDetail.route,
            arguments = listOf(navArgument("subjectId") { type = NavType.LongType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
            SubjectDetailScreen(subjectId = subjectId)
        }
    }
}