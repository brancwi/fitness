# TICKET CRIT-003: Extraire Données Hardcodées en JSON
## Type: Refactoring | Priorité: P0 | Points: 5

---

## 🎯 OBJECTIF
Extraire tous les exercices, programmes et règles hardcodés en fichiers JSON externalisés.

---

## 📁 DONNÉES À EXTRAIRE

### 1. Exercices (12 exercices)
**Source**: `app/src/main/java/com/muscu/app/data/seed/ExerciseSeedData.kt`

### 2. Programme d'entraînement (3 jours)
**Source**: À identifier dans le code

### 3. Règles lombaires
**Source**: `app/src/main/java/com/muscu/app/data/seed/` ou UI

---

## ✅ CRITÈRES D'ACCEPTATION

### Fichiers JSON à créer

```
app/src/main/assets/
├── exercises.json          # 12 exercices
├── programs.json           # 3 programmes
└── rules.json              # Règles lombaires
```

### Code à modifier
1. **ExerciseRepository**: Lire JSON au lieu de seed data hardcodé
2. **Seed**: Transformer ExerciseSeedData.kt en parser JSON
3. **Parser**: Créer `JsonDataSource` qui lit assets/

### Tests
- [ ] Test parser JSON valide
- [ ] Test parser JSON invalide (graceful error)
- [ ] Test chargement depuis assets

---

## 🔧 CONTRAINTES

- Garder compatibilité DB Room
- Seeding identique
- Pas de changement UI
- Tests unitaires

---

## 📋 CHECKLIST

- [ ] Fichiers JSON dans assets/
- [ ] JsonDataSource.kt
- [ ] Modifier ExerciseRepository
- [ ] Supprimer ExerciseSeedData.kt
- [ ] Tests unitaires
- [ ] Build passe
