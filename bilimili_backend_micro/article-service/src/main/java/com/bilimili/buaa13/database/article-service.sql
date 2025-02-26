DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
                           `aid` int(11) NOT NULL AUTO_INCREMENT COMMENT '文章ID',
                           `uid` int(11) NOT NULL COMMENT '投稿用户ID',
                           `title` varchar(80) NOT NULL COMMENT '标题',
                           `cover_url` varchar(500) NOT NULL COMMENT '封面url',
                           `vid` varchar(500) NOT NULL COMMENT '关联的视频',
                           `content_url` varchar(500) NOT NULL COMMENT '文章内容url',
                           `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0审核中 1已过审 2未通过 3已删除',
                           PRIMARY KEY (`aid`),
                           UNIQUE KEY `aid` (`aid`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='文章表';

--
-- Table structure for table `article_stats`
--

DROP TABLE IF EXISTS `article_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_status` (
                                  `aid` int(11) NOT NULL COMMENT '专栏ID',
                                  `view` int(11) NOT NULL DEFAULT '0' COMMENT '观看量',
                                  `good` int(11) NOT NULL DEFAULT '0' COMMENT '点赞数',
                                  `bad` int(11) NOT NULL DEFAULT '0' COMMENT '点踩数',
                                  `coin` int(11) NOT NULL DEFAULT '0' COMMENT '投币数',
                                  `collect` int(11) NOT NULL DEFAULT '0' COMMENT '收藏数',
                                  `share` int(11) NOT NULL DEFAULT '0' COMMENT '分享数',
                                  `critique` int(11) DEFAULT '0' COMMENT '评论数量统计',
                                  PRIMARY KEY (`aid`),
                                  UNIQUE KEY `vid` (`aid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='专栏数据统计表';
/*!40101 SET character_set_client = @saved_cs_client */;