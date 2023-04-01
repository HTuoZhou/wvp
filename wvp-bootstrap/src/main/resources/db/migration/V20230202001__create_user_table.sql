CREATE TABLE wvpDb.`user` (
        id INT auto_increment NOT NULL COMMENT '主键，自增',
        name varchar(100) NOT NULL COMMENT '姓名',
        nickname varchar(100) NOT NULL COMMENT '昵称',
        sex INT NOT NULL COMMENT '性别（0、男 1、女）',
        age INT NOT NULL COMMENT '年龄',
        create_time DATETIME NOT NULL COMMENT '创建时间',
        update_time DATETIME NOT NULL COMMENT '更新时间',
        deleted INT DEFAULT 0 NOT NULL COMMENT '删除标识（0、逻辑删除 1、物理删除）',
        version INT DEFAULT 0 NOT NULL COMMENT '版本号',
        PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8mb4
COLLATE=utf8mb4_general_ci
COMMENT='用户表';
