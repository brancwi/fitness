# Déploiement automatique via GitHub Pages

## 1. Configurer les Secrets GitHub

Va dans **Settings → Secrets and variables → Actions → New repository secret** et ajoute :

| Secret | Valeur |
|---|---|
| `KEYSTORE_BASE64` | Contenu du fichier `keystore/muscu-release.keystore.base64.txt` |
| `KEYSTORE_PASSWORD` | `muscupass` |
| `KEY_ALIAS` | `muscu` |
| `KEY_PASSWORD` | `muscupass` |

> Le fichier `keystore/muscu-release.keystore.base64.txt` a été généré dans ce repo. Copie son contenu entier dans le secret `KEYSTORE_BASE64`.

## 2. Activer GitHub Pages

Va dans **Settings → Pages** :
- **Source** : Deploy from a branch
- **Branch** : `main` → `/docs`
- Clique sur **Save**

L'URL sera : `https://TON_USER.github.io/muscu/`

## 3. Déclencher un premier build

Pousse sur `main` ou lance manuellement le workflow **Build & Release APK** dans l'onglet **Actions**.

## 4. Installer sur ton téléphone

1. Va sur l'URL GitHub Pages (`https://TON_USER.github.io/muscu/`)
2. Scanne le QR code ou clique sur **Télécharger l'APK**
3. Autorise l'installation de source inconnue si demandé
4. L'application s'installe !

## Mise à jour

À chaque push sur `main`, un nouveau build est automatiquement créé et la release "latest" est mise à jour. Retourne sur la page GitHub Pages et télécharge à nouveau — tes données sont conservées.
