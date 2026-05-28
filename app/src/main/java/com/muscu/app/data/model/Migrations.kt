package com.muscu.app.data.model

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {

    /**
     * Migration 12 → 13
     * Cleans up legacy column from app_settings to make the app generic.
     * SQLite on API 26 does not support DROP COLUMN, so we recreate the table.
     */
    val MIGRATION_12_13 = object : Migration(12, 13) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // 1. Create new table without legacy rules column
            db.execSQL(
                """
                CREATE TABLE app_settings_new (
                    id INTEGER PRIMARY KEY NOT NULL,
                    workoutDaysJson TEXT NOT NULL,
                    dayNamesJson TEXT NOT NULL,
                    moderateRestSeconds INTEGER NOT NULL,
                    lightRestSeconds INTEGER NOT NULL,
                    bodyweightRestSeconds INTEGER NOT NULL,
                    minRestSeconds INTEGER NOT NULL,
                    maxRestSeconds INTEGER NOT NULL,
                    beepDurationMs INTEGER NOT NULL,
                    finalBeepDurationMs INTEGER NOT NULL,
                    toneVolume INTEGER NOT NULL,
                    defaultProteinTargetGrams INTEGER NOT NULL,
                    autoStartNextSet INTEGER NOT NULL,
                    autoFillRepsWeight INTEGER NOT NULL,
                    defaultReps INTEGER NOT NULL,
                    defaultWeightKg REAL NOT NULL,
                    tempoEccentric INTEGER NOT NULL,
                    tempoIsometricBottom INTEGER NOT NULL,
                    tempoConcentric INTEGER NOT NULL,
                    tempoIsometricTop INTEGER NOT NULL,
                    tempoProfile TEXT NOT NULL,
                    tempoAccent TEXT NOT NULL,
                    guideSpeedMultiplier REAL NOT NULL,
                    prepCountdownSeconds INTEGER NOT NULL
                )
                """.trimIndent()
            )

            // 2. Copy data from old table (all relevant columns)
            db.execSQL(
                """
                INSERT INTO app_settings_new (
                    id, workoutDaysJson, dayNamesJson,
                    moderateRestSeconds, lightRestSeconds, bodyweightRestSeconds,
                    minRestSeconds, maxRestSeconds,
                    beepDurationMs, finalBeepDurationMs, toneVolume,
                    defaultProteinTargetGrams,
                    autoStartNextSet, autoFillRepsWeight, defaultReps, defaultWeightKg,
                    tempoEccentric, tempoIsometricBottom, tempoConcentric, tempoIsometricTop,
                    tempoProfile, tempoAccent, guideSpeedMultiplier, prepCountdownSeconds
                ) SELECT
                    id, workoutDaysJson, dayNamesJson,
                    moderateRestSeconds, lightRestSeconds, bodyweightRestSeconds,
                    minRestSeconds, maxRestSeconds,
                    beepDurationMs, finalBeepDurationMs, toneVolume,
                    defaultProteinTargetGrams,
                    autoStartNextSet, autoFillRepsWeight, defaultReps, defaultWeightKg,
                    tempoEccentric, tempoIsometricBottom, tempoConcentric, tempoIsometricTop,
                    tempoProfile, tempoAccent, guideSpeedMultiplier, prepCountdownSeconds
                FROM app_settings
                """.trimIndent()
            )

            // 3. Drop old table
            db.execSQL("DROP TABLE app_settings")

            // 4. Rename new table
            db.execSQL("ALTER TABLE app_settings_new RENAME TO app_settings")
        }
    }

    val ALL: Array<Migration> = arrayOf(MIGRATION_12_13)
}
