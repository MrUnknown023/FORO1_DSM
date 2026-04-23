package com.sv.udb.registronotasapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sv.udb.registronotasapp.data.model.ActivityItem
import com.sv.udb.registronotasapp.data.repository.ActivityRepository
import com.sv.udb.registronotasapp.utils.calculateFinalGrade
import com.sv.udb.registronotasapp.utils.calculateRemainingPercentage
import com.sv.udb.registronotasapp.utils.calculateTotalPercentage
import com.sv.udb.registronotasapp.utils.currentDateTime
import com.sv.udb.registronotasapp.utils.getStatus
//TODO:
//  •	Crear pantalla o diálogo para editar actividad
//	•	Permitir modificar:
//	•	nombre
//	•	porcentaje
//	•	nota
//	•	Permitir eliminar actividad
//	•	Confirmación antes de eliminar actividad
//	•	Validar que al editar una actividad la suma de porcentajes no pase de 100
//	•	Validar que la nota esté entre 0 y 10
//	•	Validar que el porcentaje sea mayor que 0
//	•	Verificar redondeo de nota final a 2 decimales
//	•	Mostrar claramente:
//	•	porcentaje acumulado
//	•	porcentaje faltante
//	•	nota final actual
//	•	estado aprobado/reprobado
//	•	Mostrar el estado actual del usuario si es aprobado o no.
//	•	Agregar mensaje visual cuando la materia aún no completa el 100%
//	•	Mejorar diseño del detalle de materia
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    subjectId: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val repo = remember { ActivityRepository(context) }

    var activities by remember { mutableStateOf(listOf<ActivityItem>()) }

    var activityName by remember { mutableStateOf("") }
    var percentageText by remember { mutableStateOf("") }
    var scoreText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    fun loadActivities() {
        activities = repo.getActivitiesBySubject(subjectId)
    }

    LaunchedEffect(Unit) {
        loadActivities()
    }

    val totalPercentage = calculateTotalPercentage(activities)
    val remainingPercentage = calculateRemainingPercentage(activities)
    val finalGrade = calculateFinalGrade(activities)
    val status = getStatus(finalGrade)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de materia") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
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

            // --- RESUMEN DE NOTAS ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Porcentaje acumulado: ${"%.2f".format(totalPercentage)}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Porcentaje faltante: ${"%.2f".format(remainingPercentage)}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Nota final actual: ${"%.2f".format(finalGrade)}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "Estado: $status",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (status == "Aprobado") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }

            // --- FORMULARIO DE NUEVA ACTIVIDAD ---
            OutlinedTextField(
                value = activityName,
                onValueChange = {
                    activityName = it
                    errorText = ""
                },
                label = { Text("Nueva Actividad") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = percentageText,
                    onValueChange = {
                        percentageText = it
                        errorText = ""
                    },
                    label = { Text("% (Ej: 25)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = scoreText,
                    onValueChange = {
                        scoreText = it
                        errorText = ""
                    },
                    label = { Text("Nota (0-10)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            if (errorText.isNotBlank()) {
                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                onClick = {
                    val activityNameTrimmed = activityName.trim()
                    val percentage = percentageText.toDoubleOrNull()
                    val score = scoreText.toDoubleOrNull()

                    when {
                        activityNameTrimmed.isBlank() -> {
                            errorText = "El nombre de la actividad es obligatorio."
                        }
                        percentage == null || percentage <= 0.0 -> {
                            errorText = "El porcentaje debe ser un número mayor que 0."
                        }
                        score == null || score < 0.0 || score > 10.0 -> {
                            errorText = "La nota debe estar entre 0 y 10."
                        }
                        totalPercentage + percentage > 100.0 -> {
                            errorText = "No puedes exceder el 100%. Te queda un $remainingPercentage% disponible."
                        }
                        // VALIDACIÓN: Evitar nombres repetidos
                        activities.any { it.name.equals(activityNameTrimmed, ignoreCase = true) } -> {
                            errorText = "Ya existe una actividad con ese nombre."
                        }
                        else -> {
                            repo.insertActivity(
                                ActivityItem(
                                    subjectId = subjectId,
                                    name = activityNameTrimmed,
                                    percentage = percentage,
                                    score = score,
                                    createdAt = currentDateTime()
                                )
                            )
                            activityName = ""
                            percentageText = ""
                            scoreText = ""
                            errorText = ""
                            loadActivities()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Text("Agregar actividad")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- LISTA DE ACTIVIDADES ---
            if (activities.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No tienes actividades registradas.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(activities) { activity ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(activity.name, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = "Valor: ${activity.percentage}% | Nota: ${activity.score}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "Aporte final: ${"%.2f".format((activity.score * activity.percentage) / 100.0)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
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