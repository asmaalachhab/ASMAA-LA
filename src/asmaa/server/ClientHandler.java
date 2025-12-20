package asmaa.server;

import asmaa.model.*;
import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Gère la communication avec un client spécifique
 * Chaque instance s'exécute dans son propre thread
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private ReservationManager reservationManager;
    private User currentUser;
    private boolean connected;

    public ClientHandler(Socket socket, ReservationManager manager) {
        this.clientSocket = socket;
        this.reservationManager = manager;
        this.connected = true;
    }

    @Override
    public void run() {
        try {
            // Initialiser les flux I/O
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(clientSocket.getInputStream());

            log("Client connecté: " + clientSocket.getInetAddress());

            // Boucle de traitement des requêtes
            while (connected) {
                try {
                    String command = (String) input.readObject();
                    handleCommand(command);
                } catch (EOFException e) {
                    log("Client déconnecté");
                    break;
                } catch (ClassNotFoundException e) {
                    log("Erreur de protocole: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            log("Erreur I/O: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    /**
     * Traite les commandes envoyées par le client
     */
    private void handleCommand(String command) throws IOException, ClassNotFoundException {
        switch (command) {
            case "LOGIN":
                handleLogin();
                break;
            case "REGISTER":
                handleRegister();
                break;
            case "GET_SPORTS":
                handleGetSports();
                break;
            case "GET_TERRAINS":
                handleGetTerrains();
                break;
            case "GET_CENTRES":
                handleGetCentres();
                break;
            case "CHECK_DISPONIBILITE":
                handleCheckDisponibilite();
                break;
            case "RESERVER":
                handleReserver();
                break;
            case "GET_MES_RESERVATIONS":
                handleGetMesReservations();
                break;
            case "GET_ABONNEMENTS":
                handleGetAbonnements();
                break;
            case "SOUSCRIRE_ABONNEMENT":
                handleSouscrireAbonnement();
                break;
            case "ANNULER_RESERVATION":
                handleAnnulerReservation();
                break;
            case "GET_STATISTIQUES":
                handleGetStatistiques();
                break;
            case "DISCONNECT":
                connected = false;
                break;
            default:
                sendResponse("ERROR", "Commande inconnue: " + command);
        }
    }

    /**
     * Gère la connexion d'un utilisateur
     */
    private void handleLogin() throws IOException, ClassNotFoundException {
        String username = (String) input.readObject();
        String password = (String) input.readObject();

        User user = DatabaseManager.authenticateUser(username, password);

        if (user != null) {
            currentUser = user;
            sendResponse("SUCCESS", user);
            log("Utilisateur connecté: " + username);
        } else {
            sendResponse("ERROR", "Identifiants invalides");
        }
    }

    /**
     * Gère l'inscription d'un nouvel utilisateur
     */
    private void handleRegister() throws IOException, ClassNotFoundException {
        User newUser = (User) input.readObject();

        boolean success = DatabaseManager.registerUser(newUser);

        if (success) {
            sendResponse("SUCCESS", "Inscription réussie");
            log("Nouvel utilisateur enregistré: " + newUser.getUsername());
        } else {
            sendResponse("ERROR", "Nom d'utilisateur ou email déjà utilisé");
        }
    }

    /**
     * Récupère la liste des sports disponibles
     */
    private void handleGetSports() throws IOException {
        List<Sport> sports = DatabaseManager.getAllSports();
        sendResponse("SUCCESS", sports);
    }

    /**
     * Récupère les terrains selon le sport et le centre
     */
    private void handleGetTerrains() throws IOException, ClassNotFoundException {
        int sportId = input.readInt();
        int centreId = input.readInt();

        List<Terrain> terrains = DatabaseManager.getTerrainsBySportAndCentre(sportId, centreId);
        sendResponse("SUCCESS", terrains);
    }

    /**
     * Récupère les centres sportifs d'une ville
     */
    private void handleGetCentres() throws IOException, ClassNotFoundException {
        int villeId = input.readInt();

        List<Centre> centres = DatabaseManager.getCentresByVille(villeId);
        sendResponse("SUCCESS", centres);
    }

    /**
     * Vérifie la disponibilité d'un terrain
     */
    private void handleCheckDisponibilite() throws IOException, ClassNotFoundException {
        int terrainId = input.readInt();
        LocalDate date = (LocalDate) input.readObject();
        LocalTime heureDebut = (LocalTime) input.readObject();
        LocalTime heureFin = (LocalTime) input.readObject();

        boolean disponible = reservationManager.checkDisponibilite(
                terrainId, date, heureDebut, heureFin
        );

        sendResponse("SUCCESS", disponible);
    }

    /**
     * Crée une nouvelle réservation (avec synchronisation)
     */
    private void handleReserver() throws IOException, ClassNotFoundException {
        if (currentUser == null) {
            sendResponse("ERROR", "Vous devez être connecté pour réserver");
            return;
        }

        Reservation reservation = (Reservation) input.readObject();
        reservation.setUserId(currentUser.getId());

        try {
            boolean success = reservationManager.createReservation(reservation);

            if (success) {
                sendResponse("SUCCESS", "Réservation confirmée");
                log("Réservation créée pour: " + currentUser.getUsername());
            } else {
                sendResponse("ERROR", "Terrain non disponible sur ce créneau");
            }
        } catch (Exception e) {
            sendResponse("ERROR", "Erreur lors de la réservation: " + e.getMessage());
        }
    }

    /**
     * Récupère les réservations de l'utilisateur connecté
     */
    private void handleGetMesReservations() throws IOException {
        if (currentUser == null) {
            sendResponse("ERROR", "Vous devez être connecté");
            return;
        }

        List<Reservation> reservations = DatabaseManager.getReservationsByUser(currentUser.getId());
        sendResponse("SUCCESS", reservations);
    }

    /**
     * Récupère les abonnements disponibles
     */
    private void handleGetAbonnements() throws IOException {
        List<Abonnement> abonnements = DatabaseManager.getAllAbonnements();
        sendResponse("SUCCESS", abonnements);
    }

    /**
     * Souscrit à un abonnement
     */
    private void handleSouscrireAbonnement() throws IOException, ClassNotFoundException {
        if (currentUser == null) {
            sendResponse("ERROR", "Vous devez être connecté");
            return;
        }

        int abonnementId = input.readInt();

        boolean success = DatabaseManager.souscrireAbonnement(currentUser.getId(), abonnementId);

        if (success) {
            sendResponse("SUCCESS", "Abonnement activé avec succès");
        } else {
            sendResponse("ERROR", "Erreur lors de la souscription");
        }
    }

    /**
     * Annule une réservation
     */
    private void handleAnnulerReservation() throws IOException, ClassNotFoundException {
        int reservationId = input.readInt();

        boolean success = DatabaseManager.annulerReservation(reservationId);

        if (success) {
            sendResponse("SUCCESS", "Réservation annulée");
        } else {
            sendResponse("ERROR", "Impossible d'annuler la réservation");
        }
    }

    /**
     * Récupère les statistiques (admin uniquement)
     */
    private void handleGetStatistiques() throws IOException {
        if (currentUser == null || !currentUser.isAdmin()) {
            sendResponse("ERROR", "Accès non autorisé");
            return;
        }

        // Récupérer les stats depuis la base
        Object stats = DatabaseManager.getStatistiques();
        sendResponse("SUCCESS", stats);
    }

    /**
     * Envoie une réponse au client
     */
    private void sendResponse(String status, Object data) throws IOException {
        output.writeObject(status);
        output.writeObject(data);
        output.flush();
    }

    /**
     * Ferme la connexion proprement
     */
    private void disconnect() {
        try {
            if (currentUser != null) {
                log("Utilisateur déconnecté: " + currentUser.getUsername());
            }

            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null) clientSocket.close();

        } catch (IOException e) {
            log("Erreur lors de la déconnexion: " + e.getMessage());
        }
    }

    private void log(String message) {
        System.out.println("[ClientHandler] " + message);
    }
}