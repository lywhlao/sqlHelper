

DROP TABLE IF EXISTS `tb_yq_user`;
CREATE TABLE `tb_yq_user` ( 
		`id` bigInt(20) NOT NULL AUTO_INCREMENT COMMENT '主键' ,
		`uid` varchar(64)  DEFAULT '' COMMENT '账号',
		`uuid` varchar(64)  DEFAULT '' COMMENT '设备号',
		`ip` varchar(64)  DEFAULT '' COMMENT '备注',
		`mobile` varchar(64)  DEFAULT '' COMMENT '备注',
		`amount` bigInt(20)  DEFAULT '0' COMMENT '备注',
		`useRedPacket` tinyInt(1)  DEFAULT '0' COMMENT '备注',
		`isBinding` tinyInt(1)  DEFAULT '0' COMMENT '备注',
		`isBefore` tinyInt(1)  DEFAULT '0' COMMENT '备注',
		`isWinner` tinyInt(1)  DEFAULT '0' COMMENT '备注',
		`isPrized` tinyInt(1)  DEFAULT '0' COMMENT '备注',
		`insertTime` bigInt(20)  DEFAULT '0' COMMENT '备注',
		PRIMARY KEY (`id`,`uid`),
		KEY `IDX_KEY` (`useRedPacket`),
		UNIQUE KEY `IDX_YQ_USER_SECO` (`mobile`,`amount`),
		UNIQUE KEY `IDX_YQ_USER_UID` (`uid`,`uuid`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';