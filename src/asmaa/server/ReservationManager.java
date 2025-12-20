// ==================== SERVER ====================

// ReservationManager.java
package asmaa.server;

import asmaa.model.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire de réservations avec synchronisation thread-safe
 * Empêche les conflits de réservation concurrente
 */
public class ReservationManager {

    // Map de verrous par terrain pour la synchronisation
    private final Map<Integer, ReentrantLock> terrainLocks;

    public ReservationManager() {
        this.terrainLocks = new HashMap<>();
    }

    /**
     * Obtient le verrou pour un terrain spécifique
     */
    private ReentrantLock getLockForTerrain(int terrainId) {
        return terrainLocks.computeIfAbsent(terrainId, k -> new ReentrantLock(true));
    }

    /**
     * Vérifie la disponibilité d'un terrain (thread-safe)
     */
    public boolean checkDisponibilite(int terrainId, LocalDate date,
                                      LocalTime heureDebut, LocalTime heureFin) {
        ReentrantLock lock = getLockForTerrain(terrainId);
        lock.lock();

        try {
            return DatabaseManager.checkDisponibilite(terrainId, date, heureDebut, heureFin);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Crée une réservation de manière thread-safe
     * Empêche les conflits de réservation concurrente
     */
    public boolean createReservation(Reservation reservation) {
        int terrainId = reservation.getTerrainId();
        ReentrantLock lock = getLockForTerrain(terrainId);

        // Acquérir le verrou pour ce terrain
        lock.lock();

        try {
            // Vérifier à nouveau la disponibilité (double-check pattern)
            boolean disponible = DatabaseManager.checkDisponibilite(
                    terrainId,
                    reservation.getDateReservation(),
                    reservation.getHeureDebut(),
                    reservation.getHeureFin()
            );

            if (!disponible) {
                return false;
            }

            // Créer la réservation
            boolean success = DatabaseManager.createReservation(reservation);

            if (success) {
                log("Réservation créée: Terrain #" + terrainId + " le " +
                        reservation.getDateReservation() + " de " +
                        reservation.getHeureDebut() + " à " + reservation.getHeureFin());
            }

            return success;

        } finally {
            // Toujours libérer le verrou
            lock.unlock();
        }
    }

    /**
     * Log les événements du ReservationManager
     */
    private void log(String message) {
        System.out.println("[ReservationManager] " + message);
    }
}