package com.muscu.app.domain.calculator

import com.muscu.app.data.model.AppSettings

data class TempoAnalysis(
    val tempoCode: String,
    val tutPerRepSeconds: Float,
    val tutPerSetSeconds: Float,
    val objectiveLabel: String,
    val description: String
)

object TempoAnalyzer {

    fun analyze(settings: AppSettings, targetReps: Int): TempoAnalysis {
        val eccentric = settings.tempoEccentric.coerceAtLeast(0)
        val isoBottom = settings.tempoIsometricBottom.coerceAtLeast(0)
        val concentric = settings.tempoConcentric.coerceAtLeast(0)
        val isoTop = settings.tempoIsometricTop.coerceAtLeast(0)

        val tutPerRep = eccentric + isoBottom + concentric + isoTop
        val tutPerSet = tutPerRep * targetReps

        val (objective, desc) = when {
            concentric <= 1 && tutPerRep <= 2 ->
                "Puissance / Explosif" to
                        "Mouvement rapide favorisant le recrutement des fibres type II. Idéal pour la puissance et la performance sportive."
            tutPerRep < 4 ->
                "Force maximale" to
                        "TUT court (1–3s) permettant des charges lourdes. Privilégie l'adaptation neurologique."
            tutPerRep in 4..6 ->
                "Hypertrophie optimale" to
                        "TUT modéré (3–6s/rép) idéal pour la croissance musculaire. Balance entre charge et tension."
            tutPerRep in 7..10 ->
                "Hypertrophie avancée" to
                        "TUT élevé augmentant le stress métabolique. Excellent pour la congestion et l'hypertrophie."
            tutPerRep > 10 ->
                "Endurance musculaire" to
                        "Très long TUT privilégiant l'endurance et la résistance à la fatigue. Charges légères recommandées."
            else ->
                "Force maximale" to
                        "TUT court (1–3s) permettant des charges lourdes. Privilégie l'adaptation neurologique."
        }

        return TempoAnalysis(
            tempoCode = "$eccentric-${isoBottom}-${concentric}-${isoTop}",
            tutPerRepSeconds = tutPerRep.toFloat(),
            tutPerSetSeconds = tutPerSet.toFloat(),
            objectiveLabel = objective,
            description = desc
        )
    }

    fun analyzePerformedSet(settings: AppSettings, reps: Int, actualSeconds: Float): TempoAnalysis {
        val eccentric = settings.tempoEccentric.coerceAtLeast(0)
        val isoBottom = settings.tempoIsometricBottom.coerceAtLeast(0)
        val concentric = settings.tempoConcentric.coerceAtLeast(0)
        val isoTop = settings.tempoIsometricTop.coerceAtLeast(0)

        val expectedTutPerRep = eccentric + isoBottom + concentric + isoTop
        val actualTutPerRep = if (reps > 0) actualSeconds / reps else expectedTutPerRep.toFloat()
        val tutPerSet = actualSeconds

        val (objective, desc) = when {
            actualTutPerRep < 2f ->
                "Puissance / Explosif" to "Rythme très rapide détecté. Très bon pour la puissance."
            actualTutPerRep in 2f..4f ->
                "Force / Hypertrophie" to "Rythme rapide, idéal pour la force avec une composante hypertrophie."
            actualTutPerRep in 4f..7f ->
                "Hypertrophie" to "Rythme contrôlé optimal pour l'hypertrophie musculaire."
            actualTutPerRep in 7f..12f ->
                "Hypertrophie avancée" to "TUT élevé. Excellente congestion musculaire."
            else ->
                "Endurance" to "Rythme très lent. Privilégie l'endurance et la résistance à la fatigue."
        }

        return TempoAnalysis(
            tempoCode = "$eccentric-${isoBottom}-${concentric}-${isoTop}",
            tutPerRepSeconds = actualTutPerRep,
            tutPerSetSeconds = tutPerSet,
            objectiveLabel = objective,
            description = desc
        )
    }
}
