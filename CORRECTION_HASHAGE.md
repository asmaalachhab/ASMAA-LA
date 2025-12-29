# Correction du Problème de Hashage - Guide

## 🔴 Problème Identifié

Le fichier SQL contient des mots de passe hashés avec **BCrypt**, mais le code Java utilise **SHA-256**. Cela empêche l'authentification.

## Solution Recommandée : Modifier le SQL pour SHA-256

### Étape 1 : Générer les hash SHA-256

Vous devez générer les hash SHA-256 pour les mots de passe suivants :
- `admin123` (pour admin)
- `admin123` (pour client1)
- `admin123` (pour client2)

### Étape 2 : Script Java pour générer les hash

Créez un fichier temporaire `GenerateHashes.java` :

```java
import asmaa.utils.PasswordUtil;

public class GenerateHashes {
    public static void main(String[] args) {
        String password = "admin123";
        String hash = PasswordUtil.hashPassword(password);
        System.out.println("Hash pour '" + password + "': " + hash);
    }
}
```

Exécutez-le pour obtenir les hash SHA-256.

### Étape 3 : Modifier asmaa_club.sql

Remplacez les lignes 150-152 dans `database/asmaa_club.sql` :

```sql
-- AVANT (BCrypt - ne fonctionne pas)
('admin', 'admin@asmaa-club.ma', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', ...),

-- APRÈS (SHA-256 - fonctionne)
('admin', 'admin@asmaa-club.ma', '[HASH_SHA256_GÉNÉRÉ]', ...),
```

## Alternative : Utiliser BCrypt dans le code (Plus sécurisé)

Si vous préférez utiliser BCrypt (recommandé pour la production) :

1. Ajouter jBCrypt dans les dépendances
2. Modifier `PasswordUtil.java` pour utiliser BCrypt
3. Les hash existants dans le SQL fonctionneront

---

**Note** : Les hash dans le SQL actuel sont des hash BCrypt pour "admin123". 
Pour tester rapidement, vous pouvez créer un nouvel utilisateur via l'interface d'inscription, 
qui utilisera SHA-256 et fonctionnera correctement.

