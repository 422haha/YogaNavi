-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: i11d210.p.ssafy.io    Database: yoganavi
-- ------------------------------------------------------
-- Server version	8.0.39-0ubuntu0.20.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `recorded_lecture`
--

DROP TABLE IF EXISTS `recorded_lecture`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recorded_lecture` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `record_content` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `last_modified_at` datetime(6) NOT NULL,
  `like_count` bigint NOT NULL,
  `thumbnail` varchar(512) DEFAULT NULL,
  `thumbnail_small` varchar(512) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdaj61wrrbdtygins8p6s0y091` (`user_id`),
  CONSTRAINT `FKdaj61wrrbdtygins8p6s0y091` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recorded_lecture`
--

LOCK TABLES `recorded_lecture` WRITE;
/*!40000 ALTER TABLE `recorded_lecture` DISABLE KEYS */;
INSERT INTO `recorded_lecture` VALUES (1,'의 뒷모습','2024-08-07 13:23:01.943685','2024-08-10 15:49:15.064430',4,'thumbnails/019fcd17-c19d-472b-b1c0-c695223e565c','thumbnails/mini/019fcd17-c19d-472b-b1c0-c695223e565c','요가왕',84,1),(2,'선생님의 요가 수업','2024-08-07 13:44:32.512298','2024-08-13 05:59:40.047646',3,'thumbnails/f940f7f9-b241-419e-baa7-68ef51b28245','thumbnails/mini/f940f7f9-b241-419e-baa7-68ef51b28245','허수아비도 요가',29,1),(3,'산에서 하는 요가 ','2024-08-09 06:29:28.573948','2024-08-13 05:59:40.914836',5,'thumbnails/143d7e5b-b24a-4e6c-8595-e6f8704cd945','thumbnails/mini/143d7e5b-b24a-4e6c-8595-e6f8704cd945','산요가',24,5),(4,'바다에서 하는 요가','2024-08-09 07:07:47.419476','2024-08-13 05:59:42.305881',3,'thumbnails/e4afa633-80d4-4327-ba6c-f1859e2c13d4','thumbnails/mini/e4afa633-80d4-4327-ba6c-f1859e2c13d4','바다요가',33,5),(5,'스트레스엔 스트레칭','2024-08-09 07:17:01.315399','2024-08-13 05:58:01.211604',3,'thumbnails/ef1d5b13-6e5b-4d94-a801-90a2dc1ed304','thumbnails/mini/ef1d5b13-6e5b-4d94-a801-90a2dc1ed304','스트레칭',11,4),(6,'실내에서 하는 요가입니다','2024-08-09 07:26:26.324284','2024-08-13 06:31:46.788494',1,'thumbnails/d81ac0ff-9155-4855-8f36-c758911e822b','thumbnails/mini/d81ac0ff-9155-4855-8f36-c758911e822b','실내 요가',9,8),(7,'따라해보아요','2024-08-09 07:58:40.648668','2024-08-13 05:59:43.812290',2,'thumbnails/a93709bb-57c9-4d3c-ace3-22093c0c5c89','thumbnails/mini/a93709bb-57c9-4d3c-ace3-22093c0c5c89','애니메이션 요가',10,6);
/*!40000 ALTER TABLE `recorded_lecture` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-08-14  9:27:43
