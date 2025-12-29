# Comment Exécuter le Projet ASMAA-Club

## 📋 Résumé

Ce projet nécessite :
1. ✅ MySQL Server
2. ✅ Java JDK 11+
3. ✅ JavaFX SDK
4. ✅ MySQL Connector/J

---

## 🎯 Méthode la Plus Simple

### 1. Préparer la Base de Données

```bash
# Ouvrir MySQL
mysql -u root -p

# Exécuter le script
source database/asmaa_club.sql
```

### 2. ⚠️ IMPORTANT : Problème de Hashage

Le SQL contient des mots de passe en BCrypt, mais le code utilise SHA-256.

**Solution** : Créez un nouvel utilisateur via l'interface d'inscription après le démarrage.

### 3. Configurer les Scripts

**Windows** :
- Ouvrez `start-client.bat`
- Modifiez la ligne : `set JAVAFX_PATH=C:\javafx-sdk-XX\lib`
- Remplacez par votre chemin JavaFX

**Linux/Mac** :
- Ouvrez `start-client.sh`
- Modifiez la ligne : `JAVAFX_PATH="/chemin/vers/javafx-sdk-XX/lib"`
- Remplacez par votre chemin JavaFX

### 4. Vérifier la Configuration MySQL

Ouvrez `src/asmaa/server/DatabaseManager.java` lignes 19-21 :

```java
private static final String USER = "root";        // Votre user MySQL
private static final String PASSWORD = "root";   // Votre password MySQL
```

### 5. Démarrer

**Terminal 1 - Serveur** :
```bash
# Windows
start-server.bat

# Linux/Mac
./start-server.sh
```

**Terminal 2 - Client** :
```bash
# Windows
start-client.bat

# Linux/Mac
./start-client.sh
```

---

## 🔧 Si les Scripts ne Fonctionnent Pas

### Démarrer le Serveur Manuellement

```bash
java -cp "out/production/clubasmaa:lib/mysql-connector-java-8.0.33.jar" asmaa.server.ServerMain
```

### Démarrer le Client Manuellement

```bash
java --module-path "/chemin/javafx/lib" --add-modules javafx.controls,javafx.fxml -cp "out/production/clubasmaa:lib/mysql-connector-java-8.0.33.jar" asmaa.client.ClientMain
```

---

## 📚 Documentation Complète

- **Guide détaillé** : `GUIDE_EXECUTION.md`
- **Guide rapide** : `EXECUTION_RAPIDE.md`
- **Correction hashage** : `CORRECTION_HASHAGE.md`

---

## ✅ Checklist

- [ ] MySQL installé et démarré
- [ ] Base de données créée (script SQL exécuté)
- [ ] MySQL Connector/J dans `lib/`
- [ ] JavaFX SDK téléchargé et chemin configuré
- [ ] Credentials MySQL vérifiés
- [ ] Serveur démarré
- [ ] Client démarré
- [ ] Nouvel utilisateur créé (si problème de hashage)

---

**Bon développement ! 🚀**

