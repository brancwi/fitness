package com.muscu.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ExerciseRatingDialog(
    exerciseName: String,
    onConfirm: (Int) -> Unit,
    onSkip: () -> Unit
) {
    var rating by remember { mutableIntStateOf(0) }

    val label = when (rating) {
        1 -> "Trop lourd — impossible de finir"
        2 -> "Échec à la dernière rep"
        3 -> "1 rep de marge (RIR 1)"
        4 -> "2-3 reps de marge (RIR 2-3)"
        5 -> "Trop facile — 4+ reps de marge"
        else -> ""
    }

    val recommendation = when (rating) {
        1 -> "→ Diminue de 10% la prochaine fois"
        2 -> "→ Diminue de 5%"
        3 -> "→ Garde cette charge"
        4 -> "→ Garde ou augmente de 5%"
        5 -> "→ Augmente de 10%"
        else -> ""
    }

    AlertDialog(
        onDismissRequest = { },
        title = { Text("Comment s'est passé $exerciseName ?") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { star ->
                        IconButton(
                            onClick = { rating = star },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (star <= rating) Color(0xFFFFC107) else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (rating > 0) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = recommendation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(rating) },
                enabled = rating > 0
            ) {
                Text("Confirmer")
            }
        },
        dismissButton = {
            TextButton(onClick = onSkip) {
                Text("Passer")
            }
        }
    )
}
