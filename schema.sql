-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 26, 2026 at 06:54 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `habitap`
--

-- --------------------------------------------------------

--
-- Table structure for table `calendar_items`
--

CREATE TABLE `calendar_items` (
                                  `id` int(11) NOT NULL,
                                  `type` varchar(10) NOT NULL,
                                  `title` varchar(120) NOT NULL,
                                  `item_date` date NOT NULL,
                                  `start_time` time DEFAULT NULL,
                                  `end_time` time DEFAULT NULL,
                                  `category` varchar(50) DEFAULT NULL,
                                  `description` varchar(500) DEFAULT NULL,
                                  `priority` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `goals`
--

CREATE TABLE `goals` (
                         `id` int(11) NOT NULL,
                         `name` varchar(80) NOT NULL,
                         `color` varchar(16) NOT NULL,
                         `created_date` datetime NOT NULL DEFAULT current_timestamp(),
                         `due_date` datetime NOT NULL DEFAULT current_timestamp(),
                         `priority` varchar(10) NOT NULL DEFAULT 'LOW',
                         `status` varchar(10) NOT NULL DEFAULT 'ACTIVE',
                         `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `habits`
--

CREATE TABLE `habits` (
                          `id` int(11) NOT NULL,
                          `name` varchar(50) NOT NULL,
                          `description` varchar(500) NOT NULL,
                          `completed` tinyint(1) NOT NULL DEFAULT 0,
                          `frequency` varchar(10) NOT NULL DEFAULT 'daily',
                          `streak` int(11) NOT NULL DEFAULT 0,
                          `goal_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
                         `id` int(11) NOT NULL,
                         `username` varchar(60) NOT NULL,
                         `email` varchar(120) NOT NULL,
                         `password_hash` varchar(128) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `calendar_items`
--
ALTER TABLE `calendar_items`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `goals`
--
ALTER TABLE `goals`
    ADD PRIMARY KEY (`id`),
    ADD KEY `fk_goals_user` (`user_id`);

--
-- Indexes for table `habits`
--
ALTER TABLE `habits`
    ADD PRIMARY KEY (`id`),
    ADD KEY `fk_habits_goal` (`goal_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
    ADD PRIMARY KEY (`id`),
    ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `calendar_items`
--
ALTER TABLE `calendar_items`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `goals`
--
ALTER TABLE `goals`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `habits`
--
ALTER TABLE `habits`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `goals`
--
ALTER TABLE `goals`
    ADD CONSTRAINT `fk_goals_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `habits`
--
ALTER TABLE `habits`
    ADD CONSTRAINT `fk_habits_goal` FOREIGN KEY (`goal_id`) REFERENCES `goals` (`id`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
