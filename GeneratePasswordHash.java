/**
 * Script utilitaire pour générer les hash SHA-256 des mots de passe
 * À utiliser pour corriger le problème de hashage dans asmaa_club.sql
 * 
 * Compilation :
 *   javac -cp "src" GeneratePasswordHash.java
 * 
 * Exécution :
 *   java -cp ".:src" GeneratePasswordHash
 */
import asmaa.utils.PasswordUtil;

public class GeneratePasswordHash {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  GÉNÉRATION DE HASH SHA-256");
        System.out.println("========================================");
        System.out.println();
        
        String password = "admin123";
        
        System.out.println("Mot de passe : " + password);
        System.out.println();
        
        // Générer le hash
        String hash = PasswordUtil.hashPassword(password);
        
        System.out.println("Hash SHA-256 généré :");
        System.out.println(hash);
        System.out.println();
        System.out.println("========================================");
        System.out.println("  INSTRUCTIONS");
        System.out.println("========================================");
        System.out.println();
        System.out.println("1. Copiez le hash ci-dessus");
        System.out.println("2. Ouvrez database/asmaa_club.sql");
        System.out.println("3. Remplacez les hash BCrypt (lignes 150-152)");
        System.out.println("4. Remplacez par le hash SHA-256 généré");
        System.out.println("5. Réexécutez le script SQL");
        System.out.println();
        System.out.println("Exemple de remplacement :");
        System.out.println("  AVANT: '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'");
        System.out.println("  APRÈS: '" + hash + "'");
        System.out.println();
    }
}

