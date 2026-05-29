package com.muscu.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.muscu.app.data.model.Exercise
import com.muscu.app.data.model.TemplateExercise
import com.muscu.app.data.model.WorkoutTemplate
import com.muscu.app.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

data class WorkoutTemplateUiState(
    val templates: List<WorkoutTemplate> = emptyList(),
    val selectedTemplate: WorkoutTemplate? = null,
    val templateExercises: List<TemplateExercise> = emptyList(),
    val availableExercises: List<Exercise> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val saved: Boolean = false
)

class WorkoutTemplateViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutTemplateUiState())
    val uiState: StateFlow<WorkoutTemplateUiState> = _uiState

    init {
        loadTemplates()
        loadAvailableExercises()
    }

    private fun loadTemplates() {
        viewModelScope.launch {
            repository.getAllTemplates().collect { templates ->
                _uiState.value = _uiState.value.copy(templates = templates)
            }
        }
    }

    private fun loadAvailableExercises() {
        viewModelScope.launch {
            repository.getAllExercises().collect { exercises ->
                _uiState.value = _uiState.value.copy(availableExercises = exercises)
            }
        }
    }

    fun searchExercises(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        viewModelScope.launch {
            val results = if (query.isBlank()) {
                repository.getAllExercises().first()
            } else {
                repository.searchExercises(query).first()
            }
            _uiState.value = _uiState.value.copy(availableExercises = results)
        }
    }

    fun selectTemplate(template: WorkoutTemplate?) {
        _uiState.value = _uiState.value.copy(selectedTemplate = template, saved = false)
        template?.let { loadTemplateExercises(it.id) }
    }

    fun loadTemplateForEditing(templateId: String) {
        viewModelScope.launch {
            val template = repository.getTemplateById(templateId)
            val exercises = repository.getTemplateExercisesSync(templateId)
            _uiState.value = _uiState.value.copy(
                selectedTemplate = template,
                templateExercises = exercises,
                saved = false
            )
        }
    }

    fun clearSelectedTemplate() {
        _uiState.value = _uiState.value.copy(
            selectedTemplate = null,
            templateExercises = emptyList(),
            saved = false
        )
    }

    private fun loadTemplateExercises(templateId: String) {
        viewModelScope.launch {
            repository.getTemplateExercises(templateId).collect { items ->
                _uiState.value = _uiState.value.copy(templateExercises = items)
            }
        }
    }

    fun createTemplate(name: String, description: String?, dayOfWeek: Int?, exercises: List<WizardExerciseConfig>) {
        viewModelScope.launch {
            val templateId = UUID.randomUUID().toString()
            val template = WorkoutTemplate(
                id = templateId,
                name = name,
                description = description,
                dayOfWeek = dayOfWeek
            )
            repository.saveTemplate(template)

            val templateExercises = exercises.mapIndexed { index, config ->
                TemplateExercise(
                    id = UUID.randomUUID().toString(),
                    templateId = templateId,
                    exerciseId = config.exerciseId,
                    targetSets = config.sets,
                    targetRepsMin = config.repsMin,
                    targetRepsMax = config.repsMax,
                    restSeconds = config.restSeconds,
                    orderIndex = index
                )
            }
            repository.saveTemplateExercises(templateExercises)
            _uiState.value = _uiState.value.copy(saved = true)
        }
    }

    fun updateTemplate(template: WorkoutTemplate, exercises: List<WizardExerciseConfig>) {
        viewModelScope.launch {
            repository.updateTemplate(template)
            repository.deleteTemplateExercisesForTemplate(template.id)

            val templateExercises = exercises.mapIndexed { index, config ->
                TemplateExercise(
                    id = UUID.randomUUID().toString(),
                    templateId = template.id,
                    exerciseId = config.exerciseId,
                    targetSets = config.sets,
                    targetRepsMin = config.repsMin,
                    targetRepsMax = config.repsMax,
                    restSeconds = config.restSeconds,
                    orderIndex = index
                )
            }
            repository.saveTemplateExercises(templateExercises)
            _uiState.value = _uiState.value.copy(saved = true)
        }
    }

    fun deleteTemplate(template: WorkoutTemplate) {
        viewModelScope.launch {
            repository.deleteTemplateExercisesForTemplate(template.id)
            repository.deleteTemplate(template)
        }
    }

    class Factory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WorkoutTemplateViewModel(repository) as T
        }
    }
}

data class WizardExerciseConfig(
    val exerciseId: String,
    val sets: Int,
    val repsMin: Int,
    val repsMax: Int,
    val restSeconds: Int
)
