package com.muscu.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.muscu.app.domain.model.Intensity

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey val id: String,
    val name: String,
    val dayOfWeek: Int, // 1=Lundi, 2=Mardi...
    val category: String,
    val targetSets: Int,
    val targetRepsMin: Int,
    val targetRepsMax: Int,
    val intensity: Intensity,
    val warning: String? = null,
    val orderIndex: Int
)

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey val id: String,
    val dayOfWeek: Int,
    val dateTimestamp: Long,
    val isCompleted: Boolean = false,
    val overallRating: Int? = null,
    val energyLevel: Int? = null,
    val perceivedEffort: Int? = null,
    val sessionNotes: String? = null
)

@Entity(tableName = "performed_sets")
data class PerformedSet(
    @PrimaryKey val id: String,
    val sessionId: String,
    val exerciseId: String,
    val setNumber: Int,
    val reps: Int? = null,
    val weightKg: Float? = null,
    val isCompleted: Boolean = false,
    val notes: String? = null,
    val restSeconds: Int = 90
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val weightKg: Float? = null,
    val dailyProteinTargetGrams: Int = 150
)

@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey val id: String,
    val dateTimestamp: Long,
    val weightKg: Float? = null,
    val bodyFatPercent: Float? = null,
    val musclePercent: Float? = null,
    val chestCm: Float? = null,
    val armsCm: Float? = null,
    val waistCm: Float? = null,
    val hipsCm: Float? = null,
    val thighsCm: Float? = null,
    val calvesCm: Float? = null
)
