package com.muscu.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Crédits") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Illustrations
            CreditSection(
                icon = Icons.Default.Brush,
                title = "Illustrations d'exercices",
                items = listOf(
                    CreditItem(
                        name = "Everkinetic / OpenTraining",
                        description = "Illustrations vectorielles des exercices de musculation",
                        license = "CC-BY-SA 3.0",
                        url = "https://github.com/chaosbastler/opentraining-exercises"
                    )
                )
            )

            // Framework & langage
            CreditSection(
                icon = Icons.Default.Code,
                title = "Framework & langage",
                items = listOf(
                    CreditItem(
                        name = "Kotlin",
                        description = "Langage de programmation",
                        license = "Apache 2.0",
                        url = "https://kotlinlang.org/"
                    ),
                    CreditItem(
                        name = "Android Jetpack Compose",
                        description = "UI toolkit déclaratif",
                        license = "Apache 2.0",
                        url = "https://developer.android.com/jetpack/compose"
                    ),
                    CreditItem(
                        name = "Android Room",
                        description = "Persistance de données",
                        license = "Apache 2.0",
                        url = "https://developer.android.com/jetpack/androidx/releases/room"
                    ),
                    CreditItem(
                        name = "Navigation Compose",
                        description = "Navigation déclarative",
                        license = "Apache 2.0",
                        url = "https://developer.android.com/jetpack/androidx/releases/navigation"
                    )
                )
            )

            // Bibliothèques tierces
            CreditSection(
                icon = Icons.Default.Balance,
                title = "Bibliothèques open-source",
                items = listOf(
                    CreditItem(
                        name = "Coil",
                        description = "Chargement d'images (y compris SVG)",
                        license = "Apache 2.0",
                        url = "https://coil-kt.github.io/coil/"
                    ),
                    CreditItem(
                        name = "Material Icons",
                        description = "Icônes Material Design",
                        license = "Apache 2.0",
                        url = "https://fonts.google.com/icons"
                    )
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Cette application utilise des ressources open-source. Merci à toutes les communautés qui rendent cela possible.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private data class CreditItem(
    val name: String,
    val description: String,
    val license: String,
    val url: String
)

@Composable
private fun CreditSection(
    icon: ImageVector,
    title: String,
    items: List<CreditItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            items.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Licence : ${item.license}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = item.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}
