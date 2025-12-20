// Centre.java
package asmaa.model;

import java.io.Serializable;
import java.time.LocalTime;

public class Centre implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nom;
    private int villeId;
    private String adresse;
    private String telephone;
    private LocalTime horaireOuverture;
    private LocalTime horaireFermeture;
    private boolean actif;

    public Centre() {}

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public int getVilleId() { return villeId; }
    public void setVilleId(int villeId) { this.villeId = villeId; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public LocalTime getHoraireOuverture() { return horaireOuverture; }
    public void setHoraireOuverture(LocalTime horaireOuverture) { this.horaireOuverture = horaireOuverture; }

    public LocalTime getHoraireFermeture() { return horaireFermeture; }
    public void setHoraireFermeture(LocalTime horaireFermeture) { this.horaireFermeture = horaireFermeture; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}
