# Audit Clean-Code — MuscuApp

> Basé sur les principes de Robert C. Martin (*Clean Code*).
> Date : 2026-05-25 | Codebase : ~1 400 lignes Kotlin

---

## Résumé exécutif

| Critère | Score | Commentaire |
|---------|-------|-------------|
| **SRP** | ⚠️ Faible | `WorkoutRepository` = God Class ; screens > 200 lignes |
| **Noms** | ✅ Correct | Globalement explicites, quelques améliorations possibles |
| **Fonctions** | ⚠️ Moyen | Plusieurs fonctions > 50 lignes, un screen à 261 lignes |
| **Données** | 🔴 Faible | 12 exercices + programme + règles lombaires hardcodés |
| **Tests** | ⚠️ Moyen | `onNodeWithText` everywhere, pas de `testTag` sauf 2 champs |
| **DRY** | ⚠️ Moyen | 4 Factory identiques, logique jour dupliquée |

---

## 1. SRP — Single Responsibility Principle

### 🔴 `WorkoutRepository.kt` — God Class (178 lignes, 5 DAOs)

**Problème** : Une seule classe gère :
- Seeding des exercices (data-mapping statique)
- CRUD WorkoutSession + PerformedSet
- CRUD UserProfile
- CRUD Measurement
- Logique métier `ensureSetsForExercise()`
- Logique calendaire `dayStartMillis()` (morte)

**Impact** : Tout changement sur les mensurations force une recompilation du repo entier. Tests unitaires impossibles sans mock 5 DAOs.

**Recommandation** :
```
WorkoutRepository
├── ExerciseRepository        (seed + getByDay)
├── WorkoutSessionRepository  (startSession, update, getLast)
├── PerformedSetRepository    (CRUD sets)
├── ProfileRepository         (get/save UserProfile)
└── MeasurementRepository     (CRUD Measurement)
```

### 🔴 `WorkoutScreen.kt` — 261 lignes, 5 zones UI mélangées

- TopAppBar + navigation
- Carte "Séance terminée"
- Progression + exercice actuel
- Timer de récupération
- Interface de série (champs + slider + boutons)
- AlertDialog de fin

**Recommandation** : extraire 4 composables privés :
```kotlin
@Composable private fun WorkoutCompletedCard(onBack: () -> Unit)
@Composable private fun ExerciseCard(exercise: Exercise, currentSetIndex: Int, totalSets: Int)
@Composable private fun RestTimerCard(seconds: Int, onSkip: () -> Unit)
@Composable private fun SetInputCard(set: PerformedSet, onComplete: (String, String) -> Unit)
```

### 🔴 `MeasurementScreen.kt` — 191 lignes, 3 responsabilités

- Schéma corporel Canvas (`BodyDiagram`)
- Formulaire de saisie (9 champs)
- Historique des 5 dernières mesures

**Recommandation** : `BodyDiagram` déjà extrait → déplacer dans `ui/components/BodyDiagram.kt`. Extraire `MeasurementHistoryCard` et `MeasurementInputForm`.

### ⚠️ `WorkoutViewModel.kt` — 196 lignes, timer + son + logique métier

Le ViewModel gère un `ToneGenerator` (ligne 39), un `Job` de timer (ligne 38), et la logique de navigation entre exercices/séries.

**Recommandation** :
```kotlin
class RestTimerManager(
    private val toneGen: ToneGenerator,
    private val scope: CoroutineScope
) { /* countdown + beeps */ }
```

---

## 2. Taille des fonctions

| Fichier | Fonction/composable | Lignes | Seuil recommandé |
|---------|---------------------|--------|------------------|
| `WorkoutScreen.kt` | `WorkoutScreen` | **261** | 50-80 |
| `WorkoutViewModel.kt` | `completeSet()` | ~40 | 20-30 |
| `WorkoutRepository.kt` | `seedExercisesIfNeeded()` | **87** | 30-40 |
| `MeasurementScreen.kt` | `MeasurementScreen` | **191** | 50-80 |
| `ProgramScreen.kt` | `ProgramScreen` | **106** | 50-80 |
| `DashboardScreen.kt` | `DashboardScreen` | **101** | 50-80 |

**Recommandation** : `seedExercisesIfNeeded()` doit déléguer à un `ExerciseSeedDataProvider` qui retourne juste `List<Exercise>`.

---

## 3. Noms (Naming)

### ✅ Bons noms
- `PerformedSet`, `WorkoutSession`, `Measurement` — domaine clair
- `ensureSetsForExercise` — intention explicite
- `loadDay`, `completeSet`, `skipTimer` — verbes d'action

### ⚠️ À améliorer

| Actuel | Problème | Recommandé |
|--------|----------|------------|
| `loadRecommendation` | String opaque, contient "Modérée"/"Légère"/"Corporel" | `intensityLevel` + enum `Intensity { MODERATE, LIGHT, BODYWEIGHT }` |
| `adjustedToday` | Nom calculé pas assez explicite | `isoDayOfWeek` ou `adjustedDayOfWeek` |
| `nextDay` | Ambigu (jour suivant ? prochain entraînement ?) | `nextWorkoutDayIndex` |
| `dayStartMillis` | Méthode privée **non utilisée** (ligne 169-177) | **Supprimer** |
| `updateTarget` | Target de quoi ? | `updateProteinTarget` |

---

## 4. Magic Numbers & Magic Strings

### 🔴 Strings hardcodées (risque de typo)

```kotlin
// WorkoutRepository.kt + WorkoutScreen.kt (dupliqué)
when (exercise.loadRecommendation) {
    "Modérée" -> 90
    "Légère" -> 60
    "Corporel" -> 45
}
```

**Solution** :
```kotlin
enum class Intensity(val label: String, val defaultRestSeconds: Int) {
    MODERATE("Modérée", 90),
    LIGHT("Légère", 60),
    BODYWEIGHT("Corporel", 45)
}
```

### 🔴 Nombres magiques

| Valeur | Contexte | Solution |
|--------|----------|----------|
| `90` | Default rest dans `PerformedSet` | `Intensity.MODERATE.defaultRestSeconds` |
| `15f..180f` | Slider range | `const val MIN_REST_SECONDS = 15` / `MAX_REST_SECONDS = 180` |
| `32` | Steps du Slider | `((MAX - MIN) / 5) - 1` ou constante explicite |
| `3`, `4`, `6` | Jours d'entraînement | `val WORKOUT_DAYS = listOf(2, 4, 6)` |
| `2`, `4`, `6` | Navigation + logique | Réutiliser `WORKOUT_DAYS` |
| `200`, `400` | Durées bip `ToneGenerator` | `BEEP_DURATION_MS = 200`, `FINAL_BEEP_DURATION_MS = 400` |
| `100` | Volume ToneGenerator | `TONE_VOLUME = 100` |
| `5` | Dernières mesures affichées | `HISTORY_DISPLAY_LIMIT = 5` |

---

## 5. Données hardcodées (Données du domaine dans le code)

### 🔴 12 exercices hardcodés dans `WorkoutRepository.seedExercisesIfNeeded()`

Toute modification du programme (ajout d'un exercice, changement de répétitions) nécessite de modifier le code Kotlin et recompiler.

**Solution** : extraire dans un fichier JSON `raw/exercises.json` chargé au runtime, ou au minimum dans un objet `ExerciseSeedData` dédié.

### 🔴 Programme hebdomadaire hardcodé dans `ProgramScreen.kt`

Les 3 jours avec leurs 4 exercices sont recodés manuellement. Si le programme change, il faut modifier à la fois `ExerciseSeedData` ET `ProgramScreen`.

**Solution** : `ProgramScreen` devrait lire les exercices depuis le repository (via un `ProgramViewModel`) au lieu de les hardcoder.

### 🔴 Règles lombaires hardcodées dans `DashboardScreen.kt`

```kotlin
Text("• Pas de squat lourd")
Text("• Pas de soulevé de terre")
// etc.
```

**Solution** : extraire dans une liste `LUMBAR_RULES` ou charger depuis `strings.xml`.

---

## 6. DRY — Duplication de code

### 🔴 ViewModel.Factory dupliqué × 4

Chaque ViewModel (Dashboard, Settings, Measurement, Workout) contient :
```kotlin
class Factory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return XxxViewModel(repository) as T
    }
}
```

**Solution** : utiliser Hilt/Koin pour l'injection de dépendances, ou créer un `ViewModelFactory` générique.

### ⚠️ Logique "prochain jour d'entraînement" dupliquée

`DashboardViewModel.loadDashboard()` calcule `nextDay` à partir du jour actuel. Cette logique n'est pas réutilisable ailleurs.

**Solution** : `NextWorkoutDayCalculator` utilitaire testable unitairement.

---

## 7. Commentaires

### 🔴 Commentaires évitables

```kotlin
// MeasurementScreen.kt → BodyDiagram
// Tête
// Corps
// Épaules
// Bras
// Hanches
// Jambes
```

Ces commentaires décrivent ce que le code fait (évident), pas **pourquoi**.

### ⚠️ Commentaires manquants

Aucune KDoc sur les fonctions publiques du Repository. Par exemple `ensureSetsForExercise` mériterait :
```kotlin
/**
 * Crée les [PerformedSet] manquantes pour un exercice dans une session.
 * Si les séries existent déjà, ne fait rien (idempotent).
 */
```

---

## 8. Structure des fichiers

### 🔴 `MeasurementDao.kt` contient à la fois l'entité et le DAO

```kotlin
@Entity(tableName = "measurements")
data class Measurement(...)   // ← devrait être dans Entities.kt ou Measurement.kt

@Dao
interface MeasurementDao { ... }  // ← OK ici
```

**Impact** : SRP violation, import confusion.

### 🔴 `Entities.kt` ne contient pas `Measurement`

Le fichier s'appelle `Entities.kt` mais `Measurement` est dans `MeasurementDao.kt`. Incohérent.

**Solution** : renommer `Entities.kt` → `Exercise.kt`, ou créer un fichier par entité.

### ⚠️ `AppDatabase.kt` — SeedCallback vide

```kotlin
class SeedCallback : Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) { /* vide */ }
}
```

Le seeding est fait dans `DashboardViewModel.init` via `repository.seedExercisesIfNeeded()`. C'est un choix architectural (pas de seed au niveau DB) mais le callback vide est trompeur.

**Solution** : supprimer `SeedCallback` ou le documenter avec un commentaire expliquant pourquoi il est vide.

---

## 9. Tests instrumentés (`MuscuE2ETest.kt`)

### 🔴 Fragilité par `onNodeWithText`

```kotlin
composeTestRule.onNodeWithText("Tableau de bord").assertIsDisplayed()
composeTestRule.onNodeWithText("Accueil").performClick()
```

Si on change "Tableau de bord" en "Dashboard" ou "Accueil" en "Home", le test casse.

**Solution** : utiliser `testTag` systématiquement :
```kotlin
// Screen
Text("Tableau de bord", modifier = Modifier.testTag("dashboard_title"))

// Test
composeTestRule.onNodeWithTag("dashboard_title").assertIsDisplayed()
```

### ⚠️ Tests manquants

- Aucun test sur le timer de récupération
- Aucun test sur la logique "série suivante / exercice suivant"
- Aucun test unitaire sur les ViewModels (juste des E2E)

---

## 10. Divers

### ⚠️ `WorkoutRepository.dayStartMillis()` — Code mort

Méthode privée définie mais jamais appelée. À supprimer.

### ⚠️ `WorkoutScreen.kt` — `widthIn` importé mais non utilisé

Ligne 11 : `import androidx.compose.foundation.layout.widthIn` — inutilisé.

### ⚠️ `WorkoutViewModel` — `ToneGenerator` sans permission explicite

```kotlin
private val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
```

Fonctionne sur émulateur mais peut échouer sur device physique selon la ROM. Documenter ou ajouter `<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />` si nécessaire.

---

## Plan de refactoring priorisé

### 🟢 Rapide (< 30 min)
1. Supprimer `dayStartMillis()` (code mort)
2. Supprimer import `widthIn` inutilisé
3. Documenter `SeedCallback` vide ou le supprimer
4. Déplacer `Measurement` dans `Entities.kt` (ou renommer)
5. Extraire `HISTORY_DISPLAY_LIMIT = 5`

### 🟡 Moyen (1-2h)
6. Créer `enum class Intensity` et remplacer les strings
7. Extraire `ExerciseSeedData` du Repository
8. Extraire `LUMBAR_RULES` depuis `DashboardScreen`
9. Ajouter `testTag` sur tous les éléments testés
10. Créer `NextWorkoutDayCalculator`

### 🔴 Important (2-4h)
11. **Splitter `WorkoutRepository`** en 4-5 repositories spécialisés
12. **Extraire composables** dans `WorkoutScreen` (4 sous-composables)
13. **Extraire `RestTimerManager`** du `WorkoutViewModel`
14. **Rendre `ProgramScreen` dynamique** (lire depuis le repository)
15. Ajouter tests unitaires ViewModel + tests UI avec `testTag`

---

*Fin de l'audit.*
