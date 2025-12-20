package asmaa.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Serveur principal multithread pour ASMAA-Club
 * Gère les connexions clients et coordonne les réservations
 */
public class ServerMain {
    private static final int PORT = 5000;
    private static final int MAX_CLIENTS = 100;

    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private ReservationManager reservationManager;
    private boolean running;

    public ServerMain() {
        this.threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        this.reservationManager = new ReservationManager();
        this.running = false;
    }

    /**
     * Démarre le serveur
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;

            log("Serveur ASMAA-Club démarré sur le port " + PORT);
            log("En attente de connexions...");

            // Boucle principale d'acceptation des clients
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    log("Nouvelle connexion: " + clientSocket.getInetAddress());

                    // Créer un handler pour ce client et l'exécuter dans le pool
                    ClientHandler handler = new ClientHandler(
                            clientSocket,
                            reservationManager
                    );
                    threadPool.execute(handler);

                } catch (IOException e) {
                    if (running) {
                        log("Erreur lors de l'acceptation d'un client: " + e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            log("Erreur fatale du serveur: " + e.getMessage());
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    /**
     * Arrête le serveur proprement
     */
    public void shutdown() {
        try {
            running = false;

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            threadPool.shutdown();
            if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }

            log("Serveur arrêté proprement");

        } catch (Exception e) {
            log("Erreur lors de l'arrêt: " + e.getMessage());
            threadPool.shutdownNow();
        }
    }

    /**
     * Enregistre un message dans les logs avec horodatage
     */
    private static void log(String message) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("[" + timestamp + "] " + message);
    }

    /**
     * Point d'entrée principal
     */
    public static void main(String[] args) {
        // Initialiser la base de données
        DatabaseManager.initialize();

        // Créer et démarrer le serveur
        ServerMain server = new ServerMain();

        // Hook pour arrêt propre lors du Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log("Signal d'arrêt reçu");
            server.shutdown();
        }));

        // Démarrer le serveur
        server.start();
    }
}