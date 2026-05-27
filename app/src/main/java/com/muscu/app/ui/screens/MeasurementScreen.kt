package com.muscu.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.muscu.app.R
import com.muscu.app.data.model.Measurement
import com.muscu.app.viewmodel.MeasurementViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MeasurementScreen(viewModel: MeasurementViewModel, onNavigateToHistory: () -> Unit = {}) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Mensurations",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f).testTag("measurements_title")
            )
            IconButton(onClick = onNavigateToHistory) {
                Icon(Icons.Default.History, contentDescription = "Historique")
            }
        }

        // Schéma corporel SVG
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Schéma des points de mesure",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.testTag("body_diagram_title")
                )
                Spacer(Modifier.height(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.body_diagram),
                    contentDescription = "Schéma des points de mesure corporelle",
                    modifier = Modifier.size(220.dp)
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LegendDot(color = androidx.compose.ui.graphics.Color(0xFFE53935), label = "Poitrine")
                    LegendDot(color = androidx.compose.ui.graphics.Color(0xFF1E88E5), label = "Bras")
                    LegendDot(color = androidx.compose.ui.graphics.Color(0xFFFDD835), label = "Taille")
                }
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LegendDot(color = androidx.compose.ui.graphics.Color(0xFF43A047), label = "Hanches")
                    LegendDot(color = androidx.compose.ui.graphics.Color(0xFFFB8C00), label = "Cuisse")
                    LegendDot(color = androidx.compose.ui.graphics.Color(0xFF8E24AA), label = "Mollet")
                }
            }
        }

        // Composition corporelle
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        "Composition corporelle",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row {
                    MeasurementField(
                        label = "Poids (kg)",
                        value = state.weightKg,
                        onChange = viewModel::updateWeight,
                        modifier = Modifier.weight(1f),
                        helperText = "Masse totale"
                    )
                    MeasurementField(
                        label = "% Graisse",
                        value = state.bodyFatPercent,
                        onChange = viewModel::updateBodyFat,
                        modifier = Modifier.weight(1f),
                        helperText = "Masse grasse"
                    )
                    MeasurementField(
                        label = "% Muscle",
                        value = state.musclePercent,
                        onChange = viewModel::updateMuscle,
                        modifier = Modifier.weight(1f),
                        helperText = "Masse musculaire"
                    )
                }
            }
        }

        // Mensurations linéaires
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Straighten, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        "Mensurations",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 8.dp).testTag("measurements_form_title")
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row {
                    MeasurementField("Poitrine (cm)", state.chestCm, viewModel::updateChest, Modifier.weight(1f))
                    MeasurementField("Bras (cm)", state.armsCm, viewModel::updateArms, Modifier.weight(1f))
                }
                Row {
                    MeasurementField("Taille (cm)", state.waistCm, viewModel::updateWaist, Modifier.weight(1f))
                    MeasurementField("Hanches (cm)", state.hipsCm, viewModel::updateHips, Modifier.weight(1f))
                }
                Row {
                    MeasurementField("Cuisse (cm)", state.thighsCm, viewModel::updateThighs, Modifier.weight(1f))
                    MeasurementField("Mollet (cm)", state.calvesCm, viewModel::updateCalves, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth().testTag("save_measurement_button")) {
                    Text("Enregistrer")
                }
                if (state.saved) {
                    Text("Enregistré !", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Tableau de suivi
        if (state.measurements.isNotEmpty()) {
            MeasurementHistoryTable(measurements = state.measurements)
        }
    }
}

@Composable
private fun LegendDot(color: androidx.compose.ui.graphics.Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(10.dp)) {
            drawCircle(color)
        }
        Spacer(Modifier.padding(horizontal = 4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun MeasurementField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    helperText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onChange(it.filter { c -> c.isDigit() || c == '.' }) },
        label = { Text(label, maxLines = 1) },
        supportingText = helperText?.let { { Text(it, style = MaterialTheme.typography.bodySmall) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier.padding(horizontal = 4.dp),
        singleLine = true
    )
}

@Composable
private fun MeasurementHistoryTable(measurements: List<Measurement>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Accessibility, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    "Historique de suivi",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(Modifier.height(12.dp))

            // En-têtes
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Date", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                Text("Poids", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                Text("Graisse", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                Text("Muscle", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                Text("Taille", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(4.dp))

            val dateFormat = SimpleDateFormat("dd/MM", Locale.FRANCE)
            val reversed = measurements.reversed()

            reversed.forEachIndexed { index, m ->
                val prev = reversed.getOrNull(index + 1)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    Text(dateFormat.format(Date(m.dateTimestamp)), modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.bodySmall)
                    DeltaValue(current = m.weightKg, previous = prev?.weightKg, modifier = Modifier.weight(1f), suffix = "kg")
                    DeltaValue(current = m.bodyFatPercent, previous = prev?.bodyFatPercent, modifier = Modifier.weight(1f), suffix = "%")
                    DeltaValue(current = m.musclePercent, previous = prev?.musclePercent, modifier = Modifier.weight(1f), suffix = "%")
                    DeltaValue(current = m.waistCm, previous = prev?.waistCm, modifier = Modifier.weight(1f), suffix = "cm")
                }
            }
        }
    }
}

@Composable
private fun DeltaValue(current: Float?, previous: Float?, modifier: Modifier, suffix: String) {
    val text = current?.let { "${it}$suffix" } ?: "-"
    val color = when {
        current == null || previous == null -> MaterialTheme.colorScheme.onSurface
        current < previous -> MaterialTheme.colorScheme.error      // diminution = rouge pour poids/taille
        current > previous -> MaterialTheme.colorScheme.primary    // augmentation = vert
        else -> MaterialTheme.colorScheme.onSurface
    }
    // Pour % muscle, la logique est inversée (plus = mieux)
    val finalColor = if (suffix == "%" && current != null && previous != null) {
        when {
            current > previous -> MaterialTheme.colorScheme.primary
            current < previous -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSurface
        }
    } else color

    Text(text, modifier = modifier, style = MaterialTheme.typography.bodySmall, color = finalColor)
}
