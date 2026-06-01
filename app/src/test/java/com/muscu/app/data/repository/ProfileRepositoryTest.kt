package com.muscu.app.data.repository

import app.cash.turbine.test
import com.muscu.app.data.model.UserProfile
import com.muscu.app.data.model.UserProfileDao
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
class ProfileRepositoryTest {

    private lateinit var profileDao: UserProfileDao
    private lateinit var repository: ProfileRepository

    @Before
    fun setUp() {
        profileDao = mock()
        repository = ProfileRepository(profileDao)
    }

    @Test
    fun `getProfile returns flow from dao`() = runTest {
        // Given
        val profile = UserProfile(id = 1, weightKg = 75f, dailyProteinTargetGrams = 150)
        whenever(profileDao.getProfile()).thenReturn(flowOf(profile))

        // When / Then
        repository.getProfile().test {
            assertEquals(profile, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getProfile emits null when no profile`() = runTest {
        // Given
        whenever(profileDao.getProfile()).thenReturn(flowOf(null))

        // When / Then
        repository.getProfile().test {
            assertNull(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `save inserts profile with given values`() = runTest {
        // Given
        val weightKg = 80f
        val targetGrams = 160

        // When
        repository.save(weightKg, targetGrams)

        // Then
        verify(profileDao).insert(UserProfile(weightKg = weightKg, dailyProteinTargetGrams = targetGrams))
    }
}
