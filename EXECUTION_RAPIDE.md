# Guide d'Exécution Rapide - ASMAA-Club

## 🚀 Démarrage Rapide

### Étape 1 : Préparer la Base de Données

```bash
# 1. Démarrer MySQL
# 2. Exécuter le script SQL
mysql -u root -p < database/asmaa_club.sql
```

### Étape 2 : ⚠️ CORRIGER LE HASHAGE (OBLIGATOIRE)

**IMPORTANT** : Les mots de passe dans le SQL utilisent BCrypt, mais le code utilise SHA-256.

**Solution rapide** : Créez un nouvel utilisateur via l'interface d'inscription après le démarrage.

### Étape 3 : Démarrer le Serveur

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

### Étape 4 : Démarrer le Client

**Windows** :
1. Ouvrez `start-client.bat`
2. Modifiez `JAVAFX_PATH` avec votre chemin JavaFX
3. Double-cliquez sur `start-client.bat`

**Linux/Mac** :
1. Ouvrez `start-client.sh`
2. Modifiez `JAVAFX_PATH` avec votre chemin JavaFX
3. Exécutez : `chmod +x start-client.sh && ./start-client.sh`

**OU manuellement** :
```bash
java --module-path "/chemin/javafx/lib" --add-modules javafx.controls,javafx.fxml -cp "out/production/clubasmaa:lib/mysql-connector-java-8.0.33.jar" asmaa.client.ClientMain
```

---

## 📝 Configuration Requise

### 1. MySQL
- Port : 3306
- Base : `asmaa_club`
- User : `root` (modifiable dans `DatabaseManager.java`)
- Password : `root` (modifiable dans `DatabaseManager.java`)

### 2. JavaFX
- Télécharger depuis : https://openjfx.io/
- Extraire et noter le chemin vers `lib/`

### 3. MySQL Connector
- Télécharger depuis : https://dev.mysql.com/downloads/connector/j/
- Placer dans `lib/mysql-connector-java-XX.jar`

---

## 🔐 Connexion

### Utilisateurs par défaut (après correction hashage) :
- **Admin** : `admin` / `admin123`
- **Client 1** : `client1` / `admin123`
- **Client 2** : `client2` / `admin123`

### OU créer un nouvel utilisateur :
- Utilisez l'interface d'inscription
- Fonctionnera directement (utilise SHA-256)

---

## ⚠️ Problème de Hashage

**Si vous ne pouvez pas vous connecter** :
1. Le SQL contient des hash BCrypt
2. Le code utilise SHA-256
3. **Solution** : Créez un nouvel utilisateur via l'interface

---

Pour plus de détails, voir `GUIDE_EXECUTION.md`

