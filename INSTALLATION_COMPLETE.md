# Guide d'Installation et d'Exécution Complet

## 📦 ÉTAPE 1 : Installer les Dépendances

### 1.1 MySQL Server

**Windows** :
- Télécharger depuis : https://dev.mysql.com/downloads/mysql/
- Installer et noter le mot de passe root

**Linux** :
```bash
sudo apt-get update
sudo apt-get install mysql-server
sudo mysql_secure_installation
```

**Mac** :
```bash
brew install mysql
brew services start mysql
```

### 1.2 Java JDK 11+

**Vérifier l'installation** :
```bash
java -version
```

Si non installé, télécharger depuis : https://www.oracle.com/java/technologies/downloads/

### 1.3 JavaFX SDK

1. Télécharger depuis : https://openjfx.io/
2. Choisir votre OS (Windows/Linux/Mac)
3. Extraire dans un dossier (ex: `C:\javafx-sdk-17` ou `/opt/javafx-sdk-17`)
4. **Noter le chemin vers le dossier `lib/`** (ex: `C:\javafx-sdk-17\lib`)

### 1.4 MySQL Connector/J

1. Télécharger depuis : https://dev.mysql.com/downloads/connector/j/
2. Choisir "Platform Independent"
3. Télécharger le fichier `.jar`
4. **Placer le fichier dans le dossier `lib/` du projet**

Structure attendue :
```
lib/
└── mysql-connector-java-8.0.33.jar  (ou version similaire)
```

---

## 🗄️ ÉTAPE 2 : Configurer la Base de Données

### 2.1 Démarrer MySQL

```bash
# Windows (via Services)
# OU
mysql -u root -p

# Linux/Mac
sudo systemctl start mysql
# OU
mysql -u root -p
```

### 2.2 Créer la Base de Données

```bash
# Méthode 1 : Via ligne de commande
mysql -u root -p < database/asmaa_club.sql

# Méthode 2 : Via MySQL
mysql -u root -p
source database/asmaa_club.sql;
exit;
```

### 2.3 ⚠️ CORRIGER LE PROBLÈME DE HASHAGE

**Problème** : Le SQL contient des hash BCrypt, le code utilise SHA-256.

**Solution A - Générer les hash SHA-256** :

1. Compiler le script utilitaire :
```bash
javac -cp "src" GeneratePasswordHash.java
```

2. Exécuter :
```bash
java -cp ".:src" GeneratePasswordHash
```

3. Copier le hash généré

4. Modifier `database/asmaa_club.sql` lignes 150-152 :
```sql
-- Remplacer les hash BCrypt par les hash SHA-256 générés
INSERT INTO users (username, email, password, nom, prenom, telephone, role) VALUES
('admin', 'admin@asmaa-club.ma', '[HASH_SHA256_GÉNÉRÉ]', 'Admin', 'ASMAA', '0600000000', 'ADMIN'),
('client1', 'client1@example.com', '[HASH_SHA256_GÉNÉRÉ]', 'Alami', 'Mohammed', '0612345678', 'CLIENT'),
('client2', 'client2@example.com', '[HASH_SHA256_GÉNÉRÉ]', 'Bennani', 'Fatima', '0623456789', 'CLIENT');
```

5. Réexécuter le script SQL

**Solution B - Créer un nouvel utilisateur** :
- Démarrer le serveur et le client
- Utiliser l'interface d'inscription
- Le nouvel utilisateur utilisera SHA-256 et fonctionnera

---

## ⚙️ ÉTAPE 3 : Configurer le Projet

### 3.1 Vérifier les Credentials MySQL

Ouvrir `src/asmaa/server/DatabaseManager.java` :

```java
// Lignes 19-21
private static final String URL = "jdbc:mysql://localhost:3306/asmaa_club?serverTimezone=UTC";
private static final String USER = "root";        // ⚠️ Modifier si nécessaire
private static final String PASSWORD = "root";    // ⚠️ Modifier si nécessaire
```

**Modifier** `USER` et `PASSWORD` selon votre configuration MySQL.

### 3.2 Configurer les Scripts d'Exécution

**Windows - `start-client.bat`** :
```batch
set JAVAFX_PATH=C:\javafx-sdk-17\lib
```
Remplacez par votre chemin JavaFX.

**Linux/Mac - `start-client.sh`** :
```bash
JAVAFX_PATH="/opt/javafx-sdk-17/lib"
```
Remplacez par votre chemin JavaFX.

---

## 🚀 ÉTAPE 4 : Compiler le Projet

### Option A : Via IDE (Recommandé)

- **IntelliJ IDEA** : Ouvrir le projet, Build > Build Project
- **Eclipse** : Ouvrir le projet, Project > Build Project
- **NetBeans** : Ouvrir le projet, Build > Build Project

### Option B : Via Ligne de Commande

```bash
# Compiler tous les fichiers
javac -d out/production/clubasmaa -cp "lib/mysql-connector-java-8.0.33.jar:src" src/asmaa/**/*.java
```

---

## ▶️ ÉTAPE 5 : Exécuter le Projet

### 5.1 Démarrer le Serveur

**Windows** :
```bash
start-server.bat
```

**Linux/Mac** :
```bash
chmod +x start-server.sh
./start-server.sh
```

**OU manuellement** :
```bash
java -cp "out/production/clubasmaa:lib/mysql-connector-java-8.0.33.jar" asmaa.server.ServerMain
```

**Vous devriez voir** :
```
✓ Connexion à la base de données réussie
[2025-XX-XX XX:XX:XX] Serveur ASMAA-Club démarré sur le port 5000
[2025-XX-XX XX:XX:XX] En attente de connexions...
```

### 5.2 Démarrer le Client (dans un nouveau terminal)

**Windows** :
```bash
start-client.bat
```

**Linux/Mac** :
```bash
./start-client.sh
```

**OU manuellement** :
```bash
java --module-path "/chemin/javafx/lib" --add-modules javafx.controls,javafx.fxml -cp "out/production/clubasmaa:lib/mysql-connector-java-8.0.33.jar" asmaa.client.ClientMain
```

**Vous devriez voir** :
```
✓ Connecté au serveur
```

Et l'interface JavaFX s'ouvre.

---

## 🔐 ÉTAPE 6 : Se Connecter

### Option A : Utiliser un Utilisateur Existant (après correction hashage)

- **Admin** : `admin` / `admin123`
- **Client 1** : `client1` / `admin123`
- **Client 2** : `client2` / `admin123`

### Option B : Créer un Nouvel Utilisateur (recommandé si hashage non corrigé)

1. Cliquer sur "S'inscrire" dans l'interface
2. Remplir le formulaire
3. Le nouvel utilisateur utilisera SHA-256 et fonctionnera

---

## ⚠️ Dépannage

### Erreur : "Driver MySQL non trouvé"

**Solution** :
- Vérifiez que `mysql-connector-java-XX.jar` est dans `lib/`
- Vérifiez le nom du fichier dans la commande `-cp`

### Erreur : "Impossible de se connecter au serveur"

**Solution** :
- Vérifiez que le serveur est démarré AVANT le client
- Vérifiez que le port 5000 n'est pas utilisé
- Vérifiez le firewall

### Erreur : "Erreur de connexion à la base de données"

**Solution** :
- Vérifiez que MySQL est démarré
- Vérifiez les credentials dans `DatabaseManager.java`
- Vérifiez que la base `asmaa_club` existe
- Vérifiez que le script SQL a été exécuté

### Erreur : "JavaFX non trouvé"

**Solution** :
- Vérifiez que JavaFX SDK est téléchargé
- Vérifiez le chemin dans `--module-path`
- Vérifiez que le dossier `lib/` existe dans JavaFX SDK

### Erreur : "Identifiants invalides"

**Solution** :
- ⚠️ **Vérifiez que le problème de hashage est corrigé**
- OU créez un nouvel utilisateur via l'interface

---

## 📁 Structure du Projet

```
clubasmaa/
├── src/                          # Code source Java
├── database/
│   └── asmaa_club.sql           # Script SQL (à exécuter)
├── lib/
│   └── mysql-connector-java-XX.jar  # Driver MySQL (à ajouter)
├── resources/                    # Ressources (FXML, images, CSS)
├── out/production/clubasmaa/     # Fichiers compilés
├── start-server.bat/.sh          # Scripts serveur
├── start-client.bat/.sh          # Scripts client
└── GeneratePasswordHash.java    # Script génération hash
```

---

## ✅ Checklist Complète

- [ ] MySQL installé et démarré
- [ ] Java JDK 11+ installé
- [ ] JavaFX SDK téléchargé et extrait
- [ ] MySQL Connector/J dans `lib/`
- [ ] Base de données créée (script SQL exécuté)
- [ ] **Problème de hashage corrigé** (OBLIGATOIRE)
- [ ] Credentials MySQL vérifiés dans `DatabaseManager.java`
- [ ] Chemin JavaFX configuré dans les scripts
- [ ] Projet compilé
- [ ] Serveur démarré
- [ ] Client démarré
- [ ] Connexion réussie

---

## 📚 Documentation

- **Guide détaillé** : `GUIDE_EXECUTION.md`
- **Guide rapide** : `EXECUTION_RAPIDE.md`
- **Correction hashage** : `CORRECTION_HASHAGE.md`
- **Ce guide** : `INSTALLATION_COMPLETE.md`

---

**Bon développement ! 🚀**

