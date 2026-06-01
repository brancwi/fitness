# TICKET CRIT-001: Refactor WorkoutRepository
## Type: Refactoring | Priorité: P0 | Points: 8

---

## 🎯 OBJECTIF
Transformer la God Class `WorkoutRepository` (178 lignes, 5 DAOs) en 5 repositories spécialisés suivant le Single Responsibility Principle.

---

## 📁 FICHIER ACTUEL

**Chemin**: `app/src/main/java/com/muscu/app/data/repository/WorkoutRepository.kt`

**Problèmes identifiés**:
- Gère: seeding, CRUD WorkoutSession, CRUD PerformedSet, CRUD UserProfile, CRUD Measurement, logique métier ensureSetsForExercise(), logique calendaire dayStartMillis()
- Impact: Tout changement force recompilation, tests impossibles sans mock 5 DAOs

---

## ✅ CRITÈRES D'ACCEPTATION

### 1. Créer 5 nouveaux repositories

```
data/repository/
├── ExerciseRepository.kt         (seed + getByDay)
├── WorkoutSessionRepository.kt   (startSession, update, getLast)
├── PerformedSetRepository.kt     (CRUD sets)
├── ProfileRepository.kt          (get/save UserProfile)
└── MeasurementRepository.kt      (CRUD Measurement)
```

### 2. Chaque repository doit:
- Avoir son propre DAO injecté
- Être testable unitairement (mock du DAO)
- Avoir une interface définie
- Être documenté (KDoc)

### 3. WorkoutRepository existant:
- Devenir un facade optionnel OU être supprimé
- Si conservé: déléguer uniquement, pas de logique métier

### 4. Tests:
- [ ] Test unitaire ExerciseRepository
- [ ] Test unitaire WorkoutSessionRepository
- [ ] Test unitaire PerformedSetRepository
- [ ] Test unitaire ProfileRepository
- [ ] Test unitaire MeasurementRepository
- [ ] Tests E2E passent toujours

---

## 🔧 CONTRAINTES TECHNIQUES

- **Langage**: Kotlin
- **Framework**: Android Jetpack + Room
- **Injection**: Hilt (si utilisé) ou manuel
- **Tests**: JUnit + Mockito + Turbine (Flow)
- **Pas de régression**: L'app doit compiler et fonctionner identiquement

---

## 📋 CHECKLIST LIVRAISON

- [ ] Code poussé sur branche `feature/CRIT-001-split-repositories`
- [ ] PR créée avec description détaillée
- [ ] Tests unitaires passent: `./gradlew test`
- [ ] Tests E2E passent: `./gradlew connectedAndroidTest`
- [ ] Build release: `./gradlew assembleRelease`
- [ ] Clean Code Audit mis à jour
- [ ] Documentation README mise à jour si besoin

---

## 🎁 BONUS (optionnel)
- Ajouter des logs Timber pour chaque opération CRUD
- Ajouter des annotations @WorkerThread où pertinent

---

## 📎 RÉFÉRENCES
- Audit Clean Code: `CLEAN_CODE_AUDIT.md` section 1.1
- Architecture existante: `app/src/main/java/com/muscu/app/data/`

