package com.muscu.app.data.repository

import com.muscu.app.data.model.Exercise
import com.muscu.app.data.model.PerformedSet
import com.muscu.app.data.model.PerformedSetDao
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
        val existing = setDao.getSetsForExercise(sessionId, exercise.id).first()
        if (existing.isEmpty()) {
            val settings = appSettingsRepository.getLatest()
            val defaultRest = when (exercise.intensity) {
                Intensity.MODERATE -> settings.moderateRestSeconds
                Intensity.LIGHT -> settings.lightRestSeconds
                Intensity.BODYWEIGHT -> settings.bodyweightRestSeconds
            }
            repeat(exercise.targetSets) { index ->
                setDao.insert(
                    PerformedSet(
                        id = UUID.randomUUID().toString(),
                        sessionId = sessionId,
                        exerciseId = exercise.id,
                        setNumber = index + 1,
                        restSeconds = defaultRest
                    )
                )
            }
        }
    }

    suspend fun update(set: PerformedSet) = setDao.update(set)
}
