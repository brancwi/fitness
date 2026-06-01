package com.muscu.app

import android.app.Application
import com.muscu.app.data.model.AppDatabase
import com.muscu.app.data.repository.AppSettingsRepository
import com.muscu.app.data.repository.WorkoutRepository

class MuscuApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val appSettingsRepository by lazy { AppSettingsRepository(database.appSettingsDao()) }
    val jsonDataSource by lazy { com.muscu.app.data.seed.JsonDataSource(this) }
    val repository by lazy { WorkoutRepository(database, appSettingsRepository, jsonDataSource) }
}
