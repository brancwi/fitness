package com.muscu.app.data.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Query("SELECT * FROM measurements ORDER BY dateTimestamp DESC")
    fun getAllMeasurements(): Flow<List<Measurement>>

    @Query("SELECT * FROM measurements ORDER BY dateTimestamp DESC LIMIT 1")
    suspend fun getLatestMeasurement(): Measurement?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(measurement: Measurement)

    @Query("DELETE FROM measurements WHERE id = :id")
    suspend fun delete(id: String)
}
