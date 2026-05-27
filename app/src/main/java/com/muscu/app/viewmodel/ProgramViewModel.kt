package com.muscu.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.muscu.app.data.repository.AppSettingsRepository
import com.muscu.app.data.repository.WorkoutRepository
import com.muscu.app.domain.model.DayProgram
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProgramViewModel(
    private val repository: WorkoutRepository,
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<List<DayProgram>>(emptyList())
    val uiState: StateFlow<List<DayProgram>> = _uiState

    init {
        viewModelScope.launch {
            val days = appSettingsRepository.workoutDays().first()
            val names = appSettingsRepository.dayNames().first()
            val programs = days.map { index ->
                DayProgram(
                    dayName = names[index] ?: "Jour $index",
                    dayIndex = index,
                    exercises = repository.getExercisesForDay(index).first()
                )
            }
            _uiState.value = programs
        }
    }

    class Factory(
        private val repository: WorkoutRepository,
        private val appSettingsRepository: AppSettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProgramViewModel(repository, appSettingsRepository) as T
        }
    }
}
