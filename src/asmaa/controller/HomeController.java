package asmaa.controller;

import asmaa.client.ClientMain;
import asmaa.utils.SessionManager;
import asmaa.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

/**
 * Contrôleur HOME avec toute la logique de navigation et scroll
 */
public class HomeController {

    // Boutons header
    @FXML private Button btnLogin;
    @FXML private Button btnInscription;
    @FXML private MenuButton btnCompte;

    // Sections
    @FXML private VBox mainContent;
    @FXML private VBox sectionHero;
    @FXML private VBox sectionSports;
    @FXML private VBox sectionNotreClub;

    private SessionManager sessionManager;

    @FXML
    public void initialize() {
        sessionManager = SessionManager.getInstance();

        // Mettre à jour l'UI selon l'état de connexion
        updateUIForUserState();

        System.out.println("✅ HomeController initialisé avec scroll activé");
    }

    /**
     * Met à jour l'interface selon l'état de connexion
     */
    private void updateUIForUserState() {
        if (sessionManager.isLoggedIn()) {
            User user = sessionManager.getCurrentUser();

            // Cacher Login/Inscription
            btnLogin.setVisible(false);
            btnLogin.setManaged(false);
            btnInscription.setVisible(false);
            btnInscription.setManaged(false);

            // Afficher le bouton Compte
            btnCompte.setVisible(true);
            btnCompte.setManaged(true);
            btnCompte.setText("👤 " + user.getPrenom());

            System.out.println("✅ Interface mise à jour pour: " + user.getUsername());
        } else {
            // Afficher Login/Inscription
            btnLogin.setVisible(true);
            btnLogin.setManaged(true);
            btnInscription.setVisible(true);
            btnInscription.setManaged(true);

            // Cacher le bouton Compte
            btnCompte.setVisible(false);
            btnCompte.setManaged(false);
        }
    }

    // ==================== NAVIGATION MENU ====================

    @FXML
    private void handleAccueil() {
        System.out.println("🏠 Navigation: Accueil");
        scrollToSection(sectionHero);
    }

    @FXML
    private void handleNotreClub() {
        System.out.println("🏢 Navigation: Notre Club");
        scrollToSection(sectionNotreClub);
    }

    @FXML
    private void handleVilles() {
        System.out.println("🌆 Navigation: Villes");
        ClientMain.changeScene("villes.fxml");
    }

    @FXML
    private void handleCentres() {
        System.out.println("🏟️ Navigation: Centres");
        ClientMain.changeScene("centres.fxml");
    }

    @FXML
    private void handleReserver() {
        System.out.println("⚽ Navigation: Réserver");
        scrollToSection(sectionSports);
    }

    @FXML
    private void handleContact() {
        System.out.println("📞 Navigation: Contact");
        showContactDialog();
    }

    @FXML
    private void handleAML() {
        System.out.println("🔄 Navigation: AML");
        ClientMain.changeScene("aml.fxml");
    }

    // ==================== AUTHENTIFICATION ====================

    @FXML
    private void handleLogin() {
        System.out.println("🔑 Ouverture page de connexion");
        ClientMain.changeScene("login.fxml");
    }

    @FXML
    private void handleInscription() {
        System.out.println("📝 Ouverture page d'inscription");
        ClientMain.changeScene("register.fxml");
    }

    // ==================== MENU COMPTE ====================

    @FXML
    private void handleProfil() {
        System.out.println("👤 Navigation: Mon Profil");
        ClientMain.changeScene("profil.fxml");
    }

    @FXML
    private void handleMesReservations() {
        System.out.println("📅 Navigation: Mes Réservations");
        ClientMain.changeScene("mes-reservations.fxml");
    }

    @FXML
    private void handleHistorique() {
        System.out.println("📜 Navigation: Mon Historique");
        ClientMain.changeScene("historique.fxml");
    }

    @FXML
    private void handleDeconnexion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText(null);
        alert.setContentText("Voulez-vous vraiment vous déconnecter ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                sessionManager.logout();
                System.out.println("✅ Déconnexion réussie");

                // Recharger la page d'accueil
                ClientMain.changeScene("home.fxml");
            }
        });
    }

    // ==================== SELECTION SPORTS ====================

    @FXML
    private void handleSportFootball() {
        System.out.println("⚽ Sport sélectionné: Football");
        navigateToSport(1, "Football");
    }

    @FXML
    private void handleSportBasketball() {
        System.out.println("🏀 Sport sélectionné: Basketball");
        navigateToSport(2, "Basketball");
    }

    @FXML
    private void handleSportTennis() {
        System.out.println("🎾 Sport sélectionné: Tennis");
        navigateToSport(3, "Tennis");
    }

    @FXML
    private void handleSportPaddle() {
        System.out.println("🎯 Sport sélectionné: Paddle");
        navigateToSport(4, "Paddle");
    }

    /**
     * Navigation vers la page de sélection de terrains
     */
    private void navigateToSport(int sportId, String sportNom) {
        // Stocker le sport sélectionné
        SportsController.setSelectedSport(sportId, sportNom);

        // Naviguer vers la page de sélection
        ClientMain.changeScene("sports.fxml");
    }

    // ==================== DIALOGUE CONTACT ====================

    /**
     * Affiche la popup de contact
     */
    private void showContactDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Contactez-nous");
        alert.setHeaderText("ASMAA CLUB - Informations de contact");

        String content =
                "📧 Email: contact@asmaaclub.ma\n\n" +
                        "📱 WhatsApp: +212 600-123456\n\n" +
                        "☎️ Standard téléphonique:\n" +
                        "   • Casablanca: 0522-123456\n" +
                        "   • Rabat: 0537-234567\n" +
                        "   • Marrakech: 0524-345678\n" +
                        "   • Tanger: 0539-456789\n" +
                        "   • Agadir: 0528-567890\n\n" +
                        "🌐 Site web: www.asmaaclub.ma\n\n" +
                        "📍 Siège social:\n" +
                        "   Boulevard Zerktouni, Casablanca\n\n" +
                        "⏰ Horaires d'ouverture:\n" +
                        "   Tous les jours de 7h à 23h";

        alert.setContentText(content);
        alert.getDialogPane().setMinWidth(500);
        alert.showAndWait();
    }

    // ==================== SCROLL AUTOMATIQUE ====================

    /**
     * Scroll automatique vers une section
     */
    private void scrollToSection(VBox targetSection) {
        if (targetSection == null) {
            System.out.println("⚠️ Section cible introuvable");
            return;
        }

        try {
            // Calculer la position Y de la section
            double targetY = targetSection.getLayoutY();

            // Animation de scroll fluide
            TranslateTransition transition = new TranslateTransition(
                    Duration.millis(800),
                    mainContent
            );
            transition.setToY(-targetY);
            transition.play();

            System.out.println("✅ Scroll vers la section à Y=" + targetY);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du scroll: " + e.getMessage());
        }
    }

    // ==================== UTILITAIRES ====================

    /**
     * Affiche une info
     */
    private void showInfo(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une erreur
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche un succès
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}