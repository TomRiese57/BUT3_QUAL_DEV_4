-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : ven. 21 nov. 2025 à 13:27
-- Version du serveur : 9.1.0
-- Version de PHP : 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `bank_iut_test`
--

-- --------------------------------------------------------

--
-- Structure de la table `compte`
--

DROP TABLE IF EXISTS `Compte`;
CREATE TABLE IF NOT EXISTS `Compte` (
  `numeroCompte` varchar(50) NOT NULL,
  `userId` varchar(50) NOT NULL,
  `solde` double NOT NULL,
  `avecDecouvert` varchar(5) NOT NULL,
  `decouvertAutorise` decimal(10,0) DEFAULT NULL,
  PRIMARY KEY (`numeroCompte`),
  INDEX `index_userClient` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Déchargement des données de la table `compte`
--

INSERT INTO `Compte` (`numeroCompte`, `userId`, `solde`, `avecDecouvert`, `decouvertAutorise`) VALUES
('AB7328887341', 'j.doe2', 4242, 'AVEC', 123),
('AV1011011011', 'g.descomptes', 5, 'AVEC', 100),
('BD4242424242', 'j.doe1', 100, 'SANS', NULL),
('CADNV00000', 'j.doe1', 42, 'AVEC', 42),
('CADV000000', 'j.doe1', 0, 'AVEC', 42),
('CSDNV00000', 'j.doe1', 42, 'SANS', NULL),
('CSDV000000', 'j.doe1', 0, 'SANS', NULL),
('IO1010010001', 'j.doe2', 6868, 'SANS', NULL),
('KL4589219196', 'g.descomptesvides', 0, 'AVEC', 150),
('KO7845154956', 'g.descomptesvides', 0, 'SANS', NULL),
('LA1021931215', 'j.doe1', 100, 'SANS', NULL),
('MD8694030938', 'j.doe1', 500, 'SANS', NULL),
('PP1285735733', 'a.lidell1', 37, 'SANS', NULL),
('SA1011011011', 'g.descomptes', 10, 'SANS', 0),
('TD0398455576', 'j.doe1', 23, 'AVEC', 500),
('XD1829451029', 'j.doe1', -48, 'AVEC', 100);

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

DROP TABLE IF EXISTS `Utilisateur`;
CREATE TABLE IF NOT EXISTS `Utilisateur` (
  `userId` varchar(50) NOT NULL,
  `nom` varchar(45) NOT NULL,
  `prenom` varchar(45) NOT NULL,
  `adresse` varchar(100) NOT NULL,
  `userPwd` varchar(64) DEFAULT NULL,
    `male` tinyint(1) NOT NULL,
  `type` varchar(10) NOT NULL,
  `numClient` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `numClient_UNIQUE` (`numClient`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `Utilisateur` (`userId`, `nom`, `prenom`, `adresse`, `userPwd`, `male`, `type`, `numClient`) VALUES
('a.lidell1', 'Lidell', 'Alice', '789, grande rue, Metz', '31f7a65e315586ac198bd798b6629ce4903d0899476d5741a9f32e2e521b6a66', 1, 'CLIENT', '9865432100'),
('admin', 'Smith', 'Joe', '123, grande rue, Metz', '713bfda78870bf9d1b261f565286f85e97ee614efe5f0faf7c34e7ca4f65baca', 1, 'MANAGER', ''),
('c.exist', 'TEST NOM', 'TEST PRENOM', 'TEST ADRESSE', '309fe82e71b868f0f3292e4d5bc92f936166afddf1d9f8676e32030e48b8b814', 1, 'CLIENT', '0101010101'),
('g.descomptes', 'TEST NOM', 'TEST PRENOM', 'TEST ADRESSE', '309fe82e71b868f0f3292e4d5bc92f936166afddf1d9f8676e32030e48b8b814', 1, 'CLIENT', '1000000001'),
('g.descomptesvides', 'TEST NOM', 'TEST PRENOM', 'TEST ADRESSE', '309fe82e71b868f0f3292e4d5bc92f936166afddf1d9f8676e32030e48b8b814', 1, 'CLIENT', '0000000002'),
('g.exist', 'TEST NOM', 'TEST PRENOM', 'TEST ADRESSE', '309fe82e71b868f0f3292e4d5bc92f936166afddf1d9f8676e32030e48b8b814', 1, 'CLIENT', '1010101010'),
('g.pasdecompte', 'TEST NOM', 'TEST PRENOM', 'TEST ADRESSE', '309fe82e71b868f0f3292e4d5bc92f936166afddf1d9f8676e32030e48b8b814', 1, 'CLIENT', '5544554455'),
('j.doe1', 'Doe', 'Jane', '456, grand boulevard, Brest', '31f7a65e315586ac198bd798b6629ce4903d0899476d5741a9f32e2e521b6a66', 1, 'CLIENT', '1234567890'),
('j.doe2', 'Doe', 'John', '457, grand boulevard, Perpignan', '31f7a65e315586ac198bd798b6629ce4903d0899476d5741a9f32e2e521b6a66', 1, 'CLIENT', '0000000001');

--
-- Structure de la table `carte_bancaire`
--

DROP TABLE IF EXISTS `carte_bancaire`;

CREATE TABLE IF NOT EXISTS `carte_bancaire` (
    `numero_carte`               VARCHAR(16)     NOT NULL,
    `numero_compte`              VARCHAR(50)     NOT NULL,
    `paiement_differe`           TINYINT(1)      NOT NULL DEFAULT 0   COMMENT '1=différé, 0=immédiat',
    `plafond_paiement`           DOUBLE          NOT NULL DEFAULT 3000,
    `plafond_retrait`            DOUBLE          NOT NULL DEFAULT 1000,
    `paiements_mois_courant`     DOUBLE          NOT NULL DEFAULT 0,
    `retraits_mois_courant`      DOUBLE          NOT NULL DEFAULT 0,
    `montant_differe_en_attente` DOUBLE          NOT NULL DEFAULT 0,
    `bloquee`                    TINYINT(1)      NOT NULL DEFAULT 0,
    `date_expiration`            DATE            NOT NULL,
    PRIMARY KEY (`numero_carte`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Données de test
INSERT INTO `carte_bancaire`
(`numero_carte`, `numero_compte`, `paiement_differe`, `plafond_paiement`,
 `plafond_retrait`, `paiements_mois_courant`, `retraits_mois_courant`,
 `montant_differe_en_attente`, `bloquee`, `date_expiration`)
VALUES
    ('1234567890123456', 'BD4242424242', 0, 3000, 1000, 150,  50,   0,   0, DATE_ADD(CURDATE(), INTERVAL 3 YEAR)),
    ('9876543210987654', 'FF5050500202', 1, 2000,  800, 300,   0, 300,   0, DATE_ADD(CURDATE(), INTERVAL 3 YEAR)),
    ('1111222233334444', 'LA1021931215', 0, 1500,  500,   0,   0,   0,   1, DATE_ADD(CURDATE(), INTERVAL 2 YEAR));



--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `compte`
--
ALTER TABLE `Compte`
  ADD CONSTRAINT `fk_Compte_userId` FOREIGN KEY (`userId`) REFERENCES `Utilisateur` (`userId`);
COMMIT;

ALTER TABLE `carte_bancaire`
    ADD CONSTRAINT `fk_carte_compte` FOREIGN KEY (`numero_compte`) REFERENCES `compte` (`numeroCompte`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
