package asmaa.controller;

import asmaa.client.ClientMain;
import asmaa.client.NetworkClient;
import asmaa.model.*;

import asmaa.utils.SessionManager;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * CONTROLEUR ADMIN – VERSION COMPATIBLE MODELS ACTUELS
 */
public class AdminController {

    // ==================== TABLES ====================

    @FXML private TableView<Centre> tableCentres;
    @FXML private TableColumn<Centre, String> colCentreNom;
    @FXML private TableColumn<Centre, String> colCentreVille;
    @FXML private TableColumn<Centre, String> colCentreAdresse;
    @FXML private TableColumn<Centre, String> colCentreStatut;

    @FXML private TableView<Terrain> tableTerrains;
    @FXML private TableColumn<Terrain, String> colTerrainNom;
    @FXML private TableColumn<Terrain, String> colTerrainCentre;
    @FXML private TableColumn<Terrain, String> colTerrainSport;
    @FXML private TableColumn<Terrain, Double> colTerrainPrix;
    @FXML private TableColumn<Terrain, String> colTerrainStatut;

    @FXML private TableView<Reservation> tableReservations;
    @FXML private TableColumn<Reservation, Integer> colResId;
    @FXML private TableColumn<Reservation, String> colResClient;
    @FXML private TableColumn<Reservation, String> colResTerrain;
    @FXML private TableColumn<Reservation, String> colResDate;
    @FXML private TableColumn<Reservation, String> colResStatut;

    // ==================== FORM CENTRE ====================

    @FXML private VBox formCentre;
    @FXML private TextField txtCentreNom;
    @FXML private TextField txtCentreAdresse;
    @FXML private TextField txtCentreTelephone;
    @FXML private CheckBox chkCentreActif;

    // ==================== FORM TERRAIN ====================

    @FXML private VBox formTerrain;
    @FXML private TextField txtTerrainNom;
    @FXML private TextField txtTerrainTypeSurface;
    @FXML private TextField txtTerrainCapacite;
    @FXML private TextField txtTerrainPrix;
    @FXML private CheckBox chkTerrainActif;

    // ==================== DATA ====================

    private NetworkClient networkClient;
    private SessionManager sessionManager;

    private final ObservableList<Centre> centres = FXCollections.observableArrayList();
    private final ObservableList<Terrain> terrains = FXCollections.observableArrayList();
    private final ObservableList<Reservation> reservations = FXCollections.observableArrayList();

    // ==================== INITIALISATION ====================

    @FXML
    public void initialize() {

        networkClient = ClientMain.getNetworkClient();
        sessionManager = SessionManager.getInstance();

        User user = sessionManager.getCurrentUser();
        if (user == null || !user.isAdmin()) {
            showError("Accès refusé", "Vous devez être administrateur");
            ClientMain.changeScene("home.fxml");
            return;
        }

        setupTables();
        loadAllData();

        System.out.println("✅ Admin connecté : " + user.getUsername());
    }

    // ==================== TABLE SETUP ====================

    private void setupTables() {

        // ----- CENTRES -----
        colCentreNom.setCellValueFactory(d -> {
            Centre c = d.getValue();
            return new SimpleStringProperty(c != null && c.getNom() != null ? c.getNom() : "");
        });

        colCentreVille.setCellValueFactory(d -> {
            Centre c = d.getValue();
            return new SimpleStringProperty(c != null ? "Ville ID : " + c.getVilleId() : "");
        });

        colCentreAdresse.setCellValueFactory(d -> {
            Centre c = d.getValue();
            return new SimpleStringProperty(c != null && c.getAdresse() != null ? c.getAdresse() : "");
        });

        colCentreStatut.setCellValueFactory(d -> {
            Centre c = d.getValue();
            return new SimpleStringProperty(c != null && c.isActif() ? "✅ Actif" : "❌ Inactif");
        });

        tableCentres.setItems(centres);

        // ----- TERRAINS -----
        colTerrainNom.setCellValueFactory(d -> {
            Terrain t = d.getValue();
            return new SimpleStringProperty(t != null && t.getNom() != null ? t.getNom() : "");
        });

        colTerrainCentre.setCellValueFactory(d -> {
            Terrain t = d.getValue();
            return new SimpleStringProperty(t != null && t.getCentreNom() != null ? t.getCentreNom() : "");
        });

        colTerrainSport.setCellValueFactory(d -> {
            Terrain t = d.getValue();
            return new SimpleStringProperty(t != null && t.getSportNom() != null ? t.getSportNom() : "");
        });

        colTerrainPrix.setCellValueFactory(d -> {
            Terrain t = d.getValue();
            return new SimpleObjectProperty<>(t != null ? t.getPrixHeure() : 0.0);
        });

        colTerrainStatut.setCellValueFactory(d -> {
            Terrain t = d.getValue();
            return new SimpleStringProperty(t != null && t.isActif() ? "✅ Actif" : "❌ Bloqué");
        });

        tableTerrains.setItems(terrains);

        // ----- RESERVATIONS -----
        colResId.setCellValueFactory(d -> {
            Reservation r = d.getValue();
            return new SimpleObjectProperty<>(r != null ? r.getId() : 0);
        });

        colResClient.setCellValueFactory(d -> {
            Reservation r = d.getValue();
            return new SimpleStringProperty(r != null ? "User #" + r.getUserId() : "");
        });

        colResTerrain.setCellValueFactory(d -> {
            Reservation r = d.getValue();
            return new SimpleStringProperty(r != null && r.getTerrainNom() != null ? r.getTerrainNom() : "");
        });

        colResDate.setCellValueFactory(d -> {
            Reservation r = d.getValue();
            return new SimpleStringProperty(r != null && r.getDateReservation() != null 
                    ? r.getDateReservation().toString() : "");
        });

        colResStatut.setCellValueFactory(d -> {
            Reservation r = d.getValue();
            return new SimpleStringProperty(r != null && r.getStatut() != null 
                    ? r.getStatut().name() : "");
        });

        tableReservations.setItems(reservations);
    }

    // ==================== LOAD DATA ====================

    private void loadAllData() {
        loadCentres();
        loadTerrains();
        loadReservations();
    }

    private void loadCentres() {
        new Thread(() -> {
            try {
                List<Centre> list = networkClient.getAllCentres();
                Platform.runLater(() -> {
                    if (list != null) {
                        centres.setAll(list);
                    } else {
                        centres.clear();
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Erreur", "Impossible de charger les centres: " + e.getMessage());
                });
            }
        }).start();
    }

    private void loadTerrains() {
        new Thread(() -> {
            try {
                List<Terrain> list = networkClient.getAllTerrains();
                Platform.runLater(() -> {
                    if (list != null) {
                        terrains.setAll(list);
                    } else {
                        terrains.clear();
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Erreur", "Impossible de charger les terrains: " + e.getMessage());
                });
            }
        }).start();
    }

    private void loadReservations() {
        new Thread(() -> {
            try {
                List<Reservation> list = networkClient.getAllReservations();
                Platform.runLater(() -> {
                    if (list != null) {
                        reservations.setAll(list);
                    } else {
                        reservations.clear();
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Erreur", "Impossible de charger les réservations: " + e.getMessage());
                });
            }
        }).start();
    }

    // ==================== ACTIONS ====================

    @FXML
    private void handleSupprimerCentre() {
        Centre c = tableCentres.getSelectionModel().getSelectedItem();
        if (c == null) {
            showWarning("Sélection", "Sélectionnez un centre");
            return;
        }

        new Thread(() -> {
            boolean ok = networkClient.deleteCentre(c.getId());
            Platform.runLater(() -> {
                if (ok) {
                    showSuccess("Centre supprimé");
                    loadCentres();
                } else {
                    showError("Erreur", "Suppression échouée");
                }
            });
        }).start();
    }

    @FXML
    private void handleBloquerTerrain() {
        Terrain t = tableTerrains.getSelectionModel().getSelectedItem();
        if (t == null) {
            showWarning("Sélection", "Sélectionnez un terrain");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Bloquer terrain");
        dialog.setHeaderText("Terrain : " + t.getNom());
        dialog.setContentText("Raison :");

        dialog.showAndWait().ifPresent(raison -> {
            new Thread(() -> {
                boolean ok = networkClient.bloquerTerrain(t.getId(), raison);
                Platform.runLater(() -> {
                    if (ok) {
                        showSuccess("Terrain bloqué");
                        loadTerrains();
                    } else {
                        showError("Erreur", "Blocage échoué");
                    }
                });
            }).start();
        });
    }

    @FXML
    private void handleAnnulerReservation() {
        Reservation r = tableReservations.getSelectionModel().getSelectedItem();
        if (r == null) {
            showWarning("Sélection", "Sélectionnez une réservation");
            return;
        }

        new Thread(() -> {
            boolean ok = networkClient.annulerReservation(r.getId());
            Platform.runLater(() -> {
                if (ok) {
                    showSuccess("Réservation annulée");
                    loadReservations();
                } else {
                    showError("Erreur", "Annulation échouée");
                }
            });
        }).start();
    }

    @FXML
    private void handleBack() {
        ClientMain.changeScene("home.fxml");
    }

    // ==================== ALERTS ====================

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showWarning(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showSuccess(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Succès");
        a.setContentText(msg);
        a.showAndWait();
    }
}
