package com.muscu.app.domain.model

import com.muscu.app.data.model.Exercise

data class DayProgram(
    val dayName: String,
    val dayIndex: Int,
    val exercises: List<Exercise>
)
