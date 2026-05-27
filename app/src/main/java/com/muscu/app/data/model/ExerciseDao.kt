package com.muscu.app.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE dayOfWeek = :day ORDER BY orderIndex ASC")
    fun getExercisesForDay(day: Int): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises ORDER BY dayOfWeek ASC, orderIndex ASC")
    fun getAllExercises(): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<Exercise>)

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun count(): Int
}
