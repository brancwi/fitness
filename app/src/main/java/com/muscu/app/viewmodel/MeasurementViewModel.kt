package com.muscu.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.muscu.app.data.model.Measurement
import com.muscu.app.data.repository.AppSettingsRepository
import com.muscu.app.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class MeasurementUiState(
    val measurements: List<Measurement> = emptyList(),
    val weightKg: String = "",
    val bodyFatPercent: String = "",
    val musclePercent: String = "",
    val chestCm: String = "",
    val armsCm: String = "",
    val waistCm: String = "",
    val hipsCm: String = "",
    val thighsCm: String = "",
    val calvesCm: String = "",
    val saved: Boolean = false
)

class MeasurementViewModel(
    private val repository: WorkoutRepository,
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MeasurementUiState())
    val uiState: StateFlow<MeasurementUiState> = _uiState

    init {
        loadMeasurements()
    }

    private fun loadMeasurements() {
        viewModelScope.launch {
            repository.getAllMeasurements().collect { measurements ->
                val latest = measurements.firstOrNull()
                _uiState.value = _uiState.value.copy(
                    measurements = measurements,
                    weightKg = latest?.weightKg?.toString() ?: "",
                    bodyFatPercent = latest?.bodyFatPercent?.toString() ?: "",
                    musclePercent = latest?.musclePercent?.toString() ?: "",
                    chestCm = latest?.chestCm?.toString() ?: "",
                    armsCm = latest?.armsCm?.toString() ?: "",
                    waistCm = latest?.waistCm?.toString() ?: "",
                    hipsCm = latest?.hipsCm?.toString() ?: "",
                    thighsCm = latest?.thighsCm?.toString() ?: "",
                    calvesCm = latest?.calvesCm?.toString() ?: ""
                )
            }
        }
    }

    fun updateWeight(value: String) { _uiState.value = _uiState.value.copy(weightKg = value, saved = false) }
    fun updateBodyFat(value: String) { _uiState.value = _uiState.value.copy(bodyFatPercent = value, saved = false) }
    fun updateMuscle(value: String) { _uiState.value = _uiState.value.copy(musclePercent = value, saved = false) }
    fun updateChest(value: String) { _uiState.value = _uiState.value.copy(chestCm = value, saved = false) }
    fun updateArms(value: String) { _uiState.value = _uiState.value.copy(armsCm = value, saved = false) }
    fun updateWaist(value: String) { _uiState.value = _uiState.value.copy(waistCm = value, saved = false) }
    fun updateHips(value: String) { _uiState.value = _uiState.value.copy(hipsCm = value, saved = false) }
    fun updateThighs(value: String) { _uiState.value = _uiState.value.copy(thighsCm = value, saved = false) }
    fun updateCalves(value: String) { _uiState.value = _uiState.value.copy(calvesCm = value, saved = false) }

    fun save() {
        viewModelScope.launch {
            val s = _uiState.value
            repository.saveMeasurement(
                Measurement(
                    id = UUID.randomUUID().toString(),
                    dateTimestamp = System.currentTimeMillis(),
                    weightKg = s.weightKg.toFloatOrNull(),
                    bodyFatPercent = s.bodyFatPercent.toFloatOrNull(),
                    musclePercent = s.musclePercent.toFloatOrNull(),
                    chestCm = s.chestCm.toFloatOrNull(),
                    armsCm = s.armsCm.toFloatOrNull(),
                    waistCm = s.waistCm.toFloatOrNull(),
                    hipsCm = s.hipsCm.toFloatOrNull(),
                    thighsCm = s.thighsCm.toFloatOrNull(),
                    calvesCm = s.calvesCm.toFloatOrNull()
                )
            )
            _uiState.value = s.copy(saved = true)
        }
    }

    fun deleteMeasurement(id: String) {
        viewModelScope.launch {
            repository.deleteMeasurement(id)
        }
    }

    class Factory(
        private val repository: WorkoutRepository,
        private val appSettingsRepository: AppSettingsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MeasurementViewModel(repository, appSettingsRepository) as T
        }
    }
}
