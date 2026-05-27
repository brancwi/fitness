package com.muscu.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun ProgramScreen(
    viewModel: ProgramViewModel,
    onStartWorkout: (Int) -> Unit,
    onExerciseInfo: (String, String) -> Unit = { _, _ -> }
) {
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
                    exercises = day.exercises,
                    onStart = onStartWorkout,
                    onExerciseInfo = onExerciseInfo
                )
            }
        }
    }
}

@Composable
private fun DayCard(
    day: String,
    dayIndex: Int,
    exercises: List<com.muscu.app.data.model.Exercise>,
    onStart: (Int) -> Unit,
    onExerciseInfo: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(day, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            exercises.forEach { ex ->
                val reps = if (ex.targetRepsMax != ex.targetRepsMin)
                    "${ex.targetRepsMin}–${ex.targetRepsMax}"
                else
                    "${ex.targetRepsMin}"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExerciseInfo(ex.id, ex.name) }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "• ${ex.name} – ${ex.targetSets} x $reps (${ex.intensity.label})",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Voir la fiche",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.height(20.dp)
                    )
                }
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
