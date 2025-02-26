--
-- Table structure for table `chat`
--

DROP TABLE IF EXISTS `chat`;

CREATE TABLE `chat` (
                        `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '唯一主键',
                        `post_id` int(11) NOT NULL COMMENT '对象UID',
                        `accept_id` int(11) NOT NULL COMMENT '用户UID',
                        `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否移除聊天 0否 1是',
                        `unread_num` int(11) NOT NULL DEFAULT '0' COMMENT '消息未读数量',
                        `latest_time` datetime NOT NULL COMMENT '最近接收消息的时间或最近打开聊天窗口的时间',
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `from_to` (post_id,accept_id),
                        UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='聊天表';





--
-- Table structure for table `chat_detailed`
--

DROP TABLE IF EXISTS `chat_detailed`;

CREATE TABLE `chat_detailed` (
                                 `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '唯一主键',
                                 `post_id` int(11) NOT NULL COMMENT '消息发送者',
                                 `accept_id` int(11) NOT NULL COMMENT '消息接收者',
                                 `content` varchar(500) NOT NULL COMMENT '消息内容',
                                 `post_del` tinyint(4) NOT NULL DEFAULT '0' COMMENT '发送者是否删除',
                                 `accept_del` tinyint(4) NOT NULL DEFAULT '0' COMMENT '接受者是否删除',
                                 `withdraw` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否撤回',
                                 `time` datetime NOT NULL COMMENT '消息发送时间',
                                 `status` int(4) not null default 1 comment '消息状态',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='聊天记录表';








--
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;

CREATE TABLE `favorite` (
                            `fid` int(11) NOT NULL AUTO_INCREMENT COMMENT '收藏夹ID',
                            `uid` int(11) NOT NULL COMMENT '所属用户ID',
                            `type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '收藏夹类型 1默认收藏夹 2用户创建',
                            `visible` tinyint(4) NOT NULL DEFAULT '1' COMMENT '对外开放 0隐藏 1公开',
                            `cover` varchar(255) DEFAULT NULL COMMENT '收藏夹封面',
                            `title` varchar(20) NOT NULL COMMENT '标题',
                            `description` varchar(200) DEFAULT '' COMMENT '简介',
                            `count` int(11) NOT NULL DEFAULT '0' COMMENT '收藏夹中视频数量',
                            `is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0否 1已删除',
                            PRIMARY KEY (`fid`),
                            UNIQUE KEY `fid` (`fid`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='收藏夹';


--
-- Table structure for table `favorite_video`
--

DROP TABLE IF EXISTS `favorite_video`;

CREATE TABLE `favorite_video` (
                                  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
                                  `vid` int(11) NOT NULL COMMENT '视频ID',
                                  `fid` int(11) NOT NULL COMMENT '收藏夹ID',
                                  `time` datetime NOT NULL COMMENT '收藏时间',
                                  `is_remove` tinyint(4) DEFAULT NULL COMMENT '是否移除 null否 1已移除',
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `id` (`id`),
                                  UNIQUE KEY `vid_fid__index` (`vid`,`fid`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='视频收藏夹关系表';






Drop table if exists follow;

CREATE TABLE `follow` (
                          `uid_follow` int(11) NOT NULL COMMENT 'up',
                          `uid_fans` varchar(2000) NOT NULL COMMENT '粉丝',
                          `visible` int(11) NOT NULL DEFAULT '0' COMMENT '是否可见'
) ENGINE=InnoDB AUTO_INCREMENT=5000 DEFAULT CHARSET=utf8mb4 COMMENT='关注和粉丝表';




DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
                        `uid` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                        `account` varchar(50) NOT NULL COMMENT '用户账号',
                        `password` varchar(255) NOT NULL COMMENT '用户密码',
                        `nickname` varchar(32) NOT NULL COMMENT '用户昵称',
                        head_portrait varchar(500) DEFAULT NULL COMMENT '用户头像url',
                        `background` varchar(500) DEFAULT NULL COMMENT '主页背景图url',
                        `gender` tinyint(4) NOT NULL DEFAULT '2' COMMENT '性别 0女 1男 2未知',
                        `description` varchar(100) DEFAULT NULL COMMENT '个性签名',
                        experience int(11) NOT NULL DEFAULT '0' COMMENT '经验值',
                        `coin` double NOT NULL DEFAULT '0' COMMENT '硬币数',
                        `state` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0正常 1封禁 2注销',
                        `role` tinyint(4) NOT NULL DEFAULT '0' COMMENT '角色类型 0普通用户 1管理员 2超级管理员',
                        `create_date` datetime NOT NULL COMMENT '创建时间',
                        `delete_date` datetime DEFAULT NULL COMMENT '注销时间',
                        PRIMARY KEY (`uid`),
                        UNIQUE KEY `uid` (`uid`),
                        UNIQUE KEY `account` (`account`),
                        UNIQUE KEY `nickname` (`nickname`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='用户表';





-- Dump completed on 2024-04-16 17:42:34

DROP TABLE IF EXISTS `user_record_string`;

CREATE TABLE `user_record_string` (
                                      `uid` int(11) NOT NULL COMMENT '用户id',
                                      `play_json` varchar(2000) NOT NULL COMMENT '用户近七天播放量',
                                      `play_new` int(11) NOT NULL DEFAULT '0' COMMENT '今天的新播放量',
                                      `play_old` int(11) DEFAULT '0' COMMENT '昨天老播放量',
                                      `love_json` varchar(2000) NOT NULL COMMENT '用户近七天点赞量',
                                      `love_new` int(11) NOT NULL DEFAULT '0' COMMENT '今天的新点赞量',
                                      `love_old` int(11) DEFAULT '0' COMMENT '昨天老点赞量',
                                      `collect_json` varchar(2000) NOT NULL COMMENT '用户近七天收藏量',
                                      `collect_new` int(11) NOT NULL DEFAULT '0' COMMENT '今天的新收藏量',
                                      `collect_old` int(11) DEFAULT '0' COMMENT '昨天老收藏量',
                                      `fan_json` varchar(2000) NOT NULL COMMENT '用户近七天粉丝量',
                                      `fans_new` int(11) NOT NULL DEFAULT '0' COMMENT '今天的新粉丝量',
                                      `fans_old` int(11) DEFAULT '0' COMMENT '昨天老粉丝量',
                                      PRIMARY KEY (`uid`),
                                      UNIQUE KEY `id` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=5000 DEFAULT CHARSET=utf8mb4 COMMENT='创作周报表';







--
-- Table structure for table `user_video`
--

DROP TABLE IF EXISTS `user_video`;

CREATE TABLE `user_video` (
                              `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '唯一标识',
                              `uid` int(11) NOT NULL COMMENT '观看视频的用户UID',
                              `vid` int(11) NOT NULL COMMENT '视频ID',
                              `play` int(11) NOT NULL DEFAULT '0' COMMENT '播放次数',
                              `love` tinyint(4) NOT NULL DEFAULT '0' COMMENT '点赞 0没赞 1已点赞',
                              `unlove` tinyint(4) NOT NULL DEFAULT '0' COMMENT '不喜欢 0没点 1已不喜欢',
                              `coin` tinyint(4) NOT NULL DEFAULT '0' COMMENT '投币数 0-2 默认0',
                              `collect` tinyint(4) NOT NULL DEFAULT '0' COMMENT '收藏 0没收藏 1已收藏',
                              `play_time` datetime NOT NULL COMMENT '最近播放时间',
                              `love_time` datetime DEFAULT NULL COMMENT '点赞时间',
                              `coin_time` datetime DEFAULT NULL COMMENT '投币时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `id` (`id`),
                              UNIQUE KEY `uid_vid__index` (`uid`,`vid`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='用户视频关联表';




--
-- Table structure for table `message_unread`
--

DROP TABLE IF EXISTS `message_unread`;

CREATE TABLE message_unread (
                                `uid` int(11) NOT NULL COMMENT '用户ID',
                                `reply` int(11) NOT NULL DEFAULT '0' COMMENT '回复我的',
                                `at_num` int(11) NOT NULL DEFAULT '0' COMMENT '@我的',
                                `up_vote` int(11) NOT NULL DEFAULT '0' COMMENT '收到的赞',
                                `system_message` int(11) NOT NULL DEFAULT '0' COMMENT '系统通知',
                                `message` int(11) NOT NULL DEFAULT '0' COMMENT '我的消息',
                                `dynamic` int(11) NOT NULL DEFAULT '0' COMMENT '动态',
                                PRIMARY KEY (`uid`),
                                UNIQUE KEY `uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息未读数';





DROP TABLE IF EXISTS `postComment`;

CREATE TABLE `postComment` (
                               `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '评论主id',
                               `pid` int(11) NOT NULL COMMENT '评论的动态id',
                               `uid` int(11) DEFAULT NULL COMMENT '发送者id',
                               `root_id` int(11) NOT NULL DEFAULT '0' COMMENT '根节点评论的id,如果为0表示为根节点',
                               `parent_id` int(11) NOT NULL COMMENT '被回复的评论id，只有root_id为0时才允许为0，表示根评论',
                               `to_user_id` int(11) NOT NULL COMMENT '回复目标用户id',
                               `content` varchar(2000) NOT NULL COMMENT '评论内容',
                               `love` int(11) NOT NULL DEFAULT '0' COMMENT '该条评论的点赞数',
                               `bad` int(11) DEFAULT '0' COMMENT '不喜欢的数量',
                               `create_time` datetime NOT NULL COMMENT '创建时间',
                               `is_top` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否置顶 0普通 1置顶',
                               `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '软删除 0未删除 1已删除',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='评论表';



DROP TABLE IF EXISTS `post`;

CREATE TABLE `post` (
                        `pid` int(11) NOT NULL AUTO_INCREMENT COMMENT '动态主id',
                        `uid` int(11) NOT NULL COMMENT '发送者的id',
                        `content` varchar(2000) NOT NULL COMMENT '动态内容',
                        `love` int(11) NOT NULL DEFAULT '0' COMMENT '该条评论的点赞数',
                        `unlove` int(11) DEFAULT '0' COMMENT '不喜欢的数量',
                        `create_time` datetime NOT NULL COMMENT '创建时间',
                        PRIMARY KEY (`pid`),
                        UNIQUE KEY `id` (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=5000 DEFAULT CHARSET=utf8mb4 COMMENT='参照视频实现逻辑的动态表';