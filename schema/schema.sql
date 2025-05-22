DROP TABLE IF EXISTS `order_entity`;
CREATE TABLE `order_entity` (
  `id` varchar(255) NOT NULL,
  `created` datetime(6) DEFAULT NULL,
  `credit_id` varchar(255) DEFAULT NULL,
  `payment_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `payment_entity`;
CREATE TABLE `payment_entity` (
  `id` varchar(255) NOT NULL,
  `amount` int NOT NULL,
  `created` datetime(6) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_2c68eb8ba0wblbsif3oi0cnp2` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `credit_request_entity`;
CREATE TABLE `credit_request_entity` (
  `id` varchar(255) NOT NULL,
  `bank_id` varchar(255) DEFAULT NULL,
  `created` datetime(6) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_kqocuv0qn3m070x3oqc1n9k63` (`bank_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;