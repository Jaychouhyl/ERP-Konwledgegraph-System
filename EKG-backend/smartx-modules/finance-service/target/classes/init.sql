-- 初始化财务流水表
CREATE TABLE IF NOT EXISTS `fin_cash_flow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow_no` varchar(64) NOT NULL COMMENT '流水号',
  `flow_type` varchar(16) NOT NULL COMMENT '流向: IN收入/OUT支出',
  `amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '金额',
  `business_type` varchar(32) DEFAULT NULL COMMENT '业务类型',
  `business_id` bigint(20) DEFAULT NULL COMMENT '业务单据ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
