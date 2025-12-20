package asmaa.controller;

import asmaa.client.ClientMain;
import asmaa.client.NetworkClient;
import asmaa.model.Centre;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

import static asmaa.controller.SportsController.*;

/**
 * Contrôleur pour la page des centres
 */
public class CentresController {

    @FXML private Label lblVilleNom;
    @FXML private ListView<Centre> listCentres;
    @FXML private Button btnContinuer;
    @FXML private Label lblInfo;

    private static int selectedVilleId;
    private static String selectedVilleNom;

    private NetworkClient networkClient;
    private ObservableList<Centre> centres;

    public static void setSelectedVille(int villeId, String villeNom) {
        selectedVilleId = villeId;
        selectedVilleNom = villeNom;
    }

    @FXML
    public void initialize() {
        networkClient = ClientMain.getNetworkClient();
        centres = FXCollections.observableArrayList();

        // Afficher la ville sélectionnée
        lblVilleNom.setText("Centres à " + selectedVilleNom);

        // Configurer la liste
        listCentres.setItems(centres);
        listCentres.setCellFactory(param -> new javafx.scene.control.ListCell<Centre>() {
            @Override
            protected void updateItem(Centre centre, boolean empty) {
                super.updateItem(centre, empty);
                if (empty || centre == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(centre.getNom() + "\n" +
                            centre.getAdresse() + "\n" +
                            "☎️ " + centre.getTelephone() + "\n" +
                            "⏰ " + centre.getHoraireOuverture() + " - " + centre.getHoraireFermeture());
                }
            }
        });

        // Listener sur la sélection
        listCentres.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> btnContinuer.setDisable(newVal == null)
        );

        // Charger les centres
        loadCentres();

        System.out.println("✅ CentresController initialisé pour " + selectedVilleNom);
    }

    /**
     * Charge les centres de la ville
     */
    private void loadCentres() {
        new Thread(() -> {
            List<Centre> centresList = networkClient.getCentres(selectedVilleId);

            javafx.application.Platform.runLater(() -> {
                if (centresList != null && !centresList.isEmpty()) {
                    centres.addAll(centresList);
                    lblInfo.setText("✅ " + centresList.size() + " centre(s) trouvé(s)");
                } else {
                    lblInfo.setText("⚠️ Aucun centre disponible dans cette ville");
                }
            });
        }).start();
    }

    @FXML
    private void handleContinuer() {
        Centre selectedCentre = listCentres.getSelectionModel().getSelectedItem();

        if (selectedCentre != null) {
            // Passer à la sélection des sports
            setSelectedSport(selectedCentre.getId(), selectedCentre.getNom());
            ClientMain.changeScene("sports.fxml");
        }
    }

    @FXML
    private void handleBack() {
        ClientMain.changeScene("villes.fxml");
    }
}