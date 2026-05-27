package com.muscu.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.muscu.app.domain.timer.GuidePhase
import com.muscu.app.domain.timer.GuideState
import kotlin.math.PI
import kotlin.math.cos

@Composable
fun SinusoidalGuide(
    guideState: GuideState,
    speedMultiplier: Float,
    tempoCode: String,
    objectiveLabel: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Guide de tempo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Tempo $tempoCode — $objectiveLabel",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            SineWaveCanvas(
                phase = guideState.phase,
                phaseProgress = guideState.phaseProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(Modifier.height(12.dp))

            val phaseLabel = when (guideState.phase) {
                GuidePhase.Eccentric -> "Descente (excentrique)"
                GuidePhase.IsometricBottom -> "Pause bas"
                GuidePhase.Concentric -> "Montée (concentrique)"
                GuidePhase.IsometricTop -> "Contraction haute"
                else -> "Prêt"
            }
            val phaseColor = when (guideState.phase) {
                GuidePhase.Eccentric -> Color(0xFFD32F2F)
                GuidePhase.IsometricBottom -> Color(0xFFFBC02D)
                GuidePhase.Concentric -> Color(0xFF388E3C)
                GuidePhase.IsometricTop -> Color(0xFF1976D2)
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }

            Text(
                text = phaseLabel,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = phaseColor
            )

            if (speedMultiplier != 1.0f) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Vitesse : ${speedMultiplier}x",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SineWaveCanvas(
    phase: GuidePhase,
    phaseProgress: Float,
    modifier: Modifier = Modifier
) {
    val activeColor = when (phase) {
        GuidePhase.Eccentric -> Color(0xFFD32F2F)
        GuidePhase.IsometricBottom -> Color(0xFFFBC02D)
        GuidePhase.Concentric -> Color(0xFF388E3C)
        GuidePhase.IsometricTop -> Color(0xFF1976D2)
        else -> MaterialTheme.colorScheme.primary
    }
    val inactiveCurveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f
        val amplitude = height * 0.35f
        val padding = 16f
        val plotWidth = width - 2 * padding

        // Map physical phase to position on the cosine curve:
        // 0.0 = top (arms extended), 0.5 = bottom (chest), 1.0 = top again
        val curveProgress = when (phase) {
            GuidePhase.Eccentric -> phaseProgress * 0.5f          // top → bottom
            GuidePhase.IsometricBottom -> 0.5f                    // hold at bottom
            GuidePhase.Concentric -> 0.5f + phaseProgress * 0.5f  // bottom → top
            GuidePhase.IsometricTop -> 1.0f                       // hold at top
            else -> 0f
        }

        // Full static cosine curve (one period)
        // y = centerY - cos(t * 2π) * amplitude
        // Canvas Y grows downward, so minus flips it:
        // t=0 → top, t=0.5 → bottom, t=1.0 → top
        val fullPath = Path()
        val steps = 200
        for (i in 0..steps) {
            val t = i / steps.toFloat()
            val x = padding + t * plotWidth
            val y = centerY - cos(t * 2f * PI.toFloat()) * amplitude
            if (i == 0) fullPath.moveTo(x, y) else fullPath.lineTo(x, y)
        }

        drawPath(
            path = fullPath,
            color = inactiveCurveColor,
            style = Stroke(width = 3f, cap = StrokeCap.Round)
        )

        // Active portion: from start to current curveProgress
        if (curveProgress > 0f && phase != GuidePhase.Idle) {
            val activePath = Path()
            val activeSteps = (steps * curveProgress).toInt().coerceAtLeast(1)
            for (i in 0..activeSteps) {
                val t = i / steps.toFloat()
                val x = padding + t * plotWidth
                val y = centerY - cos(t * 2f * PI.toFloat()) * amplitude
                if (i == 0) activePath.moveTo(x, y) else activePath.lineTo(x, y)
            }
            drawPath(
                path = activePath,
                color = activeColor,
                style = Stroke(width = 4f, cap = StrokeCap.Round)
            )
        }

        // Moving dot
        val dotX = padding + curveProgress * plotWidth
        val dotY = centerY - cos(curveProgress * 2f * PI.toFloat()) * amplitude

        drawCircle(
            color = Color.White,
            radius = 10f,
            center = Offset(dotX, dotY)
        )
        drawCircle(
            color = activeColor,
            radius = 10f,
            center = Offset(dotX, dotY),
            style = Stroke(width = 3f)
        )
    }
}
