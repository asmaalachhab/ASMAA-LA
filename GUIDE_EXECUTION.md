# Guide d'Exécution - Projet ASMAA-Club

## 📋 Prérequis

Avant d'exécuter le projet, assurez-vous d'avoir :

1. ✅ **Java JDK 11 ou supérieur** installé
2. ✅ **MySQL Server** installé et démarré
3. ✅ **JavaFX SDK** (pour JavaFX 11+)
4. ✅ **MySQL Connector/J** (driver JDBC)

---

## 🔧 ÉTAPE 1 : Configuration de la Base de Données

### 1.1 Créer la base de données

```bash
# Se connecter à MySQL
mysql -u root -p

# Exécuter le script SQL
source database/asmaa_club.sql
# OU
mysql -u root -p < database/asmaa_club.sql
```

### 1.2 ⚠️ CORRIGER LE PROBLÈME DE HASHAGE (OBLIGATOIRE)

**Problème** : Le SQL contient des hash BCrypt, mais le code utilise SHA-256.

**Solution rapide** : Générer les hash SHA-256 et mettre à jour le SQL.

#### Option A : Utiliser un script Java temporaire

Créez un fichier `GeneratePasswordHash.java` :

```java
import asmaa.utils.PasswordUtil;

public class GeneratePasswordHash {
    public static void main(String[] args) {
        String password = "admin123";
        String hash = PasswordUtil.hashPassword(password);
        System.out.println("Hash SHA-256 pour '" + password + "':");
        System.out.println(hash);
    }
}
```

Compilez et exécutez :
```bash
javac -cp ".:src" GeneratePasswordHash.java
java -cp ".:src" GeneratePasswordHash
```

Copiez le hash généré et mettez à jour `database/asmaa_club.sql` :

```sql
-- Remplacer les lignes 150-152
INSERT INTO users (username, email, password, nom, prenom, telephone, role) VALUES
('admin', 'admin@asmaa-club.ma', '[HASH_GÉNÉRÉ_ICI]', 'Admin', 'ASMAA', '0600000000', 'ADMIN'),
('client1', 'client1@example.com', '[HASH_GÉNÉRÉ_ICI]', 'Alami', 'Mohammed', '0612345678', 'CLIENT'),
('client2', 'client2@example.com', '[HASH_GÉNÉRÉ_ICI]', 'Bennani', 'Fatima', '0623456789', 'CLIENT');
```

Puis réexécutez le script SQL.

#### Option B : Créer un nouvel utilisateur via l'interface

1. Démarrer le serveur et le client
2. Utiliser la fonction d'inscription pour créer un nouvel utilisateur
3. Ce nouvel utilisateur utilisera SHA-256 et fonctionnera

---

## 🔧 ÉTAPE 2 : Configuration des Dépendances

### 2.1 Télécharger MySQL Connector/J

```bash
# Télécharger depuis : https://dev.mysql.com/downloads/connector/j/
# Ou utiliser Maven/Gradle si configuré
```

Placez le fichier `mysql-connector-java-X.X.XX.jar` dans le dossier `lib/` du projet.

### 2.2 Télécharger JavaFX SDK

```bash
# Télécharger depuis : https://openjfx.io/
# Extraire dans un dossier (ex: C:/javafx-sdk-XX)
```

Notez le chemin vers `lib/` de JavaFX (ex: `C:/javafx-sdk-XX/lib`).

### 2.3 Vérifier la configuration MySQL

Ouvrez `src/asmaa/server/DatabaseManager.java` et vérifiez les lignes 19-21 :

```java
private static final String URL = "jdbc:mysql://localhost:3306/asmaa_club?serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "root";  // ⚠️ Modifier si nécessaire
```

**Modifiez** `USER` et `PASSWORD` selon votre configuration MySQL.

---

## 🚀 ÉTAPE 3 : Compilation du Projet

### 3.1 Compiler le serveur

```bash
# Depuis la racine du projet
javac -d out/production/clubasmaa -cp "lib/mysql-connector-java-8.0.33.jar:src" src/asmaa/server/*.java src/asmaa/model/*.java src/asmaa/utils/*.java
```

### 3.2 Compiler le client

```bash
# Compiler tous les fichiers Java
javac -d out/production/clubasmaa -cp "lib/mysql-connector-java-8.0.33.jar:src" src/asmaa/**/*.java
```

**OU** utilisez votre IDE (IntelliJ IDEA, Eclipse, etc.) pour compiler automatiquement.

---

## 🖥️ ÉTAPE 4 : Exécution

### 4.1 Démarrer le Serveur (Terminal 1)

```bash
# Depuis la racine du projet
java -cp "out/production/clubasmaa:lib/mysql-connector-java-8.0.33.jar" asmaa.server.ServerMain
```

**Vous devriez voir** :
```
✓ Connexion à la base de données réussie
[2025-XX-XX XX:XX:XX] Serveur ASMAA-Club démarré sur le port 5000
[2025-XX-XX XX:XX:XX] En attente de connexions...
```

### 4.2 Démarrer le Client (Terminal 2)

#### Sur Windows :

```bash
java --module-path "C:/javafx-sdk-XX/lib" --add-modules javafx.controls,javafx.fxml -cp "out/production/clubasmaa:lib/mysql-connector-java-8.0.33.jar" asmaa.client.ClientMain
```

#### Sur Linux/Mac :

```bash
java --module-path "/chemin/vers/javafx-sdk-XX/lib" --add-modules javafx.controls,javafx.fxml -cp "out/production/clubasmaa:lib/mysql-connector-java-8.0.33.jar" asmaa.client.ClientMain
```

**Remplacez** :
- `C:/javafx-sdk-XX/lib` par le chemin réel vers votre JavaFX SDK
- `mysql-connector-java-8.0.33.jar` par votre version du driver

**Vous devriez voir** :
```
✓ Connecté au serveur
```

Et l'interface JavaFX devrait s'ouvrir.

---

## 📝 Scripts d'Exécution Automatisés

### Script Windows (start-server.bat)

Créez `start-server.bat` :

```batch
@echo off
echo Démarrage du serveur ASMAA-Club...
java -cp "out/production/clubasmaa;lib/mysql-connector-java-8.0.33.jar" asmaa.server.ServerMain
pause
```

### Script Windows (start-client.bat)

Créez `start-client.bat` :

```batch
@echo off
echo Démarrage du client ASMAA-Club...
set JAVAFX_PATH=C:\javafx-sdk-XX\lib
java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp "out/production/clubasmaa;lib/mysql-connector-java-8.0.33.jar" asmaa.client.ClientMain
pause
```

**Modifiez** `JAVAFX_PATH` selon votre installation.

### Script Linux/Mac (start-server.sh)

Créez `start-server.sh` :

```bash
#!/bin/bash
echo "Démarrage du serveur ASMAA-Club..."
java -cp "out/production/clubasmaa:lib/mysql-connector-java-8.0.33.jar" asmaa.server.ServerMain
```

Rendez-le exécutable :
```bash
chmod +x start-server.sh
./start-server.sh
```

### Script Linux/Mac (start-client.sh)

Créez `start-client.sh` :

```bash
#!/bin/bash
echo "Démarrage du client ASMAA-Club..."
JAVAFX_PATH="/chemin/vers/javafx-sdk-XX/lib"
java --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml -cp "out/production/clubasmaa:lib/mysql-connector-java-8.0.33.jar" asmaa.client.ClientMain
```

Rendez-le exécutable :
```bash
chmod +x start-client.sh
./start-client.sh
```

---

## 🔐 Comptes Utilisateurs

### Après correction du hashage :

- **Admin** :
  - Username : `admin`
  - Password : `admin123`

- **Client 1** :
  - Username : `client1`
  - Password : `admin123`

- **Client 2** :
  - Username : `client2`
  - Password : `admin123`

### Si vous créez un nouvel utilisateur :

Utilisez la fonction d'inscription dans l'interface. Le hashage SHA-256 sera utilisé automatiquement.

---

## ⚠️ Dépannage

### Erreur : "Driver MySQL non trouvé"

**Solution** :
- Vérifiez que `mysql-connector-java-XX.jar` est dans le dossier `lib/`
- Vérifiez que le chemin est correct dans la commande `-cp`

### Erreur : "Impossible de se connecter au serveur"

**Solution** :
- Vérifiez que le serveur est démarré avant le client
- Vérifiez que le port 5000 n'est pas utilisé par un autre programme
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
- Vérifiez que les modules sont ajoutés : `--add-modules javafx.controls,javafx.fxml`

### Erreur : "Identifiants invalides"

**Solution** :
- ⚠️ **Vérifiez que le problème de hashage est corrigé** (voir Étape 1.2)
- Les utilisateurs par défaut ne fonctionneront pas sans correction du hashage

### Erreur : "Port déjà utilisé"

**Solution** :
```bash
# Windows
netstat -ano | findstr :5000
taskkill /PID [PID] /F

# Linux/Mac
lsof -ti:5000 | xargs kill -9
```

---

## 📦 Structure des Fichiers Nécessaires

```
clubasmaa/
├── src/                          # Code source
├── database/
│   └── asmaa_club.sql           # Script SQL (à exécuter)
├── lib/
│   └── mysql-connector-java-XX.jar  # Driver MySQL
├── out/production/clubasmaa/     # Fichiers compilés
├── resources/                    # Ressources (FXML, images, CSS)
└── [javafx-sdk-XX/]             # JavaFX SDK (externe)
```

---

## ✅ Checklist d'Exécution

Avant de démarrer :

- [ ] MySQL installé et démarré
- [ ] Base de données `asmaa_club` créée
- [ ] Script SQL exécuté
- [ ] **Problème de hashage corrigé** (OBLIGATOIRE)
- [ ] MySQL Connector/J dans `lib/`
- [ ] JavaFX SDK téléchargé
- [ ] Credentials MySQL vérifiés dans `DatabaseManager.java`
- [ ] Projet compilé
- [ ] Serveur démarré
- [ ] Client démarré

---

## 🎯 Ordre d'Exécution

1. ✅ **Démarrer MySQL**
2. ✅ **Créer la base de données** (exécuter SQL)
3. ✅ **Corriger le hashage** (OBLIGATOIRE)
4. ✅ **Compiler le projet**
5. ✅ **Démarrer le serveur** (Terminal 1)
6. ✅ **Démarrer le client** (Terminal 2)

---

## 📞 Support

Si vous rencontrez des problèmes :

1. Vérifiez les logs du serveur
2. Vérifiez les logs du client
3. Vérifiez que toutes les dépendances sont présentes
4. Vérifiez que le problème de hashage est corrigé

---

**Bon développement ! 🚀**

