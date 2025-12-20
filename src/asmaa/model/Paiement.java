// Paiement.java
package asmaa.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Paiement implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum ModePaiement {
        CARTE, ESPECE, CHEQUE, VIREMENT
    }

    public enum StatutPaiement {
        EN_ATTENTE, VALIDE, REFUSE, REMBOURSE
    }

    private int id;
    private int reservationId;
    private int userId;
    private double montant;
    private ModePaiement modePaiement;
    private StatutPaiement statut;
    private LocalDateTime datePaiement;

    public Paiement() {
        this.modePaiement = ModePaiement.CARTE;
        this.statut = StatutPaiement.VALIDE;
        this.datePaiement = LocalDateTime.now();
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public ModePaiement getModePaiement() { return modePaiement; }
    public void setModePaiement(ModePaiement modePaiement) { this.modePaiement = modePaiement; }

    public StatutPaiement getStatut() { return statut; }
    public void setStatut(StatutPaiement statut) { this.statut = statut; }

    public LocalDateTime getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDateTime datePaiement) { this.datePaiement = datePaiement; }
}
