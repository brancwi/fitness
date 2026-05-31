package com.muscu.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProgressionChip(
    reps: Int?,
    targetRepsMin: Int,
    targetRepsMax: Int
) {
    val message = when {
        reps == null -> null
        reps > targetRepsMax -> "→ Augmente de 5-10%"
        reps < targetRepsMin -> "→ Diminue ou vérifie ta technique"
        else -> "→ Garde cette charge"
    } ?: return

    val color = when {
        reps == null -> MaterialTheme.colorScheme.primary
        reps > targetRepsMax -> Color(0xFF4CAF50)
        reps < targetRepsMin -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    AssistChip(
        onClick = { },
        label = { Text(message) },
        modifier = Modifier.padding(top = 4.dp),
        colors = AssistChipDefaults.assistChipColors(
            labelColor = color
        )
    )
}
