-- ==================== BASE DE DONNÉES ASMAA-CLUB ====================
-- Script complet de création de la base de données
-- À exécuter dans MySQL Workbench

DROP DATABASE IF EXISTS asmaa_club;
CREATE DATABASE as_club CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE as_club;

-- ==================== TABLE ROLES ====================
CREATE TABLE roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO roles (id, name) VALUES
(1, 'VISITOR'),
(2, 'CLIENT'),
(3, 'ADMIN');

-- ==================== TABLE SUBSCRIPTIONS ====================
CREATE TABLE subscriptions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    duration_months INT NOT NULL,
    discount DECIMAL(5,2) NOT NULL COMMENT 'Pourcentage de réduction',
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO subscriptions (name, duration_months, discount, price) VALUES
('Abonnement 1 Mois', 1, 5.00, 200.00),
('Abonnement 6 Mois', 6, 10.00, 1000.00),
('Abonnement 1 An', 12, 15.00, 1800.00);

-- ==================== TABLE USERS ====================
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role_id INT DEFAULT 2,
    subscription_id INT NULL,
    profile_image VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id),
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB;

-- Utilisateur admin par défaut (password: admin123)
INSERT INTO users (username, email, password, phone, role_id) VALUES
('admin', 'admin@asmaa-club.ma', 'admin123', '+212600000000', 3);

-- Utilisateurs de test
INSERT INTO users (username, email, password, phone, role_id, subscription_id) VALUES
('client1', 'client1@example.com', 'password123', '+212611111111', 2, 1),
('client2', 'client2@example.com', 'password123', '+212622222222', 2, 2),
('client3', 'client3@example.com', 'password123', '+212633333333', 2, 3);

-- ==================== TABLE CITIES ====================
CREATE TABLE cities (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO cities (name, description, image_url) VALUES
('Casablanca', 'La capitale économique du Maroc', 'casablanca.jpg'),
('Rabat', 'La capitale administrative du Maroc', 'rabat.jpg'),
('Marrakech', 'La ville rouge, capitale touristique', 'marrakech.jpg'),
('Fès', 'La capitale spirituelle du Maroc', 'fes.jpg'),
('Tanger', 'La perle du détroit', 'tanger.jpg'),
('Agadir', 'La ville balnéaire du sud', 'agadir.jpg');

-- ==================== TABLE SPORTS ====================
CREATE TABLE sports (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO sports (name, description, image_url) VALUES
('Football', 'Le sport roi', 'football.jpg'),
('Basketball', 'Sport d\'équipe dynamique', 'basketball.jpg'),
('Tennis', 'Sport de raquette élégant', 'tennis.jpg'),
('Padel', 'Tennis en double sur terrain fermé', 'padel.jpg');

-- ==================== TABLE CLUBS ====================
CREATE TABLE clubs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    city_id INT NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE CASCADE,
    INDEX idx_city (city_id)
) ENGINE=InnoDB;

-- Clubs à Casablanca
INSERT INTO clubs (name, address, city_id, phone) VALUES
('Club Sportif Casa', 'Maarif, Casablanca', 1, '+212522111111'),
('Arena Sports', 'Ain Diab, Casablanca', 1, '+212522222222'),
('Champions Club', 'Californie, Casablanca', 1, '+212522333333');

-- Clubs à Rabat
INSERT INTO clubs (name, address, city_id, phone) VALUES
('Rabat Sports Center', 'Agdal, Rabat', 2, '+212537111111'),
('Royal Club Rabat', 'Hassan, Rabat', 2, '+212537222222');

-- Clubs à Marrakech
INSERT INTO clubs (name, address, city_id, phone) VALUES
('Marrakech Sports Complex', 'Guéliz, Marrakech', 3, '+212524111111'),
('Atlas Sports', 'Hivernage, Marrakech', 3, '+212524222222');

-- Clubs à Fès
INSERT INTO clubs (name, address, city_id, phone) VALUES
('Fès Sports Club', 'Ville Nouvelle, Fès', 4, '+212535111111');

-- Clubs à Tanger
INSERT INTO clubs (name, address, city_id, phone) VALUES
('Tanger Beach Club', 'Malabata, Tanger', 5, '+212539111111');

-- Clubs à Agadir
INSERT INTO clubs (name, address, city_id, phone) VALUES
('Agadir Sports Arena', 'Marina, Agadir', 6, '+212528111111');

-- ==================== TABLE TERRAINS ====================
CREATE TABLE terrains (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    club_id INT NOT NULL,
    sport_id INT NOT NULL,
    image_url VARCHAR(255),
    is_blocked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE,
    FOREIGN KEY (sport_id) REFERENCES sports(id) ON DELETE CASCADE,
    INDEX idx_club_sport (club_id, sport_id)
) ENGINE=InnoDB;

-- Terrains Casablanca - Club Sportif Casa
INSERT INTO terrains (name, description, price, club_id, sport_id, image_url) VALUES
('Terrain Football Pro 1', 'Terrain en gazon synthétique de qualité', 300.00, 1, 1, 'football_terrain1.jpg'),
('Terrain Football Pro 2', 'Terrain avec éclairage nocturne', 320.00, 1, 1, 'football_terrain2.jpg'),
('Court Basketball Indoor', 'Salle couverte climatisée', 250.00, 1, 2, 'basketball_court1.jpg'),
('Court Tennis Central', 'Court en terre battue', 200.00, 1, 3, 'tennis_court1.jpg'),
('Court Padel 1', 'Court moderne avec éclairage LED', 180.00, 1, 4, 'padel_court1.jpg');

-- Terrains Casablanca - Arena Sports
INSERT INTO terrains (name, description, price, club_id, sport_id, image_url) VALUES
('Stade Football Arena', 'Grand terrain avec tribunes', 350.00, 2, 1, 'football_terrain3.jpg'),
('Parquet Basketball', 'Parquet professionnel NBA', 280.00, 2, 2, 'basketball_court2.jpg'),
('Court Tennis Premium', 'Surface dure type US Open', 220.00, 2, 3, 'tennis_court2.jpg'),
('Padel Premium', 'Court avec vue sur mer', 200.00, 2, 4, 'padel_court2.jpg');

-- Terrains Casablanca - Champions Club
INSERT INTO terrains (name, description, price, club_id, sport_id) VALUES
('Terrain Foot 7vs7', 'Terrain format réduit', 250.00, 3, 1),
('Mini Basketball', 'Terrain 3x3', 180.00, 3, 2),
('Tennis Quick Court', 'Surface rapide', 190.00, 3, 3);

-- Terrains Rabat
INSERT INTO terrains (name, description, price, club_id, sport_id) VALUES
('Terrain Football Agdal', 'Gazon naturel', 280.00, 4, 1),
('Court Basketball Rabat', 'Salle polyvalente', 230.00, 4, 2),
('Tennis Club Royal', 'Courts multiples', 210.00, 5, 3),
('Padel Royal', 'Équipement haut de gamme', 190.00, 5, 4);

-- Terrains Marrakech
INSERT INTO terrains (name, description, price, club_id, sport_id) VALUES
('Terrain Football Guéliz', 'Vue sur Atlas', 290.00, 6, 1),
('Basketball Complex', 'Centre moderne', 240.00, 6, 2),
('Tennis Atlas', 'Cadre exceptionnel', 200.00, 7, 3),
('Padel Hivernage', 'Ambiance luxueuse', 180.00, 7, 4);

-- Terrains Fès
INSERT INTO terrains (name, description, price, club_id, sport_id) VALUES
('Football Fès Center', 'Terrain central', 260.00, 8, 1),
('Basketball Fès', 'Salle équipée', 220.00, 8, 2);

-- Terrains Tanger
INSERT INTO terrains (name, description, price, club_id, sport_id) VALUES
('Beach Football', 'Sur le front de mer', 270.00, 9, 1),
('Tennis Malabata', 'Vue panoramique', 190.00, 9, 3);

-- Terrains Agadir
INSERT INTO terrains (name, description, price, club_id, sport_id) VALUES
('Marina Football', 'Près du port', 280.00, 10, 1),
('Padel Marina', 'Ambiance maritime', 170.00, 10, 4);

-- ==================== TABLE RESERVATIONS ====================
CREATE TABLE reservations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    terrain_id INT NOT NULL,
    date DATE NOT NULL,
    time_slot VARCHAR(20) NOT NULL COMMENT 'Format: 08:00-10:00',
    price DECIMAL(10,2) NOT NULL,
    status ENUM('CONFIRMED', 'CANCELLED', 'COMPLETED') DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (terrain_id) REFERENCES terrains(id) ON DELETE CASCADE,
    UNIQUE KEY unique_reservation (terrain_id, date, time_slot, status),
    INDEX idx_user (user_id),
    INDEX idx_terrain_date (terrain_id, date),
    INDEX idx_status (status)
) ENGINE=InnoDB;

-- Quelques réservations de test
INSERT INTO reservations (user_id, terrain_id, date, time_slot, price, status) VALUES
(2, 1, '2025-01-10', '10:00-12:00', 285.00, 'CONFIRMED'),
(2, 5, '2025-01-12', '14:00-16:00', 171.00, 'CONFIRMED'),
(3, 2, '2025-01-15', '18:00-20:00', 288.00, 'CONFIRMED');

-- ==================== TABLE PAYMENTS ====================
CREATE TABLE payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    method ENUM('CARD', 'PAYPAL', 'CASH') NOT NULL,
    status ENUM('PAID', 'PENDING', 'REFUNDED') DEFAULT 'PAID',
    transaction_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE,
    INDEX idx_reservation (reservation_id)
) ENGINE=InnoDB;

INSERT INTO payments (reservation_id, amount, method, status) VALUES
(1, 285.00, 'CARD', 'PAID'),
(2, 171.00, 'PAYPAL', 'PAID'),
(3, 288.00, 'CASH', 'PENDING');

-- ==================== TABLE ANNOUNCEMENTS ====================
CREATE TABLE announcements (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

INSERT INTO announcements (title, content) VALUES
('Bienvenue sur ASMAA-Club', 'Découvrez nos terrains de sport à travers tout le Maroc !'),
('Promotion Spéciale', '-20% sur tous les abonnements annuels ce mois-ci !'),
('Nouveaux terrains', 'Ouverture prochaine de nouveaux terrains à Casablanca');

-- ==================== TABLE LOGS ====================
CREATE TABLE logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    action VARCHAR(100) NOT NULL,
    details TEXT,
    ip_address VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_user_action (user_id, action),
    INDEX idx_created (created_at)
) ENGINE=InnoDB;

-- ==================== VUES UTILES ====================

-- Vue des réservations actives
CREATE VIEW active_reservations AS
SELECT 
    r.id,
    u.username,
    t.name as terrain_name,
    c.name as club_name,
    ci.name as city_name,
    r.date,
    r.time_slot,
    r.price,
    r.status
FROM reservations r
JOIN users u ON r.user_id = u.id
JOIN terrains t ON r.terrain_id = t.id
JOIN clubs c ON t.club_id = c.id
JOIN cities ci ON c.city_id = ci.id
WHERE r.status = 'CONFIRMED'
ORDER BY r.date DESC, r.time_slot;

-- Vue des statistiques par ville
CREATE VIEW city_statistics AS
SELECT 
    ci.name as city_name,
    COUNT(DISTINCT c.id) as total_clubs,
    COUNT(DISTINCT t.id) as total_terrains,
    COUNT(r.id) as total_reservations,
    COALESCE(SUM(r.price), 0) as total_revenue
FROM cities ci
LEFT JOIN clubs c ON ci.id = c.city_id
LEFT JOIN terrains t ON c.id = t.club_id
LEFT JOIN reservations r ON t.id = r.terrain_id AND r.status = 'CONFIRMED'
GROUP BY ci.id, ci.name;

-- ==================== PROCÉDURES STOCKÉES ====================

DELIMITER $$

-- Procédure pour vérifier la disponibilité
CREATE PROCEDURE check_slot_availability(
    IN p_terrain_id INT,
    IN p_date DATE,
    IN p_time_slot VARCHAR(20)
)
BEGIN
    SELECT COUNT(*) as is_available
    FROM reservations
    WHERE terrain_id = p_terrain_id
    AND date = p_date
    AND time_slot = p_time_slot
    AND status != 'CANCELLED';
END$$

-- Procédure pour obtenir les statistiques globales
CREATE PROCEDURE get_global_stats()
BEGIN
    SELECT 
        (SELECT COUNT(*) FROM users WHERE role_id = 2) as total_clients,
        (SELECT COUNT(*) FROM terrains WHERE is_blocked = FALSE) as total_terrains,
        (SELECT COUNT(*) FROM reservations WHERE status = 'CONFIRMED') as total_reservations,
        (SELECT COALESCE(SUM(price), 0) FROM reservations WHERE status = 'CONFIRMED') as total_revenue,
        (SELECT COUNT(*) FROM reservations WHERE DATE(created_at) = CURDATE()) as today_reservations;
END$$

DELIMITER ;

-- ==================== TRIGGERS ====================

DELIMITER $$

-- Trigger pour empêcher les réservations en double
CREATE TRIGGER before_reservation_insert
BEFORE INSERT ON reservations
FOR EACH ROW
BEGIN
    DECLARE existing_count INT;
    
    SELECT COUNT(*) INTO existing_count
    FROM reservations
    WHERE terrain_id = NEW.terrain_id
    AND date = NEW.date
    AND time_slot = NEW.time_slot
    AND status != 'CANCELLED';
    
    IF existing_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ce créneau est déjà réservé';
    END IF;
END$$

-- Trigger pour logger les réservations
CREATE TRIGGER after_reservation_insert
AFTER INSERT ON reservations
FOR EACH ROW
BEGIN
    INSERT INTO logs (user_id, action, details)
    VALUES (NEW.user_id, 'RESERVATION_CREATED', 
            CONCAT('Terrain ID: ', NEW.terrain_id, ', Date: ', NEW.date, ', Slot: ', NEW.time_slot));
END$$

DELIMITER ;

-- ==================== AFFICHAGE FINAL ====================
SELECT '✅ Base de données ASMAA-Club créée avec succès!' as status;
SELECT CONCAT('📊 Villes: ', COUNT(*)) as count FROM cities;
SELECT CONCAT('🏢 Clubs: ', COUNT(*)) as count FROM clubs;
SELECT CONCAT('🏟️ Terrains: ', COUNT(*)) as count FROM terrains;
SELECT CONCAT('👥 Utilisateurs: ', COUNT(*)) as count FROM users;