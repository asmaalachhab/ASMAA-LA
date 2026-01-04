package asmaa.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utilitaire de gestion des mots de passe
 * Algorithme : SHA-256 + salt externe
 *
 * ⚠️ Usage académique / projet universitaire
 * En production réelle : BCrypt ou Argon2
 */
public class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";

    /**
     * Hash un mot de passe avec un salt fourni
     *
     * @param password mot de passe en clair
     * @param salt valeur unique (email, username, id...)
     * @return hash encodé en Base64
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);

            // Ajouter le salt
            messageDigest.update(salt.getBytes());

            // Hasher le mot de passe
            byte[] hashedBytes = messageDigest.digest(password.getBytes());

            // Retourner en Base64
            return Base64.getEncoder().encodeToString(hashedBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }

    /**
     * Vérifie si le mot de passe correspond au hash stocké
     *
     * @param password mot de passe saisi
     * @param salt salt utilisé à l'inscription
     * @param storedHash hash enregistré en base
     * @return true si correct, false sinon
     */
    public static boolean verifyPassword(String password, String salt, String storedHash) {
        String generatedHash = hashPassword(password, salt);
        return generatedHash.equals(storedHash);
    }
}
