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
-- Table structure for table `recorded_lecture_chapter`
--

DROP TABLE IF EXISTS `recorded_lecture_chapter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recorded_lecture_chapter` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `chapter_number` int NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `video_url` varchar(512) NOT NULL,
  `lecture_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKji5ju3jbysepa7g2wcmfujny2` (`lecture_id`),
  CONSTRAINT `FKji5ju3jbysepa7g2wcmfujny2` FOREIGN KEY (`lecture_id`) REFERENCES `recorded_lecture` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recorded_lecture_chapter`
--

LOCK TABLES `recorded_lecture_chapter` WRITE;
/*!40000 ALTER TABLE `recorded_lecture_chapter` DISABLE KEYS */;
INSERT INTO `recorded_lecture_chapter` VALUES (7,1,'늘려봅시다','첫번째 동작','videos/220a9523-bd77-4f93-9099-53cc7e5931d1',3),(8,1,'만세해봅시다','만세동작','videos/c14a8082-3592-45bd-b334-fc817bfcdddb',4),(9,2,'다리 근육을 높여주는 운동입니다','다리 스트레칭','videos/364bcaa9-028e-412b-a97a-22311c7f332a',4),(10,3,'마무리동작입니다 ','마무리','videos/d42ce816-947b-42e2-872d-c669900de88e',4),(11,0,'코어를 단련하고 척추를 펴주는 동작입니다','코어단련','videos/7a07ddf4-e48e-4648-b831-80f2202f3e26',3),(12,1,'플랭크 변형자세입니다.\n힘드시거나 허리가 아프신 분들은 무릎을 붙이고 진행하셔도 됩니다.','플랭크 변형','videos/4a6fea0d-99c6-4c14-8457-bb4c99618694',5),(13,2,'버틸수록 코어근육이 늘어나는 동작입니다.','하늘찌르기','videos/91ecd476-4081-4d8b-8ad5-73b13191d805',5),(14,1,'코어 근육을 위한 자세입니다','고양이','videos/94a10749-03e1-4341-9a88-7a1823c44be1',6),(15,2,'런지와 스쿼트로 상하체 근육을 키워봅시다','런지, 스쿼트','videos/a1b6382f-4fc6-4bc5-831e-928f44265e68',6),(16,1,'조금씩 따라해보세요','천천히 가봅시다','videos/95dd6059-85ff-413e-9d23-8d5f905a19d6',7),(20,0,'천천히 숨쉬며 요가를 준비합니다','요가 숨쉬기','videos/bdfcdf75-3280-4648-875c-eff43dac0158',1),(21,0,'팔을 들어올리며 전신 근육을 플어줍니다','팔동작','videos/51f5d982-ae17-4852-95e9-d1a8d606635b',1),(22,1,'관절을푸는데 좋은 동작입니다.','허수아비동작','videos/56d690bd-5b49-4e96-acc9-606434d35fd2',2),(23,2,'팔과 다리를 할 수 있는한 늘려봅시다','팔동작','videos/0e1081eb-f1bb-4ffd-8876-bf434ec208a1',2);
/*!40000 ALTER TABLE `recorded_lecture_chapter` ENABLE KEYS */;
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
