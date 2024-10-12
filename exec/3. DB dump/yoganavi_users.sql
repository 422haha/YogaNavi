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
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `auth_token` varchar(255) DEFAULT NULL,
  `auth_token_expiration_time` datetime(6) DEFAULT NULL,
  `content` varchar(100) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `fcm_token` varchar(512) DEFAULT NULL,
  `is_deleted` bit(1) NOT NULL,
  `nickname` varchar(255) NOT NULL,
  `profile_image_url` varchar(512) DEFAULT NULL,
  `profile_image_url_small` varchar(512) DEFAULT NULL,
  `pwd` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK2ty1xmrrgtn89xt7kyxx6ta7h` (`nickname`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,NULL,NULL,'나는야 요가왕 \n룰루랄라',NULL,'csjune99@naver.com','d4e_Sc6-Q4CJZtfygot-uz:APA91bFD6iNVMleTmCSl1L2nehSkMzilzhxtH63aSo2xrwG2Ndu7ICVgjgMqdle-5LgNZ9d15Wn-DySJ5WLsE_kGUjthI_pXA0CRGQvR9RAyuwHVBzIhwMt1A1bMMKNuY3rnU95NMRDy',_binary '\0','요가 1등','logo/7aef8b4c-c955-4f04-acad-1e186b09d52d','logo/mini/7aef8b4c-c955-4f04-acad-1e186b09d52d','$2a$10$VMK9AncQgY/CJeVn24TE7efgkfFiZMti85q5TBZASTqHboZus5YBe','TEACHER'),(2,NULL,NULL,'안녕하세요 신입강사 요창민입니다 :)\n빨리강의로 찾아뵙겠습니다 ㅎㅎ',NULL,'yeo2507@naver.com','d4e_Sc6-Q4CJZtfygot-uz:APA91bFD6iNVMleTmCSl1L2nehSkMzilzhxtH63aSo2xrwG2Ndu7ICVgjgMqdle-5LgNZ9d15Wn-DySJ5WLsE_kGUjthI_pXA0CRGQvR9RAyuwHVBzIhwMt1A1bMMKNuY3rnU95NMRDy',_binary '\0','요창민',NULL,NULL,'$2a$10$p7id1MQ4U/h9AZ3VGA7x3eUW5Vu00aG50ThzzODnBs/AbNCjjC0RC','TEACHER'),(3,NULL,NULL,NULL,'2024-08-12 06:01:47.910981','deleted_3@yoganavi.com',NULL,_binary '','삭제된 사용자3',NULL,NULL,'$2a$10$PyqtNvankswcYcQCSIleTOSdlKJ4ZNVnjCZQpQf8F.5w0A4L8OEMC','TEACHER'),(4,NULL,NULL,'스트레칭을 전문으로하고 있습니다:)',NULL,'1@example.com','eZKQPJ9ARKuSBsLDvWhHu5:APA91bHxnTeTXCk__w_rhzDlvzLGLcq0FAe9TRxX3xdRkL8zng7VjJXGjsOkewXULcagAbBvkVbBEsqnD94oXph2PZS8rHOK4HaltraJFBucbjuDBsfLG-jaeFfCsOkmOI7laHxQhY9w',_binary '\0','요를레이','logo/de8cd1ce-a6cd-470e-a62c-7e2237c41978','logo/mini/de8cd1ce-a6cd-470e-a62c-7e2237c41978','$2a$10$uMs4GgI0yhU7chl6C9nlA.UbzcMPg0CZdejyQmalw/cEE4e6TnYJK','TEACHER'),(5,NULL,NULL,'자연에서 주로 요가를 가르칩니다',NULL,'teacher@example.com','cdZopwZ3RtuQ0z93agWRlc:APA91bELjJMwgZTVuFk2Zoz281FjvJSZLBc9BDxt1RFHZeHsEddTFqG0QYAgTxDzSaYTA0VmFmjIsRe3hsVm9Bla5WX1h_oItCSp0rRt2SaH2dkcD5dbTOGlHpian0UIPIVuyE8r6565',_binary '\0','네이쳐','logo/ba0af496-8185-401a-8975-eaca69a2785c','logo/mini/ba0af496-8185-401a-8975-eaca69a2785c','$2a$10$vdpAHJV6Pcs6DEFVWPaL7.Aj8fmikFCO8u0cHjNRQRhS0YAmTgO4y','TEACHER'),(6,NULL,NULL,'안녕하세요\n버츄얼 요가강사 애니입니다\n같이 운동해봐요',NULL,'3@example.com','eyPbATsCSnmv39eGbpGSLp:APA91bHhxvL242rPpTBAedbWKed4x_5MuqgAKafPnyopxu7BDTTsHqkzAYm8Oqn4oM2-wVBTtGMbfzUJhUhBRdBYvIxTYqKAHdsPOHn0AenjqW0cOuAHmAXYaCGHEcRxmXOX24ZZu2GZ',_binary '\0','애니','logo/ac769bbe-dc86-4295-beac-2878ab5d0f03','logo/mini/ac769bbe-dc86-4295-beac-2878ab5d0f03','$2a$10$euXrHPNbCPe67wQu2ZyLF.IBRmFjQlxd45AEmAsD9GZxGc09cYAKS','TEACHER'),(7,NULL,NULL,'어르신 재활요가를 전문으로 합니다\n관절을 강화하는 요가! 같이 해보아요^^',NULL,'4@example.com','eyPbATsCSnmv39eGbpGSLp:APA91bHhxvL242rPpTBAedbWKed4x_5MuqgAKafPnyopxu7BDTTsHqkzAYm8Oqn4oM2-wVBTtGMbfzUJhUhBRdBYvIxTYqKAHdsPOHn0AenjqW0cOuAHmAXYaCGHEcRxmXOX24ZZu2GZ',_binary '\0','요하','logo/dad0d10e-4f09-4ac4-95c6-7da1e0b63ab0','logo/mini/dad0d10e-4f09-4ac4-95c6-7da1e0b63ab0','$2a$10$8rRNPkbA5rK3LtJP8by2W.12iqcah9TpAzshYuNlDa6dRYTnAD/5.','TEACHER'),(8,NULL,NULL,'집에서 편하게 배우는 요가를 가르칩니다.',NULL,'5@example.com','eZKQPJ9ARKuSBsLDvWhHu5:APA91bHxnTeTXCk__w_rhzDlvzLGLcq0FAe9TRxX3xdRkL8zng7VjJXGjsOkewXULcagAbBvkVbBEsqnD94oXph2PZS8rHOK4HaltraJFBucbjuDBsfLG-jaeFfCsOkmOI7laHxQhY9w',_binary '\0','요가마스터','logo/2b16256a-77d0-4d73-a92a-b28535dd040c','logo/mini/2b16256a-77d0-4d73-a92a-b28535dd040c','$2a$10$lvX86vinbFAf01Cd.lhTk.O0VD3K8v/VDuiY597C83vxqxSy4HN7G','TEACHER'),(9,NULL,NULL,'요가 초심자의 입장에서 생각하며 요가를 가르칩니다',NULL,'6@example.com','eZKQPJ9ARKuSBsLDvWhHu5:APA91bHxnTeTXCk__w_rhzDlvzLGLcq0FAe9TRxX3xdRkL8zng7VjJXGjsOkewXULcagAbBvkVbBEsqnD94oXph2PZS8rHOK4HaltraJFBucbjuDBsfLG-jaeFfCsOkmOI7laHxQhY9w',_binary '\0','플로우가이드','logo/00854b5f-86a3-4dc4-94d5-cba5b3a262aa','logo/mini/00854b5f-86a3-4dc4-94d5-cba5b3a262aa','$2a$10$d7QesLyyoSf7eCzcPO.gV.rmCioYgSR404cuqQrGVlxD4PeuFAte.','TEACHER'),(10,NULL,NULL,NULL,NULL,'student@example.com','eyPbATsCSnmv39eGbpGSLp:APA91bHhxvL242rPpTBAedbWKed4x_5MuqgAKafPnyopxu7BDTTsHqkzAYm8Oqn4oM2-wVBTtGMbfzUJhUhBRdBYvIxTYqKAHdsPOHn0AenjqW0cOuAHmAXYaCGHEcRxmXOX24ZZu2GZ',_binary '\0','유연한연꽃',NULL,NULL,'$2a$10$2S9C5X4Krs8gTcDUmrxUyOh1yR/xUfJmKNVtJKuzi8r/xYSU9GYFi','STUDENT'),(11,NULL,NULL,NULL,NULL,'8@example.com','eyPbATsCSnmv39eGbpGSLp:APA91bHhxvL242rPpTBAedbWKed4x_5MuqgAKafPnyopxu7BDTTsHqkzAYm8Oqn4oM2-wVBTtGMbfzUJhUhBRdBYvIxTYqKAHdsPOHn0AenjqW0cOuAHmAXYaCGHEcRxmXOX24ZZu2GZ',_binary '\0','아사나전문가',NULL,NULL,'$2a$10$4NFI2vcgZNNSBiq7q6IugOx.JRj/kYv65n77rxQDYrAe0hjsHp.eu','STUDENT'),(12,NULL,NULL,NULL,NULL,'9@example.com','cFXStojcRKqICX8Aa8Za-Z:APA91bETJLf1eWlmgzbYCk80lPlswGNY2gObBVk3hrBFXqhvQMxTLwSIw9VFhFhxf5CUNJhPcKS5_-Q32sROOP-kOeM0IR3k3tIIfL3THHmwGQw5T0aghHToKOERdMVS3RPWVVm6QUN4',_binary '\0','균형잡힌나무',NULL,NULL,'$2a$10$IH7dkMJtT0pi1aesIG.o6eJVyHoqlZRs.NrWDdZNuIkmHR.QlEU5a','STUDENT'),(13,NULL,NULL,NULL,NULL,'10@example.com','epB_pibZTBi-18PzCJm2RN:APA91bEX4mVW1h_G42carLdxP6YLVOY4gwovLLBHHXNrbZHo8EXq2TxGTBtWQBAgF5Kfo93S_3rB0LccyED8fPY3LJynkMidMQDHmcFPSKIoWtI-jrHiDmHfGU1eV0VS0Md-IxS11SL2',_binary '\0','명상하는달',NULL,NULL,'$2a$10$jncICwSzP56AufsMJsxtd.9P7oaXrEI5OsTSi3UHJz4iLaxl.kBRO','STUDENT'),(14,NULL,NULL,'요가와 서브웨이에 인생을 건 서브웨이입니다. 꾸벅',NULL,'subway@kumoh.ac.kr','fTyVoEOyTn-9m0-EH3q-lS:APA91bFD2xpjtDABWnkzOdHwRhI2D1oIvApRlWD_q85bq7swKMFoCZ32mnlSCsd-Xe3uFa-BYbtRIxA2SGDVX_AY_76CuJ2nWxESM9Xo0-Hd2ZLRo_x0Gdp_tjyH5EXNIdUON1K34yaU',_binary '\0','서브웨이','logo/0a1361e8-94b6-4d95-97e2-b10badf51106','logo/mini/0a1361e8-94b6-4d95-97e2-b10badf51106','$2a$10$8K1pHVXCmQn90jiGzG.6z.87FVxyzs6cD5GMv6W7pNL6QnW/i/tNu','TEACHER'),(15,NULL,NULL,NULL,NULL,'seojang980510@naver.com','eo6vBarXSZukgVNHyqob4d:APA91bHFMn_Bjb3L3ffM2Zo9CAPsGkn4KfyzZyzLScowL_-vlX31_cRrron9LEj8vfzQYmrGBn664yJ9T7DbehEf6wT6r_hs0LRxGIG3IbIEatNeA-OY-VSsXxTA4M3uPhz-a5d7LaLq',_binary '\0','서장원','logo/35a4f8a4-2fbc-4f55-8f4c-77f39794bc0b','logo/mini/35a4f8a4-2fbc-4f55-8f4c-77f39794bc0b','$2a$10$PwBoZNnAwFz/OJH2i2EVj.XWQdQ8FdPJ0TU5nRUL9MqBar0uLxW/6','STUDENT'),(16,NULL,NULL,NULL,NULL,'2@example.com','cFXStojcRKqICX8Aa8Za-Z:APA91bETJLf1eWlmgzbYCk80lPlswGNY2gObBVk3hrBFXqhvQMxTLwSIw9VFhFhxf5CUNJhPcKS5_-Q32sROOP-kOeM0IR3k3tIIfL3THHmwGQw5T0aghHToKOERdMVS3RPWVVm6QUN4',_binary '\0','제임스',NULL,NULL,'$2a$10$aI1Ysind8DT0COPfjyTnOuCukkfMXg1sMHgEdxkjd7z6a7hs/2xAW','TEACHER'),(17,NULL,NULL,NULL,NULL,'7@example.com','cFXStojcRKqICX8Aa8Za-Z:APA91bETJLf1eWlmgzbYCk80lPlswGNY2gObBVk3hrBFXqhvQMxTLwSIw9VFhFhxf5CUNJhPcKS5_-Q32sROOP-kOeM0IR3k3tIIfL3THHmwGQw5T0aghHToKOERdMVS3RPWVVm6QUN4',_binary '\0','숨쉬는산',NULL,NULL,'$2a$10$kqg0je/aaNIAJDII6eTMe.zi2LJbpi1q17IMCjmMAfeebB9JuFFbm','STUDENT'),(18,NULL,NULL,NULL,NULL,'ric111@kumoh.ac.kr','exMOX5pJTI6Z5yNCzqXald:APA91bEG85qKuGh9ALvu-1rIJ_YHmCAc45vHEFMORsA3jQGwo6BDlrafZa4R3vrDCTEvYM-oFFGj1rn41-7oHwEPC9cPr-mZJhVRC4qqwYWJ3vCkgjpaJ3BASVXaJWGxNAQjRvf3dZgK',_binary '\0','서브웨이제자',NULL,NULL,'$2a$10$rVDpDOARqWCMJNhiL224cOlYcjS3.qXmj9nBmnHkc5ePF03jsDqQm','STUDENT');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
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
