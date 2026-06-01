package com.muscu.app.data.repository

import app.cash.turbine.test
import com.muscu.app.data.model.Exercise
import com.muscu.app.data.model.ExerciseDao
import com.muscu.app.data.seed.ExerciseSeedData
import com.muscu.app.domain.model.Intensity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class ExerciseRepositoryTest {

    private lateinit var exerciseDao: ExerciseDao
    private lateinit var repository: ExerciseRepository

    @Before
    fun setUp() {
        exerciseDao = mock()
        repository = ExerciseRepository(exerciseDao)
    }

    @Test
    fun `seedIfNeeded inserts seed data when count is zero`() = runTest {
        // Given
        whenever(exerciseDao.count()).thenReturn(0)

        // When
        repository.seedIfNeeded()

        // Then
        verify(exerciseDao).insertAll(ExerciseSeedData.all())
    }

    @Test
    fun `seedIfNeeded skips when count is greater than zero`() = runTest {
        // Given
        whenever(exerciseDao.count()).thenReturn(5)

        // When
        repository.seedIfNeeded()

        // Then
        verify(exerciseDao, never()).insertAll(org.mockito.kotlin.any())
    }

    @Test
    fun `getForDay returns flow from dao`() = runTest {
        // Given
        val exercises = listOf(
            Exercise("1", "Bench", 1, "Chest", 3, 8, 10, Intensity.MODERATE, null, 0)
        )
        whenever(exerciseDao.getExercisesForDay(1)).thenReturn(flowOf(exercises))

        // When / Then
        repository.getForDay(1).test {
            assertEquals(exercises, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getAll returns flow from dao`() = runTest {
        // Given
        val exercises = listOf(
            Exercise("1", "Bench", 1, "Chest", 3, 8, 10, Intensity.MODERATE, null, 0)
        )
        whenever(exerciseDao.getAllExercises()).thenReturn(flowOf(exercises))

        // When / Then
        repository.getAll().test {
            assertEquals(exercises, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `search returns flow from dao`() = runTest {
        // Given
        val exercises = listOf(
            Exercise("1", "Bench", 1, "Chest", 3, 8, 10, Intensity.MODERATE, null, 0)
        )
        whenever(exerciseDao.searchByName("bench")).thenReturn(flowOf(exercises))

        // When / Then
        repository.search("bench").test {
            assertEquals(exercises, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getById returns exercise from dao`() = runTest {
        // Given
        val exercise = Exercise("1", "Bench", 1, "Chest", 3, 8, 10, Intensity.MODERATE, null, 0)
        whenever(exerciseDao.getById("1")).thenReturn(exercise)

        // When
        val result = repository.getById("1")

        // Then
        assertEquals(exercise, result)
    }

    @Test
    fun `getById returns null when not found`() = runTest {
        // Given
        whenever(exerciseDao.getById("99")).thenReturn(null)

        // When
        val result = repository.getById("99")

        // Then
        assertNull(result)
    }

    @Test
    fun `insert delegates to dao`() = runTest {
        // Given
        val exercise = Exercise("1", "Bench", 1, "Chest", 3, 8, 10, Intensity.MODERATE, null, 0)

        // When
        repository.insert(exercise)

        // Then
        verify(exerciseDao).insert(exercise)
    }

    @Test
    fun `update delegates to dao`() = runTest {
        // Given
        val exercise = Exercise("1", "Bench", 1, "Chest", 3, 8, 10, Intensity.MODERATE, null, 0)

        // When
        repository.update(exercise)

        // Then
        verify(exerciseDao).update(exercise)
    }

    @Test
    fun `delete delegates to dao`() = runTest {
        // Given
        val exercise = Exercise("1", "Bench", 1, "Chest", 3, 8, 10, Intensity.MODERATE, null, 0)

        // When
        repository.delete(exercise)

        // Then
        verify(exerciseDao).delete(exercise)
    }
}
