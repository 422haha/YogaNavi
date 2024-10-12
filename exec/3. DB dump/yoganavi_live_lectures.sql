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
-- Table structure for table `live_lectures`
--

DROP TABLE IF EXISTS `live_lectures`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `live_lectures` (
  `live_id` bigint NOT NULL AUTO_INCREMENT,
  `available_day` varchar(100) NOT NULL,
  `end_date` date NOT NULL,
  `end_time` time NOT NULL,
  `live_content` varchar(300) DEFAULT NULL,
  `live_title` varchar(30) NOT NULL,
  `max_live_num` int NOT NULL,
  `reg_date` datetime(6) NOT NULL,
  `start_date` date NOT NULL,
  `start_time` time NOT NULL,
  `user_id` int DEFAULT NULL,
  `is_on_air` bit(1) NOT NULL,
  PRIMARY KEY (`live_id`),
  KEY `FKgt9xat80qsabhp1t591oceh96` (`user_id`),
  CONSTRAINT `FKgt9xat80qsabhp1t591oceh96` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `live_lectures`
--

LOCK TABLES `live_lectures` WRITE;
/*!40000 ALTER TABLE `live_lectures` DISABLE KEYS */;
INSERT INTO `live_lectures` VALUES (38,'MON,TUE,WED,THU,FRI','2024-12-31','08:00:00','아침을 요가로 깨우는 아침요가입니다. ㅎㅎ\n50분동안 요가로 스트레칭, 자세교정을 해보아요','아침햇살요가(평일)',1,'2024-08-12 06:19:39.259896','2024-08-01','07:00:00',5,_binary '\0'),(39,'SAT,SUN','2024-09-01','09:00:00','주말아침 요가로 부지런히 시작해봅시다 :)\n스트레칭부터 차분히 진행할게요','아침햇살요가(주말)',1,'2024-08-12 06:21:05.850851','2024-08-01','08:00:00',5,_binary '\0'),(40,'MON,WED,FRI','2025-08-12','13:30:00','숲속의 소리를 들으며 하는 요가입니다.','숲속요가',1,'2024-08-12 06:25:23.641626','2024-08-12','12:00:00',5,_binary '\0'),(41,'MON,TUE,WED,THU,FRI','2024-06-30','20:30:00','새로운 마음으로 시작하는 새해요가','새해요가(상반기)',1,'2024-08-12 06:26:52.625051','2024-01-01','19:00:00',5,_binary '\0'),(43,'MON,WED,FRI','2024-08-31','21:00:00','퇴근 후 요가로 자세를 교정해요!','8월 한달요가-월수금',1,'2024-08-12 06:38:09.207390','2024-08-01','20:00:00',4,_binary '\0'),(44,'TUE,THU,SAT','2024-08-31','21:00:00','퇴근 후 요가로 자세를 교정해요!','8월 한달요가-화목토',1,'2024-08-12 06:39:23.814991','2024-08-01','20:00:00',4,_binary '\0'),(46,'MON,WED,THU','2024-08-29','20:00:00','퇴근후에 짧게 속성으로 진행되는 요가 강의입니다. 초급자에겐 힘들 수 있으니 주의해주세요~','[중급] 직장인을 위한 저녁 요가',1,'2024-08-12 06:49:09.626747','2024-08-12','19:00:00',14,_binary '\0'),(47,'TUE,FRI','2024-08-30','17:00:00','요추 추간판 탈출증으로 굉장히 고생하다가 요가로 극복한 서브웨이 강사의 특강입니다.','[재활] 근골격계 질환자를 위한 재활 요가 강의',1,'2024-08-12 06:52:07.884052','2024-08-13','15:00:00',14,_binary '\0'),(48,'MON,TUE,WED,THU,FRI','2024-08-30','10:30:00','평일 내내 오전에 진행되는 숙련자를 위한 강의입니다. 주부 환영','[고급] 부지런한 오전을 위한 요가 강의',1,'2024-08-12 06:55:22.779671','2024-08-12','09:00:00',14,_binary '\0'),(49,'MON,WED,FRI','2025-08-01','17:00:00','보통보다 조금 유연하게 진행되는 어르신요가입니다','어르신 요가 월수금(2)',1,'2024-08-12 07:14:53.087764','2024-08-01','15:00:00',7,_binary '\0'),(50,'TUE,THU,SAT','2025-08-01','17:00:00','조금 느린 템포로 진행되는 어르신요가입니다','어르신요가 화목(2)',1,'2024-08-12 07:17:30.665856','2024-08-01','15:00:00',7,_binary '\0'),(51,'MON,WED,FRI','2025-08-01','14:00:00','조금 느린 템포로 진행되는 어르신요가입니다.','어르신요가 월수금(1)',1,'2024-08-12 07:18:53.849460','2024-08-01','13:00:00',7,_binary '\0'),(52,'TUE,THU','2025-08-01','15:00:00','조금 느린 템포로 진행되는 어르신요가입니다.','어르신요가 화목(1)',1,'2024-08-12 07:19:41.800326','2024-08-01','13:00:00',7,_binary '\0'),(53,'MON,WED,FRI','2025-08-01','17:30:00','요가 초급편입니다.\n가장 기본적인 동작부터 호흡법까지 가르칩니다.','초보요가',1,'2024-08-12 07:31:52.998883','2024-08-01','16:00:00',9,_binary '\0'),(54,'MON,WED,FRI','2025-03-01','18:30:00','집에서 편하게 할 수 있는 실내요가입니다 ','실내요가',1,'2024-08-12 07:53:33.481927','2024-03-01','17:00:00',8,_binary '\0'),(55,'TUE,FRI','2025-08-01','21:00:00','기초부터 요가를 시작한다!\n쉽게 배울 수 있는 요가를 가르칩니다.\n동작의 정확도를 높이는 기초요가','기초요가',1,'2024-08-12 08:00:24.276425','2024-08-01','20:00:00',8,_binary '\0'),(57,'TUE,THU,SAT','2024-08-16','09:00:00','test','test',1,'2024-08-12 22:52:38.532435','2024-08-13','08:00:00',4,_binary '\0'),(61,'TUE,THU,SAT','2025-08-01','18:00:00','요가식 숨쉬기 방법부터 자세 하나하나 자세히 알려주는 기초요가입니다.','기초요가 오후반',1,'2024-08-13 07:11:20.425701','2024-08-01','16:30:00',8,_binary '\0'),(63,'TUE','2024-08-15','18:45:00','테스트','테스트 강의',1,'2024-08-13 08:31:01.349654','2024-08-13','17:45:00',9,_binary '\0'),(64,'TUE,WED','2024-08-17','23:11:00','들어오셔유','승준 교육생 화상강의',1,'2024-08-13 11:32:57.621051','2024-08-13','12:00:00',4,_binary '\0'),(67,'WED,THU,FRI','2025-08-01','11:00:00','요가의 호흡법부터 기본동작을 자세히 알려주는 기초요가반입니다.','기초요가 오전반 ',1,'2024-08-14 00:27:27.353731','2024-08-01','09:45:00',8,_binary '\0');
/*!40000 ALTER TABLE `live_lectures` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-08-14  9:27:44
