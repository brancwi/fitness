package com.muscu.app.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class MetricType(val label: String) {
    WEIGHT("Poids"),
    BODY_FAT("% Graisse"),
    MUSCLE("% Muscle"),
    CHEST("Poitrine"),
    ARM("Bras"),
    WAIST("Taille"),
    HIPS("Hanches"),
    THIGH("Cuisse"),
    CALF("Mollet")
}

@Composable
fun MetricChipSelector(
    selected: MetricType,
    onSelect: (MetricType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricType.entries.forEach { metric ->
            FilterChip(
                selected = selected == metric,
                onClick = { onSelect(metric) },
                label = { Text(metric.label) },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
