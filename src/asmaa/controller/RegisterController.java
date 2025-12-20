// RegisterController.java
package asmaa.controller;

import asmaa.client.ClientMain;
import asmaa.client.NetworkClient;
import asmaa.model.User;
import asmaa.utils.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Contrôleur pour la page d'inscription
 */
public class RegisterController {

    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtTelephone;
    @FXML private Button btnRegister;
    @FXML private Button btnLogin;
    @FXML private Label lblError;
    @FXML private Label lblSuccess;

    private NetworkClient networkClient;

    @FXML
    public void initialize() {
        networkClient = ClientMain.getNetworkClient();
        lblError.setVisible(false);
        lblSuccess.setVisible(false);
    }

    /**
     * Gère l'inscription
     */
    @FXML
    private void handleRegister() {
        // Effacer les messages
        lblError.setVisible(false);
        lblSuccess.setVisible(false);

        // Récupérer les valeurs
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String telephone = txtTelephone.getText().trim();

        // Validation
        if (!ValidationUtil.isNotEmpty(username, email, password, nom, prenom)) {
            showError("Veuillez remplir tous les champs obligatoires");
            return;
        }

        if (!ValidationUtil.isValidUsername(username)) {
            showError("Nom d'utilisateur invalide (3-20 caractères alphanumériques)");
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            showError("Adresse email invalide");
            return;
        }

        if (!ValidationUtil.isValidPassword(password)) {
            showError("Mot de passe trop court (minimum 6 caractères)");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Les mots de passe ne correspondent pas");
            return;
        }

        if (!telephone.isEmpty() && !ValidationUtil.isValidPhone(telephone)) {
            showError("Numéro de téléphone invalide");
            return;
        }

        // Créer l'utilisateur
        User newUser = new User(username, email, password, nom, prenom);
        newUser.setTelephone(telephone);

        // Désactiver le bouton
        btnRegister.setDisable(true);
        btnRegister.setText("Inscription...");

        // Inscription dans un thread séparé
        new Thread(() -> {
            try {
                boolean success = networkClient.register(newUser);

                javafx.application.Platform.runLater(() -> {
                    if (success) {
                        showSuccess("Inscription réussie ! Vous pouvez maintenant vous connecter.");
                        clearFields();
                    } else {
                        showError("Nom d'utilisateur ou email déjà utilisé");
                    }

                    btnRegister.setDisable(false);
                    btnRegister.setText("S'inscrire");
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    showError("Erreur lors de l'inscription: " + e.getMessage());
                    btnRegister.setDisable(false);
                    btnRegister.setText("S'inscrire");
                });
            }
        }).start();
    }

    /**
     * Redirige vers la page de connexion
     */
    @FXML
    private void handleLogin() {
        ClientMain.changeScene("login.fxml");
    }

    /**
     * Retour à la page d'accueil
     */
    @FXML
    private void handleBack() {
        ClientMain.changeScene("home.fxml");
    }

    /**
     * Vide tous les champs
     */
    private void clearFields() {
        txtUsername.clear();
        txtEmail.clear();
        txtPassword.clear();
        txtConfirmPassword.clear();
        txtNom.clear();
        txtPrenom.clear();
        txtTelephone.clear();
    }

    /**
     * Affiche un message d'erreur
     */
    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
        lblSuccess.setVisible(false);
    }

    /**
     * Affiche un message de succès
     */
    private void showSuccess(String message) {
        lblSuccess.setText(message);
        lblSuccess.setVisible(true);
        lblError.setVisible(false);
    }
}