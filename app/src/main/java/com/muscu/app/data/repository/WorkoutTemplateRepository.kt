package com.muscu.app.data.repository

import com.muscu.app.data.model.WorkoutTemplate
import com.muscu.app.data.model.WorkoutTemplateDao
import kotlinx.coroutines.flow.Flow

class WorkoutTemplateRepository(private val dao: WorkoutTemplateDao) {

    fun getAll(): Flow<List<WorkoutTemplate>> = dao.getAll()

    suspend fun getById(id: String): WorkoutTemplate? = dao.getById(id)

    fun getByDay(day: Int): Flow<List<WorkoutTemplate>> = dao.getByDay(day)

    suspend fun insert(template: WorkoutTemplate) = dao.insert(template)

    suspend fun update(template: WorkoutTemplate) = dao.update(template)

    suspend fun delete(template: WorkoutTemplate) = dao.delete(template)
}
