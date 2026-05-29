package com.muscu.app.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions WHERE dateTimestamp >= :start AND dateTimestamp < :end ORDER BY dateTimestamp DESC")
    fun getSessionsInRange(start: Long, end: Long): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE dayOfWeek = :day ORDER BY dateTimestamp DESC LIMIT 1")
    suspend fun getLastSessionForDay(day: Int): WorkoutSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: WorkoutSession): Long

    @Update
    suspend fun update(session: WorkoutSession)

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    suspend fun getById(id: String): WorkoutSession?

    @Query("DELETE FROM workout_sessions WHERE id = :id")
    suspend fun deleteById(id: String)
}
