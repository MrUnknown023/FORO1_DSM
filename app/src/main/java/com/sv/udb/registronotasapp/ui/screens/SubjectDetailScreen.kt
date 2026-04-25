package com.sv.udb.registronotasapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sv.udb.registronotasapp.data.model.ActivityItem
import com.sv.udb.registronotasapp.data.repository.ActivityRepository
import com.sv.udb.registronotasapp.utils.calculateFinalGrade
import com.sv.udb.registronotasapp.utils.calculateRemainingPercentage
import com.sv.udb.registronotasapp.utils.calculateTotalPercentage
import com.sv.udb.registronotasapp.utils.currentDateTime
import com.sv.udb.registronotasapp.utils.getStatus

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

    var activityToEdit by remember { mutableStateOf<ActivityItem?>(null) }
    var activityToDelete by remember { mutableStateOf<ActivityItem?>(null) }

    fun loadActivities() {
        activities = repo.getActivitiesBySubject(subjectId)
    }

    LaunchedEffect(Unit) {
        loadActivities()
    }

    // Cálculos reactivos
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

            // --- MENSAJE VISUAL: Materia incompleta ---
            if (totalPercentage < 100.0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Warning, contentDescription = "Advertencia", tint = MaterialTheme.colorScheme.onTertiaryContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aún falta registrar el ${"%.2f".format(remainingPercentage)}% de la materia.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            // --- RESUMEN DE NOTAS ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Porcentaje acumulado:", style = MaterialTheme.typography.bodyMedium)
                        Text("${"%.2f".format(totalPercentage)}%", fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Porcentaje faltante:", style = MaterialTheme.typography.bodyMedium)
                        Text("${"%.2f".format(remainingPercentage)}%", fontWeight = FontWeight.Bold)
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Nota final actual:", style = MaterialTheme.typography.titleMedium)
                        Text("${"%.2f".format(finalGrade)}", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Estado:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = status,
                            fontWeight = FontWeight.Bold,
                            color = if (status.equals("Aprobado", ignoreCase = true))
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // --- FORMULARIO DE NUEVA ACTIVIDAD ---
            OutlinedTextField(
                value = activityName,
                onValueChange = { activityName = it; errorText = "" },
                label = { Text("Nueva Actividad") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = percentageText,
                    onValueChange = { percentageText = it; errorText = "" },
                    label = { Text("% (Ej: 25)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = scoreText,
                    onValueChange = { scoreText = it; errorText = "" },
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
                        activityNameTrimmed.isBlank() -> errorText = "El nombre es obligatorio."
                        percentage == null || percentage <= 0.0 -> errorText = "El porcentaje debe ser mayor a 0."
                        score == null || score < 0.0 || score > 10.0 -> errorText = "La nota debe estar entre 0 y 10."
                        totalPercentage + percentage > 100.0 -> errorText = "Excedes el 100%. Te queda un ${"%.2f".format(remainingPercentage)}%."
                        activities.any { it.name.equals(activityNameTrimmed, ignoreCase = true) } -> errorText = "Ya existe esa actividad."
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
                            activityName = ""; percentageText = ""; scoreText = ""; errorText = ""
                            loadActivities()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                Text("Agregar actividad")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- LISTA DE ACTIVIDADES ---
            if (activities.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes actividades registradas.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(activities) { activity ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(activity.name, style = MaterialTheme.typography.titleMedium)
                                    Text("Valor: ${"%.2f".format(activity.percentage)}% | Nota: ${"%.2f".format(activity.score)}", style = MaterialTheme.typography.bodySmall)
                                    Text(
                                        "Aporte final: ${"%.2f".format((activity.score * activity.percentage) / 100.0)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { activityToEdit = activity }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.secondary)
                                }
                                IconButton(onClick = { activityToDelete = activity }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIÁLOGO DE EDICIÓN ---
    if (activityToEdit != null) {
        var editName by remember { mutableStateOf(activityToEdit!!.name) }
        var editPercentageText by remember { mutableStateOf(activityToEdit!!.percentage.toString()) }
        var editScoreText by remember { mutableStateOf(activityToEdit!!.score.toString()) }
        var editErrorText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { activityToEdit = null },
            title = { Text("Editar Actividad") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it; editErrorText = "" },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editPercentageText,
                        onValueChange = { editPercentageText = it; editErrorText = "" },
                        label = { Text("Porcentaje") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editScoreText,
                        onValueChange = { editScoreText = it; editErrorText = "" },
                        label = { Text("Nota") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        singleLine = true
                    )
                    if (editErrorText.isNotBlank()) {
                        Text(text = editErrorText, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val p = editPercentageText.toDoubleOrNull()
                    val s = editScoreText.toDoubleOrNull()
                    val currentOtherPercentage = totalPercentage - activityToEdit!!.percentage

                    when {
                        editName.trim().isBlank() -> editErrorText = "Nombre obligatorio."
                        p == null || p <= 0.0 -> editErrorText = "El porcentaje debe ser > 0."
                        s == null || s < 0.0 || s > 10.0 -> editErrorText = "La nota debe estar entre 0 y 10."
                        currentOtherPercentage + p > 100.0 -> editErrorText = "Excedes el 100%. Disponible: ${"%.2f".format(100.0 - currentOtherPercentage)}%"
                        else -> {
                            repo.updateActivity(
                                activityToEdit!!.copy(
                                    name = editName.trim(),
                                    percentage = p,
                                    score = s
                                )
                            )
                            activityToEdit = null
                            loadActivities()
                        }
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { activityToEdit = null }) { Text("Cancelar") }
            }
        )
    }

    // --- DIÁLOGO DE ELIMINACIÓN ---
    if (activityToDelete != null) {
        AlertDialog(
            onDismissRequest = { activityToDelete = null },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar la actividad '${activityToDelete!!.name}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        repo.deleteActivity(activityToDelete!!.id)
                        activityToDelete = null
                        loadActivities()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { activityToDelete = null }) { Text("Cancelar") }
            }
        )
    }
}