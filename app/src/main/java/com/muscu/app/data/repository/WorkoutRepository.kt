package com.muscu.app.data.repository

import com.muscu.app.data.model.AppDatabase
import com.muscu.app.data.model.Exercise
import com.muscu.app.data.model.Measurement
import com.muscu.app.data.model.PerformedSet
import com.muscu.app.data.model.UserProfile
import com.muscu.app.data.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

/**
 * Facade repository exposing all domain operations.
 * Delegates to specialized repositories to respect SRP.
 */
class WorkoutRepository(db: AppDatabase, appSettingsRepository: AppSettingsRepository) {

    private val exerciseRepository = ExerciseRepository(db.exerciseDao())
    private val sessionRepository = WorkoutSessionRepository(db.workoutSessionDao())
    private val setRepository = PerformedSetRepository(db.performedSetDao(), appSettingsRepository)
    private val profileRepository = ProfileRepository(db.userProfileDao())
    private val measurementRepository = MeasurementRepository(db.measurementDao())

    // ── Exercise ──
    suspend fun seedExercisesIfNeeded() = exerciseRepository.seedIfNeeded()
    fun getExercisesForDay(day: Int): Flow<List<Exercise>> = exerciseRepository.getForDay(day)
    fun getAllExercises(): Flow<List<Exercise>> = exerciseRepository.getAll()

    // ── Workout Session ──
    suspend fun startSession(dayOfWeek: Int): WorkoutSession = sessionRepository.startSession(dayOfWeek)
    suspend fun getLastSessionForDay(day: Int): WorkoutSession? = sessionRepository.getLastForDay(day)
    suspend fun updateSession(session: WorkoutSession) = sessionRepository.update(session)

    // ── Performed Set ──
    fun getSetsForExercise(sessionId: String, exerciseId: String): Flow<List<PerformedSet>> =
        setRepository.getForExercise(sessionId, exerciseId)

    suspend fun ensureSetsForExercise(sessionId: String, exercise: Exercise) =
        setRepository.ensureSetsForExercise(sessionId, exercise)

    suspend fun updateSet(set: PerformedSet) = setRepository.update(set)

    // ── User Profile ──
    fun getUserProfile(): Flow<UserProfile?> = profileRepository.getProfile()
    suspend fun saveUserProfile(weightKg: Float, targetGrams: Int) =
        profileRepository.save(weightKg, targetGrams)

    // ── Measurement ──
    fun getAllMeasurements(): Flow<List<Measurement>> = measurementRepository.getAll()
    suspend fun getLatestMeasurement(): Measurement? = measurementRepository.getLatest()
    suspend fun saveMeasurement(measurement: Measurement) = measurementRepository.save(measurement)
    suspend fun deleteMeasurement(id: String) = measurementRepository.delete(id)
}
