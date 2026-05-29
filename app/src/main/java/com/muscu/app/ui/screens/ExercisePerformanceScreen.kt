package com.muscu.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muscu.app.data.model.PerformedSetWithDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePerformanceScreen(
    exerciseName: String,
    history: List<PerformedSetWithDate>,
    onBack: () -> Unit,
    onDeleteSet: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historique : $exerciseName") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (history.size < 2) {
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Pas assez d'historique pour cet exercice.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Complète au moins 2 séances pour voir l'évolution.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Graphique
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Évolution charge & répétitions",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        PerformanceChart(history = history)
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            LegendDot(color = Color(0xFF2196F3), label = "Charge (kg)")
                            LegendDot(color = Color(0xFFFF9800), label = "Répétitions")
                        }
                    }
                }

                // Tableau
                Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Historique complet",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        PerformanceTable(history = history, onDelete = onDeleteSet)
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(color = color, radius = size.minDimension / 2)
            }
        }
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun PerformanceChart(history: List<PerformedSetWithDate>) {
    val weights = history.map { it.set.weightKg ?: 0f }
    val reps = history.map { it.set.reps ?: 0 }

    val minWeight = weights.minOrNull() ?: 0f
    val maxWeight = weights.maxOrNull() ?: 1f
    val weightRange = max(maxWeight - minWeight, 0.01f)

    val minReps = reps.minOrNull() ?: 0
    val maxReps = reps.maxOrNull() ?: 1
    val repsRange = max((maxReps - minReps).toFloat(), 0.01f)

    val weightColor = Color(0xFF2196F3)
    val repsColor = Color(0xFFFF9800)
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Box(modifier = Modifier.fillMaxWidth().height(220.dp).padding(8.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val paddingLeft = 8f
            val paddingBottom = 30f
            val plotWidth = width - paddingLeft - 16f
            val plotHeight = height - paddingBottom - 16f

            drawLine(
                color = onSurfaceVariant.copy(alpha = 0.3f),
                start = Offset(paddingLeft, height - paddingBottom),
                end = Offset(width - 16f, height - paddingBottom),
                strokeWidth = 1f
            )
            drawLine(
                color = onSurfaceVariant.copy(alpha = 0.3f),
                start = Offset(paddingLeft, 16f),
                end = Offset(paddingLeft, height - paddingBottom),
                strokeWidth = 1f
            )

            // Grille
            val ySteps = 4
            for (i in 0..ySteps) {
                val yPos = 16f + (i.toFloat() / ySteps) * plotHeight
                drawLine(
                    color = onSurfaceVariant.copy(alpha = 0.1f),
                    start = Offset(paddingLeft, yPos),
                    end = Offset(width - 16f, yPos),
                    strokeWidth = 1f
                )
            }

            fun drawSeries(values: List<Float>, minVal: Float, range: Float, color: Color) {
                val points = values.mapIndexed { index, value ->
                    val x = paddingLeft + (index.toFloat() / (values.size - 1).coerceAtLeast(1)) * plotWidth
                    val yRatio = (value - minVal) / range
                    val y = 16f + (1f - yRatio) * plotHeight
                    Offset(x, y)
                }

                val path = Path()
                if (points.isNotEmpty()) {
                    path.moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        path.lineTo(points[i].x, points[i].y)
                    }
                }
                drawPath(path = path, color = color, style = Stroke(width = 2.5f, cap = StrokeCap.Round))

                points.forEach { point ->
                    drawCircle(color = Color.White, radius = 4f, center = point)
                    drawCircle(color = color, radius = 4f, center = point, style = Stroke(width = 2f))
                }
            }

            drawSeries(weights, minWeight, weightRange, weightColor)
            drawSeries(reps.map { it.toFloat() }, minReps.toFloat(), repsRange, repsColor)
        }

        // Dates X
        val dateFormat = SimpleDateFormat("dd/MM", Locale.FRANCE)
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart).padding(start = 8.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(history.firstOrNull(), history.getOrNull(history.size / 2), history.lastOrNull())
                .filterNotNull()
                .forEach { entry ->
                    Text(
                        text = dateFormat.format(Date(entry.sessionDate)),
                        style = TextStyle(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
        }
    }
}

@Composable
private fun PerformanceTable(history: List<PerformedSetWithDate>, onDelete: (String) -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
    var confirmDeleteId by remember { mutableStateOf<String?>(null) }

    Column {
        history.forEachIndexed { index, entry ->
            val prev = history.getOrNull(index - 1)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    dateFormat.format(Date(entry.sessionDate)),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1.5f)
                )
                Text(
                    "Série ${entry.set.setNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "${entry.set.reps ?: "—"} reps",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "${entry.set.weightKg?.let { "%.1f".format(it) } ?: "—"} kg",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { confirmDeleteId = entry.set.id },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            if (index < history.size - 1) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }

    // Confirmation dialog
    confirmDeleteId?.let { id ->
        AlertDialog(
            onDismissRequest = { confirmDeleteId = null },
            title = { Text("Supprimer ?") },
            text = { Text("Cette série sera définitivement supprimée.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(id)
                        confirmDeleteId = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteId = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}
