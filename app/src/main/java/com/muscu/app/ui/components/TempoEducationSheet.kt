package com.muscu.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TempoEducationSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Comprendre le tempo",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            EducationSection(
                title = "Qu'est-ce que le TUT ?",
                content = "Le Time Under Tension (TUT) est le temps total pendant lequel tes muscles sont sous tension pendant une série. C'est un indicateur clé de l'objectif d'entraînement."
            )

            EducationSection(
                title = "Les 4 phases du mouvement",
                content = "• Excentrique (descente) : allongement du muscle\n" +
                        "• Isométrique bas : pause en bas\n" +
                        "• Concentrique (montée) : raccourcissement du muscle\n" +
                        "• Isométrique haut : contraction maximale en haut"
            )

            EducationSection(
                title = "Objectifs selon le TUT",
                content = "• Explosif (<1s) : Puissance, fibres type II\n" +
                        "• 1–3s : Force maximale\n" +
                        "• 3–6s : Hypertrophie optimale\n" +
                        "• 6–10s : Hypertrophie avancée / TUT élevé\n" +
                        "• 10s+ : Endurance musculaire"
            )

            EducationSection(
                title = "TUT par série",
                content = "• Force : 2–20 secondes\n" +
                        "• Hypertrophie : 20–70 secondes\n" +
                        "• Endurance : 70 secondes et plus"
            )

            EducationSection(
                title = "Conseil pratique",
                content = "Pour l'hypertrophie, vise un tempo de 3-1-1-1 (3s descente, 1s pause, 1s montée, 1s squeeze). Ajuste avec les boutons de vitesse si besoin."
            )

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("J'ai compris")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun EducationSection(title: String, content: String) {
    Column {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
