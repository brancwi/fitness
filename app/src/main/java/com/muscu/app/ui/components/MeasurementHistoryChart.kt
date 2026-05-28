package com.muscu.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muscu.app.data.model.Measurement
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.max

@Composable
fun MeasurementHistoryChart(
    measurements: List<Measurement>,
    metrics: List<MeasurementMetric>,
    modifier: Modifier = Modifier
) {
    val sorted = remember(measurements) { measurements.sortedBy { it.dateTimestamp } }
    if (sorted.size < 2 || metrics.isEmpty()) {
        Box(modifier = modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
            Text("Pas assez de données pour afficher le graphique")
        }
        return
    }

    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Box(modifier = modifier.fillMaxWidth().height(240.dp).padding(8.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val paddingLeft = 8f
            val paddingBottom = 30f
            val plotWidth = width - paddingLeft - 16f
            val plotHeight = height - paddingBottom - 16f

            // Ligne de base
            drawLine(
                color = onSurfaceVariant.copy(alpha = 0.3f),
                start = Offset(paddingLeft, height - paddingBottom),
                end = Offset(width - 16f, height - paddingBottom),
                strokeWidth = 1f
            )

            // Ligne Y
            drawLine(
                color = onSurfaceVariant.copy(alpha = 0.3f),
                start = Offset(paddingLeft, 16f),
                end = Offset(paddingLeft, height - paddingBottom),
                strokeWidth = 1f
            )

            // Grille horizontale (sans valeurs, car échelles mixtes)
            val ySteps = 4
            for (i in 0..ySteps) {
                val yRatio = i.toFloat() / ySteps
                val yPos = 16f + yRatio * plotHeight
                drawLine(
                    color = onSurfaceVariant.copy(alpha = 0.1f),
                    start = Offset(paddingLeft, yPos),
                    end = Offset(width - 16f, yPos),
                    strokeWidth = 1f
                )
            }

            // Dessiner chaque métrique normalisée entre 0 et 1
            metrics.forEach { metric ->
                val values = sorted.map { metric.extract(it) }
                val minVal = values.minOrNull() ?: 0f
                val maxVal = values.maxOrNull() ?: 1f
                val range = max(maxVal - minVal, 0.01f)
                val color = metric.color

                val points = values.mapIndexed { index, value ->
                    val x = paddingLeft + (index.toFloat() / (values.size - 1).coerceAtLeast(1)) * plotWidth
                    val yRatio = (value - minVal) / range
                    val y = 16f + (1f - yRatio) * plotHeight
                    Offset(x, y)
                }

                // Path
                val path = Path()
                if (points.isNotEmpty()) {
                    path.moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        path.lineTo(points[i].x, points[i].y)
                    }
                }

                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                )

                // Points
                points.forEach { point ->
                    drawCircle(
                        color = Color.White,
                        radius = 4f,
                        center = point
                    )
                    drawCircle(
                        color = color,
                        radius = 4f,
                        center = point,
                        style = Stroke(width = 2f)
                    )
                }
            }
        }

        // Labels X (dates)
        val formatter = DateTimeFormatter.ofPattern("dd/MM")
        val zone = ZoneId.systemDefault()
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart).padding(start = 8.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(sorted.firstOrNull(), sorted.getOrNull(sorted.size / 2), sorted.lastOrNull())
                .filterNotNull()
                .forEach { m ->
                val dateStr = Instant.ofEpochMilli(m.dateTimestamp)
                    .atZone(zone)
                    .toLocalDate()
                    .format(formatter)
                Text(
                    text = dateStr,
                    style = TextStyle(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }
    }
}

enum class MeasurementMetric(val label: String, val unit: String, val color: Color) {
    WEIGHT("Poids", "kg", Color(0xFF2196F3)),
    BODY_FAT("% Graisse", "%", Color(0xFFFF9800)),
    MUSCLE("% Muscle", "%", Color(0xFF4CAF50)),
    CHEST("Poitrine", "cm", Color(0xFFE91E63)),
    ARMS("Bras", "cm", Color(0xFF9C27B0)),
    WAIST("Taille", "cm", Color(0xFF00BCD4)),
    HIPS("Hanches", "cm", Color(0xFF795548)),
    THIGH("Cuisse", "cm", Color(0xFFFFEB3B)),
    CALF("Mollet", "cm", Color(0xFF607D8B));

    fun extract(m: Measurement): Float = when (this) {
        WEIGHT -> m.weightKg ?: 0f
        BODY_FAT -> m.bodyFatPercent ?: 0f
        MUSCLE -> m.musclePercent ?: 0f
        CHEST -> m.chestCm ?: 0f
        ARMS -> m.armsCm ?: 0f
        WAIST -> m.waistCm ?: 0f
        HIPS -> m.hipsCm ?: 0f
        THIGH -> m.thighsCm ?: 0f
        CALF -> m.calvesCm ?: 0f
    }
}
