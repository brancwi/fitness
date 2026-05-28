package com.muscu.app.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded

/**
 * PerformedSet enriched with its parent session date for history queries.
 */
data class PerformedSetWithDate(
    @Embedded val set: PerformedSet,
    @ColumnInfo(name = "sessionDate") val sessionDate: Long
)
