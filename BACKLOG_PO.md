# BACKLOG PRODUIT - MuscuApp
## PO: Hermes | Date: 2026-06-01

---

## 📊 ÉTAT ACTUEL

| Métrique | Valeur |
|----------|--------|
| Fichiers Kotlin | 73 |
| Lignes de code | ~8 200 |
| Architecture | Android + Jetpack Compose |
| Base de données | Room (SQLite) |
| Repositories | 8 (déjà refactorisés) |
| Clean Code Audit | À refaire |

---

## ✅ DÉJÀ FAIT (à vérifier)

### [DONE] Refactor Repositories
- WorkoutRepository: 100 lignes, facade propre
- 7 repositories spécialisés existants
- **Action**: Vérifier tests unitaires

---

## 🔴 CRITIQUES (Doit être fait)

### [CRIT-002] Refactor WorkoutScreen - 261 lignes
- **Problème**: 5 zones UI mélangées
- **Solution**: Composants séparés
- **Critères**:
  - [ ] TopAppBar component
  - [ ] SessionCompleteCard component
  - [ ] ExerciseProgress component
  - [ ] RestTimer component
  - [ ] Chaque composant < 80 lignes

### [CRIT-003] Extraire données hardcodées
- **Problème**: 12 exercices + programme en dur
- **Solution**: Fichiers JSON + parser
- **Critères**:
  - [ ] exercises.json
  - [ ] programs.json
  - [ ] Parser JSON avec validation
  - [ ] Tests des parsers

### [CRIT-004] Ajouter tests unitaires
- **Problème**: Couverture inconnue, probablement faible
- **Solution**: Tests pour tous les repositories
- **Critères**:
  - [ ] ExerciseRepositoryTest
  - [ ] WorkoutSessionRepositoryTest
  - [ ] PerformedSetRepositoryTest
  - [ ] ProfileRepositoryTest
  - [ ] MeasurementRepositoryTest

---

## 🟡 IMPORTANT

### [IMP-001] Factoriser les 4 Factory identiques
### [IMP-002] Ajouter testTag pour tests E2E
### [IMP-003] Unifier la logique "jour"

---

## 🟢 NICE TO HAVE

### [NTH-001] Synchronisation cloud
### [NTH-002] Statistiques avancées
### [NTH-003] Timer HIIT

---

## 📋 DÉFINITION OF DONE

- [ ] Code review par PO (Hermes)
- [ ] Tests passent (unit + E2E)
- [ ] Clean Code Audit > 8/10
- [ ] Documentation à jour
- [ ] APK build sans erreur

---

## 🎯 SPRINT 1 (AJUSTÉ)

| Story | Points | Priorité |
|-------|--------|----------|
| CRIT-004: Tests repositories | 5 | P0 |
| CRIT-003: JSON data | 5 | P0 |
| CRIT-002: Split WorkoutScreen | 8 | P1 |

**Total: 18 points**
