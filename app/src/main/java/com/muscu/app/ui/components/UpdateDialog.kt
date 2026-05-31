package com.muscu.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow
import com.muscu.app.data.remote.GitHubRelease

@Composable
fun UpdateDialog(
    release: GitHubRelease,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Mise à jour disponible") },
        text = {
            Text(
                text = buildString {
                    appendLine(release.name)
                    appendLine()
                    release.body?.let { body ->
                        append(body.trim().take(500))
                        if (body.length > 500) append("…")
                    } ?: append("Aucune note de release.")
                },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 12,
                overflow = TextOverflow.Ellipsis
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Télécharger")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Plus tard")
            }
        }
    )
}
