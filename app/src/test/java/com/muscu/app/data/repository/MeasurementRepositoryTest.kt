package com.muscu.app.data.repository

import app.cash.turbine.test
import com.muscu.app.data.model.Measurement
import com.muscu.app.data.model.MeasurementDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class MeasurementRepositoryTest {

    private lateinit var measurementDao: MeasurementDao
    private lateinit var repository: MeasurementRepository

    @Before
    fun setUp() {
        measurementDao = mock()
        repository = MeasurementRepository(measurementDao)
    }

    @Test
    fun `getAll returns flow from dao`() = runTest {
        // Given
        val measurements = listOf(
            Measurement("m1", 123456L, weightKg = 75f),
            Measurement("m2", 123457L, weightKg = 76f)
        )
        whenever(measurementDao.getAllMeasurements()).thenReturn(flowOf(measurements))

        // When / Then
        repository.getAll().test {
            assertEquals(measurements, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getLatest returns measurement from dao`() = runTest {
        // Given
        val measurement = Measurement("m1", 123456L, weightKg = 75f)
        whenever(measurementDao.getLatestMeasurement()).thenReturn(measurement)

        // When
        val result = repository.getLatest()

        // Then
        assertEquals(measurement, result)
    }

    @Test
    fun `getLatest returns null when no measurement`() = runTest {
        // Given
        whenever(measurementDao.getLatestMeasurement()).thenReturn(null)

        // When
        val result = repository.getLatest()

        // Then
        assertNull(result)
    }

    @Test
    fun `save delegates insert to dao`() = runTest {
        // Given
        val measurement = Measurement("m1", 123456L, weightKg = 75f)

        // When
        repository.save(measurement)

        // Then
        verify(measurementDao).insert(measurement)
    }

    @Test
    fun `delete delegates to dao`() = runTest {
        // When
        repository.delete("m1")

        // Then
        verify(measurementDao).delete("m1")
    }
}
