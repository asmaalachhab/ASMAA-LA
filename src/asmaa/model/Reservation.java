// ==================== MODELS ====================

// Reservation.java
package asmaa.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Statut {
        EN_ATTENTE,
        CONFIRMEE,
        ANNULEE,
        TERMINEE
    }

    private int id;
    private int userId;
    private int terrainId;
    private LocalDate dateReservation;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private double prixTotal;
    private double remiseAppliquee;
    private Statut statut;

    // Champs supplémentaires pour l'affichage
    private String terrainNom;
    private String sportNom;
    private String centreNom;

    public Reservation() {
        this.statut = Statut.CONFIRMEE;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getTerrainId() { return terrainId; }
    public void setTerrainId(int terrainId) { this.terrainId = terrainId; }

    public LocalDate getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDate dateReservation) { this.dateReservation = dateReservation; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public LocalTime getHeureFin() { return heureFin; }
    public void setHeureFin(LocalTime heureFin) { this.heureFin = heureFin; }

    public double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(double prixTotal) { this.prixTotal = prixTotal; }

    public double getRemiseAppliquee() { return remiseAppliquee; }
    public void setRemiseAppliquee(double remiseAppliquee) { this.remiseAppliquee = remiseAppliquee; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public String getTerrainNom() { return terrainNom; }
    public void setTerrainNom(String terrainNom) { this.terrainNom = terrainNom; }

    public String getSportNom() { return sportNom; }
    public void setSportNom(String sportNom) { this.sportNom = sportNom; }

    public String getCentreNom() { return centreNom; }
    public void setCentreNom(String centreNom) { this.centreNom = centreNom; }
}