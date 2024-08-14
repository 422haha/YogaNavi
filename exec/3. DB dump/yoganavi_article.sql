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
-- Table structure for table `article`
--

DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
  `article_id` bigint NOT NULL AUTO_INCREMENT,
  `content` varchar(512) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `image_url` varchar(512) DEFAULT NULL,
  `image_url_small` varchar(512) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`article_id`),
  KEY `FK15yy37u1qw43hjduyhs3bgomr` (`user_id`),
  CONSTRAINT `FK15yy37u1qw43hjduyhs3bgomr` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article`
--

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;
INSERT INTO `article` VALUES (5,'새롭게 찾아뵙게된 요를레이입니다.\n주로 올바른 스트레칭을 가르치고 있습니다.\n그럼 화상강의로 찾아뵐게요^^','2024-08-12 01:12:49.647959','',NULL,'2024-08-12 06:57:55.039206',1,4),(6,'어르신들과 함께하는 재활요가를 전문으로 하고있습니다!!\n잘 부탁드립니다 ? ','2024-08-12 06:11:42.230964','notices/4538b1fe-3393-4fac-9ece-08e0c78cb52e','notices/mini/4538b1fe-3393-4fac-9ece-08e0c78cb52e','2024-08-12 06:11:42.227099',0,7),(8,'추석기간 2024.09.16-2024.09.18 수업 없습니다! 즐거운 추석되세요~','2024-08-12 06:16:26.450081','',NULL,'2024-08-12 06:16:33.022394',1,5),(11,'안녕하세요 버츄얼 요가강사 애니입니다!\n\n저는 녹화강의를 중점으로 요가강의를 진행하고있습니다.\n주로 에어로빅을 베이스로 한 느리지만 경쾌한 요가를 가르칩니다. ^_^\n\n\n기술이 발전해서 실시간 강의로 찾아뵐 때날까지 같이 요가합시다~','2024-08-12 07:03:39.662049','notices/044bb68c-1b80-4e11-9e94-32e501eb2f9e','notices/mini/044bb68c-1b80-4e11-9e94-32e501eb2f9e','2024-08-12 07:04:53.151188',1,6),(14,'안녕하세요, 여러분!\n\n요가 강사 서브웨이입니다. 항상 저의 요가 수업에 참여해주시는 여러분께 깊은 감사를 드립니다. 다가오는 9월 중에 저는 요가 세미나에 참석하게 되어, 그 기간 동안 수업을 잠시 쉬게 되었습니다. 이 기간 동안 여러분과 함께 할 수 없어 아쉽지만, 더 깊이 있는 지식을 얻어 돌아와 여러분께 더 나은 수업을 제공할 수 있도록 노력하겠습니다.\n\n수업이 쉬는 기간은 9월 2일부터 9월 6일까지입니다. 이후 일정대로 다시 수업이 진행될 예정이니 참고해 주시기 바랍니다.\n\n여러분의 이해와 협조에 감사드리며, 세미나 후 더 나은 모습으로 다시 뵙겠습니다. 문의사항이 있으시면 언제든지 연락 주세요.\n\n감사합니다.\n\n서브웨이 드림.','2024-08-12 07:09:21.709701','notices/aae5c043-1766-4033-8041-09a416ce6c15','notices/mini/aae5c043-1766-4033-8041-09a416ce6c15','2024-08-12 07:09:21.709555',0,14),(16,'신입강사 요창민입니다!\n\n8월 16일 새로운 강의로 찾아뵙겠습니다 ㅎㅎ','2024-08-12 08:07:53.002080','',NULL,'2024-08-12 08:07:52.998621',0,2);
/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-08-14  9:27:48
