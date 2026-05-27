package com.muscu.app.domain.model

/**
 * Intensity category for an exercise.
 * Does NOT carry default rest seconds anymore — those are user-configurable via AppSettings.
 */
enum class Intensity(val label: String) {
    MODERATE("Modérée"),
    LIGHT("Légère"),
    BODYWEIGHT("Corporel");

    companion object {
        fun fromLabel(label: String): Intensity =
            entries.find { it.label == label } ?: MODERATE
    }
}
