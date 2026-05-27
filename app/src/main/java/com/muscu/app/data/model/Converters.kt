package com.muscu.app.data.model

import androidx.room.TypeConverter
import com.muscu.app.domain.model.Intensity

class Converters {
    @TypeConverter
    fun fromIntensity(intensity: Intensity): String = intensity.name

    @TypeConverter
    fun toIntensity(name: String): Intensity =
        Intensity.entries.find { it.name == name } ?: Intensity.MODERATE
}
