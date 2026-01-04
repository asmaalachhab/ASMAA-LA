package asmaa.server;

import asmaa.model.*;
import asmaa.utils.PasswordUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire de base de données MySQL pour ASMAA-Club
 * Utilise le pattern Singleton et un pool de connexions
 */
public class DatabaseManager {

    // Configuration de la base de données
    private static final String URL = "jdbc:mysql://localhost:3306/asmaa_club?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";  // Modifier selon votre configuration

    private static Connection connection;
    private static final Object connectionLock = new Object();

    /**
     * Initialise la connexion à la base de données
     */
    public static void initialize() {
        synchronized (connectionLock) {
            try {
                // Charger le driver MySQL
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Créer la connexion
                connection = DriverManager.getConnection(URL, USER, PASSWORD);

                System.out.println("✓ Connexion à la base de données réussie");

            } catch (ClassNotFoundException e) {
                System.err.println("✗ Driver MySQL non trouvé: " + e.getMessage());
                System.exit(1);
            } catch (SQLException e) {
                System.err.println("✗ Erreur de connexion à la base de données: " + e.getMessage());
                System.exit(1);
            }
        }
    }

    /**
     * Obtenir une connexion à la base de données (thread-safe)
     * Note: Pour une meilleure performance en production, utilisez un pool de connexions (HikariCP)
     */
    public static Connection getConnection() throws SQLException {
        synchronized (connectionLock) {
            try {
                if (connection == null || connection.isClosed()) {
                    initialize();
                }
            } catch (SQLException e) {
                // Si la connexion est fermée, réinitialiser
                initialize();
            }
            return connection;
        }
    }

    // ==================== GESTION DES UTILISATEURS ====================

    /**
     * Authentifie un utilisateur
     */
    public static User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND actif = TRUE";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                // Vérifier le mot de passe (utilisez BCrypt en production)

                    if (PasswordUtil.verifyPassword(password, hashedPassword, username)) {

                        return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'authentification: " + e.getMessage());
        }

        return null;
    }

    /**
     * Enregistre un nouvel utilisateur
     */
    public static boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, email, password, nom, prenom, telephone, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {


            stmt.setString(3, PasswordUtil.hashPassword(user.getPassword(), user.getUsername()));

            stmt.setString(2, user.getEmail());
            
            stmt.setString(3, PasswordUtil.hashPassword(user.getPassword(), user.getUsername()));

            stmt.setString(4, user.getNom());
            stmt.setString(5, user.getPrenom());
            stmt.setString(6, user.getTelephone());
            stmt.setString(7, user.getRole().name());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'inscription: " + e.getMessage());
        }

        return false;
    }

    /**
     * Extrait un objet User depuis un ResultSet
     */
    private static User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setNom(rs.getString("nom"));
        user.setPrenom(rs.getString("prenom"));
        user.setTelephone(rs.getString("telephone"));
        user.setRole(User.Role.valueOf(rs.getString("role")));
        user.setActif(rs.getBoolean("actif"));
        return user;
    }

    // ==================== GESTION DES SPORTS ====================

    /**
     * Récupère tous les sports disponibles
     */
    public static List<Sport> getAllSports() {
        List<Sport> sports = new ArrayList<>();
        String sql = "SELECT * FROM sports ORDER BY nom";

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Sport sport = new Sport();
                sport.setId(rs.getInt("id"));
                sport.setNom(rs.getString("nom"));
                sport.setDescription(rs.getString("description"));
                sport.setImageUrl(rs.getString("image_url"));
                sports.add(sport);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des sports: " + e.getMessage());
        }

        return sports;
    }

    // ==================== GESTION DES VILLES ====================

    /**
     * Récupère toutes les villes
     */
    public static List<Ville> getAllVilles() {
        List<Ville> villes = new ArrayList<>();
        String sql = "SELECT * FROM villes ORDER BY nom";

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Ville ville = new Ville();
                ville.setId(rs.getInt("id"));
                ville.setNom(rs.getString("nom"));
                ville.setAdresse(rs.getString("adresse"));
                ville.setTelephone(rs.getString("telephone"));
                ville.setEmail(rs.getString("email"));
                villes.add(ville);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des villes: " + e.getMessage());
        }

        return villes;
    }

    // ==================== GESTION DES CENTRES ====================

    /**
     * Récupère les centres d'une ville spécifique
     */
    public static List<Centre> getCentresByVille(int villeId) {
        List<Centre> centres = new ArrayList<>();
        String sql = "SELECT * FROM centres WHERE ville_id = ? AND actif = TRUE ORDER BY nom";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, villeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Centre centre = new Centre();
                centre.setId(rs.getInt("id"));
                centre.setNom(rs.getString("nom"));
                centre.setVilleId(rs.getInt("ville_id"));
                centre.setAdresse(rs.getString("adresse"));
                centre.setTelephone(rs.getString("telephone"));
                centre.setHoraireOuverture(rs.getTime("horaire_ouverture").toLocalTime());
                centre.setHoraireFermeture(rs.getTime("horaire_fermeture").toLocalTime());
                centre.setActif(rs.getBoolean("actif"));
                centres.add(centre);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des centres: " + e.getMessage());
        }

        return centres;
    }

    // ==================== GESTION DES TERRAINS ====================

    /**
     * Récupère les terrains selon le sport et le centre
     */
    public static List<Terrain> getTerrainsBySportAndCentre(int sportId, int centreId) {
        List<Terrain> terrains = new ArrayList<>();
        String sql = "SELECT t.*, s.nom as sport_nom, c.nom as centre_nom " +
                "FROM terrains t " +
                "JOIN sports s ON t.sport_id = s.id " +
                "JOIN centres c ON t.centre_id = c.id " +
                "WHERE t.sport_id = ? AND t.centre_id = ? AND t.actif = TRUE " +
                "ORDER BY t.nom";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, sportId);
            stmt.setInt(2, centreId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Terrain terrain = new Terrain();
                terrain.setId(rs.getInt("id"));
                terrain.setNom(rs.getString("nom"));
                terrain.setCentreId(rs.getInt("centre_id"));
                terrain.setSportId(rs.getInt("sport_id"));
                terrain.setTypeSurface(rs.getString("type_surface"));
                terrain.setCapacite(rs.getInt("capacite"));
                terrain.setPrixHeure(rs.getDouble("prix_heure"));
                terrain.setActif(rs.getBoolean("actif"));
                terrain.setSportNom(rs.getString("sport_nom"));
                terrain.setCentreNom(rs.getString("centre_nom"));
                terrains.add(terrain);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des terrains: " + e.getMessage());
        }

        return terrains;
    }

    /**
     * Récupère un terrain par son ID
     */
    public static Terrain getTerrainById(int terrainId) {
        String sql = "SELECT t.*, s.nom as sport_nom, c.nom as centre_nom " +
                "FROM terrains t " +
                "JOIN sports s ON t.sport_id = s.id " +
                "JOIN centres c ON t.centre_id = c.id " +
                "WHERE t.id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, terrainId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Terrain terrain = new Terrain();
                terrain.setId(rs.getInt("id"));
                terrain.setNom(rs.getString("nom"));
                terrain.setCentreId(rs.getInt("centre_id"));
                terrain.setSportId(rs.getInt("sport_id"));
                terrain.setTypeSurface(rs.getString("type_surface"));
                terrain.setCapacite(rs.getInt("capacite"));
                terrain.setPrixHeure(rs.getDouble("prix_heure"));
                terrain.setActif(rs.getBoolean("actif"));
                terrain.setSportNom(rs.getString("sport_nom"));
                terrain.setCentreNom(rs.getString("centre_nom"));
                return terrain;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du terrain: " + e.getMessage());
        }

        return null;
    }

    // ==================== GESTION DES RÉSERVATIONS ====================

    /**
     * Vérifie la disponibilité d'un terrain (utilisé par ReservationManager)
     */
    public static boolean checkDisponibilite(int terrainId, LocalDate date,
                                             LocalTime heureDebut, LocalTime heureFin) {
        String sql = "CALL verifier_disponibilite(?, ?, ?, ?)";

        try (CallableStatement stmt = getConnection().prepareCall(sql)) {
            stmt.setInt(1, terrainId);
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(heureDebut));
            stmt.setTime(4, Time.valueOf(heureFin));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("conflit") == 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de disponibilité: " + e.getMessage());
        }

        return false;
    }

    /**
     * Crée une nouvelle réservation
     */
    public static boolean createReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations (user_id, terrain_id, date_reservation, " +
                "heure_debut, heure_fin, prix_total, remise_appliquee, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);  // Début de transaction

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setInt(1, reservation.getUserId());
                stmt.setInt(2, reservation.getTerrainId());
                stmt.setDate(3, Date.valueOf(reservation.getDateReservation()));
                stmt.setTime(4, Time.valueOf(reservation.getHeureDebut()));
                stmt.setTime(5, Time.valueOf(reservation.getHeureFin()));
                stmt.setDouble(6, reservation.getPrixTotal());
                stmt.setDouble(7, reservation.getRemiseAppliquee());
                stmt.setString(8, reservation.getStatut().name());

                int affectedRows = stmt.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        reservation.setId(generatedKeys.getInt(1));
                    }

                    // Créer le paiement associé
                    createPaiement(conn, reservation);

                    conn.commit();  // Valider la transaction
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la réservation: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();  // Annuler en cas d'erreur
                }
            } catch (SQLException ex) {
                System.err.println("Erreur lors du rollback: " + ex.getMessage());
            }
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la réinitialisation d'autoCommit: " + e.getMessage());
            }
        }

        return false;
    }

    /**
     * Crée un paiement pour une réservation
     */
    private static void createPaiement(Connection conn, Reservation reservation) throws SQLException {
        String sql = "INSERT INTO paiements (reservation_id, user_id, montant, mode_paiement, statut) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservation.getId());
            stmt.setInt(2, reservation.getUserId());
            stmt.setDouble(3, reservation.getPrixTotal());
            stmt.setString(4, "CARTE");  // Mode de paiement par défaut
            stmt.setString(5, "VALIDE");

            stmt.executeUpdate();
        }
    }

    /**
     * Récupère les réservations d'un utilisateur
     */
    public static List<Reservation> getReservationsByUser(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, t.nom as terrain_nom, s.nom as sport_nom, c.nom as centre_nom " +
                "FROM reservations r " +
                "JOIN terrains t ON r.terrain_id = t.id " +
                "JOIN sports s ON t.sport_id = s.id " +
                "JOIN centres c ON t.centre_id = c.id " +
                "WHERE r.user_id = ? " +
                "ORDER BY r.date_reservation DESC, r.heure_debut DESC";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Reservation reservation = extractReservationFromResultSet(rs);
                reservations.add(reservation);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réservations: " + e.getMessage());
        }

        return reservations;
    }

    /**
     * Annule une réservation
     */
    public static boolean annulerReservation(int reservationId) {
        String sql = "UPDATE reservations SET statut = 'ANNULEE' WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, reservationId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'annulation: " + e.getMessage());
        }

        return false;
    }

    /**
     * Extrait une réservation depuis un ResultSet
     */
    private static Reservation extractReservationFromResultSet(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getInt("id"));
        reservation.setUserId(rs.getInt("user_id"));
        reservation.setTerrainId(rs.getInt("terrain_id"));
        reservation.setDateReservation(rs.getDate("date_reservation").toLocalDate());
        reservation.setHeureDebut(rs.getTime("heure_debut").toLocalTime());
        reservation.setHeureFin(rs.getTime("heure_fin").toLocalTime());
        reservation.setPrixTotal(rs.getDouble("prix_total"));
        reservation.setRemiseAppliquee(rs.getDouble("remise_appliquee"));
        reservation.setStatut(Reservation.Statut.valueOf(rs.getString("statut")));
        reservation.setTerrainNom(rs.getString("terrain_nom"));
        reservation.setSportNom(rs.getString("sport_nom"));
        reservation.setCentreNom(rs.getString("centre_nom"));
        return reservation;
    }

    // ==================== GESTION DES ABONNEMENTS ====================

    /**
     * Récupère tous les abonnements disponibles
     */
    public static List<Abonnement> getAllAbonnements() {
        List<Abonnement> abonnements = new ArrayList<>();
        String sql = "SELECT * FROM abonnements WHERE actif = TRUE ORDER BY prix";

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Abonnement abonnement = new Abonnement();
                abonnement.setId(rs.getInt("id"));
                abonnement.setNom(rs.getString("nom"));
                abonnement.setDescription(rs.getString("description"));
                abonnement.setDureeMois(rs.getInt("duree_mois"));
                abonnement.setPrix(rs.getDouble("prix"));
                abonnement.setRemisePourcentage(rs.getDouble("remise_pourcentage"));
                abonnement.setNombreReservations(rs.getInt("nombre_reservations"));
                abonnement.setActif(rs.getBoolean("actif"));
                abonnements.add(abonnement);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des abonnements: " + e.getMessage());
        }

        return abonnements;
    }

    /**
     * Souscrit un utilisateur à un abonnement
     */
    public static boolean souscrireAbonnement(int userId, int abonnementId) {
        String sql = "INSERT INTO user_abonnements (user_id, abonnement_id, date_debut, date_fin, " +
                "reservations_restantes, actif) " +
                "SELECT ?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL a.duree_mois MONTH), " +
                "a.nombre_reservations, TRUE " +
                "FROM abonnements a WHERE a.id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, abonnementId);
            stmt.setInt(3, abonnementId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la souscription: " + e.getMessage());
        }

        return false;
    }

    // ==================== GESTION ADMIN ====================

    /**
     * Récupère tous les centres (admin)
     */
    public static List<Centre> getAllCentres() {
        List<Centre> centres = new ArrayList<>();
        String sql = "SELECT * FROM centres ORDER BY nom";

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Centre centre = new Centre();
                centre.setId(rs.getInt("id"));
                centre.setNom(rs.getString("nom"));
                centre.setVilleId(rs.getInt("ville_id"));
                centre.setAdresse(rs.getString("adresse"));
                centre.setTelephone(rs.getString("telephone"));
                centre.setHoraireOuverture(rs.getTime("horaire_ouverture").toLocalTime());
                centre.setHoraireFermeture(rs.getTime("horaire_fermeture").toLocalTime());
                centre.setActif(rs.getBoolean("actif"));
                centres.add(centre);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des centres: " + e.getMessage());
        }

        return centres;
    }

    /**
     * Récupère tous les terrains (admin)
     */
    public static List<Terrain> getAllTerrains() {
        List<Terrain> terrains = new ArrayList<>();
        String sql = "SELECT t.*, s.nom as sport_nom, c.nom as centre_nom " +
                "FROM terrains t " +
                "JOIN sports s ON t.sport_id = s.id " +
                "JOIN centres c ON t.centre_id = c.id " +
                "ORDER BY t.nom";

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Terrain terrain = new Terrain();
                terrain.setId(rs.getInt("id"));
                terrain.setNom(rs.getString("nom"));
                terrain.setCentreId(rs.getInt("centre_id"));
                terrain.setSportId(rs.getInt("sport_id"));
                terrain.setTypeSurface(rs.getString("type_surface"));
                terrain.setCapacite(rs.getInt("capacite"));
                terrain.setPrixHeure(rs.getDouble("prix_heure"));
                terrain.setActif(rs.getBoolean("actif"));
                terrain.setSportNom(rs.getString("sport_nom"));
                terrain.setCentreNom(rs.getString("centre_nom"));
                terrains.add(terrain);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des terrains: " + e.getMessage());
        }

        return terrains;
    }

    /**
     * Récupère toutes les réservations (admin)
     */
    public static List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, t.nom as terrain_nom, s.nom as sport_nom, c.nom as centre_nom " +
                "FROM reservations r " +
                "JOIN terrains t ON r.terrain_id = t.id " +
                "JOIN sports s ON t.sport_id = s.id " +
                "JOIN centres c ON t.centre_id = c.id " +
                "ORDER BY r.date_reservation DESC, r.heure_debut DESC";

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Reservation reservation = extractReservationFromResultSet(rs);
                reservations.add(reservation);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réservations: " + e.getMessage());
        }

        return reservations;
    }

    /**
     * Supprime un centre (admin)
     */
    public static boolean deleteCentre(int centreId) {
        String sql = "UPDATE centres SET actif = FALSE WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, centreId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du centre: " + e.getMessage());
        }

        return false;
    }

    /**
     * Bloque un terrain (admin)
     */
    public static boolean bloquerTerrain(int terrainId, String raison) {
        String sql = "UPDATE terrains SET actif = FALSE WHERE id = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, terrainId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Log de la raison (pourrait être stocké dans une table séparée)
                System.out.println("Terrain #" + terrainId + " bloqué. Raison: " + raison);
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du blocage du terrain: " + e.getMessage());
        }

        return false;
    }

    // ==================== STATISTIQUES (ADMIN) ====================

    /**
     * Récupère les statistiques globales
     */
    public static java.util.Map<String, Object> getStatistiques() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        try {
            // Nombre total d'utilisateurs
            String sqlUsers = "SELECT COUNT(*) as total FROM users WHERE actif = TRUE";
            try (Statement stmt = getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(sqlUsers)) {
                if (rs.next()) {
                    stats.put("totalUsers", rs.getInt("total"));
                }
            }

            // Nombre total de réservations
            String sqlReservations = "SELECT COUNT(*) as total FROM reservations";
            try (Statement stmt = getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(sqlReservations)) {
                if (rs.next()) {
                    stats.put("totalReservations", rs.getInt("total"));
                }
            }

            // Réservations confirmées
            String sqlConfirmed = "SELECT COUNT(*) as total FROM reservations WHERE statut = 'CONFIRMEE'";
            try (Statement stmt = getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(sqlConfirmed)) {
                if (rs.next()) {
                    stats.put("reservationsConfirmees", rs.getInt("total"));
                }
            }

            // Revenus totaux
            String sqlRevenus = "SELECT SUM(prix_total) as total FROM reservations WHERE statut = 'CONFIRMEE'";
            try (Statement stmt = getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(sqlRevenus)) {
                if (rs.next()) {
                    double revenus = rs.getDouble("total");
                    stats.put("revenusTotaux", rs.wasNull() ? 0.0 : revenus);
                }
            }

            // Nombre de terrains actifs
            String sqlTerrains = "SELECT COUNT(*) as total FROM terrains WHERE actif = TRUE";
            try (Statement stmt = getConnection().createStatement();
                 ResultSet rs = stmt.executeQuery(sqlTerrains)) {
                if (rs.next()) {
                    stats.put("terrainsActifs", rs.getInt("total"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des statistiques: " + e.getMessage());
        }

        return stats;
    }

    /**
     * Ferme la connexion à la base de données
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connexion à la base de données fermée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }
}