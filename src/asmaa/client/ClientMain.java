// ClientMain.java
package asmaa.client;

import asmaa.utils.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Application principale JavaFX pour ASMAA-Club
 */
public class ClientMain extends Application {

    private static NetworkClient networkClient;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;

            // Connecter au serveur
            networkClient = new NetworkClient();
            if (!networkClient.connect()) {
                showErrorAndExit("Impossible de se connecter au serveur");
                return;
            }

            // Charger la page d'accueil
            showHomePage();

            // Configuration de la fenêtre
            primaryStage.setTitle("ASMAA-Club - Réservation de Terrains Sportifs");
            primaryStage.setResizable(true);
            primaryStage.setMaximized(true);

            // Handler pour la fermeture
            primaryStage.setOnCloseRequest(event -> {
                networkClient.disconnect();
                SessionManager.getInstance().logout();
            });

            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAndExit("Erreur lors du démarrage: " + e.getMessage());
        }
    }

    /**
     * Affiche la page d'accueil
     */
    private void showHomePage() throws Exception {
        // ✅ Chemin FXML corrigé : commence toujours par /
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/home.fxml")
        );

        // Vérification rapide
        if (loader.getLocation() == null) {
            throw new RuntimeException("FXML home.fxml introuvable ! Vérifie le chemin et Resources Root.");
        }

        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(
                getClass().getResource("/css/styles.css").toExternalForm()
        );

        primaryStage.setScene(scene);
    }

    /**
     * Change la scène actuelle
     */
    public static void changeScene(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ClientMain.class.getResource("/fxml/" + fxmlFile)
            );

            if (loader.getLocation() == null) {
                throw new RuntimeException("FXML " + fxmlFile + " introuvable !");
            }

            Parent root = loader.load();
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(
                    ClientMain.class.getResource("/css/styles.css").toExternalForm()
            );

            primaryStage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du changement de scène: " + e.getMessage());
        }
    }

    /**
     * Obtient le client réseau
     */
    public static NetworkClient getNetworkClient() {
        return networkClient;
    }

    /**
     * Obtient le stage principal
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Affiche une erreur et quitte
     */
    private void showErrorAndExit(String message) {
        System.err.println(message);
        javafx.application.Platform.exit();
    }

    @Override
    public void stop() {
        if (networkClient != null) {
            networkClient.disconnect();
        }
    }

    /**
     * Point d'entrée principal
     */
    public static void main(String[] args) {
        launch(args);
    }
}
