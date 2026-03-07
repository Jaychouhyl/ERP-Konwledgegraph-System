-- ==========================================================
-- SmartX ERP 数据库全量初始化脚本 (企业级 RBAC 架构 + 操作人审计版)
-- ==========================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 0. 销毁旧表 (避免冲突)
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
DROP TABLE IF EXISTS `sys_role`;
DROP TABLE IF EXISTS `sys_menu`;
DROP TABLE IF EXISTS `sys_user_role`;
DROP TABLE IF EXISTS `sys_role_menu`;
DROP TABLE IF EXISTS `base_supplier`;
DROP TABLE IF EXISTS `base_customer`;
DROP TABLE IF EXISTS `base_material`;
DROP TABLE IF EXISTS `scm_purchase_order`;
DROP TABLE IF EXISTS `scm_purchase_detail`;
DROP TABLE IF EXISTS `scm_inventory`;
DROP TABLE IF EXISTS `sls_sales_order`;
DROP TABLE IF EXISTS `sls_sales_detail`;
DROP TABLE IF EXISTS `fin_cash_flow`;

-- ==========================================================
-- 1. 系统与权限模块 (auth-service)
-- ==========================================================

CREATE TABLE `sys_user` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `username` varchar(50) NOT NULL COMMENT '登录账号',
                            `password` varchar(100) NOT NULL COMMENT '密码',
                            `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
                            `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
                            `email` varchar(100) DEFAULT NULL COMMENT '电子邮箱',
                            `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL(为空时前端用首字母代替)',
                            `department` varchar(50) DEFAULT NULL COMMENT '所属部门',
                            `status` tinyint(1) DEFAULT '1' COMMENT '状态(1正常 0停用)',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

CREATE TABLE `sys_role` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                            `role_name` varchar(30) NOT NULL COMMENT '角色名称',
                            `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串(如: admin, buyer)',
                            `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '角色状态（1正常 0停用）',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色信息表';

CREATE TABLE `sys_menu` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
                            `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
                            `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
                            `order_num` int DEFAULT '0' COMMENT '显示顺序',
                            `path` varchar(200) DEFAULT '' COMMENT '路由地址',
                            `component` varchar(255) DEFAULT NULL COMMENT '前端组件路径',
                            `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
                            `perms` varchar(100) DEFAULT NULL COMMENT '权限标识(如: scm:purchase:add)',
                            `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
                            `status` tinyint(1) DEFAULT '1' COMMENT '菜单状态（1正常 0停用）',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

CREATE TABLE `sys_user_role` (
                                 `user_id` bigint NOT NULL COMMENT '用户ID',
                                 `role_id` bigint NOT NULL COMMENT '角色ID',
                                 PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';

CREATE TABLE `sys_role_menu` (
                                 `role_id` bigint NOT NULL COMMENT '角色ID',
                                 `menu_id` bigint NOT NULL COMMENT '菜单ID',
                                 PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

CREATE TABLE `base_supplier` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `supplier_name` varchar(100) NOT NULL COMMENT '供应商名称',
                                 `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
                                 `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
                                 `risk_level` varchar(20) DEFAULT 'LOW' COMMENT '风险等级(HIGH/MEDIUM/LOW)',
                                  `address` varchar(200) DEFAULT NULL COMMENT '供应商地址',
                                  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                                 `create_by` bigint DEFAULT NULL COMMENT '🌟 创建人/操作人ID',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 `is_deleted` tinyint(1) DEFAULT '0',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商信息表';

CREATE TABLE `base_customer` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `customer_name` varchar(100) NOT NULL COMMENT '客户名称',
                                  `contact_person` varchar(50) DEFAULT NULL COMMENT '客户联系人',
                                 `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
                                 `address` varchar(200) DEFAULT NULL COMMENT '收货地址',
                                 `create_by` bigint DEFAULT NULL COMMENT '🌟 创建人/操作人ID',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 `is_deleted` tinyint(1) DEFAULT '0',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户信息表';

-- ==========================================================
-- 2. 核心供应链模块 (scm-service)
-- ==========================================================

CREATE TABLE `base_material` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `material_code` varchar(50) NOT NULL COMMENT '物料编码',
                                 `material_name` varchar(100) NOT NULL COMMENT '物料名称',
                                 `material_type` varchar(20) NOT NULL COMMENT '物料类型(RAW:原材料, FINISHED:成品)',
                                 `unit` varchar(20) DEFAULT '个' COMMENT '计量单位',
                                 `standard_cost` decimal(10,2) DEFAULT NULL COMMENT '标准成本(美元)',
                                 `description` varchar(255) DEFAULT NULL COMMENT '物料描述',
                                 `create_by` bigint DEFAULT NULL COMMENT '🌟 创建人/操作人ID',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_material_code` (`material_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物料基础信息表';

CREATE TABLE `scm_purchase_order` (
                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                      `order_no` varchar(50) NOT NULL COMMENT '采购单号',
                                      `supplier_id` bigint NOT NULL COMMENT '供应商ID',
                                      `total_amount` decimal(10,2) DEFAULT '0.00' COMMENT '总金额',
                                      `audit_status` varchar(20) DEFAULT 'PENDING' COMMENT '审核状态',
                                      `audit_suggestion` text COMMENT 'RAG智能审核建议',
                                      `create_by` bigint DEFAULT NULL COMMENT '申请人ID',
                                      `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                      `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购申请单主表';

CREATE TABLE `scm_purchase_detail` (
                                       `id` bigint NOT NULL AUTO_INCREMENT,
                                       `purchase_order_id` bigint NOT NULL COMMENT '关联主单ID',
                                       `material_id` bigint NOT NULL COMMENT '物料ID',
                                       `quantity` int NOT NULL COMMENT '采购数量',
                                       `unit_price` decimal(10,2) NOT NULL COMMENT '采购单价',
                                       `total_price` decimal(10,2) NOT NULL COMMENT '明细总价',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单明细表';

CREATE TABLE `scm_inventory` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `material_id` bigint NOT NULL COMMENT '物料ID',
                                 `current_quantity` int NOT NULL DEFAULT '0' COMMENT '当前库存数量',
                                 `safe_quantity` int DEFAULT '0' COMMENT '安全库存阈值',
                                 `warehouse_location` varchar(50) DEFAULT NULL COMMENT '库位编码',
                                 `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存状态表';

-- ==========================================================
-- 3. 销售订单模块 (sales-service)
-- ==========================================================

CREATE TABLE `sls_sales_order` (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `order_no` varchar(50) NOT NULL COMMENT '销售单号',
                                   `customer_id` bigint NOT NULL COMMENT '客户ID',
                                   `total_amount` decimal(10,2) DEFAULT '0.00' COMMENT '订单总金额',
                                   `status` varchar(20) DEFAULT 'CREATED' COMMENT '状态',
                                   `create_by` bigint DEFAULT NULL COMMENT '🌟 创建人/销售员ID',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                   `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单表';

CREATE TABLE `sls_sales_detail` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `sales_order_id` bigint NOT NULL COMMENT '关联销售主单ID',
                                    `material_id` bigint NOT NULL COMMENT '成品物料ID',
                                    `quantity` int NOT NULL COMMENT '销售数量',
                                    `unit_price` decimal(10,2) NOT NULL COMMENT '销售单价',
                                    `total_price` decimal(10,2) NOT NULL COMMENT '明细总价',
                                    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单明细表';

-- ==========================================================
-- 4. 财务流水模块 (finance-service)
-- ==========================================================

CREATE TABLE `fin_cash_flow` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `flow_no` varchar(50) NOT NULL COMMENT '流水号',
                                 `flow_type` varchar(10) NOT NULL COMMENT '流向(IN/OUT)',
                                 `amount` decimal(10,2) NOT NULL COMMENT '金额',
                                 `business_type` varchar(20) NOT NULL COMMENT '业务类型',
                                 `business_id` bigint NOT NULL COMMENT '关联单据ID',
                                 `create_by` bigint DEFAULT NULL COMMENT '操作人ID',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务资金流水表';

-- ==========================================================
-- 5. 初始化核心数据
-- ==========================================================
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `email`, `department`, `status`) VALUES
  (1, 'admin', '123456', '超级管理员', '13800138000', 'admin@smartx.com', '核心开发团队', 1),
  (2, 'buyer01', '123456', '采购员张三', '13900139001', 'zhangsan@smartx.com', '供应链部', 1);

INSERT INTO `sys_role` (`id`, `role_name`, `role_key`) VALUES
                                                           (1, '超级管理员', 'admin'),
                                                           (2, '采购员', 'buyer');

INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
                                                       (1, 1), (2, 2);

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `menu_type`, `perms`, `icon`) VALUES
                                                                                                                            (1, '供应链管理', 0, 1, '/scm', 'Layout', 'M', '', 'shopping-cart'),
                                                                                                                            (2, '采购单管理', 1, 1, 'purchase', 'scm/purchase/index', 'C', 'scm:purchase:list', 'file-text'),
                                                                                                                            (3, '新增采购单', 2, 1, '', '', 'F', 'scm:purchase:add', '#'),
                                                                                                                            (4, '扣减库存(内部)', 1, 2, '', '', 'F', 'scm:inventory:deduct', '#');

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
                                                       (1, 1), (1, 2), (1, 3), (1, 4),
                                                       (2, 1), (2, 2), (2, 3);

INSERT INTO `base_material` (`id`, `material_code`, `material_name`, `material_type`, `standard_cost`) VALUES
    (1, 'MAT-001', 'SmartX-1 测试物料', 'RAW', 3000.00);

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS `undo_log` (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                          `branch_id` bigint(20) NOT NULL COMMENT '分支事务ID',
                                          `xid` varchar(100) NOT NULL COMMENT '全局事务ID',
                                          `context` varchar(128) NOT NULL COMMENT '上下文',
                                          `rollback_info` longblob NOT NULL COMMENT '回滚信息',
                                          `log_status` int(11) NOT NULL COMMENT '状态',
                                          `log_created` datetime NOT NULL COMMENT '创建时间',
                                          `log_modified` datetime NOT NULL COMMENT '修改时间',
                                          `ext` varchar(100) DEFAULT NULL COMMENT '扩展字段',
                                          PRIMARY KEY (`id`),
                                          UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='Seata 分布式事务回滚日志表';




-- ========================================
-- sys_user 字段增强（增量脚本）
-- ========================================

-- 1. 角色关联：虽然有 sys_user_role 关联表，但为了登录接口快速返回角色名，冗余一个 role_name
ALTER TABLE `sys_user` ADD COLUMN `role_name` varchar(30) DEFAULT '普通员工' COMMENT '冗余角色名(超级管理员/采购员/...)' AFTER `department`;

-- 2. 性别
ALTER TABLE `sys_user` ADD COLUMN `gender` tinyint(1) DEFAULT NULL COMMENT '性别(0女 1男)' AFTER `real_name`;

-- 3. 最后登录时间（审计用）
ALTER TABLE `sys_user` ADD COLUMN `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间' AFTER `status`;

-- 4. 最后登录IP（审计用）
ALTER TABLE `sys_user` ADD COLUMN `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP' AFTER `last_login_time`;

-- 5. 备注
ALTER TABLE `sys_user` ADD COLUMN `remark` varchar(500) DEFAULT NULL COMMENT '备注信息' AFTER `last_login_ip`;

-- 更新初始数据中 admin 的角色名
UPDATE `sys_user` SET `role_name` = '超级管理员' WHERE `id` = 1;
UPDATE `sys_user` SET `role_name` = '采购员' WHERE `id` = 2;