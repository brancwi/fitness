package com.muscu.app.data.repository

import com.muscu.app.data.model.AppDatabase
import com.muscu.app.data.model.Exercise
import com.muscu.app.data.model.Measurement
import com.muscu.app.data.model.PerformedSet
import com.muscu.app.data.model.PerformedSetWithDate
import com.muscu.app.data.model.TemplateExercise
import com.muscu.app.data.model.UserProfile
import com.muscu.app.data.model.WorkoutSession
import com.muscu.app.data.model.WorkoutTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Facade repository exposing all domain operations.
 * Delegates to specialized repositories to respect SRP.
 */
class WorkoutRepository(
    db: AppDatabase,
    appSettingsRepository: AppSettingsRepository,
    jsonDataSource: com.muscu.app.data.seed.JsonDataSource? = null
) {

    private val exerciseRepository = ExerciseRepository(db.exerciseDao(), jsonDataSource)
    private val templateRepository = WorkoutTemplateRepository(db.workoutTemplateDao())
    private val templateExerciseRepository = TemplateExerciseRepository(db.templateExerciseDao())
    private val sessionRepository = WorkoutSessionRepository(db.workoutSessionDao())
    private val setRepository = PerformedSetRepository(db.performedSetDao(), appSettingsRepository)
    private val profileRepository = ProfileRepository(db.userProfileDao())
    private val measurementRepository = MeasurementRepository(db.measurementDao())

    // ── Exercise (Catalog) ──
    suspend fun seedExercisesIfNeeded() = exerciseRepository.seedIfNeeded()
    fun getExercisesForDay(day: Int): Flow<List<Exercise>> = exerciseRepository.getForDay(day)
    fun getAllExercises(): Flow<List<Exercise>> = exerciseRepository.getAll()
    fun searchExercises(query: String): Flow<List<Exercise>> = exerciseRepository.search(query)
    suspend fun getExerciseById(id: String): Exercise? = exerciseRepository.getById(id)
    suspend fun saveExercise(exercise: Exercise) = exerciseRepository.insert(exercise)
    suspend fun updateExercise(exercise: Exercise) = exerciseRepository.update(exercise)
    suspend fun deleteExercise(exercise: Exercise) = exerciseRepository.delete(exercise)

    // ── Workout Templates ──
    fun getAllTemplates(): Flow<List<WorkoutTemplate>> = templateRepository.getAll()
    suspend fun getTemplateById(id: String): WorkoutTemplate? = templateRepository.getById(id)
    fun getTemplatesByDay(day: Int): Flow<List<WorkoutTemplate>> = templateRepository.getByDay(day)
    suspend fun saveTemplate(template: WorkoutTemplate) = templateRepository.insert(template)
    suspend fun updateTemplate(template: WorkoutTemplate) = templateRepository.update(template)
    suspend fun deleteTemplate(template: WorkoutTemplate) = templateRepository.delete(template)

    // ── Template Exercises ──
    fun getTemplateExercises(templateId: String): Flow<List<TemplateExercise>> =
        templateExerciseRepository.getForTemplate(templateId)

    suspend fun getTemplateExercisesSync(templateId: String): List<TemplateExercise> =
        templateExerciseRepository.getForTemplateSync(templateId)

    suspend fun saveTemplateExercise(item: TemplateExercise) = templateExerciseRepository.insert(item)
    suspend fun saveTemplateExercises(items: List<TemplateExercise>) = templateExerciseRepository.insertAll(items)
    suspend fun updateTemplateExercise(item: TemplateExercise) = templateExerciseRepository.update(item)
    suspend fun deleteTemplateExercise(item: TemplateExercise) = templateExerciseRepository.delete(item)
    suspend fun deleteTemplateExercisesForTemplate(templateId: String) = templateExerciseRepository.deleteForTemplate(templateId)

    // ── Workout Session ──
    suspend fun startSession(dayOfWeek: Int): WorkoutSession = sessionRepository.startSession(dayOfWeek)
    suspend fun getLastSessionForDay(day: Int): WorkoutSession? = sessionRepository.getLastForDay(day)
    suspend fun updateSession(session: WorkoutSession) = sessionRepository.update(session)
    suspend fun deleteWorkoutSession(id: String) = sessionRepository.deleteById(id)
    fun getAllSessions(): Flow<List<WorkoutSession>> = sessionRepository.getAllSessions()

    // ── Performed Set ──
    fun getSetsForExercise(sessionId: String, exerciseId: String): Flow<List<PerformedSet>> =
        setRepository.getForExercise(sessionId, exerciseId)

    suspend fun ensureSetsForExercise(sessionId: String, exercise: Exercise) =
        setRepository.ensureSetsForExercise(sessionId, exercise)

    suspend fun ensureSetsForExerciseWithConfig(
        sessionId: String,
        exercise: Exercise,
        targetSets: Int,
        restSeconds: Int?
    ) = setRepository.ensureSetsForExerciseWithConfig(sessionId, exercise, targetSets, restSeconds)

    suspend fun updateSet(set: PerformedSet) = setRepository.update(set)

    suspend fun getLastCompletedSetForExercise(exerciseId: String): PerformedSet? =
        setRepository.getLastCompletedSetForExercise(exerciseId)

    suspend fun getPerformanceHistoryForExercise(exerciseId: String): List<PerformedSetWithDate> =
        setRepository.getPerformanceHistory(exerciseId)

    suspend fun deletePerformedSet(id: String) = setRepository.deleteById(id)

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
