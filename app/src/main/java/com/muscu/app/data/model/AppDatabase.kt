package com.muscu.app.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        Exercise::class,
        WorkoutSession::class,
        PerformedSet::class,
        UserProfile::class,
        Measurement::class,
        AppSettings::class
    ],
    version = 13,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun performedSetDao(): PerformedSetDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun measurementDao(): MeasurementDao
    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private var hasSeeded = false

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "muscu_database"
                )
                    .addMigrations(*Migrations.ALL)
                    .build()
                INSTANCE = instance

                // Seed demo measurements once after DB creation
                if (!hasSeeded) {
                    hasSeeded = true
                    GlobalScope.launch(Dispatchers.IO) {
                        // Only seed if table is empty
                        if (instance.measurementDao().getLatestMeasurement() == null) {
                            DatabaseSeeder.seedMeasurements(instance.measurementDao())
                        }
                    }
                }

                instance
            }
        }
    }
}
