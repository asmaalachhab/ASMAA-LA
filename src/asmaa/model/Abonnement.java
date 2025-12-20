// Abonnement.java
package asmaa.model;

import java.io.Serializable;

public class Abonnement implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nom;
    private String description;
    private int dureeMois;
    private double prix;
    private double remisePourcentage;
    private int nombreReservations;
    private boolean actif;

    public Abonnement() {}

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDureeMois() { return dureeMois; }
    public void setDureeMois(int dureeMois) { this.dureeMois = dureeMois; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public double getRemisePourcentage() { return remisePourcentage; }
    public void setRemisePourcentage(double remisePourcentage) { this.remisePourcentage = remisePourcentage; }

    public int getNombreReservations() { return nombreReservations; }
    public void setNombreReservations(int nombreReservations) { this.nombreReservations = nombreReservations; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}