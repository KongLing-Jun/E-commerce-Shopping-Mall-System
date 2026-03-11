/*
 Navicat Premium Data Transfer

 Source Server         : MYSQL
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : mall_db

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 09/03/2026 14:50:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL,
  `receiver` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `area` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_default` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_address_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES (1, 3, 'Alan', '13420684857', '广东省', '深圳市', '龙岗区', '无', 1);

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `link_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `link_target` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `subtitle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `button_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sort` int(0) NULL DEFAULT 0,
  `status` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_banner_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (1, 'https://images.unsplash.com/photo-1556656793-08538906a9f8?auto=format&fit=crop&w=1200&q=80', 'PRODUCT', '1', '新品上市', '高能3C新品抢先看', '立即购买', 1, 1);
INSERT INTO `banner` VALUES (2, 'https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=1200&q=80', 'PRODUCT', '2', '办公利器', '轻薄高性能笔记本', '查看详情', 2, 1);
INSERT INTO `banner` VALUES (3, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=1200&q=80', 'PRODUCT', '3', '潮流配件', '耳机与穿戴好物', '立即选购', 3, 1);

-- ----------------------------
-- Table structure for cart_item
-- ----------------------------
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(0) NOT NULL,
  `product_id` bigint(0) NOT NULL,
  `quantity` int(0) NOT NULL DEFAULT 1,
  `checked` int(0) NULL DEFAULT NULL,
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_cart_user_product`(`user_id`, `product_id`) USING BTREE,
  INDEX `idx_cart_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart_item
-- ----------------------------
INSERT INTO `cart_item` VALUES (10, 1, 5, 4, 1, '2026-01-27 22:42:21');
INSERT INTO `cart_item` VALUES (11, 1, 2, 2, 0, '2026-01-27 23:08:57');
INSERT INTO `cart_item` VALUES (12, 1, 3, 1, 1, '2026-01-27 23:09:20');
INSERT INTO `cart_item` VALUES (13, 1, 4, 1, 1, '2026-01-27 23:09:34');
INSERT INTO `cart_item` VALUES (14, 1, 6, 2, 1, '2026-01-27 23:09:52');

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `parent_id` bigint(0) NULL DEFAULT 0,
  `sort` int(0) NULL DEFAULT 0,
  `status` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, 'Electronics', 0, 1, 1);
INSERT INTO `category` VALUES (2, 'Home', 0, 2, 1);
INSERT INTO `category` VALUES (3, 'Fashion', 0, 3, 1);

-- ----------------------------
-- Table structure for menu
-- ----------------------------
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(0) NULL DEFAULT 0,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `perm_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `sort` int(0) NULL DEFAULT 0,
  `visible` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_menu_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of menu
-- ----------------------------
INSERT INTO `menu` VALUES (1, NULL, 'Home', '/', 'Home', 'MENU', NULL, 1, 1);
INSERT INTO `menu` VALUES (2, NULL, 'Products', '/products', 'ProductList', 'MENU', NULL, 2, 1);
INSERT INTO `menu` VALUES (3, NULL, 'User Management', '/admin/users', 'AdminUsers', 'MENU', 'admin:users:list', 1, 1);
INSERT INTO `menu` VALUES (4, NULL, 'Product Management', '/admin/products', 'AdminProducts', 'MENU', 'admin:products:list', 2, 1);
INSERT INTO `menu` VALUES (5, NULL, 'Disable User', NULL, NULL, 'BUTTON', 'admin:users:disable', NULL, 0);
INSERT INTO `menu` VALUES (6, NULL, 'Enable Product', NULL, NULL, 'BUTTON', 'admin:products:on', NULL, 0);
INSERT INTO `menu` VALUES (7, 0, 'Category Management', '/admin/categories', 'AdminCategories', 'MENU', 'admin:categories:list', 3, 1);
INSERT INTO `menu` VALUES (8, 0, 'Manage Category', NULL, NULL, 'BUTTON', 'admin:categories:edit', 0, 0);
INSERT INTO `menu` VALUES (9, 0, 'Delete Category', NULL, NULL, 'BUTTON', 'admin:categories:delete', 0, 0);
INSERT INTO `menu` VALUES (10, 0, 'Role Management', '/admin/roles', 'AdminRoles', 'MENU', 'admin:roles:list', 2, 1);
INSERT INTO `menu` VALUES (11, 0, 'Manage Role', NULL, NULL, 'BUTTON', 'admin:roles:edit', 0, 0);
INSERT INTO `menu` VALUES (12, 0, 'Delete Role', NULL, NULL, 'BUTTON', 'admin:roles:delete', 0, 0);
INSERT INTO `menu` VALUES (13, 0, 'Assign Role', NULL, NULL, 'BUTTON', 'admin:users:role', 0, 0);
INSERT INTO `menu` VALUES (14, 0, 'Create User', NULL, NULL, 'BUTTON', 'admin:users:create', 0, 0);
INSERT INTO `menu` VALUES (15, 0, 'Edit User', NULL, NULL, 'BUTTON', 'admin:users:edit', 0, 0);

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(0) NULL DEFAULT NULL,
  `admin_username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `action` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `target` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `detail` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_admin_id`(`admin_id`) USING BTREE,
  INDEX `idx_action`(`action`) USING BTREE,
  INDEX `idx_created_at`(`created_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES (1, 1, 'admin', 'PRODUCT_OFF', 'product:1', 'status=OFF', '0:0:0:0:0:0:0:1', '2026-02-14 17:46:13');
INSERT INTO `operation_log` VALUES (2, 1, 'admin', 'PRODUCT_ON', 'product:1', 'status=ON', '0:0:0:0:0:0:0:1', '2026-02-14 17:46:14');
INSERT INTO `operation_log` VALUES (3, 1, 'admin', 'USER_ROLE', 'user:3', 'role assigned', '0:0:0:0:0:0:0:1', '2026-02-14 17:47:32');
INSERT INTO `operation_log` VALUES (4, 1, 'admin', 'USER_ROLE', 'user:3', 'role assigned', '0:0:0:0:0:0:0:1', '2026-02-14 17:47:50');
INSERT INTO `operation_log` VALUES (5, 1, 'admin', 'USER_UPDATE', 'user:3', 'AlanThinking', '0:0:0:0:0:0:0:1', '2026-02-14 19:19:19');

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_id` bigint(0) NOT NULL,
  `total_amount` decimal(38, 2) NULL DEFAULT NULL,
  `pay_amount` decimal(38, 2) NULL DEFAULT NULL,
  `status` int(0) NULL DEFAULT NULL,
  `address_snapshot` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `paid_at` datetime(0) NULL DEFAULT NULL,
  `shipped_at` datetime(0) NULL DEFAULT NULL,
  `finished_at` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no`) USING BTREE,
  INDEX `idx_order_user_id`(`user_id`) USING BTREE,
  INDEX `idx_order_status`(`status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (1, 'NOAC71FB3C33EE4BE3AE', 3, 318.00, 318.00, 3, 'Alan 13420684857 广东省 深圳市 龙岗区 无', '2026-01-26 12:44:11', '2026-01-26 12:44:15', '2026-01-26 12:48:16', '2026-02-23 11:59:49');
INSERT INTO `order` VALUES (2, 'NOEADE75973B11440D9D', 3, 248.00, 248.00, 1, 'Alan 13420684857 广东省 深圳市 龙岗区 无', '2026-02-23 12:00:48', '2026-02-23 12:00:56', NULL, NULL);

-- ----------------------------
-- Table structure for order_delivery
-- ----------------------------
DROP TABLE IF EXISTS `order_delivery`;
CREATE TABLE `order_delivery`  (
  `order_id` bigint(0) NOT NULL,
  `express_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `express_company` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for order_tracking_event
-- ----------------------------
DROP TABLE IF EXISTS `order_tracking_event`;
CREATE TABLE `order_tracking_event`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(0) NOT NULL,
  `status` int(0) NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `event_time` datetime NULL DEFAULT NULL,
  `created_at` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tracking_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(0) NOT NULL,
  `product_id` bigint(0) NOT NULL,
  `product_name_snapshot` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price_snapshot` decimal(38, 2) NULL DEFAULT NULL,
  `quantity` int(0) NOT NULL,
  `image_snapshot` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_item_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 2, 'Smart Watch', 249.00, 1, 'https://picsum.photos/seed/watch/600/400');
INSERT INTO `order_item` VALUES (2, 1, 5, 'Canvas Backpack', 69.00, 1, 'https://picsum.photos/seed/backpack/600/400');
INSERT INTO `order_item` VALUES (3, 2, 3, 'Coffee Maker', 89.00, 1, 'https://picsum.photos/seed/coffee/600/400');
INSERT INTO `order_item` VALUES (4, 2, 4, 'Air Purifier', 159.00, 1, 'https://picsum.photos/seed/purifier/600/400');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(0) NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `brief` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` decimal(38, 2) NULL DEFAULT NULL,
  `stock` int(0) NOT NULL DEFAULT 0,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `detail_html` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_category_id`(`category_id`) USING BTREE,
  INDEX `idx_product_status`(`status`) USING BTREE,
  INDEX `idx_product_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, 1, 'Wireless Headphones', 'Noise cancelling, 30h battery', 199.00, 119, 'ON', 'https://picsum.photos/seed/headphones/600/400', '<p>Sample detail for Wireless Headphones</p>', '2026-01-25 16:18:26');
INSERT INTO `product` VALUES (2, 1, 'Smart Watch', 'Fitness tracking and notifications', 249.00, 79, 'ON', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=800&q=80', '<p>Sample detail for Smart Watch</p>', '2026-01-25 16:18:26');
INSERT INTO `product` VALUES (3, 1, '27-inch 4K Monitor', 'IPS panel, HDR, USB-C docking', 459.00, 58, 'ON', 'https://images.unsplash.com/photo-1527443224154-c4e9d81f5f7d?auto=format&fit=crop&w=900&q=80', '<p>IPS panel, HDR, USB-C docking</p>', '2026-01-25 16:18:26');
INSERT INTO `product` VALUES (4, 2, 'Air Purifier', 'HEPA filtration for clean air', 159.00, 44, 'ON', 'https://picsum.photos/seed/purifier/600/400', '<p>Sample detail for Air Purifier</p>', '2026-01-25 16:18:26');
INSERT INTO `product` VALUES (5, 1, 'Wireless Mouse Pro', 'Dual-mode Bluetooth + 2.4G, silent click', 59.00, 149, 'ON', 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?auto=format&fit=crop&w=900&q=80', '<p>Dual-mode Bluetooth + 2.4G, silent click</p>', '2026-01-25 16:18:26');
INSERT INTO `product` VALUES (6, 1, 'Gaming Headset H7', '7.1 surround sound, detachable microphone', 129.00, 95, 'ON', 'https://images.unsplash.com/photo-1599669454699-248893623440?auto=format&fit=crop&w=900&q=80', '<p>7.1 surround sound, detachable microphone</p>', '2026-01-25 16:18:26');

-- ----------------------------
-- Table structure for product_image
-- ----------------------------
DROP TABLE IF EXISTS `product_image`;
CREATE TABLE `product_image`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(0) NOT NULL,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `sort` int(0) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_image_product_id`(`product_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_image
-- ----------------------------
INSERT INTO `product_image` VALUES (1, 1, 'https://picsum.photos/seed/headphones/600/400', 1);
INSERT INTO `product_image` VALUES (2, 1, 'https://picsum.photos/seed/headphones/600/400&v=angleA', 2);
INSERT INTO `product_image` VALUES (3, 1, 'https://picsum.photos/seed/headphones/600/400&v=angleB', 3);
INSERT INTO `product_image` VALUES (4, 2, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=800&q=80', 1);
INSERT INTO `product_image` VALUES (5, 2, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=800&q=80&v=angleA', 2);
INSERT INTO `product_image` VALUES (6, 2, 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=800&q=80&v=angleB', 3);
INSERT INTO `product_image` VALUES (10, 4, 'https://picsum.photos/seed/purifier/600/400', 1);
INSERT INTO `product_image` VALUES (11, 4, 'https://picsum.photos/seed/purifier/600/400&v=angleA', 2);
INSERT INTO `product_image` VALUES (12, 4, 'https://picsum.photos/seed/purifier/600/400&v=angleB', 3);
INSERT INTO `product_image` VALUES (19, 3, 'https://images.unsplash.com/photo-1527443224154-c4e9d81f5f7d?auto=format&fit=crop&w=900&q=80', 1);
INSERT INTO `product_image` VALUES (20, 5, 'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?auto=format&fit=crop&w=900&q=80', 1);
INSERT INTO `product_image` VALUES (21, 6, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80', 1);
INSERT INTO `product_image` VALUES (22, 6, 'https://images.unsplash.com/photo-1545127398-14699f92334b?auto=format&fit=crop&w=900&q=80', 2);
INSERT INTO `product_image` VALUES (23, 6, 'https://images.unsplash.com/photo-1583394838336-acd977736f90?auto=format&fit=crop&w=900&q=80', 3);

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `role_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `role_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_key`(`role_key`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, 'ADMIN', 'Admin');
INSERT INTO `role` VALUES (2, 'USER', 'User');

-- ----------------------------
-- Table structure for role_menu
-- ----------------------------
DROP TABLE IF EXISTS `role_menu`;
CREATE TABLE `role_menu`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(0) NOT NULL,
  `menu_id` bigint(0) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_menu`(`role_id`, `menu_id`) USING BTREE,
  INDEX `idx_role_menu_role_id`(`role_id`) USING BTREE,
  INDEX `idx_role_menu_menu_id`(`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_menu
-- ----------------------------
INSERT INTO `role_menu` VALUES (1, 1, 1);
INSERT INTO `role_menu` VALUES (2, 1, 2);
INSERT INTO `role_menu` VALUES (3, 1, 3);
INSERT INTO `role_menu` VALUES (4, 1, 4);
INSERT INTO `role_menu` VALUES (5, 1, 5);
INSERT INTO `role_menu` VALUES (6, 1, 6);
INSERT INTO `role_menu` VALUES (9, 1, 7);
INSERT INTO `role_menu` VALUES (10, 1, 8);
INSERT INTO `role_menu` VALUES (11, 1, 9);
INSERT INTO `role_menu` VALUES (12, 1, 10);
INSERT INTO `role_menu` VALUES (13, 1, 11);
INSERT INTO `role_menu` VALUES (14, 1, 12);
INSERT INTO `role_menu` VALUES (15, 1, 13);
INSERT INTO `role_menu` VALUES (16, 1, 14);
INSERT INTO `role_menu` VALUES (17, 1, 15);
INSERT INTO `role_menu` VALUES (7, 2, 1);
INSERT INTO `role_menu` VALUES (8, 2, 2);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` int(0) NULL DEFAULT NULL,
  `role_id` bigint(0) NULL DEFAULT NULL,
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_username`(`username`) USING BTREE,
  UNIQUE INDEX `uk_user_phone`(`phone`) USING BTREE,
  INDEX `idx_user_role_id`(`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '13800000000', '$2a$10$67.crwaZw.DOBS5mpC3Scug36JMeJqNgBrU.yRR4QvlYuBGL/k5nK', 1, 1, '2026-01-25 16:18:25');
INSERT INTO `user` VALUES (2, 'user', '13800000001', '$2a$10$8.I9ISblB2CetGYNqqNwgO0.sUGHC9x0SPR5PifDRCZWrc9M15wIm', 1, 2, '2026-01-25 16:18:26');
INSERT INTO `user` VALUES (3, 'AlanThinking', '13420684857', '$2a$10$l2jXMkXx/l9ju8v84zcVJu037Zf2eohEw23Io8MG2KLmiGKFM1XQK', 1, 1, '2026-01-26 01:21:42');
INSERT INTO `user` VALUES (4, 'Lily', '12345678901', '$2a$10$DEck5BjCm9Jc/x42Vm5bWOAmRA3vBRmPjGLxGgCA/Ajna6ZvXUkXS', 1, 2, '2026-02-14 19:33:15');

SET FOREIGN_KEY_CHECKS = 1;
