package com.muscu.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.muscu.app.data.model.Measurement
import com.muscu.app.ui.components.MeasurementHistoryChart
import com.muscu.app.ui.components.MeasurementMetric
import com.muscu.app.viewmodel.MeasurementViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MeasurementHistoryScreen(
    viewModel: MeasurementViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val selectedMetric = remember { mutableStateOf(MeasurementMetric.WEIGHT) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historique des mensurations") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sélecteur de métrique
            Text("Métrique", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            MeasurementMetricSelector(
                selected = selectedMetric.value,
                onSelect = { selectedMetric.value = it }
            )

            // Graphique
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "${selectedMetric.value.label} (${selectedMetric.value.unit})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    MeasurementHistoryChart(
                        measurements = state.measurements,
                        metric = selectedMetric.value
                    )
                }
            }

            // Tableau historique
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Historique complet",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    if (state.measurements.isEmpty()) {
                        Text("Aucune mesure enregistrée", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        MeasurementHistoryTable(state.measurements)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MeasurementMetricSelector(
    selected: MeasurementMetric,
    onSelect: (MeasurementMetric) -> Unit
) {
    val metrics = listOf(
        MeasurementMetric.WEIGHT,
        MeasurementMetric.BODY_FAT,
        MeasurementMetric.MUSCLE,
        MeasurementMetric.CHEST,
        MeasurementMetric.ARMS,
        MeasurementMetric.WAIST,
        MeasurementMetric.HIPS,
        MeasurementMetric.THIGH,
        MeasurementMetric.CALF
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        metrics.forEach { metric ->
            val isSelected = metric == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(metric) },
                label = { Text(metric.label) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
private fun MeasurementHistoryTable(measurements: List<Measurement>) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val zone = java.time.ZoneId.systemDefault()

    Column(modifier = Modifier.testTag("measurement_history_table")) {
        measurements.forEachIndexed { index, m ->
            val prev = measurements.getOrNull(index + 1)
            val weightDiff = prev?.let { m.weightKg?.minus(it.weightKg ?: 0f) }
            val fatDiff = prev?.let { m.bodyFatPercent?.minus(it.bodyFatPercent ?: 0f) }
            val dateStr = java.time.Instant.ofEpochMilli(m.dateTimestamp)
                .atZone(zone)
                .toLocalDate()
                .format(formatter)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(dateStr, style = MaterialTheme.typography.bodySmall)

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ValueWithDiff(
                        value = m.weightKg?.let { "%.1f kg".format(it) } ?: "—",
                        diff = weightDiff
                    )
                    ValueWithDiff(
                        value = m.bodyFatPercent?.let { "%.1f%%".format(it) } ?: "—",
                        diff = fatDiff
                    )
                }
            }
            if (index < measurements.size - 1) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun ValueWithDiff(value: String, diff: Float?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(value, style = MaterialTheme.typography.bodySmall)
        diff?.let { d ->
            val isPositive = d > 0
            Icon(
                imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = if (isPositive) Color(0xFFD32F2F) else Color(0xFF388E3C),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
