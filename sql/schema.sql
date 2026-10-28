-- =====================================================================
-- 第 7 题：真正的登录注册 —— 建表脚本
-- 数据库：MySQL 8.4
-- 用法（项目根目录）：
--   mysql -h 127.0.0.1 -P 3307 -u <用户名> -p < sql/schema.sql
-- 说明：
--   1. 密码列存的是带算法前缀的散列，例如 {bcrypt}$2a$10$...
--      前缀由 DelegatingPasswordEncoder 写入，长度需兼容 pbkdf2 等较长算法。
--   2. username 的唯一性交给数据库唯一索引兜底，避免并发下“先查再插”的竞态。
--   3. 本题是单表，不需要外键与额外索引。
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `backend2_07`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `backend2_07`;

CREATE TABLE IF NOT EXISTS `users` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
    `username`   VARCHAR(32)  NOT NULL                COMMENT '登录用户名',
    `password`   VARCHAR(255) NOT NULL                COMMENT '密码散列（含 {算法} 前缀）',
    `role`       VARCHAR(16)  NOT NULL DEFAULT 'USER' COMMENT '角色：USER / ADMIN',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_users_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '用户表';

-- 内置管理员账号（仅用于本地验收）：testAdmin / admin123
-- 重跑脚本会把该账号的密码和角色重置为下面这组，请勿用于生产环境
INSERT IGNORE INTO users (username, password, role)
VALUES ('testAdmin', '{bcrypt}$2a$10$v4nA7TFvsDjxDkFterahZe4SB2ny/Rz5/PDkmKEc/TJDUTujvvrva', 'ADMIN');

UPDATE users
SET password = '{bcrypt}$2a$10$v4nA7TFvsDjxDkFterahZe4SB2ny/Rz5/PDkmKEc/TJDUTujvvrva',
    role     = 'ADMIN'
WHERE username = 'testAdmin';