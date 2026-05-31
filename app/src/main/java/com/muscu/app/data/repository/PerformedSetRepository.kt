package com.muscu.app.data.repository

import com.muscu.app.data.model.Exercise
import com.muscu.app.data.model.PerformedSet
import com.muscu.app.data.model.PerformedSetDao
import com.muscu.app.data.model.PerformedSetWithDate
import com.muscu.app.domain.model.Intensity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class PerformedSetRepository(
    private val setDao: PerformedSetDao,
    private val appSettingsRepository: AppSettingsRepository
) {

    fun getForExercise(sessionId: String, exerciseId: String): Flow<List<PerformedSet>> =
        setDao.getSetsForExercise(sessionId, exerciseId)

    suspend fun ensureSetsForExercise(sessionId: String, exercise: Exercise) {
        ensureSetsForExerciseWithConfig(sessionId, exercise, exercise.targetSets, null)
    }

    suspend fun ensureSetsForExerciseWithConfig(
        sessionId: String,
        exercise: Exercise,
        targetSets: Int,
        restSecondsOverride: Int?
    ) {
        val existing = setDao.getSetsForExercise(sessionId, exercise.id).first()
        if (existing.isEmpty()) {
            val settings = appSettingsRepository.getLatest()
            val defaultRest = restSecondsOverride ?: when (exercise.intensity) {
                Intensity.MODERATE -> settings.moderateRestSeconds
                Intensity.LIGHT -> settings.lightRestSeconds
                Intensity.BODYWEIGHT -> settings.bodyweightRestSeconds
            }

            // Try to reuse last known reps/weight for this exercise, adjusted by difficulty rating
            val lastSet = setDao.getLastCompletedSetForExercise(exercise.id)
            val defaultReps = lastSet?.reps
                ?: exercise.targetRepsMax.takeIf { it > 0 }
                ?: exercise.targetRepsMin.takeIf { it > 0 }
                ?: settings.defaultReps
            val defaultWeight = lastSet?.let { set ->
                when (set.difficultyRating) {
                    1 -> set.weightKg?.times(0.9f)
                    2 -> set.weightKg?.times(0.95f)
                    4 -> set.weightKg?.times(1.05f)
                    5 -> set.weightKg?.times(1.10f)
                    else -> set.weightKg
                }
            } ?: settings.defaultWeightKg

            repeat(targetSets) { index ->
                setDao.insert(
                    PerformedSet(
                        id = UUID.randomUUID().toString(),
                        sessionId = sessionId,
                        exerciseId = exercise.id,
                        setNumber = index + 1,
                        reps = defaultReps,
                        weightKg = defaultWeight,
                        restSeconds = defaultRest
                    )
                )
            }
        }
    }

    suspend fun update(set: PerformedSet) = setDao.update(set)

    suspend fun getLastCompletedSetForExercise(exerciseId: String): PerformedSet? =
        setDao.getLastCompletedSetForExercise(exerciseId)

    suspend fun getPerformanceHistory(exerciseId: String): List<PerformedSetWithDate> =
        setDao.getCompletedSetsWithDateForExercise(exerciseId)

    suspend fun deleteById(id: String) = setDao.deleteById(id)
}
