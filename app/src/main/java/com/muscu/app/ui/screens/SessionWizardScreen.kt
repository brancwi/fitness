@file:OptIn(ExperimentalLayoutApi::class)

package com.muscu.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.muscu.app.data.model.Exercise
import com.muscu.app.data.model.WorkoutTemplate
import com.muscu.app.viewmodel.WizardExerciseConfig
import com.muscu.app.viewmodel.WorkoutTemplateViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SessionWizardScreen(
    viewModel: WorkoutTemplateViewModel,
    templateId: String? = null,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var step by remember { mutableIntStateOf(1) }

    // Step 1 state
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDay by remember { mutableIntStateOf(0) }

    // Step 2 & 3 state
    var selectedExerciseIds by remember { mutableStateOf(setOf<String>()) }
    var exerciseConfigs by remember { mutableStateOf(listOf<WizardExerciseConfig>()) }

    // Load template data asynchronously when editing
    LaunchedEffect(templateId) {
        if (templateId != null) {
            viewModel.loadTemplateForEditing(templateId)
        } else {
            viewModel.clearSelectedTemplate()
            name = ""
            description = ""
            selectedDay = 0
            selectedExerciseIds = emptySet()
            exerciseConfigs = emptyList()
            step = 1
        }
    }

    // Pre-fill fields when selected template changes
    LaunchedEffect(state.selectedTemplate) {
        state.selectedTemplate?.let { template ->
            name = template.name
            description = template.description ?: ""
            selectedDay = template.dayOfWeek ?: 0
        }
    }

    // Pre-fill exercises when template exercises are loaded
    LaunchedEffect(state.templateExercises) {
        if (templateId != null && state.templateExercises.isNotEmpty()) {
            selectedExerciseIds = state.templateExercises.map { it.exerciseId }.toSet()
            exerciseConfigs = state.templateExercises.map { te ->
                WizardExerciseConfig(
                    exerciseId = te.exerciseId,
                    sets = te.targetSets,
                    repsMin = te.targetRepsMin,
                    repsMax = te.targetRepsMax,
                    restSeconds = te.restSeconds
                )
            }
        }
    }

    if (state.saved) {
        LaunchedEffect(Unit) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.selectedTemplate == null) "Nouvelle séance" else "Modifier la séance") },
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
                .padding(16.dp)
        ) {
            // Step indicator
            StepIndicator(currentStep = step, totalSteps = 3)
            Spacer(Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (step) {
                    1 -> Step1Info(
                        name = name,
                        onNameChange = { name = it },
                        description = description,
                        onDescriptionChange = { description = it },
                        selectedDay = selectedDay,
                        onDaySelected = { selectedDay = it }
                    )
                    2 -> Step2SelectExercises(
                        availableExercises = state.availableExercises,
                        searchQuery = state.searchQuery,
                        onSearch = { viewModel.searchExercises(it) },
                        selectedIds = selectedExerciseIds,
                        onToggleExercise = { id ->
                            selectedExerciseIds = if (selectedExerciseIds.contains(id)) {
                                selectedExerciseIds - id
                            } else {
                                selectedExerciseIds + id
                            }
                        }
                    )
                    3 -> Step3ConfigureExercises(
                        exercises = state.availableExercises.filter { it.id in selectedExerciseIds },
                        configs = exerciseConfigs,
                        onConfigsChange = { exerciseConfigs = it }
                    )
                }
            }

            // Navigation buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (step > 1) {
                    OutlinedButton(onClick = { step-- }, modifier = Modifier.weight(1f)) {
                        Text("Précédent")
                    }
                }
                if (step < 3) {
                    Button(
                        onClick = {
                            if (step == 2) {
                                // Initialize configs for newly selected exercises
                                val existingIds = exerciseConfigs.map { it.exerciseId }.toSet()
                                val newConfigs = selectedExerciseIds.filter { it !in existingIds }.map { id ->
                                    val ex = state.availableExercises.find { it.id == id }
                                    WizardExerciseConfig(
                                        exerciseId = id,
                                        sets = ex?.targetSets ?: 3,
                                        repsMin = ex?.targetRepsMin ?: 8,
                                        repsMax = ex?.targetRepsMax ?: 12,
                                        restSeconds = 90
                                    )
                                }
                                exerciseConfigs = exerciseConfigs.filter { it.exerciseId in selectedExerciseIds } + newConfigs
                            }
                            step++
                        },
                        modifier = Modifier.weight(1f),
                        enabled = when (step) {
                            1 -> name.isNotBlank()
                            2 -> selectedExerciseIds.isNotEmpty()
                            else -> true
                        }
                    ) {
                        Text("Suivant")
                    }
                } else {
                    Button(
                        onClick = {
                            val selected = state.selectedTemplate
                            if (selected != null) {
                                viewModel.updateTemplate(
                                    selected.copy(name = name, description = description.takeIf { it.isNotBlank() }, dayOfWeek = selectedDay.takeIf { it > 0 }),
                                    exerciseConfigs
                                )
                            } else {
                                viewModel.createTemplate(
                                    name = name,
                                    description = description.takeIf { it.isNotBlank() },
                                    dayOfWeek = selectedDay.takeIf { it > 0 },
                                    exercises = exerciseConfigs
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = exerciseConfigs.isNotEmpty()
                    ) {
                        Text(if (state.selectedTemplate != null) "Mettre à jour" else "Enregistrer")
                    }
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int, totalSteps: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        (1..totalSteps).forEach { step ->
            val isActive = step == currentStep
            val isDone = step < currentStep
            val color = when {
                isActive -> MaterialTheme.colorScheme.primary
                isDone -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                else -> MaterialTheme.colorScheme.outlineVariant
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        step.toString(),
                        color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                val label = when (step) {
                    1 -> "Infos"
                    2 -> "Exercices"
                    3 -> "Config"
                    else -> ""
                }
                Text(label, style = MaterialTheme.typography.labelSmall, color = color)
            }
        }
    }
}

@Composable
private fun Step1Info(
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nom de la séance *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
        Text("Jour de la semaine (optionnel)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(1 to "Lun", 2 to "Mar", 3 to "Mer", 4 to "Jeu", 5 to "Ven", 6 to "Sam", 7 to "Dim").forEach { (day, label) ->
                FilterChip(
                    selected = selectedDay == day,
                    onClick = { onDaySelected(if (selectedDay == day) 0 else day) },
                    label = { Text(label) }
                )
            }
        }
    }
}

@Composable
private fun Step2SelectExercises(
    availableExercises: List<Exercise>,
    searchQuery: String,
    onSearch: (String) -> Unit,
    selectedIds: Set<String>,
    onToggleExercise: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearch,
            label = { Text("Rechercher un exercice") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "${selectedIds.size} exercice(s) sélectionné(s)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(availableExercises, key = { it.id }) { exercise ->
                val isSelected = selectedIds.contains(exercise.id)
                ExerciseCatalogItem(
                    exercise = exercise,
                    isSelected = isSelected,
                    onToggle = { onToggleExercise(exercise.id) }
                )
            }
        }
    }
}

@Composable
private fun ExerciseCatalogItem(
    exercise: Exercise,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(exercise.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    "${exercise.category} • ${exercise.equipment}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    exercise.objective,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Checkbox(checked = isSelected, onCheckedChange = { onToggle() })
        }
    }
}

@Composable
private fun Step3ConfigureExercises(
    exercises: List<Exercise>,
    configs: List<WizardExerciseConfig>,
    onConfigsChange: (List<WizardExerciseConfig>) -> Unit
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        configs.forEachIndexed { index, config ->
            val exercise = exercises.find { it.id == config.exerciseId }
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${index + 1}. ${exercise?.name ?: config.exerciseId}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(
                                onClick = {
                                    if (index > 0) {
                                        val newList = configs.toMutableList()
                                        newList[index] = newList[index - 1].also { newList[index - 1] = newList[index] }
                                        onConfigsChange(newList)
                                    }
                                },
                                enabled = index > 0,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ArrowUpward, null, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = {
                                    if (index < configs.size - 1) {
                                        val newList = configs.toMutableList()
                                        newList[index] = newList[index + 1].also { newList[index + 1] = newList[index] }
                                        onConfigsChange(newList)
                                    }
                                },
                                enabled = index < configs.size - 1,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ArrowDownward, null, modifier = Modifier.size(18.dp))
                            }
                            IconButton(
                                onClick = { onConfigsChange(configs.filterIndexed { i, _ -> i != index }) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = config.sets.toString(),
                            onValueChange = {
                                val newList = configs.toMutableList()
                                newList[index] = config.copy(sets = it.toIntOrNull() ?: config.sets)
                                onConfigsChange(newList)
                            },
                            label = { Text("Séries") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = config.repsMin.toString(),
                            onValueChange = {
                                val newList = configs.toMutableList()
                                newList[index] = config.copy(repsMin = it.toIntOrNull() ?: config.repsMin)
                                onConfigsChange(newList)
                            },
                            label = { Text("Reps min") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = config.repsMax.toString(),
                            onValueChange = {
                                val newList = configs.toMutableList()
                                newList[index] = config.copy(repsMax = it.toIntOrNull() ?: config.repsMax)
                                onConfigsChange(newList)
                            },
                            label = { Text("Reps max") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = config.restSeconds.toString(),
                            onValueChange = {
                                val newList = configs.toMutableList()
                                newList[index] = config.copy(restSeconds = it.toIntOrNull() ?: config.restSeconds)
                                onConfigsChange(newList)
                            },
                            label = { Text("Repos") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
        }
    }
}
