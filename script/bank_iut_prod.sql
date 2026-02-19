-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1:3306
-- Généré le : ven. 07 nov. 2025 à 07:08
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
-- Base de données : `bank_iut_prod`
--

-- --------------------------------------------------------

--
-- Structure de la table `compte`
--

DROP TABLE IF EXISTS `compte`;
CREATE TABLE IF NOT EXISTS `compte` (
  `numeroCompte` varchar(50) NOT NULL,
  `userId` varchar(50) NOT NULL,
  `solde` double NOT NULL,
  `avecDecouvert` varchar(5) NOT NULL,
  `decouvertAutorise` decimal(10,0) DEFAULT NULL,
  PRIMARY KEY (`numeroCompte`),
  KEY `index_userClient` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `compte`
--

INSERT INTO `compte` (`numeroCompte`, `userId`, `solde`, `avecDecouvert`, `decouvertAutorise`) VALUES
('AB7328887341', 'client2', -97, 'AVEC', 123),
('BD4242424242', 'client1', 150, 'SANS', NULL),
('FF5050500202', 'client1', 705, 'SANS', NULL),
('IO1010010001', 'client2', 6868, 'SANS', NULL),
('LA1021931215', 'client1', 150, 'SANS', NULL),
('MD8694030938', 'client1', 70, 'SANS', NULL),
('PP1285735733', 'a', 37, 'SANS', NULL),
('TD0398455576', 'client2', 34, 'AVEC', 700),
('XD1829451029', 'client2', -93, 'AVEC', 100),
('XX7788778877', 'client2', 90, 'SANS', NULL),
('XX9999999999', 'client2', 0, 'AVEC', 500);

-- --------------------------------------------------------

--
-- Structure de la table `utilisateur`
--

DROP TABLE IF EXISTS `utilisateur`;
CREATE TABLE IF NOT EXISTS `utilisateur` (
  `userId` varchar(50) NOT NULL,
  `nom` varchar(45) NOT NULL,
  `prenom` varchar(45) NOT NULL,
  `adresse` varchar(100) NOT NULL,
  `userPwd` varchar(64) DEFAULT NULL,
  `male` bit(1) NOT NULL,
  `type` varchar(10) NOT NULL,
  `numClient` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`userId`),
  UNIQUE KEY `numClient_UNIQUE` (`numClient`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `utilisateur`
--

INSERT INTO `utilisateur` (`userId`, `nom`, `prenom`, `adresse`, `userPwd`, `male`, `type`, `numClient`) VALUES
('admin', 'Smith', 'Joe', '123, grande rue, Metz', 'cd916028a2d8a1b901e831246dd5b9b4d3832786ddc63bbf5af4b50d9fc98f50', b'1', 'MANAGER', ''),
('client1', 'client1', 'Jane', '45, grand boulevard, Brest', '2331708682afe6944b558cb54b278d38b0c0fde0e0886c98566f17499a478eae', b'1', 'CLIENT', '123456789'),
('client2', 'client2', 'Jane', '45, grand boulevard, Brest', 'ad59ce6973e8657266c468c423827a9b9ca1b1fc39d2b9029ba8148ed327f5b1', b'1', 'CLIENT', '123456788');

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `compte`
--
ALTER TABLE `compte`
  ADD CONSTRAINT `fk_Compte_userId` FOREIGN KEY (`userId`) REFERENCES `utilisateur` (`userId`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
