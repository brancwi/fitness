package com.muscu.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.muscu.app.data.model.PerformedSetWithDate
import com.muscu.app.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ExercisePerformanceUiState(
    val history: List<PerformedSetWithDate> = emptyList(),
    val isLoading: Boolean = false
)

class ExercisePerformanceViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExercisePerformanceUiState())
    val uiState: StateFlow<ExercisePerformanceUiState> = _uiState

    fun loadHistory(exerciseId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val history = repository.getPerformanceHistoryForExercise(exerciseId)
            _uiState.value = ExercisePerformanceUiState(
                history = history,
                isLoading = false
            )
        }
    }

    fun deleteSet(id: String, exerciseId: String) {
        viewModelScope.launch {
            repository.deletePerformedSet(id)
            loadHistory(exerciseId)
        }
    }

    class Factory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return ExercisePerformanceViewModel(repository) as T
        }
    }
}
