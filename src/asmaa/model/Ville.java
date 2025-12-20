package asmaa.model;

import java.io.Serializable;

public class Ville implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nom;
    private String adresse;
    private String telephone;
    private String email;

    public Ville() {}

    public Ville(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // ✅ CORRECTION: toString() retourne le NOM pour affichage dans les listes
    @Override
    public String toString() {
        return nom;  // Affiche "Casablanca", "Rabat", etc.
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ville ville = (Ville) o;
        return id == ville.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}