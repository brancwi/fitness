package com.muscu.app.data.repository

import com.muscu.app.data.model.AppSettings
import com.muscu.app.data.model.AppSettingsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class AppSettingsRepository(private val dao: AppSettingsDao) {

    val settings: Flow<AppSettings> = dao.getSettings().map { it ?: AppSettings() }

    suspend fun seedDefaultsIfNeeded() {
        if (dao.count() == 0) {
            dao.insert(AppSettings())
        }
    }

    suspend fun update(settings: AppSettings) = dao.update(settings)

    fun workoutDays(): Flow<List<Int>> = settings.map { s ->
        JSONArray(s.workoutDaysJson).let { arr ->
            List(arr.length()) { arr.getInt(it) }
        }
    }

    fun dayNames(): Flow<Map<Int, String>> = settings.map { s ->
        JSONObject(s.dayNamesJson).let { obj ->
            obj.keys().asSequence().associate { it.toInt() to obj.getString(it) }
        }
    }

    fun lumbarRules(): Flow<List<String>> = settings.map { s ->
        JSONArray(s.lumbarRulesJson).let { arr ->
            List(arr.length()) { arr.getString(it) }
        }
    }

    suspend fun getLatest(): AppSettings = settings.firstOrNull() ?: AppSettings()

    suspend fun updateWorkoutDays(days: List<Int>, names: Map<Int, String>) {
        val current = getLatest()
        dao.update(current.copy(
            workoutDaysJson = JSONArray(days).toString(),
            dayNamesJson = JSONObject(names.mapKeys { it.key.toString() }).toString()
        ))
    }

    suspend fun updateRestTimes(moderate: Int, light: Int, bodyweight: Int) {
        val current = getLatest()
        dao.update(current.copy(
            moderateRestSeconds = moderate,
            lightRestSeconds = light,
            bodyweightRestSeconds = bodyweight
        ))
    }

    suspend fun updateRestSliderRange(min: Int, max: Int) {
        val current = getLatest()
        dao.update(current.copy(minRestSeconds = min, maxRestSeconds = max))
    }

    suspend fun updateAudioPrefs(beepMs: Int, finalBeepMs: Int, volume: Int) {
        val current = getLatest()
        dao.update(current.copy(
            beepDurationMs = beepMs,
            finalBeepDurationMs = finalBeepMs,
            toneVolume = volume
        ))
    }

    suspend fun updateLumbarRules(rules: List<String>) {
        val current = getLatest()
        dao.update(current.copy(lumbarRulesJson = JSONArray(rules).toString()))
    }

    suspend fun updateAutoStart(enabled: Boolean) {
        val current = getLatest()
        dao.update(current.copy(autoStartNextSet = enabled))
    }

    suspend fun updateAutoFill(enabled: Boolean, defaultReps: Int, defaultWeightKg: Float) {
        val current = getLatest()
        dao.update(current.copy(
            autoFillRepsWeight = enabled,
            defaultReps = defaultReps,
            defaultWeightKg = defaultWeightKg
        ))
    }

    suspend fun updateGuideSpeedMultiplier(multiplier: Float) {
        val current = getLatest()
        update(current.copy(guideSpeedMultiplier = multiplier))
    }

    suspend fun updatePrepCountdown(seconds: Int) {
        val current = getLatest()
        update(current.copy(prepCountdownSeconds = seconds.coerceAtLeast(0)))
    }

    suspend fun updateTempo(eccentric: Int, isoBottom: Int, concentric: Int, isoTop: Int) {
        val current = getLatest()
        dao.update(current.copy(
            tempoEccentric = eccentric,
            tempoIsometricBottom = isoBottom,
            tempoConcentric = concentric,
            tempoIsometricTop = isoTop
        ))
    }

    suspend fun updateTempoAccent(accent: String) {
        val current = getLatest()
        dao.update(current.copy(tempoAccent = accent))
    }

    suspend fun updateTempoProfile(profile: String) {
        val current = getLatest()
        dao.update(current.copy(tempoProfile = profile))
    }
}
