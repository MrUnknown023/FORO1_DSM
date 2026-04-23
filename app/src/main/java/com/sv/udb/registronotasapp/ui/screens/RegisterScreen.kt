package com.sv.udb.registronotasapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.sv.udb.registronotasapp.data.repository.UserRepository
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import com.sv.udb.registronotasapp.utils.SessionManager
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val userRepo = remember { UserRepository(context) }
    val sessionManager = remember { SessionManager(context) }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear Cuenta", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { newValue ->
                if (!newValue.any { it.isDigit() }) {
                    fullName = newValue
                    error = ""
                }
                fullName = newValue
                error = ""
            },
            label = { Text("Nombre Completo") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                error = ""
            },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                error = ""
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                error = ""
            },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (fullName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    error = "Todos los campos son obligatorios"
                    return@Button
                }

                if (fullName.any { it.isDigit() }) {
                    error = "El nombre no debe contener números"
                    return@Button
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    error = "Ingresa un correo electrónico válido"
                    return@Button
                }

                if (password != confirmPassword) {
                    error = "Las contraseñas no coinciden"
                    return@Button
                }

                if (userRepo.userExists(email)) {
                    error = "Este correo ya está registrado"
                    return@Button
                }

                val result = userRepo.registerUser(email, password, fullName)
                if (result.isSuccess) {
                    val newUserId = result.getOrNull() ?: -1L
                    sessionManager.saveSession(newUserId, fullName)
                    onRegisterSuccess()
                } else {
                    error = "Error al crear la cuenta"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Registrarse")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onNavigateBack) {
            Text("Cancelar y regresar")
        }
    }
}