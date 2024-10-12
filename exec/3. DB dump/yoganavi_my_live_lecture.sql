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
-- Table structure for table `my_live_lecture`
--

DROP TABLE IF EXISTS `my_live_lecture`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `my_live_lecture` (
  `my_list_id` bigint NOT NULL AUTO_INCREMENT,
  `end_date` date DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `live_id` bigint NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`my_list_id`),
  KEY `FKln7dq9l29b1sr1u7ngx04wtpc` (`live_id`),
  KEY `FKf4haddq9ednn1quwv0uwmx5mj` (`user_id`),
  CONSTRAINT `FKf4haddq9ednn1quwv0uwmx5mj` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKln7dq9l29b1sr1u7ngx04wtpc` FOREIGN KEY (`live_id`) REFERENCES `live_lectures` (`live_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `my_live_lecture`
--

LOCK TABLES `my_live_lecture` WRITE;
/*!40000 ALTER TABLE `my_live_lecture` DISABLE KEYS */;
INSERT INTO `my_live_lecture` VALUES (2,'2024-12-28','2024-08-11',38,15),(4,'2024-08-31','2024-08-12',43,16),(6,'2024-08-30','2024-08-11',48,18),(8,'2024-08-31','2024-08-12',44,15),(9,'2024-08-14','2024-08-12',39,10),(13,'2024-08-16','2024-08-12',57,14),(15,'2024-08-15','2024-08-12',63,10),(16,'2024-08-15','2024-08-12',64,12),(18,'2024-08-16','2024-08-13',67,10);
/*!40000 ALTER TABLE `my_live_lecture` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-08-14  9:27:46
