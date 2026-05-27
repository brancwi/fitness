package com.muscu.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.muscu.app.data.repository.AppSettingsRepository
import com.muscu.app.data.repository.WorkoutRepository
import com.muscu.app.domain.calculator.NextWorkoutDayCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class DashboardUiState(
    val nextWorkoutDay: String = "",
    val nextWorkoutDayIndex: Int = 2,
    val hasActiveSession: Boolean = false,
    val activeSessionDay: Int? = null,
    val lumbarRules: List<String> = emptyList()
)

class DashboardViewModel(
    private val repository: WorkoutRepository,
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        viewModelScope.launch {
            appSettingsRepository.seedDefaultsIfNeeded()
            repository.seedExercisesIfNeeded()
            loadDashboard()
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            val days = appSettingsRepository.workoutDays().first()
            val names = appSettingsRepository.dayNames().first()
            val rules = appSettingsRepository.lumbarRules().first()
            val (nextDay, dayName) = NextWorkoutDayCalculator.calculate(days, names)

            _uiState.value = DashboardUiState(
                nextWorkoutDay = dayName,
                nextWorkoutDayIndex = nextDay,
                lumbarRules = rules
            )
        }
    }

    class Factory(
        private val repository: WorkoutRepository,
        private val appSettingsRepository: AppSettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(repository, appSettingsRepository) as T
        }
    }
}
