package com.muscu.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.muscu.app.data.model.WorkoutSession
import com.muscu.app.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SessionHistoryUiState(
    val sessions: List<WorkoutSession> = emptyList(),
    val isLoading: Boolean = false
)

class SessionHistoryViewModel(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionHistoryUiState())
    val uiState: StateFlow<SessionHistoryUiState> = _uiState

    init {
        loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.getAllSessions().collect { sessions ->
                _uiState.value = SessionHistoryUiState(sessions = sessions, isLoading = false)
            }
        }
    }

    fun deleteSession(id: String) {
        viewModelScope.launch {
            repository.deleteWorkoutSession(id)
        }
    }

    class Factory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SessionHistoryViewModel(repository) as T
        }
    }
}
