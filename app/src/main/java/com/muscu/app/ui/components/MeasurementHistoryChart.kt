package com.muscu.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    metric: MeasurementMetric,
    modifier: Modifier = Modifier
) {
    if (measurements.size < 2) {
        Box(modifier = modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
            Text("Pas assez de données pour afficher le graphique")
        }
        return
    }

    val values = measurements.map { metric.extract(it) }
    val minVal = values.minOrNull() ?: 0f
    val maxVal = values.maxOrNull() ?: 1f
    val range = max(maxVal - minVal, 0.01f)

    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Box(modifier = modifier.fillMaxWidth().height(220.dp).padding(8.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val paddingLeft = 40f
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

            // Axe Y labels
            val ySteps = 4
            for (i in 0..ySteps) {
                val yRatio = i.toFloat() / ySteps
                val yVal = minVal + (1f - yRatio) * range
                val yPos = 16f + yRatio * plotHeight
                drawLine(
                    color = onSurfaceVariant.copy(alpha = 0.15f),
                    start = Offset(paddingLeft, yPos),
                    end = Offset(width - 16f, yPos),
                    strokeWidth = 1f
                )
            }

            // Path
            val path = Path()
            val points = values.mapIndexed { index, value ->
                val x = paddingLeft + (index.toFloat() / (values.size - 1).coerceAtLeast(1)) * plotWidth
                val yRatio = (value - minVal) / range
                val y = 16f + (1f - yRatio) * plotHeight
                Offset(x, y)
            }

            if (points.isNotEmpty()) {
                path.moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    path.lineTo(points[i].x, points[i].y)
                }
            }

            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )

            // Points
            points.forEach { point ->
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = point
                )
                drawCircle(
                    color = primaryColor,
                    radius = 5f,
                    center = point,
                    style = Stroke(width = 2f)
                )
            }
        }

        // Labels
        val formatter = DateTimeFormatter.ofPattern("dd/MM")
        val zone = ZoneId.systemDefault()
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomStart).padding(start = 40.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            measurements.take(3).forEach { m ->
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

enum class MeasurementMetric(val label: String, val unit: String) {
    WEIGHT("Poids", "kg"),
    BODY_FAT("% Graisse", "%"),
    MUSCLE("% Muscle", "%"),
    CHEST("Poitrine", "cm"),
    ARMS("Bras", "cm"),
    WAIST("Taille", "cm"),
    HIPS("Hanches", "cm"),
    THIGH("Cuisse", "cm"),
    CALF("Mollet", "cm");

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
