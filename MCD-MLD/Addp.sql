-- phpMyAdmin SQL Dump
-- version 5.0.4deb2ubuntu5
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost:3306
-- Généré le : mar. 20 sep. 2022 à 09:01
-- Version du serveur :  8.0.29-0ubuntu0.21.10.2
-- Version de PHP : 8.0.8

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `Addp`
--

-- --------------------------------------------------------

--
-- Structure de la table `client`
--

CREATE TABLE `client` (
  `id_client` int NOT NULL,
  `name_client` varchar(50) NOT NULL,
  `first_name_client` varchar(50) NOT NULL,
  `mail_client` varchar(50) NOT NULL,
  `tel_client` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `disks`
--

CREATE TABLE `disks` (
  `id_disk` int NOT NULL,
  `name_disk` varchar(50) NOT NULL,
  `size_disk` int DEFAULT NULL,
  `date_bought` date NOT NULL,
  `price_bought` decimal(10,0) NOT NULL,
  `times_rented` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `failure`
--

CREATE TABLE `failure` (
  `id_fail` int NOT NULL,
  `date_fail` date NOT NULL,
  `description` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- Structure de la table `loan`
--

CREATE TABLE `loan` (
  `id_loan` int NOT NULL,
  `date_loan` date DEFAULT NULL,
  `date_return` date NOT NULL,
  `scheduled_date_return` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `client`
--
ALTER TABLE `client`
  ADD PRIMARY KEY (`id_client`);

--
-- Index pour la table `disks`
--
ALTER TABLE `disks`
  ADD PRIMARY KEY (`id_disk`);

--
-- Index pour la table `failure`
--
ALTER TABLE `failure`
  ADD PRIMARY KEY (`id_fail`);

--
-- Index pour la table `loan`
--
ALTER TABLE `loan`
  ADD PRIMARY KEY (`id_loan`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `client`
--
ALTER TABLE `client`
  MODIFY `id_client` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `disks`
--
ALTER TABLE `disks`
  MODIFY `id_disk` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `failure`
--
ALTER TABLE `failure`
  MODIFY `id_fail` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `loan`
--
ALTER TABLE `loan`
  MODIFY `id_loan` int NOT NULL AUTO_INCREMENT;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `failure`
--
ALTER TABLE `failure`
  ADD CONSTRAINT `endure` FOREIGN KEY (`id_fail`) REFERENCES `disks` (`id_disk`) ON DELETE RESTRICT ON UPDATE RESTRICT;

--
-- Contraintes pour la table `loan`
--
ALTER TABLE `loan`
  ADD CONSTRAINT `affect` FOREIGN KEY (`id_loan`) REFERENCES `client` (`id_client`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  ADD CONSTRAINT `loan_ibfk_1` FOREIGN KEY (`id_loan`) REFERENCES `disks` (`id_disk`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
