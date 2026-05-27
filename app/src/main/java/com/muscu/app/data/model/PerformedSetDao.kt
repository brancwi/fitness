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
}
