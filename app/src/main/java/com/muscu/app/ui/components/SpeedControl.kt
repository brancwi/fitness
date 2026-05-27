package com.muscu.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SpeedControl(
    speedMultiplier: Float,
    onSpeedSelect: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.5f, 2.0f)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        speeds.forEach { speed ->
            FilterChip(
                selected = speedMultiplier == speed,
                onClick = { onSpeedSelect(speed) },
                label = { Text("${speed}x") }
            )
        }
    }
}
