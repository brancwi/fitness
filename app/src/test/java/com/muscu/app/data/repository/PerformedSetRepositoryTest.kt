package com.muscu.app.data.repository

import app.cash.turbine.test
import com.muscu.app.data.model.AppSettings
import com.muscu.app.data.model.Exercise
import com.muscu.app.data.model.PerformedSet
import com.muscu.app.data.model.PerformedSetDao
import com.muscu.app.data.model.PerformedSetWithDate
import com.muscu.app.domain.model.Intensity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class PerformedSetRepositoryTest {

    private lateinit var setDao: PerformedSetDao
    private lateinit var appSettingsRepository: AppSettingsRepository
    private lateinit var repository: PerformedSetRepository

    @Before
    fun setUp() {
        setDao = mock()
        appSettingsRepository = mock()
        repository = PerformedSetRepository(setDao, appSettingsRepository)
    }

    @Test
    fun `getForExercise returns flow from dao`() = runTest {
        // Given
        val sets = listOf(
            PerformedSet("ps1", "s1", "ex1", 1, reps = 10, weightKg = 20f)
        )
        whenever(setDao.getSetsForExercise("s1", "ex1")).thenReturn(flowOf(sets))

        // When / Then
        repository.getForExercise("s1", "ex1").test {
            assertEquals(sets, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `ensureSetsForExercise creates sets when none exist`() = runTest {
        // Given
        val exercise = Exercise(
            id = "ex1",
            name = "Bench",
            dayOfWeek = 1,
            category = "Chest",
            targetSets = 3,
            targetRepsMin = 8,
            targetRepsMax = 10,
            intensity = Intensity.MODERATE,
            warning = null,
            orderIndex = 0
        )
        whenever(setDao.getSetsForExercise("s1", "ex1")).thenReturn(flowOf(emptyList()))
        whenever(appSettingsRepository.getLatest()).thenReturn(AppSettings())
        whenever(setDao.getLastCompletedSetForExercise("ex1")).thenReturn(null)

        // When
        repository.ensureSetsForExercise("s1", exercise)

        // Then
        verify(setDao).getLastCompletedSetForExercise("ex1")
        verify(setDao, org.mockito.kotlin.times(3)).insert(any<PerformedSet>())
    }

    @Test
    fun `ensureSetsForExercise does nothing when sets already exist`() = runTest {
        // Given
        val exercise = Exercise(
            id = "ex1",
            name = "Bench",
            dayOfWeek = 1,
            category = "Chest",
            targetSets = 3,
            targetRepsMin = 8,
            targetRepsMax = 10,
            intensity = Intensity.MODERATE,
            warning = null,
            orderIndex = 0
        )
        val existing = listOf(
            PerformedSet("ps1", "s1", "ex1", 1, reps = 10, weightKg = 20f)
        )
        whenever(setDao.getSetsForExercise("s1", "ex1")).thenReturn(flowOf(existing))

        // When
        repository.ensureSetsForExercise("s1", exercise)

        // Then
        verify(setDao, never()).insert(any<PerformedSet>())
        verify(setDao, never()).getLastCompletedSetForExercise(any())
    }

    @Test
    fun `ensureSetsForExercise uses last completed set reps and weight when available`() = runTest {
        // Given
        val exercise = Exercise(
            id = "ex1",
            name = "Bench",
            dayOfWeek = 1,
            category = "Chest",
            targetSets = 2,
            targetRepsMin = 8,
            targetRepsMax = 10,
            intensity = Intensity.MODERATE,
            warning = null,
            orderIndex = 0
        )
        val lastSet = PerformedSet(
            id = "ps-last",
            sessionId = "s0",
            exerciseId = "ex1",
            setNumber = 1,
            reps = 12,
            weightKg = 25f,
            isCompleted = true,
            difficultyRating = 3
        )
        whenever(setDao.getSetsForExercise("s1", "ex1")).thenReturn(flowOf(emptyList()))
        whenever(appSettingsRepository.getLatest()).thenReturn(AppSettings())
        whenever(setDao.getLastCompletedSetForExercise("ex1")).thenReturn(lastSet)

        // When
        repository.ensureSetsForExercise("s1", exercise)

        // Then
        verify(setDao, org.mockito.kotlin.times(2)).insert(any<PerformedSet>())
    }

    @Test
    fun `update delegates to dao`() = runTest {
        // Given
        val set = PerformedSet("ps1", "s1", "ex1", 1, reps = 10, weightKg = 20f)

        // When
        repository.update(set)

        // Then
        verify(setDao).update(set)
    }

    @Test
    fun `getLastCompletedSetForExercise returns set from dao`() = runTest {
        // Given
        val set = PerformedSet("ps1", "s1", "ex1", 1, reps = 10, weightKg = 20f, isCompleted = true)
        whenever(setDao.getLastCompletedSetForExercise("ex1")).thenReturn(set)

        // When
        val result = repository.getLastCompletedSetForExercise("ex1")

        // Then
        assertEquals(set, result)
    }

    @Test
    fun `getLastCompletedSetForExercise returns null when none`() = runTest {
        // Given
        whenever(setDao.getLastCompletedSetForExercise("ex1")).thenReturn(null)

        // When
        val result = repository.getLastCompletedSetForExercise("ex1")

        // Then
        assertNull(result)
    }

    @Test
    fun `getPerformanceHistory returns list from dao`() = runTest {
        // Given
        val history = listOf(
            PerformedSetWithDate(
                PerformedSet("ps1", "s1", "ex1", 1, reps = 10, weightKg = 20f, isCompleted = true),
                sessionDate = 123456L
            )
        )
        whenever(setDao.getCompletedSetsWithDateForExercise("ex1")).thenReturn(history)

        // When
        val result = repository.getPerformanceHistory("ex1")

        // Then
        assertEquals(history, result)
    }

    @Test
    fun `deleteById delegates to dao`() = runTest {
        // When
        repository.deleteById("ps1")

        // Then
        verify(setDao).deleteById("ps1")
    }
}
