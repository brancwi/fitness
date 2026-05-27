package com.muscu.app.data.repository

import com.muscu.app.data.model.Exercise
import com.muscu.app.data.model.ExerciseDao
import com.muscu.app.data.seed.ExerciseSeedData
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(private val exerciseDao: ExerciseDao) {

    suspend fun seedIfNeeded() {
        if (exerciseDao.count() > 0) return
        exerciseDao.insertAll(ExerciseSeedData.all())
    }

    fun getForDay(day: Int): Flow<List<Exercise>> = exerciseDao.getExercisesForDay(day)

    fun getAll(): Flow<List<Exercise>> = exerciseDao.getAllExercises()
}
