package com.muscu.app.data.seed

import com.muscu.app.data.model.Exercise
import com.muscu.app.domain.model.Intensity

object ExerciseSeedData {

    fun all(): List<Exercise> = buildList {
        addAll(tuesdayExercises())
        addAll(thursdayExercises())
        addAll(saturdayExercises())
    }

    private fun tuesdayExercises(): List<Exercise> = listOf(
        Exercise(
            id = "ex-bench-1",
            name = "Bench press haltères",
            dayOfWeek = 2,
            category = "Pectoraux/Dos",
            targetSets = 3,
            targetRepsMin = 8,
            targetRepsMax = 10,
            intensity = Intensity.MODERATE,
            warning = "Garde la colonne neutre et contrôle le mouvement.",
            orderIndex = 0
        ),
        Exercise(
            id = "ex-row-1",
            name = "Rowing 1 bras (genou sur banc)",
            dayOfWeek = 2,
            category = "Pectoraux/Dos",
            targetSets = 3,
            targetRepsMin = 10,
            targetRepsMax = 10,
            intensity = Intensity.MODERATE,
            warning = "Garde le dos droit et alterne les bras.",
            orderIndex = 1
        ),
        Exercise(
            id = "ex-curl-1",
            name = "Curl haltères",
            dayOfWeek = 2,
            category = "Bras",
            targetSets = 3,
            targetRepsMin = 12,
            targetRepsMax = 12,
            intensity = Intensity.LIGHT,
            orderIndex = 2
        ),
        Exercise(
            id = "ex-tri-1",
            name = "Extension triceps (couché ou debout)",
            dayOfWeek = 2,
            category = "Bras",
            targetSets = 3,
            targetRepsMin = 12,
            targetRepsMax = 12,
            intensity = Intensity.LIGHT,
            orderIndex = 3
        )
    )

    private fun thursdayExercises(): List<Exercise> = listOf(
        Exercise(
            id = "ex-mil-1",
            name = "Développé militaire assis",
            dayOfWeek = 4,
            category = "Épaules",
            targetSets = 3,
            targetRepsMin = 8,
            targetRepsMax = 10,
            intensity = Intensity.MODERATE,
            warning = "Assis obligatoire. Pas de charge lourde debout.",
            orderIndex = 0
        ),
        Exercise(
            id = "ex-fen-1",
            name = "Fentes alternées (haltères à la main)",
            dayOfWeek = 4,
            category = "Jambes",
            targetSets = 3,
            targetRepsMin = 10,
            targetRepsMax = 10,
            intensity = Intensity.LIGHT,
            warning = "Haltères légers, mouvement contrôlé, sans rebond.",
            orderIndex = 1
        ),
        Exercise(
            id = "ex-ele-1",
            name = "Élévations latérales",
            dayOfWeek = 4,
            category = "Épaules",
            targetSets = 3,
            targetRepsMin = 12,
            targetRepsMax = 12,
            intensity = Intensity.LIGHT,
            orderIndex = 2
        ),
        Exercise(
            id = "ex-gain-1",
            name = "Gainage ventral",
            dayOfWeek = 4,
            category = "Core",
            targetSets = 3,
            targetRepsMin = 30,
            targetRepsMax = 45,
            intensity = Intensity.BODYWEIGHT,
            warning = "Tiens la position sans creuser le dos.",
            orderIndex = 3
        )
    )

    private fun saturdayExercises(): List<Exercise> = listOf(
        Exercise(
            id = "ex-bench-serr",
            name = "Bench press prise serrée",
            dayOfWeek = 6,
            category = "Pectoraux/Dos",
            targetSets = 3,
            targetRepsMin = 8,
            targetRepsMax = 10,
            intensity = Intensity.MODERATE,
            warning = "Garde la colonne neutre et contrôle le mouvement.",
            orderIndex = 0
        ),
        Exercise(
            id = "ex-row-2",
            name = "Rowing buste penché 2 bras",
            dayOfWeek = 6,
            category = "Pectoraux/Dos",
            targetSets = 3,
            targetRepsMin = 10,
            targetRepsMax = 10,
            intensity = Intensity.LIGHT,
            warning = "Buste bien penché, dos droit, charge légère.",
            orderIndex = 1
        ),
        Exercise(
            id = "ex-curl-mart",
            name = "Curl marteau",
            dayOfWeek = 6,
            category = "Bras",
            targetSets = 3,
            targetRepsMin = 10,
            targetRepsMax = 10,
            intensity = Intensity.LIGHT,
            orderIndex = 2
        ),
        Exercise(
            id = "ex-tri-2",
            name = "Extension triceps corde ou haltère",
            dayOfWeek = 6,
            category = "Bras",
            targetSets = 3,
            targetRepsMin = 12,
            targetRepsMax = 12,
            intensity = Intensity.LIGHT,
            orderIndex = 3
        )
    )
}
