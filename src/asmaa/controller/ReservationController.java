package asmaa.controller;

import asmaa.client.ClientMain;
import asmaa.client.NetworkClient;
import asmaa.model.*;
import asmaa.server.DatabaseManager;
import asmaa.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationController {

    // ==================== UI ====================
    @FXML private ComboBox<Ville> cmbVille;
    @FXML private ComboBox<Centre> cmbCentre;
    @FXML private ComboBox<Sport> cmbSport;
    @FXML private ListView<Terrain> listTerrains;

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> cmbHeureDebut;
    @FXML private ComboBox<String> cmbHeureFin;

    @FXML private Label lblTerrainInfo;
    @FXML private Label lblPrixFinal;
    @FXML private Label lblStatus;

    @FXML private VBox vboxInvite;
    @FXML private TextField txtNomInvite;
    @FXML private TextField txtTelephoneInvite;
    @FXML private TextField txtEmailInvite;

    @FXML private Button btnVerifier;
    @FXML private Button btnReserver;
    @FXML private CheckBox chkAccepterConditions;

    // ==================== DATA ====================
    private NetworkClient networkClient;
    private SessionManager sessionManager;

    private ObservableList<Ville> villes = FXCollections.observableArrayList();
    private ObservableList<Centre> centres = FXCollections.observableArrayList();
    private ObservableList<Sport> sports = FXCollections.observableArrayList();
    private ObservableList<Terrain> terrains = FXCollections.observableArrayList();

    private Terrain selectedTerrain;
    private boolean disponible = false;

    // Pour passer le terrain depuis SportsController
    private static Terrain selectedTerrainStatic;
    public static void setSelectedTerrain(Terrain terrain) {
        selectedTerrainStatic = terrain;
    }

    // ==================== INIT ====================
    @FXML
    public void initialize() {
        networkClient = ClientMain.getNetworkClient();
        sessionManager = SessionManager.getInstance();

        cmbVille.setItems(villes);
        cmbCentre.setItems(centres);
        cmbSport.setItems(sports);
        listTerrains.setItems(terrains);

        setupHeures();
        setupDatePicker();
        updateInviteForm();

        if (selectedTerrainStatic != null) {
            selectedTerrain = selectedTerrainStatic;
            terrains.add(selectedTerrain);
            listTerrains.getSelectionModel().select(selectedTerrain);
            updateTerrainInfo();
        }

        cmbVille.setOnAction(e -> loadCentres());
        cmbCentre.setOnAction(e -> loadTerrains());
        cmbSport.setOnAction(e -> loadTerrains());

        listTerrains.getSelectionModel().selectedItemProperty()
                .addListener((obs,o,n) -> { selectedTerrain = n; updateTerrainInfo(); resetDisponibilite(); });

        cmbHeureDebut.setOnAction(e -> resetDisponibilite());
        cmbHeureFin.setOnAction(e -> resetDisponibilite());

        loadVilles();
        loadSports();
    }

    // ==================== LOADERS ====================
    private void loadVilles() {
        new Thread(() -> {
            List<Ville> list = DatabaseManager.getAllVilles();
            javafx.application.Platform.runLater(() -> villes.addAll(list));
        }).start();
    }

    private void loadSports() {
        new Thread(() -> {
            List<Sport> list = DatabaseManager.getAllSports();
            javafx.application.Platform.runLater(() -> sports.addAll(list));
        }).start();
    }

    private void loadCentres() {
        Ville ville = cmbVille.getValue();
        if (ville == null) return;

        centres.clear();
        terrains.clear();
        cmbCentre.setValue(null);
        cmbSport.setValue(null);

        new Thread(() -> {
            List<Centre> list = networkClient.getCentres(ville.getId());
            javafx.application.Platform.runLater(() -> centres.addAll(list));
        }).start();
    }

    private void loadTerrains() {
        if (cmbCentre.getValue() == null || cmbSport.getValue() == null) return;

        terrains.clear();
        new Thread(() -> {
            List<Terrain> list = networkClient.getTerrains(
                    cmbSport.getValue().getId(),
                    cmbCentre.getValue().getId()
            );
            javafx.application.Platform.runLater(() -> terrains.addAll(list));
        }).start();
    }

    // ==================== ACTIONS ====================
    @FXML
    private void handleVerifier() {
        if (!validateSelection()) return;

        disponible = networkClient.checkDisponibilite(
                selectedTerrain.getId(),
                datePicker.getValue(),
                LocalTime.parse(cmbHeureDebut.getValue()),
                LocalTime.parse(cmbHeureFin.getValue())
        );

        if (disponible) {
            showSuccess("Terrain disponible ✅");
            btnReserver.setDisable(false);
        } else {
            showError("Terrain non disponible ❌");
            btnReserver.setDisable(true);
        }
    }

    @FXML
    private void handleReserver() {
        if (!disponible) {
            showError("Veuillez vérifier la disponibilité");
            return;
        }

        if (!sessionManager.isLoggedIn() && !validateInviteForm()) return;

        Reservation reservation = new Reservation();
        reservation.setTerrainId(selectedTerrain.getId());
        reservation.setDateReservation(datePicker.getValue());
        reservation.setHeureDebut(LocalTime.parse(cmbHeureDebut.getValue()));
        reservation.setHeureFin(LocalTime.parse(cmbHeureFin.getValue()));
        reservation.setPrixTotal(selectedTerrain.getPrixHeure() * calculateDuration());

        boolean success = networkClient.createReservation(reservation);
        if (success) {
            showSuccess("Réservation confirmée 🎉");
            ClientMain.changeScene("home.fxml");
        } else {
            showError("Erreur lors de la réservation");
        }
    }

    // ==================== UTILS ====================
    private void setupHeures() {
        for (int h=7; h<=23; h++) {
            String v = String.format("%02d:00", h);
            cmbHeureDebut.getItems().add(v);
            cmbHeureFin.getItems().add(v);
        }
    }

    private void setupDatePicker() {
        datePicker.setDayCellFactory(p -> new DateCell() {
            @Override
            public void updateItem(LocalDate d, boolean empty) {
                super.updateItem(d, empty);
                setDisable(empty || d.isBefore(LocalDate.now()));
            }
        });
    }

    private double calculateDuration() {
        LocalTime d = LocalTime.parse(cmbHeureDebut.getValue());
        LocalTime f = LocalTime.parse(cmbHeureFin.getValue());
        return (f.toSecondOfDay() - d.toSecondOfDay())/3600.0;
    }

    private boolean validateSelection() {
        if (selectedTerrain==null || datePicker.getValue()==null
                || cmbHeureDebut.getValue()==null || cmbHeureFin.getValue()==null) {
            showError("Veuillez remplir tous les champs");
            return false;
        }
        return true;
    }

    private boolean validateInviteForm() {
        if (txtNomInvite.getText().trim().isEmpty()) { showError("Nom requis"); return false; }
        if (txtTelephoneInvite.getText().trim().isEmpty()) { showError("Téléphone requis"); return false; }
        if (txtEmailInvite.getText().trim().isEmpty()) { showError("Email requis"); return false; }
        return true;
    }

    private void updateInviteForm() {
        boolean logged = sessionManager.isLoggedIn();
        vboxInvite.setVisible(!logged);
        vboxInvite.setManaged(!logged);
    }

    private void resetDisponibilite() {
        disponible=false;
        btnReserver.setDisable(true);
        lblStatus.setText("");
    }

    private void updateTerrainInfo() {
        if (selectedTerrain!=null) {
            lblTerrainInfo.setText(selectedTerrain.getNom() + " - " + selectedTerrain.getCentreNom());
            lblPrixFinal.setText(String.format("%.2f DH/heure", selectedTerrain.getPrixHeure()));
        }
    }

    private void showError(String msg) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill:red;");
    }

    private void showSuccess(String msg) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill:green;");
    }

    @FXML
    private void handleBack() {
        ClientMain.changeScene("home.fxml");
    }
}
