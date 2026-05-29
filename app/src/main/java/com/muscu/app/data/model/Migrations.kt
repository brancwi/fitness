package com.muscu.app.data.model

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.util.UUID

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

    /**
     * Migration 13 → 14
     * Introduces workout templates and exercise catalog.
     * - Adds equipment, objective, difficulty to exercises
     * - Adds templateId to workout_sessions
     * - Creates workout_templates and template_exercises tables
     * - Migrates existing exercises into default templates per day
     */
    val MIGRATION_13_14 = object : Migration(13, 14) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // 1. Add new columns to exercises
            db.execSQL("ALTER TABLE exercises ADD COLUMN equipment TEXT DEFAULT ''")
            db.execSQL("ALTER TABLE exercises ADD COLUMN objective TEXT DEFAULT ''")
            db.execSQL("ALTER TABLE exercises ADD COLUMN difficulty TEXT DEFAULT 'Intermédiaire'")

            // 2. Add templateId to workout_sessions
            db.execSQL("ALTER TABLE workout_sessions ADD COLUMN templateId TEXT")

            // 3. Create workout_templates
            db.execSQL(
                """
                CREATE TABLE workout_templates (
                    id TEXT PRIMARY KEY NOT NULL,
                    name TEXT NOT NULL,
                    description TEXT,
                    dayOfWeek INTEGER,
                    createdAt INTEGER NOT NULL
                )
                """.trimIndent()
            )

            // 4. Create template_exercises
            db.execSQL(
                """
                CREATE TABLE template_exercises (
                    id TEXT PRIMARY KEY NOT NULL,
                    templateId TEXT NOT NULL,
                    exerciseId TEXT NOT NULL,
                    targetSets INTEGER NOT NULL,
                    targetRepsMin INTEGER NOT NULL,
                    targetRepsMax INTEGER NOT NULL,
                    restSeconds INTEGER NOT NULL,
                    orderIndex INTEGER NOT NULL
                )
                """.trimIndent()
            )

            // 5. Migrate existing exercises into default templates
            val dayNames = mapOf(1 to "Lundi", 2 to "Mardi", 3 to "Mercredi", 4 to "Jeudi", 5 to "Vendredi", 6 to "Samedi", 7 to "Dimanche")
            val cursor = db.query("SELECT DISTINCT dayOfWeek FROM exercises")
            val days = mutableListOf<Int>()
            while (cursor.moveToNext()) {
                days.add(cursor.getInt(0))
            }
            cursor.close()

            val now = System.currentTimeMillis()
            for (day in days) {
                val templateId = UUID.randomUUID().toString()
                val name = "Programme ${dayNames[day] ?: day}"
                db.execSQL(
                    "INSERT INTO workout_templates (id, name, description, dayOfWeek, createdAt) VALUES (?, ?, ?, ?, ?)",
                    arrayOf(templateId, name, null, day, now)
                )

                val exCursor = db.query(
                    "SELECT id, targetSets, targetRepsMin, targetRepsMax, orderIndex FROM exercises WHERE dayOfWeek = ? ORDER BY orderIndex ASC",
                    arrayOf(day.toString())
                )
                while (exCursor.moveToNext()) {
                    val exerciseId = exCursor.getString(0)
                    val targetSets = exCursor.getInt(1)
                    val targetRepsMin = exCursor.getInt(2)
                    val targetRepsMax = exCursor.getInt(3)
                    val orderIndex = exCursor.getInt(4)
                    val templateExId = UUID.randomUUID().toString()
                    db.execSQL(
                        "INSERT INTO template_exercises (id, templateId, exerciseId, targetSets, targetRepsMin, targetRepsMax, restSeconds, orderIndex) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        arrayOf(templateExId, templateId, exerciseId, targetSets, targetRepsMin, targetRepsMax, 90, orderIndex)
                    )
                }
                exCursor.close()
            }
        }
    }

    val ALL: Array<Migration> = arrayOf(MIGRATION_12_13, MIGRATION_13_14)
}
