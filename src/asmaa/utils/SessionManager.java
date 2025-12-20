// SessionManager.java
package asmaa.utils;

import asmaa.model.User;

/**
 * Gestionnaire de session pour l'utilisateur connecté
 * Singleton pattern
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    /**
     * Obtenir l'instance unique
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Connecter un utilisateur
     */
    public void login(User user) {
        this.currentUser = user;
    }

    /**
     * Déconnecter l'utilisateur
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Obtenir l'utilisateur connecté
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Vérifier si un utilisateur est connecté
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Vérifier si l'utilisateur est admin
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
}