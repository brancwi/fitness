package com.muscu.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User-modifiable application configuration.
 * Single-row table (id = 1) holding all runtime settings.
 */
@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,

    // Workout schedule
    val workoutDaysJson: String = "[2,4,6]",
    val dayNamesJson: String = "{\"2\":\"Mardi\",\"4\":\"Jeudi\",\"6\":\"Samedi\"}",

    // Rest times per intensity (seconds)
    val moderateRestSeconds: Int = 90,
    val lightRestSeconds: Int = 60,
    val bodyweightRestSeconds: Int = 45,

    // Slider range for rest time
    val minRestSeconds: Int = 15,
    val maxRestSeconds: Int = 180,

    // Audio preferences
    val beepDurationMs: Int = 200,
    val finalBeepDurationMs: Int = 400,
    val toneVolume: Int = 100,

    // Lumbar rules (JSON array of strings)
    val lumbarRulesJson: String = "[\"Pas de squat lourd\",\"Pas de soulevé de terre\",\"Pas de développé militaire debout lourd\",\"Bench et rowing sûrs (colonne soutenue)\",\"Fentes : haltères légers, sans rebond\"]",

    // Default protein target (grams)
    val defaultProteinTargetGrams: Int = 150,

    // Auto-start next set when rest timer finishes
    val autoStartNextSet: Boolean = true,

    // Auto-fill reps/weight from previous set or defaults
    val autoFillRepsWeight: Boolean = true,
    val defaultReps: Int = 10,
    val defaultWeightKg: Float = 10f,

    // Tempo guide (seconds per phase)
    val tempoEccentric: Int = 3,
    val tempoIsometricBottom: Int = 0,
    val tempoConcentric: Int = 1,
    val tempoIsometricTop: Int = 1,

    // Profile: "puissance", "force", "hypertrophie", "endurance"
    val tempoProfile: String = "hypertrophie",

    // Accent: "neutre", "excentrique", "concentrique"
    val tempoAccent: String = "neutre",

    // Guide speed multiplier
    val guideSpeedMultiplier: Float = 1.0f,

    // Prep countdown before set starts (seconds)
    val prepCountdownSeconds: Int = 5
)
