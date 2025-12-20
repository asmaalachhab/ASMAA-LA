package asmaa.controller;

import asmaa.client.ClientMain;
import javafx.fxml.FXML;

/**
 * Contrôleur pour la page des villes
 */
public class VillesController {

    @FXML
    public void initialize() {
        System.out.println("✅ VillesController initialisé");
    }

    @FXML
    private void handleBack() {
        System.out.println("← Retour à l'accueil");
        ClientMain.changeScene("home.fxml");
    }

    @FXML
    private void handleCasablanca() {
        System.out.println("🏙️ Ville sélectionnée: Casablanca");
        CentresController.setSelectedVille(1, "Casablanca");
        ClientMain.changeScene("centres.fxml");
    }

    @FXML
    private void handleRabat() {
        System.out.println("🏛️ Ville sélectionnée: Rabat");
        CentresController.setSelectedVille(2, "Rabat");
        ClientMain.changeScene("centres.fxml");
    }

    @FXML
    private void handleMarrakech() {
        System.out.println("🕌 Ville sélectionnée: Marrakech");
        CentresController.setSelectedVille(3, "Marrakech");
        ClientMain.changeScene("centres.fxml");
    }

    @FXML
    private void handleTanger() {
        System.out.println("⛵ Ville sélectionnée: Tanger");
        CentresController.setSelectedVille(4, "Tanger");
        ClientMain.changeScene("centres.fxml");
    }

    @FXML
    private void handleAgadir() {
        System.out.println("🏖️ Ville sélectionnée: Agadir");
        CentresController.setSelectedVille(5, "Agadir");
        ClientMain.changeScene("centres.fxml");
    }
}