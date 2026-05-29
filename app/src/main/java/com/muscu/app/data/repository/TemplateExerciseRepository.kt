package com.muscu.app.data.repository

import com.muscu.app.data.model.TemplateExercise
import com.muscu.app.data.model.TemplateExerciseDao
import kotlinx.coroutines.flow.Flow

class TemplateExerciseRepository(private val dao: TemplateExerciseDao) {

    fun getForTemplate(templateId: String): Flow<List<TemplateExercise>> =
        dao.getForTemplate(templateId)

    suspend fun getForTemplateSync(templateId: String): List<TemplateExercise> =
        dao.getForTemplateSync(templateId)

    suspend fun insert(item: TemplateExercise) = dao.insert(item)

    suspend fun insertAll(items: List<TemplateExercise>) = dao.insertAll(items)

    suspend fun update(item: TemplateExercise) = dao.update(item)

    suspend fun delete(item: TemplateExercise) = dao.delete(item)

    suspend fun deleteForTemplate(templateId: String) = dao.deleteForTemplate(templateId)
}
