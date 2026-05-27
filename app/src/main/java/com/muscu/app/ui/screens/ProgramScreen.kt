package com.muscu.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.muscu.app.viewmodel.ProgramViewModel

@Composable
fun ProgramScreen(viewModel: ProgramViewModel, onStartWorkout: (Int) -> Unit) {
    val days by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Programme hebdomadaire",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("program_title")
        )

        if (days.isEmpty()) {
            Text("Chargement...")
        } else {
            days.forEach { day ->
                DayCard(
                    day = day.dayName,
                    dayIndex = day.dayIndex,
                    exercises = day.exercises.map { ex ->
                        val reps = if (ex.targetRepsMax != ex.targetRepsMin)
                            "${ex.targetRepsMin}–${ex.targetRepsMax}"
                        else
                            "${ex.targetRepsMin}"
                        "${ex.name} – ${ex.targetSets} x $reps (${ex.intensity.label})"
                    },
                    onStart = onStartWorkout
                )
            }
        }
    }
}

@Composable
private fun DayCard(
    day: String,
    dayIndex: Int,
    exercises: List<String>,
    onStart: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(day, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            exercises.forEach { ex ->
                Text("• $ex", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { onStart(dayIndex) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Text("Démarrer", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}
