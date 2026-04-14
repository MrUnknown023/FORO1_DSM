package com.sv.udb.registronotasapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
@Composable
fun SubjectDetailScreen(
    subjectId: Long
) {
    val context = LocalContext.current
    val repo = remember { ActivityRepository(context) }

    var activities by remember { mutableStateOf(listOf<ActivityItem>()) }

    var activityName by remember { mutableStateOf("") }
    var percentageText by remember { mutableStateOf("") }
    var scoreText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Detalle de materia", style = MaterialTheme.typography.headlineMedium)

        Text(
            text = "Porcentaje acumulado: ${"%.2f".format(totalPercentage)}%",
            modifier = Modifier.padding(top = 12.dp)
        )

        Text(
            text = "Porcentaje faltante: ${"%.2f".format(remainingPercentage)}%",
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = "Nota final actual: ${"%.2f".format(finalGrade)}",
            modifier = Modifier.padding(top = 4.dp)
        )

        Text(
            text = "Estado: $status",
            modifier = Modifier.padding(top = 4.dp)
        )

        OutlinedTextField(
            value = activityName,
            onValueChange = { activityName = it },
            label = { Text("Actividad") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = percentageText,
                onValueChange = { percentageText = it },
                label = { Text("%") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = scoreText,
                onValueChange = { scoreText = it },
                label = { Text("Nota") },
                modifier = Modifier.weight(1f)
            )
        }

        if (error.isNotBlank()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                val percentage = percentageText.toDoubleOrNull()
                val score = scoreText.toDoubleOrNull()

                when {
                    activityName.isBlank() -> {
                        error = "El nombre de la actividad es obligatorio."
                    }
                    percentage == null || percentage <= 0.0 -> {
                        error = "El porcentaje debe ser mayor que 0."
                    }
                    score == null || score < 0.0 || score > 10.0 -> {
                        error = "La nota debe estar entre 0 y 10."
                    }
                    totalPercentage + percentage > 100.0 -> {
                        error = "La suma de porcentajes no puede superar 100%."
                    }
                    else -> {
                        repo.insertActivity(
                            ActivityItem(
                                subjectId = subjectId,
                                name = activityName,
                                percentage = percentage,
                                score = score,
                                createdAt = currentDateTime()
                            )
                        )
                        activityName = ""
                        percentageText = ""
                        scoreText = ""
                        error = ""
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

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(activities) { activity ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(activity.name, style = MaterialTheme.typography.titleMedium)
                        Text("Porcentaje: ${activity.percentage}%")
                        Text("Nota: ${activity.score}")
                        Text(
                            "Aporte: ${"%.2f".format((activity.score * activity.percentage) / 100.0)}"
                        )
                    }
                }
            }
        }
    }
}