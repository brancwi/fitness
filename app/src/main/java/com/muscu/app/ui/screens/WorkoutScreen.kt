package com.muscu.app.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import com.muscu.app.data.model.Exercise
import com.muscu.app.ui.components.ExerciseIllustration
import com.muscu.app.data.model.PerformedSet
import com.muscu.app.data.model.AppSettings
import com.muscu.app.domain.calculator.RestTimeRecommendations
import com.muscu.app.domain.model.Intensity
import com.muscu.app.domain.timer.TimerState
import com.muscu.app.ui.components.PrepCountdownCard
import com.muscu.app.ui.components.ProgressionChip
import com.muscu.app.ui.components.SetHelpDialog
import com.muscu.app.ui.components.SinusoidalGuide
import com.muscu.app.ui.components.SpeedControl
import com.muscu.app.ui.components.TempoEducationSheet
import com.muscu.app.ui.components.TempoSummaryCard
import com.muscu.app.ui.components.ExerciseRatingDialog
import com.muscu.app.viewmodel.WorkoutUiState
import com.muscu.app.viewmodel.WorkoutViewModel

private fun restRecommendation(intensity: Intensity, settings: AppSettings?): String {
    return settings?.let { RestTimeRecommendations.text(intensity, it) } ?: "60-90 sec"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    viewModel: WorkoutViewModel,
    dayOfWeek: Int,
    templateId: String? = null,
    onBack: () -> Unit,
    onExerciseInfo: (String, String) -> Unit = { _, _ -> }
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(dayOfWeek, templateId) {
        if (templateId != null) {
            viewModel.loadTemplate(templateId, dayOfWeek)
        } else {
            viewModel.loadDay(dayOfWeek)
        }
    }

    val currentExercise = state.exercises.getOrNull(state.currentExerciseIndex)
    val currentSets = currentExercise?.let { state.setsByExercise[it.id] } ?: emptyList()
    val currentSet = currentSets.getOrNull(state.currentSetIndex)

    Column(modifier = Modifier.fillMaxSize().testTag("workout_screen")) {
        TopAppBar(
            title = { Text("Séance du jour") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                }
            },
            actions = {
                if (state.allCompleted) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Terminé",
                        tint = MaterialTheme.colorScheme.primary
                    )
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
            if (state.exercises.isEmpty()) {
                Text("Chargement...", modifier = Modifier.testTag("workout_loading"))
            } else if (state.allCompleted) {
                WorkoutCompletedCard(viewModel = viewModel, onBack = onBack)
            } else {
                ProgressHeader(state)

                currentExercise?.let { ex ->
                    ExerciseCard(
                        exercise = ex,
                        onInfoClick = { onExerciseInfo(ex.id, ex.name) }
                    )
                }

                val timerState = state.timerState
                if (timerState is TimerState.RunningRest) {
                    RestTimerCard(
                        timerState = timerState,
                        onSkip = viewModel::skipTimer
                    )
                }

                if (state.showExerciseRating && currentExercise != null) {
                    ExerciseRatingDialog(
                        exerciseName = currentExercise.name,
                        onConfirm = { rating -> viewModel.submitExerciseRating(rating) },
                        onSkip = { viewModel.skipExerciseRating() }
                    )
                }

                if (timerState is TimerState.Idle && currentSet != null) {
                    val showEducationSheet = remember { mutableStateOf(false) }

                    state.tempoAnalysis?.let { analysis ->
                        TempoSummaryCard(
                            analysis = analysis,
                            onLearnMore = { showEducationSheet.value = true }
                        )
                    }

                    // Compte à rebours de préparation
                    val prepCountdown = state.prepCountdown
                    if (prepCountdown != null) {
                        PrepCountdownCard(
                            remaining = prepCountdown,
                            onSkip = viewModel::skipPrepCountdown
                        )
                    }

                    // Guide de tempo (affiché seulement après le countdown)
                    if (state.prepCountdown == null && state.showSetCompleteButton) {
                        SinusoidalGuide(
                            guideState = state.guideState,
                            speedMultiplier = state.speedMultiplier,
                            tempoCode = state.tempoAnalysis?.tempoCode ?: "3-0-1-1",
                            objectiveLabel = state.tempoAnalysis?.objectiveLabel ?: "Hypertrophie optimale"
                        )
                        SpeedControl(
                            speedMultiplier = state.speedMultiplier,
                            onSpeedSelect = viewModel::setGuideSpeed
                        )
                    }

                    SetInputCard(
                        state = state,
                        currentSet = currentSet,
                        currentExercise = currentExercise,
                        lastSet = state.lastSetForCurrentExercise,
                        onStartSet = viewModel::startSet,
                        onCompleteSet = viewModel::completeSet,
                        onUpdateRestTime = viewModel::updateRestTimeForCurrentSet
                    )

                    if (showEducationSheet.value) {
                        TempoEducationSheet(
                            onDismiss = { showEducationSheet.value = false }
                        )
                    }
                }
            }
        }
    }


}

@Composable
private fun ProgressHeader(state: WorkoutUiState) {
    Text(
        text = "Exercice ${state.currentExerciseIndex + 1} / ${state.exercises.size}",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.testTag("workout_progress")
    )
}

@Composable
private fun WorkoutCompletedCard(
    viewModel: WorkoutViewModel,
    onBack: () -> Unit
) {
    var overallRating by remember { mutableStateOf(0) }
    var energyLevel by remember { mutableStateOf(0) }
    var perceivedEffort by remember { mutableStateOf(5) }
    var notes by remember { mutableStateOf("") }
    var saved by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(64.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Séance terminée !",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(24.dp))

            // Note globale
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Note globale", modifier = Modifier.weight(1f))
                Row {
                    (1..5).forEach { star ->
                        IconButton(
                            onClick = { overallRating = star },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = if (star <= overallRating) Color(0xFFFFC107) else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // Énergie
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Niveau d'énergie", modifier = Modifier.weight(1f))
                Row {
                    (1..5).forEach { star ->
                        IconButton(
                            onClick = { energyLevel = star },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = if (star <= energyLevel) Color(0xFF4CAF50) else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // Effort perçu (RPE)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Effort perçu (RPE) : $perceivedEffort / 10")
                Slider(
                    value = perceivedEffort.toFloat(),
                    onValueChange = { perceivedEffort = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8
                )
            }
            Spacer(Modifier.height(12.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes / ressentis") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            Spacer(Modifier.height(20.dp))

            if (!saved) {
                Button(
                    onClick = {
                        viewModel.saveSessionFeedback(
                            overallRating.coerceAtLeast(1),
                            energyLevel.coerceAtLeast(1),
                            perceivedEffort,
                            notes
                        )
                        viewModel.finishSession()
                        saved = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = overallRating > 0 && energyLevel > 0
                ) {
                    Text("Enregistrer le feedback")
                }
            } else {
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text("Retour au tableau de bord")
                }
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: Exercise,
    onInfoClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    exercise.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onInfoClick) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Voir la fiche de l'exercice",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            val reps = if (exercise.targetRepsMax != exercise.targetRepsMin)
                "${exercise.targetRepsMin}–${exercise.targetRepsMax}"
            else
                "${exercise.targetRepsMin}"
            Text(
                "Objectif : ${exercise.targetSets} x $reps • Charge ${exercise.intensity.label}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!exercise.warning.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "⚠ ${exercise.warning}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(Modifier.height(8.dp))
            ExerciseIllustration(
                exerciseId = exercise.id,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun RestTimerCard(
    timerState: TimerState.RunningRest,
    onSkip: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text("Temps de récupération", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${timerState.remaining} sec",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("rest_timer")
            )
            if (timerState.remaining <= 3 && timerState.remaining > 0) {
                Text(
                    "Prépare la série suivante !",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onSkip) {
                Icon(Icons.Default.SkipNext, contentDescription = null)
                Text("Passer le timer")
            }
        }
    }
}

@Composable
private fun SetInputCard(
    state: WorkoutUiState,
    currentSet: PerformedSet,
    currentExercise: Exercise?,
    lastSet: com.muscu.app.data.model.PerformedSet?,
    onStartSet: () -> Unit,
    onCompleteSet: (String, String) -> Unit,
    onUpdateRestTime: (Int) -> Unit
) {
    val currentSets = currentExercise?.let { state.setsByExercise[it.id] } ?: emptyList()
    var showHelp by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Série ${state.currentSetIndex + 1} / ${currentSets.size}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showHelp = true }) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Conseil",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            var reps by remember { mutableStateOf("") }
            var weight by remember { mutableStateOf("") }

            LaunchedEffect(currentSet.id, state.prefilledReps) {
                reps = state.prefilledReps.ifEmpty { currentSet.reps?.toString() ?: "" }
            }
            LaunchedEffect(currentSet.id, state.prefilledWeight) {
                weight = state.prefilledWeight.ifEmpty { currentSet.weightKg?.toString() ?: "" }
            }

            var restTime by rememberSaveable(currentSet.id) { mutableStateOf(currentSet.restSeconds.toFloat()) }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it.filter { c -> c.isDigit() } },
                    label = { Text("Répétitions") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Charge (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            if (reps.isNotBlank()) {
                ProgressionChip(
                    reps = reps.toIntOrNull(),
                    targetRepsMin = currentExercise?.targetRepsMin ?: 0,
                    targetRepsMax = currentExercise?.targetRepsMax ?: 0
                )
            }

            Spacer(Modifier.height(12.dp))
            Text("Temps de récupération : ${restTime.toInt()} sec", style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = restTime,
                onValueChange = { restTime = it },
                onValueChangeFinished = { onUpdateRestTime(restTime.toInt()) },
                valueRange = 15f..180f,
                steps = 32
            )
            Text(
                "Recommandation : ${currentExercise?.intensity?.let { intensity -> restRecommendation(intensity, state.settings) } ?: "60-90 sec"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            if (!state.showSetCompleteButton) {
                Button(
                    onClick = onStartSet,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Text("Démarrer la série ${state.currentSetIndex + 1}", modifier = Modifier.padding(start = 8.dp))
                }
            } else {
                Button(
                    onClick = { onCompleteSet(reps, weight) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Text("Série terminée", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }

    if (showHelp) {
        SetHelpDialog(
            lastSet = lastSet,
            targetRepsMin = currentExercise?.targetRepsMin ?: 0,
            targetRepsMax = currentExercise?.targetRepsMax ?: 0,
            onDismiss = { showHelp = false }
        )
    }
}
