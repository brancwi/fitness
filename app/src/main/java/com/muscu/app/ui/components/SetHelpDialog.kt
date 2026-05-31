package com.muscu.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.muscu.app.data.model.PerformedSet

@Composable
fun SetHelpDialog(
    lastSet: PerformedSet?,
    targetRepsMin: Int,
    targetRepsMax: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Conseil pour cette série") },
        text = {
            Text(
                text = buildString {
                    appendLine("Objectif : $targetRepsMin–$targetRepsMax répétitions")
                    appendLine()
                    if (lastSet != null) {
                        appendLine("Dernière séance : ${lastSet.weightKg ?: "-"} kg × ${lastSet.reps ?: "-"} reps")
                        lastSet.difficultyRating?.let { rating ->
                            val label = when (rating) {
                                1 -> "Trop lourd"
                                2 -> "Difficile"
                                3 -> "Correct"
                                4 -> "Assez facile"
                                5 -> "Trop facile"
                                else -> ""
                            }
                            appendLine("Ressenti : $label ($rating/5)")
                        }
                        appendLine()
                    }
                    appendLine("💡 Rappel : arrête-toi quand tu penses pouvoir encore faire 1 ou 2 répétitions (RIR 1-2).")
                    appendLine()
                    appendLine("Si tu dépasses la fourchette haute → augmente de 5-10%.")
                    appendLine("Si tu es en dessous → diminue ou vérifie ta technique.")
                },
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Compris")
            }
        }
    )
}
