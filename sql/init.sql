-- ==========================================================
-- SmartX ERP 数据库全量初始化脚本 (企业级 RBAC 架构版)
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
-- 1. 系统与权限模块 (auth-service) - 企业级 RBAC 重构版
-- ==========================================================

-- 用户表 (移除了单薄的 role_id)
CREATE TABLE `sys_user` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `username` varchar(50) NOT NULL COMMENT '登录账号',
                            `password` varchar(100) NOT NULL COMMENT '密码',
                            `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
                            `status` tinyint(1) DEFAULT '1' COMMENT '状态(1正常 0停用)',
                            `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                            `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            `is_deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 角色表
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

-- 菜单与按钮权限表 (完美适配前端动态路由)
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

-- 用户和角色关联表 (实现一人多岗)
CREATE TABLE `sys_user_role` (
                                 `user_id` bigint NOT NULL COMMENT '用户ID',
                                 `role_id` bigint NOT NULL COMMENT '角色ID',
                                 PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';

-- 角色和菜单关联表 (实现权限分配)
CREATE TABLE `sys_role_menu` (
                                 `role_id` bigint NOT NULL COMMENT '角色ID',
                                 `menu_id` bigint NOT NULL COMMENT '菜单ID',
                                 PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

-- 供应商信息表
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
-- ==========================================================

-- 物料基础信息表
CREATE TABLE `base_material` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `material_code` varchar(50) NOT NULL COMMENT '物料编码',
                                 `material_name` varchar(100) NOT NULL COMMENT '物料名称',
                                 `material_type` varchar(20) NOT NULL COMMENT '物料类型(RAW:原材料, FINISHED:成品)',
                                 `unit` varchar(20) DEFAULT '个' COMMENT '计量单位',
                                 `standard_cost` decimal(10,2) DEFAULT NULL COMMENT '标准成本(美元)',
                                 `description` varchar(255) DEFAULT NULL COMMENT '物料描述',
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
                                      `audit_status` varchar(20) DEFAULT 'PENDING' COMMENT '审核状态',
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
                                       `material_id` bigint NOT NULL COMMENT '物料ID',
                                       `quantity` int NOT NULL COMMENT '采购数量',
                                       `unit_price` decimal(10,2) NOT NULL COMMENT '采购单价',
                                       `total_price` decimal(10,2) NOT NULL COMMENT '明细总价',
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购单明细表';

-- 库存状态表
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

-- 销售订单主表
CREATE TABLE `sls_sales_order` (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `order_no` varchar(50) NOT NULL COMMENT '销售单号',
                                   `customer_id` bigint NOT NULL COMMENT '客户ID',
                                   `total_amount` decimal(10,2) DEFAULT '0.00' COMMENT '订单总金额',
                                   `status` varchar(20) DEFAULT 'CREATED' COMMENT '状态',
                                   `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单表';

-- 销售订单明细表
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

-- 资金流水表
CREATE TABLE `fin_cash_flow` (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `flow_no` varchar(50) NOT NULL COMMENT '流水号',
                                 `flow_type` varchar(10) NOT NULL COMMENT '流向(IN/OUT)',
                                 `amount` decimal(10,2) NOT NULL COMMENT '金额',
                                 `business_type` varchar(20) NOT NULL COMMENT '业务类型',
                                 `business_id` bigint NOT NULL COMMENT '关联单据ID',
                                 `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务资金流水表';

-- ==========================================================
-- 5. 初始化核心数据 (上帝视角)
-- ==========================================================
-- 插入 admin 和 业务员 账号 (密码假设明文 123456)
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `status`) VALUES
                                                                                 (1, 'admin', '123456', '超级管理员', 1),
                                                                                 (2, 'buyer01', '123456', '采购员张三', 1);

-- 插入角色：超级管理员 和 采购员
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`) VALUES
                                                           (1, '超级管理员', 'admin'),
                                                           (2, '采购员', 'buyer');

-- 绑定用户与角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
                                                       (1, 1), -- admin 是超级管理员
                                                       (2, 2); -- buyer01 是采购员

-- 插入系统菜单和按钮权限
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `menu_type`, `perms`, `icon`) VALUES
                                                                                                                            (1, '供应链管理', 0, 1, '/scm', 'Layout', 'M', '', 'shopping-cart'),
                                                                                                                            (2, '采购单管理', 1, 1, 'purchase', 'scm/purchase/index', 'C', 'scm:purchase:list', 'file-text'),
                                                                                                                            (3, '新增采购单', 2, 1, '', '', 'F', 'scm:purchase:add', '#'),
                                                                                                                            (4, '扣减库存(内部)', 1, 2, '', '', 'F', 'scm:inventory:deduct', '#');

-- 给超级管理员(1)分配所有菜单与按钮
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
                                                       (1, 1), (1, 2), (1, 3), (1, 4);

-- 给采购员(2)只分配基础采购权限，不给扣减库存的权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
                                                       (2, 1), (2, 2), (2, 3);

-- 插入一个测试物料 (以便测试发单)
INSERT INTO `base_material` (`id`, `material_code`, `material_name`, `material_type`, `standard_cost`) VALUES
    (1, 'MAT-001', 'SmartX-1 测试物料', 'RAW', 3000.00);

SET FOREIGN_KEY_CHECKS = 1;



-- ------------------------------------------------------------------
-- Seata AT 模式必备：回滚日志表 (undo_log)
-- 必须在所有参与分布式事务的业务数据库中创建！
-- ------------------------------------------------------------------
CREATE TABLE `undo_log` (
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