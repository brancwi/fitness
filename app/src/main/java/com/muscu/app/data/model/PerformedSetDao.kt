package com.muscu.app.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PerformedSetDao {
    @Query("SELECT * FROM performed_sets WHERE sessionId = :sessionId AND exerciseId = :exerciseId ORDER BY setNumber ASC")
    fun getSetsForExercise(sessionId: String, exerciseId: String): Flow<List<PerformedSet>>

    @Query("SELECT * FROM performed_sets WHERE sessionId = :sessionId ORDER BY exerciseId, setNumber ASC")
    fun getAllSetsForSession(sessionId: String): Flow<List<PerformedSet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(set: PerformedSet)

    @Update
    suspend fun update(set: PerformedSet)

    @Query("DELETE FROM performed_sets WHERE sessionId = :sessionId AND exerciseId = :exerciseId")
    suspend fun deleteForExercise(sessionId: String, exerciseId: String)

    /**
     * Get the most recent completed set for a given exercise across all sessions.
     */
    @Query("""
        SELECT * FROM performed_sets 
        WHERE exerciseId = :exerciseId AND isCompleted = 1 
        ORDER BY id DESC LIMIT 1
    """)
    suspend fun getLastCompletedSetForExercise(exerciseId: String): PerformedSet?

    /**
     * Get all completed sets for an exercise with their session dates,
     * ordered chronologically (oldest first).
     */
    @Query("""
        SELECT ps.id, ps.sessionId, ps.exerciseId, ps.setNumber, ps.reps, ps.weightKg, 
               ps.isCompleted, ps.notes, ps.restSeconds, ws.dateTimestamp as sessionDate
        FROM performed_sets ps
        INNER JOIN workout_sessions ws ON ps.sessionId = ws.id
        WHERE ps.exerciseId = :exerciseId AND ps.isCompleted = 1
        ORDER BY ws.dateTimestamp ASC, ps.setNumber ASC
    """)
    suspend fun getCompletedSetsWithDateForExercise(exerciseId: String): List<PerformedSetWithDate>
}
