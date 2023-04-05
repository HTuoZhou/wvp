-- wvpDb.`user` definition

CREATE TABLE `user` (
                        `id` int NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
                        `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
                        `nickname` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '昵称',
                        `sex` int NOT NULL COMMENT '性别（0、男 1、女）',
                        `age` int NOT NULL COMMENT '年龄',
                        `create_time` datetime NOT NULL COMMENT '创建时间',
                        `update_time` datetime NOT NULL COMMENT '更新时间',
                        `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标识（0、逻辑删除 1、物理删除）',
                        `version` int NOT NULL DEFAULT '0' COMMENT '版本号',
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- wvp.zlm_server definition

CREATE TABLE `zlm_server` (
                                `id` int NOT NULL AUTO_INCREMENT COMMENT '主键，自增',
                                `unique_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'zlm服务器唯一id',
                                `secret` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'zlm服务器secret',
                                `ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'zlm服务器ip',
                                `stream_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '返回流地址ip',
                                `sdp_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                `hook_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                `http_port` int NOT NULL,
                                `http_ssl_port` int NOT NULL,
                                `rtsp_port` int NOT NULL,
                                `rtsp_ssl_port` int NOT NULL,
                                `rtmp_port` int NOT NULL,
                                `rtmp_ssl_port` int NOT NULL,
                                `rtp_enable` int NOT NULL,
                                `rtp_port_range` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
                                `rtp_proxy_port` int NOT NULL,
                                `hook_alive_interval` int NOT NULL COMMENT 'zlm hook 心跳间隔时间（秒）',
                                `status` int NOT NULL DEFAULT '1' NULL COMMENT '0、离线 1、在线',
                                `default_server` int NOT NULL COMMENT '是否默认服务器（0、否 1、是）',
                                `create_time` datetime NOT NULL COMMENT '创建时间',
                                `update_time` datetime NOT NULL COMMENT '更新时间',
                                `deleted` int NOT NULL DEFAULT '0' COMMENT '删除标识（0、逻辑删除 1、物理删除）',
                                `version` int NOT NULL DEFAULT '0' COMMENT '版本号',
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='zlm服务表';