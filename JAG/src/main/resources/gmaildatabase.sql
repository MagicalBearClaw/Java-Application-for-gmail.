-- phpMyAdmin SQL Dump
-- version 4.1.14
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Oct 03, 2015 at 08:39 PM
-- Server version: 5.6.17
-- PHP Version: 5.5.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `gmaildatabase`
--

DROP DATABASE IF EXISTS gmaildatabase;
CREATE DATABASE gmaildatabase;

USE gmaildatabase;




-- --------------------------------------------------------

--
-- Table structure for table `addresses`
--



DROP TABLE IF EXISTS addresses;
CREATE TABLE IF NOT EXISTS `addresses` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `address` varchar(35) NOT NULL,
  `mailId` int(11) NOT NULL,
  `mailType` char(2) NOT NULL DEFAULT 'T',
  PRIMARY KEY (`id`),
  KEY `mailId_fk` (`mailId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `attachments`
--
DROP TABLE IF EXISTS attachments;
CREATE TABLE IF NOT EXISTS `attachments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `attachment` mediumblob NOT NULL,
  `isEmbeded` tinyint(1) NOT NULL,
  `contentId` varchar(40) DEFAULT '',
  `mailId` int(11) NOT NULL,
  `fileName` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `mailId_fk` (`mailId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `email`
--
DROP TABLE IF EXISTS emails;
CREATE TABLE IF NOT EXISTS `emails` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from` varchar(35) NOT NULL,
  `subject` varchar(255) NOT NULL,
  `folderId` int(11) NOT NULL DEFAULT '1',
  `dateRecieved` timestamp NOT NULL,
  `dateSent` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  KEY `folderId_fk` (`folderId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

-- --------------------------------------------------------

--
-- Table structure for table `folders`
--
DROP TABLE IF EXISTS folders;
CREATE TABLE IF NOT EXISTS `folders` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `folderName` varchar(25) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `folderName` (`folderName`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=3 ;

--
-- Dumping data for table `folders`
--

INSERT INTO `folders` (`id`, `folderName`) VALUES
(1, 'Inbox'),
(2, 'Sent');

-- --------------------------------------------------------

--
-- Table structure for table `message`
--
DROP TABLE IF EXISTS messages;
CREATE TABLE IF NOT EXISTS `messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` mediumtext NOT NULL,
  `MimeType` varchar(15) NOT NULL,
  `mailId` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `mailid_fk` (`mailId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `addresses`
--
ALTER TABLE `addresses`
  ADD CONSTRAINT `addresses_ibfk_1` FOREIGN KEY (`mailId`) REFERENCES `emails` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `attachments`
--
ALTER TABLE `attachments`
  ADD CONSTRAINT `attachments_ibfk_1` FOREIGN KEY (`mailId`) REFERENCES `emails` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `email`
--
ALTER TABLE `emails`
  ADD CONSTRAINT `folder_fk` FOREIGN KEY (`folderId`) REFERENCES `folders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `message`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `message_ibfk_1` FOREIGN KEY (`mailId`) REFERENCES `emails` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
