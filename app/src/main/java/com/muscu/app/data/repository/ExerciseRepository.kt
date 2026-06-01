package com.muscu.app.data.repository

import com.muscu.app.data.model.Exercise
import com.muscu.app.data.model.ExerciseDao
import com.muscu.app.data.seed.JsonDataSource
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(
    private val exerciseDao: ExerciseDao,
    private val jsonDataSource: JsonDataSource? = null
) {

    suspend fun seedIfNeeded() {
        if (exerciseDao.count() == 0) {
            val exercises = jsonDataSource?.loadExercises()
                ?: com.muscu.app.data.seed.ExerciseSeedData.all()
            exerciseDao.insertAll(exercises)
        }
    }

    fun getForDay(day: Int): Flow<List<Exercise>> = exerciseDao.getExercisesForDay(day)

    fun getAll(): Flow<List<Exercise>> = exerciseDao.getAllExercises()

    fun search(query: String): Flow<List<Exercise>> = exerciseDao.searchByName(query)

    suspend fun getById(id: String): Exercise? = exerciseDao.getById(id)

    suspend fun insert(exercise: Exercise) = exerciseDao.insert(exercise)

    suspend fun update(exercise: Exercise) = exerciseDao.update(exercise)

    suspend fun delete(exercise: Exercise) = exerciseDao.delete(exercise)
}
