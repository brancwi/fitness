package com.muscu.app.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateExerciseDao {
    @Query("SELECT * FROM template_exercises WHERE templateId = :templateId ORDER BY orderIndex ASC")
    fun getForTemplate(templateId: String): Flow<List<TemplateExercise>>

    @Query("SELECT * FROM template_exercises WHERE templateId = :templateId ORDER BY orderIndex ASC")
    suspend fun getForTemplateSync(templateId: String): List<TemplateExercise>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TemplateExercise)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TemplateExercise>)

    @Update
    suspend fun update(item: TemplateExercise)

    @Delete
    suspend fun delete(item: TemplateExercise)

    @Query("DELETE FROM template_exercises WHERE templateId = :templateId")
    suspend fun deleteForTemplate(templateId: String)
}
