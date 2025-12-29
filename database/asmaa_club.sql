-- Script SQL Complet pour ASMAA-Club
-- Base de données MySQL

-- Créer la base de données
DROP DATABASE IF EXISTS asmaa_club;
CREATE DATABASE asmaa_club CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE asmaa_club;

-- Table des utilisateurs
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    telephone VARCHAR(20),
    role ENUM('INVITE', 'CLIENT', 'ADMIN') DEFAULT 'CLIENT',
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    actif BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB;

-- Table des villes
CREATE TABLE villes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) UNIQUE NOT NULL,
    adresse TEXT,
    telephone VARCHAR(20),
    email VARCHAR(100)
) ENGINE=InnoDB;

-- Table des centres sportifs
CREATE TABLE centres (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    ville_id INT NOT NULL,
    adresse TEXT NOT NULL,
    telephone VARCHAR(20),
    horaire_ouverture TIME DEFAULT '08:00:00',
    horaire_fermeture TIME DEFAULT '23:00:00',
    actif BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (ville_id) REFERENCES villes(id) ON DELETE CASCADE,
    INDEX idx_ville (ville_id)
) ENGINE=InnoDB;

-- Table des types de sport
CREATE TABLE sports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    image_url VARCHAR(255)
) ENGINE=InnoDB;

-- Table des terrains
CREATE TABLE terrains (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    centre_id INT NOT NULL,
    sport_id INT NOT NULL,
    type_surface VARCHAR(50),
    capacite INT,
    prix_heure DECIMAL(10,2) NOT NULL,
    actif BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (centre_id) REFERENCES centres(id) ON DELETE CASCADE,
    FOREIGN KEY (sport_id) REFERENCES sports(id) ON DELETE CASCADE,
    INDEX idx_centre (centre_id),
    INDEX idx_sport (sport_id)
) ENGINE=InnoDB;

-- Table des abonnements
CREATE TABLE abonnements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    duree_mois INT NOT NULL,
    prix DECIMAL(10,2) NOT NULL,
    remise_pourcentage DECIMAL(5,2) DEFAULT 0,
    nombre_reservations INT,
    actif BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- Table des abonnements utilisateurs
CREATE TABLE user_abonnements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    abonnement_id INT NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    reservations_restantes INT,
    actif BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (abonnement_id) REFERENCES abonnements(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_dates (date_debut, date_fin)
) ENGINE=InnoDB;

-- Table des réservations
CREATE TABLE reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    terrain_id INT NOT NULL,
    date_reservation DATE NOT NULL,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    prix_total DECIMAL(10,2) NOT NULL,
    remise_appliquee DECIMAL(10,2) DEFAULT 0,
    statut ENUM('EN_ATTENTE', 'CONFIRMEE', 'ANNULEE', 'TERMINEE') DEFAULT 'CONFIRMEE',
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (terrain_id) REFERENCES terrains(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_terrain (terrain_id),
    INDEX idx_date (date_reservation, heure_debut, heure_fin),
    UNIQUE KEY unique_reservation (terrain_id, date_reservation, heure_debut, heure_fin)
) ENGINE=InnoDB;

-- Table des paiements
CREATE TABLE paiements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    reservation_id INT NOT NULL,
    user_id INT NOT NULL,
    montant DECIMAL(10,2) NOT NULL,
    mode_paiement ENUM('CARTE', 'ESPECE', 'CHEQUE', 'VIREMENT') DEFAULT 'CARTE',
    statut ENUM('EN_ATTENTE', 'VALIDE', 'REFUSE', 'REMBOURSE') DEFAULT 'VALIDE',
    date_paiement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_reservation (reservation_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB;

-- Table des logs serveur
CREATE TABLE logs_serveur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    action VARCHAR(255) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    date_log TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_date (date_log)
) ENGINE=InnoDB;

-- Insertion des données initiales

-- Admin par défaut (mot de passe: admin123)
INSERT INTO users (username, email, password, nom, prenom, telephone, role) VALUES
('admin', 'admin@asmaa-club.ma', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin', 'ASMAA', '0600000000', 'ADMIN'),
('client1', 'client1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Alami', 'Mohammed', '0612345678', 'CLIENT'),
('client2', 'client2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Bennani', 'Fatima', '0623456789', 'CLIENT');

-- Villes
INSERT INTO villes (nom, adresse, telephone, email) VALUES
('Casablanca', 'Boulevard Zerktouni, Casablanca', '0522123456', 'casa@asmaa-club.ma'),
('Rabat', 'Avenue Hassan II, Rabat', '0537234567', 'rabat@asmaa-club.ma'),
('Marrakech', 'Avenue Mohammed V, Marrakech', '0524345678', 'marrakech@asmaa-club.ma'),
('Tanger', 'Boulevard Pasteur, Tanger', '0539456789', 'tanger@asmaa-club.ma');

-- Centres sportifs
INSERT INTO centres (nom, ville_id, adresse, telephone) VALUES
('ASMAA Club Casa Centre', 1, 'Boulevard Zerktouni, Casablanca', '0522111111'),
('ASMAA Club Casa Anfa', 1, 'Quartier Anfa, Casablanca', '0522222222'),
('ASMAA Club Rabat Agdal', 2, 'Quartier Agdal, Rabat', '0537333333'),
('ASMAA Club Marrakech Gueliz', 3, 'Quartier Gueliz, Marrakech', '0524444444');

-- Sports
INSERT INTO sports (nom, description, image_url) VALUES
('Football', 'Terrain de football 11 joueurs ou 5 joueurs', 'football.jpg'),
('Basketball', 'Terrain de basketball couvert ou extérieur', 'basketball.jpg'),
('Tennis', 'Court de tennis en terre battue ou dur', 'tennis.jpg'),
('Paddle', 'Court de paddle tennis', 'paddle.jpg');

-- Terrains
INSERT INTO terrains (nom, centre_id, sport_id, type_surface, capacite, prix_heure) VALUES
-- Casablanca Centre
('Terrain Football 1', 1, 1, 'Gazon Synthétique', 10, 200.00),
('Terrain Football 2', 1, 1, 'Gazon Synthétique', 10, 200.00),
('Court Basketball 1', 1, 2, 'Parquet', 10, 150.00),
('Court Tennis 1', 1, 3, 'Terre Battue', 4, 180.00),
('Court Paddle 1', 1, 4, 'Gazon Synthétique', 4, 160.00),
-- Casablanca Anfa
('Terrain Football Premium', 2, 1, 'Gazon Naturel', 22, 350.00),
('Court Basketball Indoor', 2, 2, 'Parquet', 10, 180.00),
('Court Tennis Premium', 2, 3, 'Dur', 4, 200.00),
('Court Paddle 1', 2, 4, 'Gazon Synthétique', 4, 160.00),
('Court Paddle 2', 2, 4, 'Gazon Synthétique', 4, 160.00),
-- Rabat
('Terrain Football 1', 3, 1, 'Gazon Synthétique', 10, 200.00),
('Court Basketball 1', 3, 2, 'Béton', 10, 140.00),
('Court Tennis 1', 3, 3, 'Terre Battue', 4, 170.00),
-- Marrakech
('Terrain Football 1', 4, 1, 'Gazon Synthétique', 10, 180.00),
('Court Tennis 1', 4, 3, 'Dur', 4, 160.00),
('Court Paddle 1', 4, 4, 'Gazon Synthétique', 4, 150.00);

-- Abonnements
INSERT INTO abonnements (nom, description, duree_mois, prix, remise_pourcentage, nombre_reservations) VALUES
('Abonnement Bronze', 'Accès basique avec 10% de remise', 1, 500.00, 10.00, 10),
('Abonnement Silver', 'Accès privilégié avec 15% de remise', 3, 1350.00, 15.00, 35),
('Abonnement Gold', 'Accès premium avec 20% de remise', 6, 2400.00, 20.00, 80),
('Abonnement Platinum', 'Accès illimité avec 25% de remise', 12, 4000.00, 25.00, 200);

-- Quelques réservations de test
INSERT INTO reservations (user_id, terrain_id, date_reservation, heure_debut, heure_fin, prix_total, statut) VALUES
(2, 1, '2025-12-20', '10:00:00', '11:00:00', 200.00, 'CONFIRMEE'),
(2, 3, '2025-12-21', '14:00:00', '15:00:00', 150.00, 'CONFIRMEE'),
(3, 4, '2025-12-22', '16:00:00', '17:00:00', 180.00, 'CONFIRMEE');

-- Paiements correspondants
INSERT INTO paiements (reservation_id, user_id, montant, mode_paiement, statut) VALUES
(1, 2, 200.00, 'CARTE', 'VALIDE'),
(2, 2, 150.00, 'CARTE', 'VALIDE'),
(3, 3, 180.00, 'ESPECE', 'VALIDE');

-- Vue pour les statistiques
CREATE VIEW vue_statistiques AS
SELECT 
    DATE(r.date_reservation) as date,
    COUNT(*) as nombre_reservations,
    SUM(r.prix_total) as revenu_total,
    AVG(r.prix_total) as revenu_moyen
FROM reservations r
WHERE r.statut = 'CONFIRMEE' OR r.statut = 'TERMINEE'
GROUP BY DATE(r.date_reservation);

-- Procédure pour vérifier disponibilité
DELIMITER //
CREATE PROCEDURE verifier_disponibilite(
    IN p_terrain_id INT,
    IN p_date_reservation DATE,
    IN p_heure_debut TIME,
    IN p_heure_fin TIME
)
BEGIN
    SELECT COUNT(*) as conflit
    FROM reservations
    WHERE terrain_id = p_terrain_id
        AND date_reservation = p_date_reservation
        AND statut IN ('EN_ATTENTE', 'CONFIRMEE')
        AND (
            (heure_debut <= p_heure_debut AND heure_fin > p_heure_debut)
            OR (heure_debut < p_heure_fin AND heure_fin >= p_heure_fin)
            OR (heure_debut >= p_heure_debut AND heure_fin <= p_heure_fin)
        );
END //
DELIMITER ;

-- Fonction pour calculer le prix avec remise
DELIMITER //
CREATE FUNCTION calculer_prix_avec_remise(
    p_prix_base DECIMAL(10,2),
    p_user_id INT
) RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    DECLARE v_remise DECIMAL(5,2);
    DECLARE v_prix_final DECIMAL(10,2);
    
    -- Récupérer la remise de l'abonnement actif
    SELECT COALESCE(a.remise_pourcentage, 0) INTO v_remise
    FROM user_abonnements ua
    JOIN abonnements a ON ua.abonnement_id = a.id
    WHERE ua.user_id = p_user_id
        AND ua.actif = TRUE
        AND CURDATE() BETWEEN ua.date_debut AND ua.date_fin
    LIMIT 1;
    
    SET v_prix_final = p_prix_base * (1 - v_remise / 100);
    
    RETURN v_prix_final;
END //
DELIMITER ;

-- Trigger pour enregistrer les logs
DELIMITER //
CREATE TRIGGER after_reservation_insert
AFTER INSERT ON reservations
FOR EACH ROW
BEGIN
    INSERT INTO logs_serveur (user_id, action, details)
    VALUES (NEW.user_id, 'NOUVELLE_RESERVATION', 
            CONCAT('Réservation terrain #', NEW.terrain_id, ' pour le ', NEW.date_reservation));
END //
DELIMITER ;

COMMIT;