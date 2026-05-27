package com.muscu.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MeasurementLineChart(
    data: List<Pair<Long, Float>>,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(220.dp)
) {
    if (data.size < 2) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("Pas assez de données", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    val textMeasurer = rememberTextMeasurer()
    val sorted = data.sortedBy { it.first }
    val minVal = sorted.minOf { it.second }
    val maxVal = sorted.maxOf { it.second }
    val range = if (maxVal == minVal) 1f else maxVal - minVal
    val paddingHorizontal = 48f
    val paddingVertical = 32f
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .pointerInput(data) {
                    detectTapGestures { offset ->
                        val width = size.width.toFloat()
                        val stepX = (width - 2 * paddingHorizontal) / (sorted.size - 1).coerceAtLeast(1)
                        val index = ((offset.x - paddingHorizontal) / stepX).toInt()
                            .coerceIn(0, sorted.size - 1)
                        selectedIndex = if (selectedIndex == index) null else index
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val chartWidth = width - 2 * paddingHorizontal
            val chartHeight = height - 2 * paddingVertical

            // Axe Y labels
            val ySteps = 4
            repeat(ySteps + 1) { i ->
                val yValue = minVal + (range * i / ySteps)
                val yPos = height - paddingVertical - (chartHeight * i / ySteps)
                drawText(
                    textMeasurer = textMeasurer,
                    text = "%.1f".format(yValue),
                    topLeft = Offset(4f, yPos - 8f),
                    style = TextStyle(color = Color.Gray, fontSize = 10.sp)
                )
                drawLine(
                    color = Color.LightGray,
                    start = Offset(paddingHorizontal, yPos),
                    end = Offset(width - paddingHorizontal, yPos),
                    strokeWidth = 1f
                )
            }

            // Points
            val points = sorted.mapIndexed { index, (timestamp, value) ->
                val x = paddingHorizontal + (chartWidth * index / (sorted.size - 1).coerceAtLeast(1))
                val y = height - paddingVertical - (chartHeight * (value - minVal) / range)
                Offset(x, y)
            }

            // Gradient fill area
            val fillPath = Path().apply {
                moveTo(points.first().x, height - paddingVertical)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, height - paddingVertical)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(color.copy(alpha = 0.3f), Color.Transparent),
                    startY = paddingVertical,
                    endY = height - paddingVertical
                )
            )

            // Line
            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }
            drawPath(
                path = linePath,
                color = color,
                style = Stroke(width = 3f)
            )

            // Points
            points.forEachIndexed { index, point ->
                val isSelected = selectedIndex == index
                drawCircle(
                    color = if (isSelected) color else Color.White,
                    radius = if (isSelected) 8f else 5f,
                    center = point
                )
                drawCircle(
                    color = color,
                    radius = if (isSelected) 8f else 5f,
                    center = point,
                    style = Stroke(width = 2f)
                )
            }

            // X axis date labels (show first, middle, last)
            val dateFormat = SimpleDateFormat("dd/MM", Locale.FRANCE)
            listOf(0, sorted.size / 2, sorted.size - 1).forEach { i ->
                val x = paddingHorizontal + (chartWidth * i / (sorted.size - 1).coerceAtLeast(1))
                val dateStr = dateFormat.format(Date(sorted[i].first))
                drawText(
                    textMeasurer = textMeasurer,
                    text = dateStr,
                    topLeft = Offset(x - 16f, height - paddingVertical + 8f),
                    style = TextStyle(color = Color.Gray, fontSize = 10.sp)
                )
            }
        }

        // Tooltip
        selectedIndex?.let { idx ->
            val (timestamp, value) = sorted[idx]
            val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.FRANCE).format(Date(timestamp))
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = "$dateStr : ${"%.1f".format(value)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
