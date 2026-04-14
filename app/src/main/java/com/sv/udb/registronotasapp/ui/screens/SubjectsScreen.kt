package com.sv.udb.registronotasapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.sv.udb.registronotasapp.data.model.Subject
import com.sv.udb.registronotasapp.data.repository.SubjectRepository
import com.sv.udb.registronotasapp.utils.currentDateTime

//TODO:
// • Dejar de usar userId = 1L fijo en SubjectsScreen
//	•	Pasar el userId real entre pantallas
//	•	Validar que no se creen materias vacías
//	•	Validar que no se repitan materias con el mismo nombre para el mismo usuario
//	•	Agregar opción para eliminar materia
//	•	Agregar opción para editar nombre de materia
//	•	Mostrar mensaje si el usuario no tiene materias
//	•	Confirmación antes de eliminar una materia
//	•	Mejorar diseño de la lista de materias
//	•	Agregar mensajes vacíos:sin materias,sin actividades
//	•	Agregar Snackbar o mensajes visuales para errores
//	•	Agregar estados de éxito al guardar
//	•	Mejorar espaciados, tipografías y consistencia visual
//  •   VALIDACIONES
@Composable
fun SubjectsScreen(
    onSubjectClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val repo = remember { SubjectRepository(context) }

    var subjects by remember { mutableStateOf(listOf<Subject>()) }
    var newSubject by remember { mutableStateOf("") }

    val userId = 1L

    fun loadSubjects() {
        subjects = repo.getSubjectsByUser(userId)
    }

    LaunchedEffect(Unit) {
        loadSubjects()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Materias", style = MaterialTheme.typography.headlineMedium)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            OutlinedTextField(
                value = newSubject,
                onValueChange = { newSubject = it },
                label = { Text("Nueva materia") },
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    if (newSubject.isNotBlank()) {
                        repo.insertSubject(
                            Subject(
                                userId = userId,
                                name = newSubject,
                                createdAt = currentDateTime()
                            )
                        )
                        newSubject = ""
                        loadSubjects()
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Agregar")
            }
        }

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(subjects) { subject ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = { onSubjectClick(subject.id) }
                ) {
                    Text(
                        text = subject.name,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}