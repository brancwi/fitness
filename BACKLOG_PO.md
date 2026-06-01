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
| Audit Clean Code | Score global: ⚠️ Moyen |

---

## 🔴 CRITIQUES (Doit être fait)

### [CRIT-001] Refactor WorkoutRepository - God Class
- **Problème**: 178 lignes, gère 5 DAOs
- **Impact**: Impossible à tester, tout changement = recompilation totale
- **Solution**: Split en 5 repositories spécialisés
- **Critères d'acceptation**:
  - [ ] ExerciseRepository (seed + getByDay)
  - [ ] WorkoutSessionRepository (startSession, update, getLast)
  - [ ] PerformedSetRepository (CRUD sets)
  - [ ] ProfileRepository (get/save UserProfile)
  - [ ] MeasurementRepository (CRUD Measurement)
  - [ ] Tests unitaires pour chaque repository

### [CRIT-002] Refactor WorkoutScreen - 261 lignes
- **Problème**: 5 zones UI mélangées
- **Impact**: Difficile à maintenir, tester, modifier
- **Solution**: Composants séparés
- **Critères d'acceptation**:
  - [ ] TopAppBar component
  - [ ] SessionCompleteCard component
  - [ ] ExerciseProgress component
  - [ ] RestTimer component
  - [ ] Chaque composant < 80 lignes

### [CRIT-003] Extraire données hardcodées
- **Problème**: 12 exercices + programme + règles lombaires en dur
- **Impact**: Pas de personnalisation, difficile à modifier
- **Solution**: Fichiers JSON + parser
- **Critères d'acceptation**:
  - [ ] exercises.json (titre, muscles, description, image)
  - [ ] programs.json (jour, exercices, séries, repos)
  - [ ] rules.json (règles lombaires, vigilances)
  - [ ] Parser JSON avec validation
  - [ ] Tests des parsers

---

## 🟡 IMPORTANT (Amélioration)

### [IMP-001] Factoriser les 4 Factory identiques
- **Problème**: Code dupliqué
- **Solution**: Generic Factory Pattern
- **Critères**: -50% de duplication

### [IMP-002] Ajouter testTag pour tests E2E
- **Problème**: onNodeWithText everywhere, fragile
- **Solution**: testTag sur tous les composants interactifs
- **Critères**: 100% des composants testables par tag

### [IMP-003] Unifier la logique "jour"
- **Problème**: Logique dupliquée dans plusieurs screens
- **Solution**: DayCalculator utilitaire
- **Critères**: 1 seule source de vérité

---

## 🟢 NICE TO HAVE (Évolution)

### [NTH-001] Synchronisation cloud
- Backup des données sur Google Drive / Dropbox

### [NTH-002] Statistiques avancées
- Graphiques de progression
- PR tracking
- Volume total hebdo

### [NTH-003] Timer HIIT
- Mode interval training
- Sons personnalisables

---

## 📋 DÉFINITION OF DONE

- [ ] Code review par PO (Hermes)
- [ ] Tests passent (unit + E2E)
- [ ] Clean Code Audit > 8/10
- [ ] Documentation à jour
- [ ] APK build sans erreur

---

## 🎯 SPRINT 1 PROPOSÉ

| Story | Points | Priorité |
|-------|--------|----------|
| CRIT-001: Split repositories | 8 | P0 |
| CRIT-003: JSON data | 5 | P0 |
| IMP-001: Generic Factory | 3 | P1 |

**Total: 16 points**

