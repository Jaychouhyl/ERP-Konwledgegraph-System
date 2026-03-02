-- 初始化物料表
CREATE TABLE IF NOT EXISTS `base_material` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `material_code` varchar(64) DEFAULT NULL COMMENT '物料编码',
  `material_name` varchar(128) DEFAULT NULL COMMENT '物料名称',
  `material_type` varchar(32) DEFAULT NULL COMMENT '类型',
  `unit` varchar(32) DEFAULT NULL COMMENT '单位',
  `standard_cost` decimal(10,2) DEFAULT NULL COMMENT '标准成本',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始化库存表
CREATE TABLE IF NOT EXISTS `scm_inventory` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `material_id` bigint(20) NOT NULL COMMENT '物料ID',
  `current_quantity` int(11) DEFAULT '0' COMMENT '当前库存',
  `safe_quantity` int(11) DEFAULT '0' COMMENT '安全库存',
  `warehouse_location` varchar(64) DEFAULT NULL COMMENT '库位',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 1. 创建采购订单主表
CREATE TABLE IF NOT EXISTS `scm_purchase_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) NOT NULL COMMENT '采购单号',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商ID',
  `total_amount` decimal(10,2) DEFAULT '0.00' COMMENT '总金额',
  `audit_status` varchar(20) DEFAULT 'PENDING' COMMENT '审核状态',
  `audit_suggestion` varchar(255) DEFAULT NULL COMMENT '审核意见',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 创建采购订单明细表
CREATE TABLE IF NOT EXISTS `scm_purchase_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `purchase_order_id` bigint(20) NOT NULL COMMENT '主单ID',
  `material_id` bigint(20) NOT NULL COMMENT '物料ID',
  `quantity` int(11) DEFAULT '0' COMMENT '数量',
  `unit_price` decimal(10,2) DEFAULT '0.00' COMMENT '单价',
  `total_price` decimal(10,2) DEFAULT '0.00' COMMENT '明细总价',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO base_material (id, material_code, material_name, material_type, unit, standard_cost) VALUES (1, 'PHONE-01', 'SmartX-1 智能手机', 'FINISHED', '台', 2999.00);
INSERT INTO scm_inventory (material_id, current_quantity, safe_quantity) VALUES (1, 100, 20);
