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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.muscu.app.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Réglages",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Profil
        Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Profil", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.weightKg,
                    onValueChange = viewModel::updateWeight,
                    label = { Text("Poids actuel (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("weightInput"),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.proteinTarget,
                    onValueChange = viewModel::updateTarget,
                    label = { Text("Objectif protéines (g/jour)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("proteinTargetInput"),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) {
                    Text("Enregistrer")
                }
                if (state.saved) {
                    Spacer(Modifier.height(8.dp))
                    Text("Enregistré !", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Automatisation séance
        Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Automatisation séance", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Démarrage auto série suivante", modifier = Modifier.weight(1f))
                    Switch(checked = state.autoStartNextSet, onCheckedChange = viewModel::toggleAutoStart)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Pré-remplir reps/poids", modifier = Modifier.weight(1f))
                    Switch(checked = state.autoFillRepsWeight, onCheckedChange = viewModel::toggleAutoFill)
                }

                Spacer(Modifier.height(8.dp))
                Text("Valeurs par défaut", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Row {
                    OutlinedTextField(
                        value = state.defaultReps,
                        onValueChange = viewModel::updateDefaultReps,
                        label = { Text("Reps") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).padding(end = 4.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.defaultWeightKg,
                        onValueChange = viewModel::updateDefaultWeight,
                        label = { Text("Charge (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).padding(start = 4.dp),
                        singleLine = true
                    )
                }

                Spacer(Modifier.height(12.dp))
                Text("Profil de dynamique", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "puissance" to "Puissance",
                        "force" to "Force",
                        "hypertrophie" to "Hypertrophie",
                        "endurance" to "Endurance"
                    ).forEach { (key, label) ->
                        FilterChip(
                            selected = state.tempoProfile == key,
                            onClick = { viewModel.updateTempoProfile(key) },
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text("Accent", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "neutre" to "Neutre",
                        "excentrique" to "Excentrique",
                        "concentrique" to "Concentrique"
                    ).forEach { (key, label) ->
                        FilterChip(
                            selected = state.tempoAccent == key,
                            onClick = { viewModel.updateTempoAccent(key) },
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))
                Row {
                    OutlinedTextField(value = state.tempoEccentric, onValueChange = viewModel::updateTempoEccentric, label = { Text("Descente") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f).padding(end = 2.dp), singleLine = true)
                    OutlinedTextField(value = state.tempoIsometricBottom, onValueChange = viewModel::updateTempoIsoBottom, label = { Text("Pause bas") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f).padding(horizontal = 2.dp), singleLine = true)
                    OutlinedTextField(value = state.tempoConcentric, onValueChange = viewModel::updateTempoConcentric, label = { Text("Montée") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f).padding(horizontal = 2.dp), singleLine = true)
                    OutlinedTextField(value = state.tempoIsometricTop, onValueChange = viewModel::updateTempoIsoTop, label = { Text("Pause haut") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f).padding(start = 2.dp), singleLine = true)
                }

                state.tempoAnalysis?.let { analysis ->
                    Spacer(Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "${analysis.tempoCode} — ${analysis.objectiveLabel}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "TUT : ${analysis.tutPerRepSeconds.toInt()}s/rép • ${analysis.tutPerSetSeconds.toInt()}s/série",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = analysis.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("Compte à rebours préparation (sec)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = state.prepCountdownSeconds,
                    onValueChange = viewModel::updatePrepCountdown,
                    label = { Text("Avant chaque série") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))
                Button(onClick = viewModel::saveAutomation, modifier = Modifier.fillMaxWidth()) {
                    Text("Enregistrer automation")
                }
                if (state.automationSaved) {
                    Spacer(Modifier.height(8.dp))
                    Text("Automation enregistrée !", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Paramètres avancés
        Card(elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Paramètres avancés", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(Modifier.height(12.dp))

                Text("Temps de récupération par défaut (sec)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row {
                    OutlinedTextField(value = state.moderateRestSeconds, onValueChange = viewModel::updateModerateRest, label = { Text("Modérée") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f).padding(end = 4.dp), singleLine = true)
                    OutlinedTextField(value = state.lightRestSeconds, onValueChange = viewModel::updateLightRest, label = { Text("Légère") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f).padding(horizontal = 4.dp), singleLine = true)
                    OutlinedTextField(value = state.bodyweightRestSeconds, onValueChange = viewModel::updateBodyweightRest, label = { Text("Corporel") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f).padding(start = 4.dp), singleLine = true)
                }

                Spacer(Modifier.height(12.dp))
                Text("Range slider récupération (sec)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row {
                    OutlinedTextField(value = state.minRestSeconds, onValueChange = viewModel::updateMinRest, label = { Text("Min") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f).padding(end = 4.dp), singleLine = true)
                    OutlinedTextField(value = state.maxRestSeconds, onValueChange = viewModel::updateMaxRest, label = { Text("Max") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f).padding(start = 4.dp), singleLine = true)
                }

                Spacer(Modifier.height(12.dp))
                Text("Audio timer (ms / volume)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row {
                    OutlinedTextField(value = state.beepDurationMs, onValueChange = viewModel::updateBeepDuration, label = { Text("Bip") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f).padding(end = 4.dp), singleLine = true)
                    OutlinedTextField(value = state.finalBeepDurationMs, onValueChange = viewModel::updateFinalBeepDuration, label = { Text("Bip final") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f).padding(horizontal = 4.dp), singleLine = true)
                    OutlinedTextField(value = state.toneVolume, onValueChange = viewModel::updateToneVolume, label = { Text("Volume") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f).padding(start = 4.dp), singleLine = true)
                }

                Spacer(Modifier.height(12.dp))
                Button(onClick = viewModel::saveAdvanced, modifier = Modifier.fillMaxWidth()) {
                    Text("Enregistrer paramètres avancés")
                }
                if (state.advancedSaved) {
                    Spacer(Modifier.height(8.dp))
                    Text("Paramètres avancés enregistrés !", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // À propos
        Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("À propos", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text("Programme adapté à ton matériel : banc + haltères.")
                Spacer(Modifier.height(4.dp))
                Text("Suivi de force, mensurations et feedback de séance.")
            }
        }
    }
}
