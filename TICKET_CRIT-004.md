# TICKET CRIT-004: Tests Unitaires Repositories
## Type: Testing | Priorité: P0 | Points: 5

---

## 🎯 OBJECTIF
Ajouter des tests unitaires pour tous les repositories. Couverture cible: 70%+.

---

## 📁 FICHIERS À TESTER

```
data/repository/
├── ExerciseRepository.kt         (28 lignes)
├── WorkoutTemplateRepository.kt  (à vérifier)
├── TemplateExerciseRepository.kt (à vérifier)
├── WorkoutSessionRepository.kt   (à vérifier)
├── PerformedSetRepository.kt     (à vérifier)
├── ProfileRepository.kt          (à vérifier)
├── MeasurementRepository.kt      (à vérifier)
└── WorkoutRepository.kt          (100 lignes, facade)
```

---

## ✅ CRITÈRES D'ACCEPTATION

### 1. Setup test infrastructure
- [ ] Dependencies test dans build.gradle (JUnit, Mockito, Turbine, Coroutines-test)
- [ ] Dossier app/src/test/java/com/muscu/app/data/repository/

### 2. Tests par repository

#### ExerciseRepositoryTest
- [ ] seedIfNeeded() - insert quand count=0
- [ ] seedIfNeeded() - skip quand count>0
- [ ] getForDay(day) - retourne Flow<List<Exercise>>
- [ ] getAll() - retourne tous les exercices
- [ ] search(query) - filtre par nom
- [ ] getById(id) - retourne Exercise ou null
- [ ] insert(exercise) - appelle dao.insert
- [ ] update(exercise) - appelle dao.update
- [ ] delete(exercise) - appelle dao.delete

#### WorkoutSessionRepositoryTest
- [ ] startSession(dayOfWeek) - crée session avec date
- [ ] getLastForDay(day) - retourne dernière session
- [ ] update(session) - appelle dao.update
- [ ] deleteById(id) - appelle dao.deleteById
- [ ] getAllSessions() - retourne Flow

#### PerformedSetRepositoryTest
- [ ] getForExercise(sessionId, exerciseId) - retourne Flow
- [ ] ensureSetsForExercise() - crée sets manquants
- [ ] update(set) - appelle dao.update
- [ ] getLastCompletedSetForExercise() - retourne dernier set
- [ ] getPerformanceHistory() - retourne historique

#### ProfileRepositoryTest
- [ ] getProfile() - retourne Flow<UserProfile?>
- [ ] save(weightKg, targetGrams) - insert ou update

#### MeasurementRepositoryTest
- [ ] getAll() - retourne Flow<List<Measurement>>
- [ ] getLatest() - retourne dernier measurement
- [ ] save(measurement) - appelle dao.insert
- [ ] delete(id) - appelle dao.delete

### 3. Tests WorkoutRepository (facade)
- [ ] Vérifier que chaque méthode délègue au bon repository
- [ ] Mock des 7 repositories injectés

---

## 🔧 CONTRAINTES TECHNIQUES

- **Framework**: JUnit 5 (ou 4 si déjà configuré)
- **Mock**: Mockito + Mockito-Kotlin
- **Flow testing**: Turbine (com.squareup.turbine)
- **Coroutines**: kotlinx-coroutines-test
- **Architecture**: Given-When-Then

---

## 📋 CHECKLIST LIVRAISON

- [ ] Branche feature/CRIT-004-tests-repositories
- [ ] Tests passent: `./gradlew test`
- [ ] Couverture > 70% (vérifier avec `./gradlew jacocoTestReport` si configuré)
- [ ] Build release passe
- [ ] Aucun test flaky

---

## 📎 RÉFÉRENCES

- ExerciseRepository: `app/src/main/java/com/muscu/app/data/repository/ExerciseRepository.kt`
- Dossier test: `app/src/test/java/com/muscu/app/data/repository/` (à créer)
