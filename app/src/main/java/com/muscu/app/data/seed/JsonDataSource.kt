package com.muscu.app.data.seed

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.muscu.app.data.model.Exercise
import com.muscu.app.domain.model.Intensity
import java.io.IOException

/**
 * Reads exercise and program seed data from JSON assets.
 *
 * @param context Android context used to open assets.
 * @param gson Gson instance for parsing.
 */
class JsonDataSource(
    private val context: Context,
    private val gson: Gson = Gson()
) {

    /**
     * Loads the list of exercises from assets/exercises.json.
     * Returns an empty list if the file is missing or malformed.
     */
    fun loadExercises(): List<Exercise> {
        return try {
            val json = context.assets.open("exercises.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<ExerciseJson>>() {}.type
            val parsed: List<ExerciseJson> = gson.fromJson(json, type) ?: emptyList()
            parsed.map { it.toEntity() }
        } catch (e: IOException) {
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Loads the list of programs from assets/programs.json.
     * Returns an empty list if the file is missing or malformed.
     */
    fun loadPrograms(): List<ProgramJson> {
        return try {
            val json = context.assets.open("programs.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<ProgramJson>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: IOException) {
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * DTO representing an exercise as stored in JSON.
     */
    data class ExerciseJson(
        val id: String,
        val name: String,
        val dayOfWeek: Int,
        val category: String,
        val targetSets: Int,
        val targetRepsMin: Int,
        val targetRepsMax: Int,
        val intensity: String,
        val warning: String? = null,
        val orderIndex: Int,
        val equipment: String = "",
        val objective: String = "",
        val difficulty: String = "Intermédiaire"
    ) {
        fun toEntity(): Exercise = Exercise(
            id = id,
            name = name,
            dayOfWeek = dayOfWeek,
            category = category,
            targetSets = targetSets,
            targetRepsMin = targetRepsMin,
            targetRepsMax = targetRepsMax,
            intensity = Intensity.valueOf(intensity),
            warning = warning,
            orderIndex = orderIndex,
            equipment = equipment,
            objective = objective,
            difficulty = difficulty
        )
    }

    /**
     * DTO representing a training program as stored in JSON.
     */
    data class ProgramJson(
        val id: String,
        val name: String,
        val description: String? = null,
        val dayOfWeek: Int,
        val exerciseIds: List<String>
    )
}
