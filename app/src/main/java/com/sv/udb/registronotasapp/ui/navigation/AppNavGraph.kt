package com.sv.udb.registronotasapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sv.udb.registronotasapp.ui.screens.MainScreen
import com.sv.udb.registronotasapp.ui.screens.LoginScreen
import com.sv.udb.registronotasapp.ui.screens.RegisterScreen
import com.sv.udb.registronotasapp.ui.screens.SubjectDetailScreen
import com.sv.udb.registronotasapp.ui.screens.SubjectsScreen
import androidx.compose.ui.platform.LocalContext
import com.sv.udb.registronotasapp.utils.SessionManager
import androidx.compose.runtime.remember

//TODO:
//	•	Detalle de materia
//	•	Preparar rutas futuras para editar actividades
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Main.route
    ) {
        composable(AppRoutes.Main.route) {
            MainScreen(
                onNavigateToLogin = { navController.navigate(AppRoutes.Login.route) },
                onNavigateToRegister = { navController.navigate(AppRoutes.Register.route) }
            )
        }

        composable(AppRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppRoutes.Subjects.route) {
                        popUpTo(AppRoutes.Main.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(AppRoutes.Subjects.route) {
                        popUpTo(AppRoutes.Main.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppRoutes.Subjects.route) {
            SubjectsScreen(
                userId = sessionManager.getUserId(),
                userName = sessionManager.getUserName(),
                onSubjectClick = { subjectId ->
                    navController.navigate(AppRoutes.SubjectDetail.createRoute(subjectId))
                },
                onLogout = {
                    sessionManager.clearSession()
                    navController.navigate(AppRoutes.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = AppRoutes.SubjectDetail.route,
            arguments = listOf(navArgument("subjectId") { type = NavType.LongType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: 0L
            SubjectDetailScreen(
                subjectId = subjectId,onNavigateBack = {
                navController.popBackStack()
            })
        }
    }
}