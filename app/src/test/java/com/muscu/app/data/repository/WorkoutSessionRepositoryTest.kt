package com.muscu.app.data.repository

import app.cash.turbine.test
import com.muscu.app.data.model.WorkoutSession
import com.muscu.app.data.model.WorkoutSessionDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class WorkoutSessionRepositoryTest {

    private lateinit var sessionDao: WorkoutSessionDao
    private lateinit var repository: WorkoutSessionRepository

    @Before
    fun setUp() {
        sessionDao = mock()
        repository = WorkoutSessionRepository(sessionDao)
    }

    @Test
    fun `startSession creates session with UUID and timestamp and inserts it`() = runTest {
        // Given
        val dayOfWeek = 2

        // When
        val session = repository.startSession(dayOfWeek)

        // Then
        assertNotNull(session.id)
        assertEquals(dayOfWeek, session.dayOfWeek)
        assertEquals(false, session.isCompleted)
        verify(sessionDao).insert(session)
    }

    @Test
    fun `getLastForDay returns session from dao`() = runTest {
        // Given
        val session = WorkoutSession("s1", 2, 123456L)
        whenever(sessionDao.getLastSessionForDay(2)).thenReturn(session)

        // When
        val result = repository.getLastForDay(2)

        // Then
        assertEquals(session, result)
    }

    @Test
    fun `getLastForDay returns null when no session`() = runTest {
        // Given
        whenever(sessionDao.getLastSessionForDay(2)).thenReturn(null)

        // When
        val result = repository.getLastForDay(2)

        // Then
        assertNull(result)
    }

    @Test
    fun `update delegates to dao`() = runTest {
        // Given
        val session = WorkoutSession("s1", 2, 123456L)

        // When
        repository.update(session)

        // Then
        verify(sessionDao).update(session)
    }

    @Test
    fun `deleteById delegates to dao`() = runTest {
        // When
        repository.deleteById("s1")

        // Then
        verify(sessionDao).deleteById("s1")
    }

    @Test
    fun `getAllSessions returns flow from dao`() = runTest {
        // Given
        val sessions = listOf(
            WorkoutSession("s1", 2, 123456L),
            WorkoutSession("s2", 4, 123457L)
        )
        whenever(sessionDao.getAllSessions()).thenReturn(flowOf(sessions))

        // When / Then
        repository.getAllSessions().test {
            assertEquals(sessions, awaitItem())
            awaitComplete()
        }
    }
}
