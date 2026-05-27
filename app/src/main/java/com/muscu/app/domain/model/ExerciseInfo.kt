package com.muscu.app.domain.model

import androidx.annotation.DrawableRes
import com.muscu.app.R

data class ExerciseInfo(
    val exerciseId: String,
    val muscles: String,
    val position: String,
    val movement: String,
    val vigilance: String,
    @DrawableRes val diagramResId: Int
)

object ExerciseInfoRepository {
    private val infos = listOf(
        ExerciseInfo(
            exerciseId = "ex-bench-1",
            muscles = "Pectoraux majeurs, deltoïdes antérieurs, triceps",
            position = "Allongé sur un banc plat, pieds au sol, haltères au-dessus des épaules",
            movement = "Descente contrôlée jusqu'à la poitrine, puis poussée verticale",
            vigilance = "Colonne soutenue : ne creuse pas le dos. Mouvement sûr pour la lombaire.",
            diagramResId = R.drawable.bench_press_halteres
        ),
        ExerciseInfo(
            exerciseId = "ex-row-1",
            muscles = "Dorsaux, rhomboïdes, trapèzes, biceps",
            position = "Un genou et une main sur le banc, buste parallèle au sol",
            movement = "Tire l'haltère vers la hanche en rentrant le coude le long du corps",
            vigilance = "Alterne les bras à chaque série. Garde le dos droit et la colonne neutre.",
            diagramResId = R.drawable.rowing_1_bras
        ),
        ExerciseInfo(
            exerciseId = "ex-curl-1",
            muscles = "Biceps brachial, brachial antérieur",
            position = "Debout, bras le long du corps, paumes vers l'avant",
            movement = "Fléchis les coudes en montant les haltères sans balancer le buste",
            vigilance = "Ne bascule pas en arrière. Contrôle la descente.",
            diagramResId = R.drawable.curl_halteres
        ),
        ExerciseInfo(
            exerciseId = "ex-tri-1",
            muscles = "Triceps brachial",
            position = "Couché sur le banc ou debout, haltère au-dessus de la tête / poitrine",
            movement = "Extension des coudes en poussant l'haltère vers le haut / l'avant",
            vigilance = "Ne laisse pas les coudes s'écarter. Mouvement strict.",
            diagramResId = R.drawable.extension_triceps
        ),
        ExerciseInfo(
            exerciseId = "ex-mil-1",
            muscles = "Deltoïdes antérieurs et latéraux, triceps, trapèzes",
            position = "Assis sur le banc, haltères au niveau des épaules, paumes vers l'avant",
            movement = "Pousse les haltères verticalement jusqu'à l'extension complète des bras",
            vigilance = "Assis obligatoire : protège la lombaire. Pas de charge lourde debout.",
            diagramResId = R.drawable.developpe_militaire_assis
        ),
        ExerciseInfo(
            exerciseId = "ex-fen-1",
            muscles = "Quadriceps, fessiers, mollets, stabilisateurs du tronc",
            position = "Debout, haltères le long du corps",
            movement = "Grand pas en avant, genou arrière près du sol, reviens à la position initiale. Alterne.",
            vigilance = "Haltères légers, mouvement contrôlé, sans rebond. Genou avant ne dépasse pas la pointe du pied.",
            diagramResId = R.drawable.fentes_alternees
        ),
        ExerciseInfo(
            exerciseId = "ex-ele-1",
            muscles = "Deltoïdes latéraux (épaules)",
            position = "Debout, légèrement penché en avant, haltères le long du corps",
            movement = "Lève les bras latéralement jusqu'à l'horizontal, coudes légèrement fléchis",
            vigilance = "Ne balance pas le buste. Charge légère, contrôle total.",
            diagramResId = R.drawable.elevations_laterales
        ),
        ExerciseInfo(
            exerciseId = "ex-gain-1",
            muscles = "Transverse de l'abdomen, obliques, rectus abdominis, érecteurs du rachis",
            position = "Appui sur les avant-bras et les pointes de pieds, corps en ligne droite",
            movement = "Maintiens la position sans creuser ni bomber le dos, respire normalement",
            vigilance = "Tiens la position sans creuser le dos. Arrête si douleur lombaire.",
            diagramResId = R.drawable.gainage_ventral
        ),
        ExerciseInfo(
            exerciseId = "ex-bench-serr",
            muscles = "Pectoraux majeurs (portion médiale), triceps, deltoïdes antérieurs",
            position = "Allongé sur un banc plat, prise des haltères plus étroite que les épaules",
            movement = "Descente contrôlée à la poitrine, poussée verticale sans écarter les coudes",
            vigilance = "Colonne soutenue : ne creuse pas le dos. Concentration sur les triceps.",
            diagramResId = R.drawable.bench_press_prise_serree
        ),
        ExerciseInfo(
            exerciseId = "ex-row-2",
            muscles = "Dorsaux, rhomboïdes, trapèzes moyens, biceps, érecteurs du rachis",
            position = "Debout, buste penché à 45°, genoux légèrement fléchis, haltères le long des jambes",
            movement = "Tire les deux haltères vers les hanches en rentrant les coudes le long du corps",
            vigilance = "Buste bien penché, dos droit, charge légère. Ne bascule pas.",
            diagramResId = R.drawable.rowing_buste_pencho_2_bras
        ),
        ExerciseInfo(
            exerciseId = "ex-curl-mart",
            muscles = "Biceps brachial, brachial antérieur, brachioradial",
            position = "Debout, bras le long du corps, paumes face à face (position marteau)",
            movement = "Fléchis les coudes en montant les haltères sans balancer le buste",
            vigilance = "Garde les coudes collés au corps. Contrôle la descente.",
            diagramResId = R.drawable.curl_marteau
        ),
        ExerciseInfo(
            exerciseId = "ex-tri-2",
            muscles = "Triceps brachial (3 chefs)",
            position = "Debout ou à la poulie, corde ou haltère devant / derrière la tête",
            movement = "Extension des coudes en écartant les poignets à la fin du mouvement (corde)",
            vigilance = "Ne laisse pas les coudes s'écarter. Mouvement strict et contrôlé.",
            diagramResId = R.drawable.extension_triceps_corde
        )
    )

    private val byId = infos.associateBy { it.exerciseId }

    fun getById(exerciseId: String): ExerciseInfo? = byId[exerciseId]

    fun getByName(name: String): ExerciseInfo? = infos.find { info ->
        // fuzzy match by partial name
        when {
            name.contains("bench press haltères", ignoreCase = true) && info.exerciseId == "ex-bench-1" -> true
            name.contains("rowing 1 bras", ignoreCase = true) && info.exerciseId == "ex-row-1" -> true
            name.contains("curl haltères", ignoreCase = true) && info.exerciseId == "ex-curl-1" -> true
            name.contains("extension triceps") && !name.contains("corde", ignoreCase = true) && info.exerciseId == "ex-tri-1" -> true
            name.contains("développé militaire", ignoreCase = true) && info.exerciseId == "ex-mil-1" -> true
            name.contains("fentes", ignoreCase = true) && info.exerciseId == "ex-fen-1" -> true
            name.contains("élévations latérales", ignoreCase = true) && info.exerciseId == "ex-ele-1" -> true
            name.contains("gainage", ignoreCase = true) && info.exerciseId == "ex-gain-1" -> true
            name.contains("bench press prise serrée", ignoreCase = true) && info.exerciseId == "ex-bench-serr" -> true
            name.contains("rowing buste penché 2 bras", ignoreCase = true) && info.exerciseId == "ex-row-2" -> true
            name.contains("curl marteau", ignoreCase = true) && info.exerciseId == "ex-curl-mart" -> true
            name.contains("extension triceps") && name.contains("corde", ignoreCase = true) && info.exerciseId == "ex-tri-2" -> true
            else -> false
        }
    }
}
