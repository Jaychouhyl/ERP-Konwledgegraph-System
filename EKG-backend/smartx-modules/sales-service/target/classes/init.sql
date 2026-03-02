-- 1. 创建销售订单主表
CREATE TABLE IF NOT EXISTS `sls_sales_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) NOT NULL COMMENT '销售单号',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
  `total_amount` decimal(10,2) DEFAULT '0.00' COMMENT '总金额',
  `status` varchar(20) DEFAULT 'CREATED' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 创建销售订单明细表
CREATE TABLE IF NOT EXISTS `sls_sales_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `sales_order_id` bigint(20) NOT NULL COMMENT '主单ID',
  `material_id` bigint(20) NOT NULL COMMENT '物料ID',
  `quantity` int(11) DEFAULT '0' COMMENT '数量',
  `unit_price` decimal(10,2) DEFAULT '0.00' COMMENT '单价',
  `total_price` decimal(10,2) DEFAULT '0.00' COMMENT '明细总价',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
