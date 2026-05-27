package com.muscu.app.data.repository

import com.muscu.app.data.model.Measurement
import com.muscu.app.data.model.MeasurementDao
import kotlinx.coroutines.flow.Flow

class MeasurementRepository(private val measurementDao: MeasurementDao) {

    fun getAll(): Flow<List<Measurement>> = measurementDao.getAllMeasurements()

    suspend fun getLatest(): Measurement? = measurementDao.getLatestMeasurement()

    suspend fun save(measurement: Measurement) = measurementDao.insert(measurement)

    suspend fun delete(id: String) = measurementDao.delete(id)
}
