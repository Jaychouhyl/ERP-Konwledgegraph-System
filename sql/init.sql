-- ========================================================== 
-- 1. 系统与权限模块 (auth-service) 
-- 对应功能点(1) 和 (9) 
-- ========================================================== 

-- 用户表 
CREATE TABLE `sys_user` ( 
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID', 
  `username` varchar(50) NOT NULL COMMENT '登录账号', 
  `password` varchar(100) NOT NULL COMMENT '密码(加密)', 
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名', 
  `role_id` bigint DEFAULT NULL COMMENT '角色ID', 
  `status` tinyint(1) DEFAULT '1' COMMENT '状态(1正常 0停用)', 
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP, 
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识', 
  PRIMARY KEY (`id`), 
  UNIQUE KEY `uk_username` (`username`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表'; 

-- 供应商信息表 (如：TSMC, LG Chem, Samsung) 
CREATE TABLE `base_supplier` ( 
  `id` bigint NOT NULL AUTO_INCREMENT, 
  `supplier_name` varchar(100) NOT NULL COMMENT '供应商名称', 
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人', 
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话', 
  `risk_level` varchar(20) DEFAULT 'LOW' COMMENT '风险等级(HIGH/MEDIUM/LOW)', 
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP, 
  `is_deleted` tinyint(1) DEFAULT '0', 
  PRIMARY KEY (`id`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商信息表'; 

-- 客户信息表 
CREATE TABLE `base_customer` ( 
  `id` bigint NOT NULL AUTO_INCREMENT, 
  `customer_name` varchar(100) NOT NULL COMMENT '客户名称', 
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话', 
  `address` varchar(200) DEFAULT NULL COMMENT '收货地址', 
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP, 
  `is_deleted` tinyint(1) DEFAULT '0', 
  PRIMARY KEY (`id`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户信息表'; 

-- ========================================================== 
-- 2. 核心供应链模块 (scm-service) 
-- 对应功能点(2)(4)(7) 以及 SmartX-1 模板中的原材料 
-- ========================================================== 

-- 物料基础信息表 (如：硅晶圆、锂离子单元、OLED面板) 
CREATE TABLE `base_material` ( 
  `id` bigint NOT NULL AUTO_INCREMENT, 
  `material_code` varchar(50) NOT NULL COMMENT '物料编码', 
  `material_name` varchar(100) NOT NULL COMMENT '物料名称', 
  `material_type` varchar(20) NOT NULL COMMENT '物料类型(RAW:原材料, FINISHED:成品)', 
  `unit` varchar(20) DEFAULT '个' COMMENT '计量单位(片/单元/套)', 
  `standard_cost` decimal(10,2) DEFAULT NULL COMMENT '标准成本(美元)', 
  `description` varchar(255) DEFAULT NULL COMMENT '物料描述(如: 纯度>99%)', 
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP, 
  PRIMARY KEY (`id`), 
  UNIQUE KEY `uk_material_code` (`material_code`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料基础信息表'; 

-- 采购申请单主表 
CREATE TABLE `scm_purchase_order` ( 
  `id` bigint NOT NULL AUTO_INCREMENT, 
  `order_no` varchar(50) NOT NULL COMMENT '采购单号', 
  `supplier_id` bigint NOT NULL COMMENT '供应商ID', 
  `total_amount` decimal(10,2) DEFAULT '0.00' COMMENT '总金额', 
  `audit_status` varchar(20) DEFAULT 'PENDING' COMMENT '智能审核状态(PENDING待审/PASS通过/REJECT驳回)', 
  `audit_suggestion` text COMMENT 'RAG智能审核建议', 
  `create_by` bigint DEFAULT NULL COMMENT '申请人ID', 
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP, 
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
  PRIMARY KEY (`id`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购申请单主表'; 

-- 采购申请单明细表 
CREATE TABLE `scm_purchase_detail` ( 
  `id` bigint NOT NULL AUTO_INCREMENT, 
  `purchase_order_id` bigint NOT NULL COMMENT '关联主单ID', 
  `material_id` bigint NOT NULL COMMENT '物料ID(关联base_material)', 
  `quantity` int NOT NULL COMMENT '采购数量', 
  `unit_price` decimal(10,2) NOT NULL COMMENT '采购单价', 
  `total_price` decimal(10,2) NOT NULL COMMENT '明细总价', 
  PRIMARY KEY (`id`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单明细表'; 

-- 原材料/成品库存表 
CREATE TABLE `scm_inventory` ( 
  `id` bigint NOT NULL AUTO_INCREMENT, 
  `material_id` bigint NOT NULL COMMENT '物料ID(包含原材料和成品)', 
  `current_quantity` int NOT NULL DEFAULT '0' COMMENT '当前库存数量', 
  `safe_quantity` int DEFAULT '0' COMMENT '安全库存阈值(如: 日产量x3=6000)', 
  `warehouse_location` varchar(50) DEFAULT NULL COMMENT '库位编码', 
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
  PRIMARY KEY (`id`), 
  UNIQUE KEY `uk_material_id` (`material_id`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存状态表'; 

-- ========================================================== 
-- 3. 销售订单模块 (sales-service) 
-- 对应功能点(8) 
-- ========================================================== 

-- 销售订单主表 
CREATE TABLE `sls_sales_order` ( 
  `id` bigint NOT NULL AUTO_INCREMENT, 
  `order_no` varchar(50) NOT NULL COMMENT '销售单号', 
  `customer_id` bigint NOT NULL COMMENT '客户ID', 
  `total_amount` decimal(10,2) DEFAULT '0.00' COMMENT '订单总金额', 
  `status` varchar(20) DEFAULT 'CREATED' COMMENT '状态(CREATED已创建/SHIPPED已发货/COMPLETED已完成)', 
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP, 
  PRIMARY KEY (`id`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单表'; 

-- ========================================================== 
-- 4. 财务流水模块 (finance-service) 
-- 对应功能点(10) 
-- ========================================================== 

-- 资金流水表 
CREATE TABLE `fin_cash_flow` ( 
  `id` bigint NOT NULL AUTO_INCREMENT, 
  `flow_no` varchar(50) NOT NULL COMMENT '流水号', 
  `flow_type` varchar(10) NOT NULL COMMENT '流向(IN收入 / OUT支出)', 
  `amount` decimal(10,2) NOT NULL COMMENT '金额', 
  `business_type` varchar(20) NOT NULL COMMENT '业务类型(PURCHASE采购付款 / SALES销售收款)', 
  `business_id` bigint NOT NULL COMMENT '关联单据ID(采购单ID或销售单ID)', 
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP, 
  PRIMARY KEY (`id`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务资金流水表';
