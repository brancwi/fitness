package com.muscu.app.data.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID

object DatabaseSeeder {

    fun seedMeasurements(measurementDao: MeasurementDao) {
        GlobalScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val dayMs = 24L * 60 * 60 * 1000

            data class Seed(
                val offsetDays: Int,
                val weightKg: Float,
                val bodyFatPercent: Float,
                val musclePercent: Float,
                val chestCm: Float,
                val armsCm: Float,
                val waistCm: Float,
                val hipsCm: Float,
                val thighsCm: Float,
                val calvesCm: Float
            )

            val seeds = listOf(
                Seed(0,    80.5f, 17.0f, 39.0f, 103.0f, 35.5f, 86.0f, 97.5f, 58.5f, 39.0f),
                Seed(-14,  80.8f, 17.2f, 38.8f, 102.5f, 35.3f, 86.5f, 98.0f, 58.0f, 38.8f),
                Seed(-28,  81.2f, 17.5f, 38.5f, 102.0f, 35.0f, 87.0f, 98.5f, 57.5f, 38.5f),
                Seed(-42,  81.5f, 17.8f, 38.2f, 101.5f, 34.8f, 87.5f, 99.0f, 57.0f, 38.2f),
                Seed(-56,  82.0f, 18.2f, 37.8f, 101.0f, 34.5f, 88.0f, 99.5f, 56.5f, 38.0f),
                Seed(-70,  82.5f, 18.5f, 37.5f, 100.5f, 34.2f, 88.5f, 100.0f, 56.0f, 37.8f),
                Seed(-84,  83.0f, 19.0f, 37.0f, 100.0f, 34.0f, 89.0f, 100.5f, 55.5f, 37.5f),
                Seed(-98,  83.5f, 19.3f, 36.8f, 99.5f,  33.8f, 89.5f, 101.0f, 55.0f, 37.2f),
                Seed(-112, 84.0f, 19.8f, 36.5f, 99.0f,  33.5f, 90.0f, 101.5f, 54.5f, 37.0f),
                Seed(-126, 84.5f, 20.2f, 36.0f, 98.5f,  33.2f, 90.5f, 102.0f, 54.0f, 36.8f),
                Seed(-140, 85.0f, 20.5f, 35.8f, 98.0f,  33.0f, 91.0f, 102.5f, 53.5f, 36.5f),
                Seed(-154, 85.5f, 21.0f, 35.5f, 97.5f,  32.8f, 91.5f, 103.0f, 53.0f, 36.2f),
            )

            seeds.forEach { s ->
                measurementDao.insert(
                    Measurement(
                        id = UUID.randomUUID().toString(),
                        dateTimestamp = now + s.offsetDays * dayMs,
                        weightKg = s.weightKg,
                        bodyFatPercent = s.bodyFatPercent,
                        musclePercent = s.musclePercent,
                        chestCm = s.chestCm,
                        armsCm = s.armsCm,
                        waistCm = s.waistCm,
                        hipsCm = s.hipsCm,
                        thighsCm = s.thighsCm,
                        calvesCm = s.calvesCm
                    )
                )
            }
        }
    }
}
