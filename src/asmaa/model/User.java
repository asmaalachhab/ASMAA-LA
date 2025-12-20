package asmaa.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe représentant un utilisateur du système ASMAA-Club
 * Implémente Serializable pour permettre la transmission via Socket
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    // Énumération pour les rôles
    public enum Role {
        INVITE,   // Utilisateur non inscrit
        CLIENT,   // Client inscrit
        ADMIN     // Administrateur
    }

    // Attributs
    private int id;
    private String username;
    private String email;
    private String password;      // Hashé en base
    private String nom;
    private String prenom;
    private String telephone;
    private Role role;
    private LocalDateTime dateCreation;
    private boolean actif;

    // Constructeurs
    public User() {
        this.role = Role.CLIENT;
        this.actif = true;
        this.dateCreation = LocalDateTime.now();
    }

    public User(String username, String email, String password, String nom, String prenom) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
    }

    public User(int id, String username, String email, String nom, String prenom,
                String telephone, Role role, boolean actif) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.role = role;
        this.actif = actif;
        this.dateCreation = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    // Méthodes utilitaires
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isClient() {
        return role == Role.CLIENT;
    }

    public boolean isInvite() {
        return role == Role.INVITE;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", role=" + role +
                ", actif=" + actif +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}