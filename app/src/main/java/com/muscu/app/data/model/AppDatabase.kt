package com.muscu.app.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.muscu.app.data.seed.ExerciseSeedData
import java.util.UUID

@Database(
    entities = [
        Exercise::class,
        WorkoutTemplate::class,
        TemplateExercise::class,
        WorkoutSession::class,
        PerformedSet::class,
        UserProfile::class,
        Measurement::class,
        AppSettings::class
    ],
    version = 16,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
    abstract fun templateExerciseDao(): TemplateExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun performedSetDao(): PerformedSetDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun measurementDao(): MeasurementDao
    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "muscu_database"
                )
                    .addMigrations(*Migrations.ALL)
                    .addCallback(SeedCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SeedCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Seed exercises on fresh install
            ExerciseSeedData.all().forEach { ex ->
                db.execSQL(
                    """INSERT INTO exercises
                        (id, name, dayOfWeek, category, targetSets, targetRepsMin, targetRepsMax,
                         intensity, warning, orderIndex, equipment, objective, difficulty)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""".trimIndent(),
                    arrayOf(
                        ex.id, ex.name, ex.dayOfWeek, ex.category, ex.targetSets,
                        ex.targetRepsMin, ex.targetRepsMax, ex.intensity.name,
                        ex.warning, ex.orderIndex, ex.equipment, ex.objective, ex.difficulty
                    )
                )
            }

            // Create default templates from seeded exercises
            val dayNames = mapOf(1 to "Lundi", 2 to "Mardi", 3 to "Mercredi",
                                 4 to "Jeudi", 5 to "Vendredi", 6 to "Samedi", 7 to "Dimanche")
            val exercises = ExerciseSeedData.all()
            val now = System.currentTimeMillis()

            val days = exercises.map { it.dayOfWeek }.distinct().sorted()
            for (day in days) {
                val templateId = UUID.randomUUID().toString()
                val name = "Programme ${dayNames[day] ?: day}"
                db.execSQL(
                    "INSERT INTO workout_templates (id, name, description, dayOfWeek, createdAt) VALUES (?, ?, ?, ?, ?)",
                    arrayOf(templateId, name, null, day, now)
                )

                exercises.filter { it.dayOfWeek == day }.sortedBy { it.orderIndex }.forEach { ex ->
                    val templateExId = UUID.randomUUID().toString()
                    db.execSQL(
                        """INSERT INTO template_exercises
                            (id, templateId, exerciseId, targetSets, targetRepsMin, targetRepsMax, restSeconds, orderIndex)
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?)""".trimIndent(),
                        arrayOf(templateExId, templateId, ex.id, ex.targetSets,
                                ex.targetRepsMin, ex.targetRepsMax, 90, ex.orderIndex)
                    )
                }
            }
        }
    }
}
