package com.muscu.app.domain.calculator

import com.muscu.app.domain.model.Intensity

object RestTimeRecommendations {

    fun text(intensity: Intensity, settings: com.muscu.app.data.model.AppSettings): String {
        val (min, max) = when (intensity) {
            Intensity.MODERATE -> settings.moderateRestSeconds to settings.moderateRestSeconds + 30
            Intensity.LIGHT -> settings.lightRestSeconds to settings.lightRestSeconds + 30
            Intensity.BODYWEIGHT -> settings.bodyweightRestSeconds - 15 to settings.bodyweightRestSeconds
        }
        return "$min-$max sec"
    }
}
