SET NAMES utf8mb4;

-- 创建数据库（仅执行一次）
CREATE DATABASE IF NOT EXISTS `auth_db`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE `auth_db`;

-- ---------- products 表 ----------
DROP TABLE IF EXISTS `products`;

CREATE TABLE `products` (
    `id`   bigint      NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 插入测试数据
LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` (`name`) VALUES
    ('iPhone 15'),
    ('MacBook Pro'),
    ('iPad Air'),
    ('Test'),
    ('Test'),
    ('Test'),
    ('Test'),
    ('Test');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

-- ---------- users 表 ----------
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
    `id`       bigint NOT NULL AUTO_INCREMENT,
    `username` varchar(50)  NOT NULL,
    `password` varchar(255) DEFAULT NULL,
    `role`     enum('USER','EDITOR','PRODUCT_ADMIN') DEFAULT 'USER',
    `enabled`  tinyint(1) DEFAULT '1',
    `ldap_dn`  varchar(255) DEFAULT NULL COMMENT 'LDAP Distinguished Name',
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`),
    UNIQUE KEY `ldap_dn` (`ldap_dn`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 插入初始用户（密码已用 BCrypt 加密）
LOCK TABLES `users` WRITE;
INSERT INTO `users` (`username`, `password`, `role`, `enabled`, `ldap_dn`) VALUES
    ('user_1',   '$2a$10$pW9Ycz74Qd6ZnIYns9OUT.KWT7iwdMOWMJydzScPVvdsaNYv/ibZq', 'USER',           1, NULL),
    ('editor_1', '$2a$10$wOUiSGOjlKmg2VKBYb3.5u/6Y.LEwlAdiN/aCJbqHwiknbqUiKMya', 'EDITOR',         1, NULL),
    ('adm_1',    '$2a$10$F3Kg.YAqO0rno2P8OS6Fne.GFtlYfcpet0o571gA5jubL7rOg0Ii.', 'PRODUCT_ADMIN',  1, NULL);
UNLOCK TABLES;