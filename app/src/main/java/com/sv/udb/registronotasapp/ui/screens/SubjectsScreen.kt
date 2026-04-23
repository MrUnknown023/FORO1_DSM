package com.sv.udb.registronotasapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sv.udb.registronotasapp.data.repository.SubjectRepository
import com.sv.udb.registronotasapp.data.model.Subject
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//TODO:
//	•	Mejorar diseño de la lista de materias
//	•	Agregar mensajes vacíos:sin actividades
//	•	Agregar Snackbar o mensajes visuales para errores
//	•	Agregar estados de éxito al guardar
//	•	Mejorar espaciados, tipografías y consistencia visual
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectsScreen(
    userId: Long,
    userName: String,
    onLogout: () -> Unit,
    onSubjectClick: (Long) -> Unit,
) {
    val context = LocalContext.current
    val repo = remember { SubjectRepository(context) }

    var subjects by remember { mutableStateOf(listOf<Subject>()) }
    var newSubject by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") } // Para errores de validación

    // Estados para los dialogs
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }
    var editSubjectName by remember { mutableStateOf("") }

    fun loadSubjects() {
        subjects = repo.getSubjectsByUser(userId)
    }

    LaunchedEffect(userId) {
        loadSubjects()
    }

    // 1. Confirmación de Eliminación
    if (subjectToDelete != null) {
        AlertDialog(
            onDismissRequest = { subjectToDelete = null },
            title = { Text("Eliminar materia") },
            text = { Text("¿Estás seguro de eliminar '${subjectToDelete?.name}'? Se borrarán todas sus notas. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    repo.deleteSubject(subjectToDelete!!.id)
                    loadSubjects()
                    subjectToDelete = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { subjectToDelete = null }) { Text("Cancelar") }
            }
        )
    }

    // 2. Edicion
    if (subjectToEdit != null) {
        AlertDialog(
            onDismissRequest = { subjectToEdit = null },
            title = { Text("Editar materia") },
            text = {
                OutlinedTextField(
                    value = editSubjectName,
                    onValueChange = { editSubjectName = it },
                    label = { Text("Nombre de la materia") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (editSubjectName.isNotBlank()) {
                        repo.updateSubjectName(subjectToEdit!!.id, editSubjectName.trim())
                        loadSubjects()
                        subjectToEdit = null
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { subjectToEdit = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Hola, $userName", style = MaterialTheme.typography.titleMedium)
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text("Mis Materias", style = MaterialTheme.typography.headlineMedium)

            // Formulario de agregar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newSubject,
                    onValueChange = {
                        newSubject = it
                        errorText = ""
                    },
                    label = { Text("Nueva materia") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    isError = errorText.isNotEmpty()
                )

                Button(
                    onClick = {
                        val subjectNameTrimmed = newSubject.trim()

                        // VALIDACIÓN 1: Vacío
                        if (subjectNameTrimmed.isBlank()) {
                            errorText = "El nombre no puede estar vacío"
                            return@Button
                        }

                        // VALIDACIÓN 2: Repetido
                        val exists =
                            subjects.any { it.name.equals(subjectNameTrimmed, ignoreCase = true) }
                        if (exists) {
                            errorText = "Ya tienes una materia con este nombre"
                            return@Button
                        }

                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val currentDate = sdf.format(Date())

                        repo.insertSubject(
                            Subject(
                                userId = userId,
                                name = subjectNameTrimmed,
                                createdAt = currentDate
                            )
                        )
                        newSubject = ""
                        loadSubjects()
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Agregar")
                }
            }

            // Mensaje de error debajo del input
            if (errorText.isNotEmpty()) {
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LISTA DE MATERIAS O MENSAJE VACÍO
            if (subjects.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No tienes materias registradas.\n¡Agrega una para comenzar!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(subjects) { subject ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = subject.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSubjectClick(subject.id) }
                                        .padding(vertical = 8.dp)
                                )

                                IconButton(onClick = {
                                    editSubjectName = subject.name
                                    subjectToEdit = subject
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }

                                IconButton(onClick = { subjectToDelete = subject }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}