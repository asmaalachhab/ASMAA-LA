package asmaa.client;

import asmaa.model.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Client réseau pour communiquer avec le serveur
 * Gère toutes les requêtes vers le serveur
 */
public class NetworkClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean connected;

    // ==================== CONNEXION ====================
    public boolean connect() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socket.getInputStream());
            connected = true;
            System.out.println("✓ Connecté au serveur");
            return true;
        } catch (IOException e) {
            System.err.println("✗ Erreur de connexion au serveur: " + e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        try {
            if (connected) {
                sendCommand("DISCONNECT");
                if (input != null) input.close();
                if (output != null) output.close();
                if (socket != null) socket.close();
                connected = false;
                System.out.println("✓ Déconnecté du serveur");
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la déconnexion: " + e.getMessage());
        }
    }

    private void sendCommand(String command) throws IOException {
        output.writeObject(command);
        output.flush();
    }

    @SuppressWarnings("unchecked")
    private <T> T readResponse() throws IOException, ClassNotFoundException {
        String status = (String) input.readObject();
        Object data = input.readObject();

        if ("ERROR".equals(status)) {
            throw new RuntimeException((String) data);
        }
        return (T) data;
    }

    public boolean isConnected() {
        return connected;
    }

    // ==================== AUTHENTIFICATION ====================
    public User login(String username, String password) {
        try {
            sendCommand("LOGIN");
            output.writeObject(username);
            output.writeObject(password);
            output.flush();
            return readResponse();
        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion: " + e.getMessage());
            return null;
        }
    }

    public boolean register(User user) {
        try {
            sendCommand("REGISTER");
            output.writeObject(user);
            output.flush();
            String response = readResponse();
            return "Inscription réussie".equals(response);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'inscription: " + e.getMessage());
            return false;
        }
    }

    // ==================== SPORTS / TERRAINS ====================
    public List<Sport> getSports() {
        try {
            sendCommand("GET_SPORTS");
            return readResponse();
        } catch (Exception e) {
            System.err.println("Erreur getSports: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Terrain> getTerrains(int sportId, int centreId) {
        try {
            sendCommand("GET_TERRAINS");
            output.writeInt(sportId);
            output.writeInt(centreId);
            output.flush();
            return readResponse();
        } catch (Exception e) {
            System.err.println("Erreur getTerrains: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Centre> getCentres(int villeId) {
        try {
            sendCommand("GET_CENTRES");
            output.writeInt(villeId);
            output.flush();
            return readResponse();
        } catch (Exception e) {
            System.err.println("Erreur getCentres: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ==================== RÉSERVATIONS ====================
    public boolean checkDisponibilite(int terrainId, LocalDate date,
                                      LocalTime heureDebut, LocalTime heureFin) {
        try {
            sendCommand("CHECK_DISPONIBILITE");
            output.writeInt(terrainId);
            output.writeObject(date);
            output.writeObject(heureDebut);
            output.writeObject(heureFin);
            output.flush();
            return readResponse();
        } catch (Exception e) {
            System.err.println("Erreur checkDisponibilite: " + e.getMessage());
            return false;
        }
    }

    public boolean createReservation(Reservation reservation) {
        try {
            sendCommand("RESERVER");
            output.writeObject(reservation);
            output.flush();
            String response = readResponse();
            return "Réservation confirmée".equals(response);
        } catch (Exception e) {
            System.err.println("Erreur createReservation: " + e.getMessage());
            return false;
        }
    }

    public List<Reservation> getMesReservations() {
        try {
            sendCommand("GET_MES_RESERVATIONS");
            return readResponse();
        } catch (Exception e) {
            System.err.println("Erreur getMesReservations: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean annulerReservation(int reservationId) {
        try {
            sendCommand("ANNULER_RESERVATION");
            output.writeInt(reservationId);
            output.flush();
            String response = readResponse();
            return "Réservation annulée".equals(response);
        } catch (Exception e) {
            System.err.println("Erreur annulerReservation: " + e.getMessage());
            return false;
        }
    }

    // ==================== ABONNEMENTS ====================
    public List<Abonnement> getAbonnements() {
        try {
            sendCommand("GET_ABONNEMENTS");
            return readResponse();
        } catch (Exception e) {
            System.err.println("Erreur getAbonnements: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean souscrireAbonnement(int abonnementId) {
        try {
            sendCommand("SOUSCRIRE_ABONNEMENT");
            output.writeInt(abonnementId);
            output.flush();
            String response = readResponse();
            return "Abonnement activé avec succès".equals(response);
        } catch (Exception e) {
            System.err.println("Erreur souscrireAbonnement: " + e.getMessage());
            return false;
        }
    }

    // ==================== ADMIN ====================
    public List<Centre> getAllCentres() {
        try {
            sendCommand("ADMIN_GET_CENTRES");
            return readResponse();
        } catch (Exception e) {
            System.err.println("Erreur getAllCentres: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Terrain> getAllTerrains() {
        try {
            sendCommand("ADMIN_GET_TERRAINS");
            return readResponse();
        } catch (Exception e) {
            System.err.println("Erreur getAllTerrains: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Reservation> getAllReservations() {
        try {
            sendCommand("ADMIN_GET_RESERVATIONS");
            return readResponse();
        } catch (Exception e) {
            System.err.println("Erreur getAllReservations: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public boolean deleteCentre(int centreId) {
        try {
            sendCommand("ADMIN_DELETE_CENTRE");
            output.writeInt(centreId);
            output.flush();
            return readResponse();
        } catch (Exception e) {
            System.err.println("Erreur deleteCentre: " + e.getMessage());
            return false;
        }
    }

    public boolean bloquerTerrain(int terrainId, String raison) {
        try {
            sendCommand("ADMIN_BLOQUER_TERRAIN");
            output.writeInt(terrainId);
            output.writeObject(raison);
            output.flush();
            return readResponse();
        } catch (Exception e) {
            System.err.println("Erreur bloquerTerrain: " + e.getMessage());
            return false;
        }
    }

    public Object getStatistiques() {
        try {
            sendCommand("ADMIN_STATS");
            return readResponse();
        } catch (Exception e) {
            System.err.println("Erreur getStatistiques: " + e.getMessage());
            return null;
        }
    }
}
