# Guide de Configuration Java pour le Backend Spring Boot

## Problème Identifié
Le backend Spring Boot ne peut pas démarrer car `JAVA_HOME` n'est pas configuré dans l'environnement Windows.

**Erreur rencontrée :**
```
Error: JAVA_HOME not found in your environment. 
Please set the JAVA_HOME variable in your environment to match the 
location of your Java installation.
```

## Solutions de Configuration Java

### 1. Vérifier l'Installation Java

Ouvrez PowerShell et vérifiez si Java est installé :

```powershell
java -version
javac -version
```

Si Java n'est pas installé, téléchargez et installez :
- **Java JDK 11** (recommandé) : https://adoptium.net/
- **Java JDK 17** (compatible) : https://adoptium.net/

### 2. Configurer JAVA_HOME (Méthode Temporaire)

Dans PowerShell, définissez JAVA_HOME pour la session actuelle :

```powershell
# Trouver l'installation Java
where java

# Exemple de configuration (remplacez par votre chemin)
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-11.0.19.7-hotspot"

# Vérifier la configuration
echo $env:JAVA_HOME
```

### 3. Configurer JAVA_HOME (Méthode Permanente)

#### Option A : Via l'Interface Windows
1. Ouvrir **Panneau de configuration** → **Système** → **Paramètres système avancés**
2. Cliquer sur **Variables d'environnement**
3. Dans **Variables système**, cliquer **Nouvelle**
4. Nom : `JAVA_HOME`
5. Valeur : Chemin vers votre installation Java (ex: `C:\Program Files\Eclipse Adoptium\jdk-11.0.19.7-hotspot`)
6. Redémarrer le terminal/IDE

#### Option B : Via PowerShell (Admin)
```powershell
# Exécuter en tant qu'administrateur
[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-11.0.19.7-hotspot", "Machine")
```

### 4. Démarrer le Backend

Une fois JAVA_HOME configuré :

```powershell
cd C:\Users\user\Downloads\back-master\back-master
./mvnw.cmd spring-boot:run
```

## Vérification du Démarrage

Le backend devrait démarrer sur le port **8089** avec les logs suivants :
```
Started PiApplication in X.XXX seconds (JVM running for X.XXX)
```

## URLs API Disponibles

Une fois le backend démarré :
- **Base URL** : `http://localhost:8089/PI`
- **Profile Dashboard** : `/user-profile/{userId}/dashboard`
- **Gamification** : `/user-profile/{userId}/gamification`
- **Notifications** : `/user-profile/{userId}/notifications`
- **Activities** : `/user-profile/{userId}/activities`
- **User Data** : `/user/all`

## Corrections Déjà Appliquées

✅ **Backend JPA Queries** : Corrigées pour utiliser `demandeur.id` au lieu de `user.id`
✅ **Frontend Port** : Corrigé de 8080 → 8089 dans Profile.js
✅ **Enum StatutDemande** : Vérifié et fonctionnel
✅ **Fallback Data** : Données de secours complètes pour toutes les sections

## Test de l'Intégration

1. **Démarrer le backend** (après configuration Java)
2. **Frontend React** déjà démarré sur `http://localhost:3001`
3. **Naviguer vers** `http://localhost:3001/admin/profile`
4. **Vérifier** que les données dynamiques remplacent les fallbacks

## Support

Si vous rencontrez des problèmes :
1. Vérifiez que Java JDK 11+ est installé
2. Confirmez que JAVA_HOME pointe vers le JDK (pas JRE)
3. Redémarrez le terminal après configuration
4. Vérifiez les logs du backend pour d'autres erreurs
