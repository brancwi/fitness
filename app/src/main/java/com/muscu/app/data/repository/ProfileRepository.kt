package com.muscu.app.data.repository

import com.muscu.app.data.model.UserProfile
import com.muscu.app.data.model.UserProfileDao
import kotlinx.coroutines.flow.Flow

class ProfileRepository(private val profileDao: UserProfileDao) {

    fun getProfile(): Flow<UserProfile?> = profileDao.getProfile()

    suspend fun save(weightKg: Float, targetGrams: Int) {
        profileDao.insert(UserProfile(weightKg = weightKg, dailyProteinTargetGrams = targetGrams))
    }
}
