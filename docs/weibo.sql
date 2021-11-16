-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        8.0.27 - MySQL Community Server - GPL
-- 服务器操作系统:                      Linux
-- HeidiSQL 版本:                  11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- 导出 weibo 的数据库结构
DROP DATABASE IF EXISTS `weibo`;
CREATE DATABASE IF NOT EXISTS `weibo` /*!40100 DEFAULT CHARACTER SET utf8 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `weibo`;

-- 导出  表 weibo.feed 结构
DROP TABLE IF EXISTS `feed`;
CREATE TABLE IF NOT EXISTS `feed` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime NOT NULL DEFAULT '2021-01-01 01:01:01',
  `type` int NOT NULL DEFAULT '0' COMMENT '为1代表关联类型为话题',
  `link_id` int NOT NULL DEFAULT '0',
  `user_id` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `type_link_id_user_id` (`type`,`link_id`,`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 正在导出表  weibo.feed 的数据：~12 rows (大约)
DELETE FROM `feed`;
/*!40000 ALTER TABLE `feed` DISABLE KEYS */;
INSERT INTO `feed` (`id`, `create_time`, `type`, `link_id`, `user_id`) VALUES
	(1, '2021-01-01 01:01:01', 1, 3, 1),
	(2, '2021-11-11 10:23:55', 1, 4, 1),
	(3, '2021-11-11 10:27:42', 1, 5, 1),
	(4, '2021-11-11 10:39:51', 1, 6, 1),
	(5, '2021-11-13 11:24:07', 1, 22, 1),
	(6, '2021-11-13 21:32:48', 1, 23, 1),
	(7, '2021-11-15 18:37:17', 1, 32, 1),
	(8, '2021-11-15 19:08:11', 1, 33, 1),
	(9, '2021-11-15 20:50:49', 1, 34, 1),
	(14, '2021-11-15 21:26:36', 1, 39, 1),
	(15, '2021-11-15 21:39:30', 1, 40, 1),
	(16, '2021-11-16 00:17:49', 1, 41, 1);
/*!40000 ALTER TABLE `feed` ENABLE KEYS */;

-- 导出  表 weibo.topic 结构
DROP TABLE IF EXISTS `topic`;
CREATE TABLE IF NOT EXISTS `topic` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(50) NOT NULL DEFAULT '',
  `description` varchar(100) NOT NULL DEFAULT '',
  `read_count` int NOT NULL DEFAULT '0',
  `follow_count` int NOT NULL DEFAULT '0',
  `discuss_count` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 正在导出表  weibo.topic 的数据：~2 rows (大约)
DELETE FROM `topic`;
/*!40000 ALTER TABLE `topic` DISABLE KEYS */;
INSERT INTO `topic` (`id`, `title`, `description`, `read_count`, `follow_count`, `discuss_count`) VALUES
	(1, '小猪佩奇', '讲述了佩奇与家人的故事', 2, 1, 4),
	(2, '小猪佩奇2', '讲述了佩奇与家人的故事2', 2, 1, 2);
/*!40000 ALTER TABLE `topic` ENABLE KEYS */;

-- 导出  表 weibo.user_info 结构
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE IF NOT EXISTS `user_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `login_name` varchar(50) NOT NULL DEFAULT '',
  `password` varchar(50) NOT NULL DEFAULT '',
  `nick_name` varchar(50) NOT NULL DEFAULT '',
  `gender` tinyint NOT NULL DEFAULT '0',
  `age` int NOT NULL DEFAULT '0',
  `avatar_url` varchar(100) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 正在导出表  weibo.user_info 的数据：~2 rows (大约)
DELETE FROM `user_info`;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
INSERT INTO `user_info` (`id`, `login_name`, `password`, `nick_name`, `gender`, `age`, `avatar_url`) VALUES
	(1, 'qwe', '202cb962ac59075b964b07152d234b70', 'ewq', 21, 1, 'https://img2.baidu.com/it/u=2629761637,447021566&fm=26&fmt=auto'),
	(2, '123', '202cb962ac59075b964b07152d234b70', '123', 123, 1, '123');
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;

-- 导出  表 weibo.user_topic_follow 结构
DROP TABLE IF EXISTS `user_topic_follow`;
CREATE TABLE IF NOT EXISTS `user_topic_follow` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL DEFAULT '0',
  `topic_id` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id_topic_id` (`user_id`,`topic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 正在导出表  weibo.user_topic_follow 的数据：~2 rows (大约)
DELETE FROM `user_topic_follow`;
/*!40000 ALTER TABLE `user_topic_follow` DISABLE KEYS */;
INSERT INTO `user_topic_follow` (`id`, `user_id`, `topic_id`) VALUES
	(1, 1, 1),
	(2, 1, 2);
/*!40000 ALTER TABLE `user_topic_follow` ENABLE KEYS */;

-- 导出  表 weibo.user_topic_read 结构
DROP TABLE IF EXISTS `user_topic_read`;
CREATE TABLE IF NOT EXISTS `user_topic_read` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL DEFAULT '0',
  `topic_id` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id_topic_id` (`user_id`,`topic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 正在导出表  weibo.user_topic_read 的数据：~4 rows (大约)
DELETE FROM `user_topic_read`;
/*!40000 ALTER TABLE `user_topic_read` DISABLE KEYS */;
INSERT INTO `user_topic_read` (`id`, `user_id`, `topic_id`) VALUES
	(1, 1, 1),
	(2, 1, 2),
	(26, 2, 1),
	(17, 2, 2);
/*!40000 ALTER TABLE `user_topic_read` ENABLE KEYS */;

-- 导出  表 weibo.user_weibo_like 结构
DROP TABLE IF EXISTS `user_weibo_like`;
CREATE TABLE IF NOT EXISTS `user_weibo_like` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL DEFAULT '0',
  `weibo_id` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id_weibo_id` (`user_id`,`weibo_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 正在导出表  weibo.user_weibo_like 的数据：~4 rows (大约)
DELETE FROM `user_weibo_like`;
/*!40000 ALTER TABLE `user_weibo_like` DISABLE KEYS */;
INSERT INTO `user_weibo_like` (`id`, `user_id`, `weibo_id`) VALUES
	(2, 1, 1),
	(1, 1, 2),
	(14, 1, 5),
	(12, 1, 39);
/*!40000 ALTER TABLE `user_weibo_like` ENABLE KEYS */;

-- 导出  表 weibo.weibo 结构
DROP TABLE IF EXISTS `weibo`;
CREATE TABLE IF NOT EXISTS `weibo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `content` varchar(300) NOT NULL DEFAULT '',
  `img_url` varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `like_count` int NOT NULL DEFAULT '0',
  `remark_count` int NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT '2021-01-01 11:11:11',
  `user_id` int NOT NULL DEFAULT '0',
  `topic_id` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- 正在导出表  weibo.weibo 的数据：~13 rows (大约)
DELETE FROM `weibo`;
/*!40000 ALTER TABLE `weibo` DISABLE KEYS */;
INSERT INTO `weibo` (`id`, `content`, `img_url`, `like_count`, `remark_count`, `create_time`, `user_id`, `topic_id`) VALUES
	(1, '小猪佩奇的一天', 'https://gss3.bdstatic.com/84oSdTum2Q5BphGlnYG/timg?wapp&quality=80&size=b150_150&subsize=20480&cut_x', 1, 0, '2021-11-08 20:14:53', 1, 1),
	(2, '小猪佩奇的一天', 'https://iconfont.alicdn.com/t/9b1fe7e7-2cb9-440f-afe3-b624ae708279.png', 1, 0, '2021-11-08 20:20:53', 1, 2),
	(3, '20211110', 'https://iconfont.alicdn.com/t/2aacf902-2736-4b31-bdac-a30fef105042.png', 0, 0, '2021-11-10 23:29:24', 2, 2),
	(4, '用户123看了一部动画片', 'https://iconfont.alicdn.com/t/26ca7ba2-9ce5-4693-802f-8395ee70a677.png', 0, 0, '2021-11-11 10:23:55', 2, 1),
	(5, '123', 'https://iconfont.alicdn.com/t/26ca7ba2-9ce5-4693-802f-8395ee70a677.png', 1, 0, '2021-11-11 10:27:42', 2, 1),
	(10, '1111晚上qwe发了微博', 'https://iconfont.alicdn.com/t/51b0b63e-b0d7-4092-a3a4-24e6d46ae165.png', 0, 0, '2021-11-11 21:32:03', 1, 1),
	(15, '今天是2021年11月12日', 'https://iconfont.alicdn.com/t/1c6cada4-cb47-461e-a556-71af3f14bb5a.png', 0, 0, '2021-11-12 09:32:59', 1, 2),
	(22, '21年1113号发了微博', 'https://iconfont.alicdn.com/t/1c6cada4-cb47-461e-a556-71af3f14bb5a.png', 0, 0, '2021-11-13 11:24:07', 1, 1),
	(23, '小猪佩奇来开心的一天', 'https://iconfont.alicdn.com/t/1c6cada4-cb47-461e-a556-71af3f14bb5a.png', 0, 0, '2021-11-13 21:32:48', 1, 1),
	(33, '21年11月15号的微博', 'https://iconfont.alicdn.com/t/1c6cada4-cb47-461e-a556-71af3f14bb5a.png', 0, 0, '2021-11-15 19:08:11', 1, 1),
	(39, 'qwe', 'https://iconfont.alicdn.com/t/1c6cada4-cb47-461e-a556-71af3f14bb5a.png', 1, 0, '2021-11-15 21:26:36', 1, 1),
	(40, '额2额', '2312312', 0, 0, '2021-11-15 21:39:30', 1, 1),
	(41, '123', '123', 0, 0, '2021-11-16 00:17:49', 1, 1);
/*!40000 ALTER TABLE `weibo` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
