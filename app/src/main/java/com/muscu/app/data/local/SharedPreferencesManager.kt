package com.muscu.app.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Simple wrapper around SharedPreferences for storing user-defined reference weights.
 * Used as a fallback when no workout history exists yet.
 */
class SharedPreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getReferenceWeight(exerciseId: String): Float? {
        val key = "ref_weight_$exerciseId"
        return if (prefs.contains(key)) prefs.getFloat(key, 0f) else null
    }

    fun setReferenceWeight(exerciseId: String, weightKg: Float) {
        prefs.edit { putFloat("ref_weight_$exerciseId", weightKg) }
    }

    fun removeReferenceWeight(exerciseId: String) {
        prefs.edit { remove("ref_weight_$exerciseId") }
    }

    fun getAllReferenceWeights(): Map<String, Float> {
        return prefs.all
            .filterKeys { it.startsWith("ref_weight_") }
            .mapKeys { it.key.removePrefix("ref_weight_") }
            .mapValues { it.value as Float }
    }

    companion object {
        private const val PREFS_NAME = "muscu_prefs"
    }
}
