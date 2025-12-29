// LoginController.java
package asmaa.controller;

import asmaa.client.ClientMain;
import asmaa.client.NetworkClient;
import asmaa.model.User;
import asmaa.utils.SessionManager;
import asmaa.utils.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;

/**
 * Contrôleur pour la page de connexion
 */
public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Button btnRegister;
    @FXML private Label lblError;
    @FXML private CheckBox chkRememberMe;

    private NetworkClient networkClient;

    @FXML
    public void initialize() {
        networkClient = ClientMain.getNetworkClient();
        lblError.setVisible(false);

        // Enter key pour se connecter
        txtPassword.setOnAction(e -> handleLogin());
    }

    /**
     * Gère la connexion
     */
    @FXML
    private void handleLogin() {
        // Effacer le message d'erreur
        lblError.setVisible(false);

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        // Validation
        if (!ValidationUtil.isNotEmpty(username, password)) {
            showError("Veuillez remplir tous les champs");
            return;
        }

        // Désactiver le bouton pendant la connexion
        btnLogin.setDisable(true);
        btnLogin.setText("Connexion...");

        // Connexion dans un thread séparé
        new Thread(() -> {
            try {
                User user = networkClient.login(username, password);

                javafx.application.Platform.runLater(() -> {
                    if (user != null) {
                        // Connexion réussie
                        SessionManager.getInstance().login(user);

                        // Rediriger vers la page d'accueil
                        ClientMain.changeScene("home.fxml");

                    } else {
                        // Échec de la connexion
                        showError("Nom d'utilisateur ou mot de passe incorrect");
                        btnLogin.setDisable(false);
                        btnLogin.setText("Se connecter");
                    }
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    showError("Erreur de connexion: " + e.getMessage());
                    btnLogin.setDisable(false);
                    btnLogin.setText("Se connecter");
                });
            }
        }).start();
    }

    /**
     * Redirige vers la page d'inscription
     */
    @FXML
    private void handleRegister() {
        ClientMain.changeScene("register.fxml");
    }

    /**
     * Retour à la page d'accueil
     */
    @FXML
    private void handleBack() {
        ClientMain.changeScene("home.fxml");
    }

    /**
     * Affiche un message d'erreur
     */
    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }
}