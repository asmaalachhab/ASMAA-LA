// Terrain.java
package asmaa.model;

import java.io.Serializable;

public class Terrain implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nom;
    private int centreId;
    private int sportId;
    private String typeSurface;
    private int capacite;
    private double prixHeure;
    private boolean actif;

    // Champs supplémentaires
    private String sportNom;
    private String centreNom;

    public Terrain() {}

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getCentreId() { return centreId; }
    public void setCentreId(int centreId) { this.centreId = centreId; }

    public int getSportId() { return sportId; }
    public void setSportId(int sportId) { this.sportId = sportId; }

    public String getTypeSurface() { return typeSurface; }
    public void setTypeSurface(String typeSurface) { this.typeSurface = typeSurface; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public double getPrixHeure() { return prixHeure; }
    public void setPrixHeure(double prixHeure) { this.prixHeure = prixHeure; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public String getSportNom() { return sportNom; }
    public void setSportNom(String sportNom) { this.sportNom = sportNom; }

    public String getCentreNom() { return centreNom; }
    public void setCentreNom(String centreNom) { this.centreNom = centreNom; }
}