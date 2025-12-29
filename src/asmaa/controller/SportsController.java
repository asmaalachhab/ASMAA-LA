package asmaa.controller;

import asmaa.client.ClientMain;
import asmaa.client.NetworkClient;
import asmaa.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Contrôleur pour la sélection de ville/centre et terrains
 */
public class SportsController {

    @FXML private Label lblSportNom;
    @FXML private ComboBox<Ville> cmbVille;
    @FXML private ComboBox<Centre> cmbCentre;
    @FXML private ListView<Terrain> listTerrains;
    @FXML private Button btnContinuer;
    @FXML private Label lblInfo;

    private static int selectedSportId;
    private static String selectedSportNom;

    private NetworkClient networkClient;
    private ObservableList<Ville> villes;
    private ObservableList<Centre> centres;
    private ObservableList<Terrain> terrains;

    public static void setSelectedSport(int sportId, String sportNom) {
        selectedSportId = sportId;
        selectedSportNom = sportNom;
    }

    @FXML
    public void initialize() {
        networkClient = ClientMain.getNetworkClient();

        // Afficher le sport sélectionné (avec vérification null)
        if (selectedSportNom != null && !selectedSportNom.isEmpty()) {
            lblSportNom.setText("Réserver un terrain de " + selectedSportNom);
        } else {
            lblSportNom.setText("Sélectionnez un sport");
        }

        // Initialiser les listes
        villes = FXCollections.observableArrayList();
        centres = FXCollections.observableArrayList();
        terrains = FXCollections.observableArrayList();

        // Configurer les ComboBox et ListView
        cmbVille.setItems(villes);
        cmbCentre.setItems(centres);
        listTerrains.setItems(terrains);

        // Listeners
        cmbVille.setOnAction(e -> handleVilleSelection());
        cmbCentre.setOnAction(e -> handleCentreSelection());
        listTerrains.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> btnContinuer.setDisable(newVal == null)
        );

        // Charger les villes
        loadVilles();
    }

    /** Charge les villes disponibles */
    private void loadVilles() {
        // CORRECTION: Utiliser NetworkClient au lieu de données hardcodées
        new Thread(() -> {
            try {
                List<Ville> villesList = networkClient.getVilles();
                javafx.application.Platform.runLater(() -> {
                    if (villesList != null && !villesList.isEmpty()) {
                        villes.setAll(villesList);
                    } else {
                        // Fallback si la base de données n'est pas disponible
                        villes.addAll(
                                new Ville() {{ setId(1); setNom("Casablanca"); }},
                                new Ville() {{ setId(2); setNom("Rabat"); }},
                                new Ville() {{ setId(3); setNom("Marrakech"); }},
                                new Ville() {{ setId(4); setNom("Tanger"); }}
                        );
                    }
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    // Fallback en cas d'erreur
                    villes.addAll(
                            new Ville() {{ setId(1); setNom("Casablanca"); }},
                            new Ville() {{ setId(2); setNom("Rabat"); }},
                            new Ville() {{ setId(3); setNom("Marrakech"); }},
                            new Ville() {{ setId(4); setNom("Tanger"); }}
                    );
                });
            }
        }).start();
    }

    /** Gère la sélection d'une ville */
    private void handleVilleSelection() {
        Ville ville = cmbVille.getValue();
        if (ville == null) return;

        centres.clear();
        terrains.clear();

        new Thread(() -> {
            List<Centre> centresList = networkClient.getCentres(ville.getId());
            javafx.application.Platform.runLater(() -> {
                if (centresList != null && !centresList.isEmpty()) {
                    centres.addAll(centresList);
                    lblInfo.setText("");
                } else {
                    showInfo("Aucun centre disponible dans cette ville");
                }
            });
        }).start();
    }

    /** Gère la sélection d'un centre */
    private void handleCentreSelection() {
        Centre centre = cmbCentre.getValue();
        if (centre == null) return;

        // Vérifier qu'un sport est sélectionné
        if (selectedSportId <= 0) {
            showInfo("Veuillez d'abord sélectionner un sport");
            return;
        }

        terrains.clear();

        new Thread(() -> {
            List<Terrain> terrainsList = networkClient.getTerrains(
                    selectedSportId,
                    centre.getId()
            );
            javafx.application.Platform.runLater(() -> {
                if (terrainsList != null && !terrainsList.isEmpty()) {
                    terrains.addAll(terrainsList);
                    lblInfo.setText("");
                } else {
                    showInfo("Aucun terrain disponible");
                }
            });
        }).start();
    }

    /** Continue vers la réservation */
    @FXML
    private void handleContinuer() {
        Terrain selectedTerrain = listTerrains.getSelectionModel().getSelectedItem();
        if (selectedTerrain != null) {
            ReservationController.setSelectedTerrain(selectedTerrain);
            ClientMain.changeScene("reservation.fxml");
        }
    }

    /** Retour à la page d'accueil */
    @FXML
    private void handleBack() {
        ClientMain.changeScene("home.fxml");
    }

    /** Affiche un message d'information */
    private void showInfo(String message) {
        lblInfo.setText(message);
    }
}
