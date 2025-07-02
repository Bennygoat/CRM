-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- 主機： localhost:8889
-- 產生時間： 2025 年 07 月 01 日 06:40
-- 伺服器版本： 8.0.40
-- PHP 版本： 8.3.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 資料庫： `sample`
--

-- --------------------------------------------------------

--
-- 資料表結構 `activities`
--

CREATE TABLE `activities` (
  `id` bigint NOT NULL,
  `activity_type` enum('CALL','DEADLINE','EMAIL','MEETING','TASK') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `end_time` datetime(6) DEFAULT NULL,
  `notes` tinytext,
  `start_time` datetime(6) NOT NULL,
  `status` enum('CANCELLED','COMPLETED','PLANNED') NOT NULL,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `contact_id` bigint DEFAULT NULL,
  `opportunity_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `authority`
--

CREATE TABLE `authority` (
  `authority_id` bigint NOT NULL,
  `code` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `display_name` varchar(255) DEFAULT NULL,
  `module` varchar(255) DEFAULT NULL,
  `module_group` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 傾印資料表的資料 `authority`
--

INSERT INTO `authority` (`authority_id`, `code`, `description`, `display_name`, `module`, `module_group`) VALUES
(1, 'USER_READ', '檢視帳號', '檢視使用者', '使用者管理', 'SYSTEM'),
(2, 'USER_CREATE', '建立帳號', '新增使用者', '使用者管理', 'SYSTEM'),
(3, 'USER_UPDATE', '修改帳號', '編輯使用者', '使用者管理', 'SYSTEM'),
(4, 'USER_DELETE', '刪除帳號', '移除使用者', '使用者管理', 'SYSTEM'),
(5, 'CUSTOMER_READ', '檢視客戶', '查詢客戶資料', '客戶管理', 'CRM'),
(6, 'CUSTOMER_CREATE', '建立客戶', '建立客戶資料', '客戶管理', 'CRM'),
(7, 'CUSTOMER_UPDATE', '更新客戶', '更改客戶資料', '客戶管理', 'CRM'),
(8, 'CUSTOMER_DELETE', '刪除客戶', '刪除客戶資料', '客戶管理', 'CRM'),
(9, 'CUSTOMER_SUPPORT', '客戶服務', '客戶需求支援', '客戶管理', 'CRM'),
(10, 'ORDER_READ', '檢視訂單', '檢視訂單', '訂單管理', 'ORDER'),
(11, 'ORDER_CREATE', '建立訂單', '建立訂單', '訂單管理', 'ORDER'),
(12, 'ORDER_UPDATE', '更新訂單', '更改訂單', '訂單管理', 'ORDER'),
(13, 'ORDER_DELETE', '刪除訂單', '刪除訂單', '訂單管理', 'ORDER'),
(14, 'ARTICLE_READ', '檢視文章', '檢視文章', '文章管理', 'CMS'),
(15, 'ARTICLE_CREATE', '建立文章', '新增文章', '文章管理', 'CMS'),
(16, 'ARTICLE_UPDATE', '更新文章', '更改文章', '文章管理', 'CMS'),
(17, 'ARTICLE_DELETE', '刪除文章', '刪除文章', '文章管理', 'CMS'),
(18, 'REPORT_VIEW', '查看報表', '查詢分析', '報表管理', 'ANALYTICS'),
(19, 'LOG_VIEW', '查看操作紀錄', '操作紀錄分析', '操作紀錄管理', 'ANALYTICS');

-- --------------------------------------------------------

--
-- 資料表結構 `b_customers`
--

CREATE TABLE `b_customers` (
  `bcustomer_level` enum('BRONZE','DIAMOND','GOLD','NEW','PLATINUM','SILVER') DEFAULT NULL,
  `bcustomer_type` enum('INACTIVE','KEY_ACCOUNT','LOST','PARTNER','PROSPECT','REGULAR','VENDOR') DEFAULT NULL,
  `industry` enum('CONSULTING','EDUCATION','ENTERTAINMENT','FINANCE','GOVERNMENT','HEALTHCARE','MANUFACTURING','OTHER','REAL_ESTATE','RETAIL','TECHNOLOGY') DEFAULT NULL,
  `tin_number` varchar(255) NOT NULL,
  `customer_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `b_customer_tags`
--

CREATE TABLE `b_customer_tags` (
  `b_customer_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `cartdetails`
--

CREATE TABLE `cartdetails` (
  `cartdetailid` bigint NOT NULL,
  `addat` datetime(6) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `cartid` bigint DEFAULT NULL,
  `productid` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `carts`
--

CREATE TABLE `carts` (
  `cartid` bigint NOT NULL,
  `createat` datetime(6) DEFAULT NULL,
  `updateat` datetime(6) DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `contacts`
--

CREATE TABLE `contacts` (
  `contact_id` bigint NOT NULL,
  `contact_name` varchar(100) NOT NULL,
  `contact_notes` text,
  `contact_phone` varchar(30) DEFAULT NULL,
  `contact_title` varchar(100) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(150) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `customer_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `contracts`
--

CREATE TABLE `contracts` (
  `id` bigint NOT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `contract_number` varchar(255) NOT NULL,
  `contract_type` enum('AGENCY','DISTRIBUTION','FRANCHISE','INFLUENCER_MARKETING','IP_AUTHORIZATION_LETTER','MEMORANDUM_OF_UNDERSTANDING','NON_DISCLOSURE_AGREEMENT','PURCHASE') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `end_date` date DEFAULT NULL,
  `start_date` date NOT NULL,
  `status` enum('ACTIVE','DRAFT','EXPIRED','SENT','SIGNED','TERMINATED') NOT NULL,
  `terms_and_conditions` tinytext,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `contact_id` bigint DEFAULT NULL,
  `opportunity_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `coupon_templates`
--

CREATE TABLE `coupon_templates` (
  `id` bigint NOT NULL,
  `code` varchar(50) DEFAULT NULL,
  `coupon_type` enum('FIXED_AMOUNT','PERCENTAGE') NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `discount_value` decimal(10,2) NOT NULL,
  `issued_quantity` int NOT NULL,
  `max_discount_amount` decimal(10,2) DEFAULT NULL,
  `min_purchase_amount` decimal(10,2) NOT NULL,
  `name` varchar(100) NOT NULL,
  `status` enum('ACTIVE','EXPIRED','INACTIVE') NOT NULL,
  `total_quantity` int NOT NULL,
  `valid_from` datetime(6) NOT NULL,
  `valid_to` datetime(6) NOT NULL,
  `target_vip_level` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `customer`
--

CREATE TABLE `customer` (
  `access_end_time` datetime(6) DEFAULT NULL,
  `access_start_time` datetime(6) DEFAULT NULL,
  `account` varchar(255) NOT NULL,
  `birthday` date DEFAULT NULL,
  `last_login` datetime(6) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `customer_id` bigint NOT NULL,
  `vip_level` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 傾印資料表的資料 `customer`
--

INSERT INTO `customer` (`access_end_time`, `access_start_time`, `account`, `birthday`, `last_login`, `password`, `customer_id`, `vip_level`) VALUES
(NULL, NULL, 'kuei', '2025-07-01', NULL, '$2a$10$KkVFTH29p0.5c1WHfToikeyFaPbioGTkVf5JXyOcrx50.yGXRRdA2', 11, NULL);

-- --------------------------------------------------------

--
-- 資料表結構 `customer_address`
--

CREATE TABLE `customer_address` (
  `addressid` bigint NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `createat` datetime(6) DEFAULT NULL,
  `isdefault` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `updateat` datetime(6) DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `customer_base`
--

CREATE TABLE `customer_base` (
  `customer_type` varchar(31) NOT NULL,
  `customer_id` bigint NOT NULL,
  `address` varchar(500) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `customer_code` varchar(50) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `is_deleted` bit(1) NOT NULL,
  `spending` bigint DEFAULT NULL,
  `customertel` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `updated_by` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 傾印資料表的資料 `customer_base`
--

INSERT INTO `customer_base` (`customer_type`, `customer_id`, `address`, `created_at`, `created_by`, `customer_code`, `name`, `email`, `is_active`, `is_deleted`, `spending`, `customertel`, `updated_at`, `updated_by`) VALUES
('B2C', 11, 'string', '2025-07-01 14:32:35.542944', 1, 'C-BFB10FA4', 'Josh', 'string@me.com', b'1', b'0', NULL, NULL, '2025-07-01 14:32:35.542944', 1);

-- --------------------------------------------------------

--
-- 資料表結構 `customer_coupons`
--

CREATE TABLE `customer_coupons` (
  `id` bigint NOT NULL,
  `received_at` datetime(6) NOT NULL,
  `status` enum('EXPIRED','UNUSED','USED') NOT NULL,
  `used_at` datetime(6) DEFAULT NULL,
  `coupon_template_id` bigint NOT NULL,
  `customer_id` bigint NOT NULL,
  `order_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `employees`
--

CREATE TABLE `employees` (
  `employeeid` bigint NOT NULL,
  `createat` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `hiredate` date DEFAULT NULL,
  `leavedate` date DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `updateat` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `inventories`
--

CREATE TABLE `inventories` (
  `inventory_id` bigint NOT NULL,
  `average_cost` decimal(38,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `current_stock` decimal(38,2) NOT NULL,
  `units_allocated` decimal(38,2) NOT NULL,
  `units_on_order` decimal(38,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `updated_by` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `warehouse_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `inventory_adjustments`
--

CREATE TABLE `inventory_adjustments` (
  `adjustment_id` bigint NOT NULL,
  `adjustment_date` date NOT NULL,
  `adjustment_number` varchar(50) NOT NULL,
  `adjustment_type` enum('CYCLE_COUNT_GAIN','CYCLE_COUNT_LOSS','INITIAL_STOCK','OTHER_GAIN','OTHER_LOSS','SCRAP') NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `remarks` text,
  `status` varchar(20) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `updated_by` bigint NOT NULL,
  `warehouse_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `inventory_adjustment_details`
--

CREATE TABLE `inventory_adjustment_details` (
  `item_id` bigint NOT NULL,
  `adjusted_quantity` decimal(18,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `remarks` text,
  `updated_at` datetime(6) NOT NULL,
  `updated_by` bigint NOT NULL,
  `adjustment_id` bigint NOT NULL,
  `product_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `inventory_movements`
--

CREATE TABLE `inventory_movements` (
  `movement_id` bigint NOT NULL,
  `current_stock_after_movement` decimal(18,2) DEFAULT NULL,
  `document_id` bigint DEFAULT NULL,
  `document_item_id` bigint DEFAULT NULL,
  `document_type` varchar(255) DEFAULT NULL,
  `movement_date` datetime(6) NOT NULL,
  `movement_type` varchar(255) NOT NULL,
  `quantity_change` decimal(18,2) NOT NULL,
  `total_cost_change` decimal(18,2) NOT NULL,
  `unit_cost_at_movement` decimal(18,2) NOT NULL,
  `product_id` bigint NOT NULL,
  `recorded_by` bigint NOT NULL,
  `warehouse_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `message`
--

CREATE TABLE `message` (
  `message_id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `is_resolved` bit(1) DEFAULT NULL,
  `question_title` varchar(255) NOT NULL,
  `customer_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `message_reply`
--

CREATE TABLE `message_reply` (
  `reply_id` bigint NOT NULL,
  `content` varchar(255) NOT NULL,
  `sender_type` enum('CUSTOMER','STAFF') DEFAULT NULL,
  `sent_at` datetime(6) DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `message_id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `opportunities`
--

CREATE TABLE `opportunities` (
  `opportunity_id` bigint NOT NULL,
  `close_date` date DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text,
  `expected_value` decimal(14,2) DEFAULT NULL,
  `number_of_ratings` int NOT NULL,
  `opportunity_name` varchar(255) NOT NULL,
  `priority` int NOT NULL DEFAULT '0',
  `stage` enum('CLOSED_LOST','CLOSED_WON','INITIAL_CONTACT','NEEDS_ANALYSIS','NEGOTIATION','PROPOSAL') DEFAULT NULL,
  `status` enum('LOST','OPEN','WON') DEFAULT NULL,
  `total_rating_sum` bigint NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `customer_id` bigint NOT NULL,
  `contact_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `opportunity_tags`
--

CREATE TABLE `opportunity_tags` (
  `opportunity_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `orderdetails`
--

CREATE TABLE `orderdetails` (
  `orderid` bigint NOT NULL,
  `productid` bigint NOT NULL,
  `createat` datetime(6) DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `unitprice` double DEFAULT NULL,
  `updateat` datetime(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `orders`
--

CREATE TABLE `orders` (
  `orderid` bigint NOT NULL,
  `createat` datetime(6) DEFAULT NULL,
  `merchant_trade_no` varchar(255) DEFAULT NULL,
  `orderstatus` enum('CANCELLED','COMPLETE','PENDING_PAYMENT','PENDING_PICKUP','PENDING_SHIPMENT','RETURNED','RETURN_REQUESTED','SHIPPED') DEFAULT NULL,
  `orderdate` date DEFAULT NULL,
  `payment_method` enum('CASH_ON_DELIVERY','ONLINE_PAYMENT') NOT NULL,
  `paymentstatus` enum('PAID','REFUNDED','REFUNDING','UNPAID') DEFAULT NULL,
  `total_amount` double DEFAULT NULL,
  `updateat` datetime(6) DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `addressid` bigint DEFAULT NULL,
  `platformid` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `password_reset_token`
--

CREATE TABLE `password_reset_token` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `expires_at` datetime(6) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  `used` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `platforms`
--

CREATE TABLE `platforms` (
  `platformid` bigint NOT NULL,
  `createat` datetime(6) DEFAULT NULL,
  `platformname` varchar(255) DEFAULT NULL,
  `updateat` datetime(6) DEFAULT NULL,
  `employeeid` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `productimgs`
--

CREATE TABLE `productimgs` (
  `imgid` bigint NOT NULL,
  `imgurl` varchar(255) DEFAULT NULL,
  `productid` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `products`
--

CREATE TABLE `products` (
  `product_id` bigint NOT NULL,
  `base_price` decimal(18,2) NOT NULL,
  `cost_method` varchar(50) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `description` text,
  `is_active` bit(1) NOT NULL,
  `is_purchasable` bit(1) NOT NULL,
  `is_salable` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `product_code` varchar(50) NOT NULL,
  `safety_stock_quantity` int NOT NULL,
  `tax_type` varchar(20) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `updated_by` bigint NOT NULL,
  `category_id` bigint DEFAULT NULL,
  `supplier_id` bigint DEFAULT NULL,
  `unit_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 傾印資料表的資料 `products`
--

INSERT INTO `products` (`product_id`, `base_price`, `cost_method`, `created_at`, `created_by`, `description`, `is_active`, `is_purchasable`, `is_salable`, `name`, `product_code`, `safety_stock_quantity`, `tax_type`, `updated_at`, `updated_by`, `category_id`, `supplier_id`, `unit_id`) VALUES
(1, 194.74, 'AVERAGE', '2025-07-01 14:29:56.949847', 1, 'Ea autem est esse. Tenetur officiis cumque vel aut sint. Est ex necessitatibus sit.', b'1', b'1', b'1', '夏日蘭姆葡萄冰淇淋 P000001', 'P000001', 84, 'TAXABLE', '2025-07-01 14:29:56.949847', 1, 3, NULL, 7),
(2, 211.60, 'AVERAGE', '2025-07-01 14:29:56.951513', 1, 'Officiis eaque sapiente consequatur fuga. Dolore officia quia placeat et veniam quia.', b'1', b'1', b'1', '極致豆乳芝麻冰棒 P000002', 'P000002', 95, 'TAXABLE', '2025-07-01 14:29:56.951513', 1, 7, NULL, 2),
(3, 157.26, 'AVERAGE', '2025-07-01 14:29:56.952496', 1, 'Necessitatibus nam illo aut aut deleniti animi consectetur. Doloribus enim voluptas nobis accusamus. Iure iure ipsam sit.', b'1', b'1', b'1', '純濃OREO雪糕 P000003', 'P000003', 33, 'TAXABLE', '2025-07-01 14:29:56.952496', 1, 8, NULL, 7),
(4, 315.61, 'AVERAGE', '2025-07-01 14:29:56.953491', 1, 'Repudiandae ex corporis. Et optio dolorum omnis voluptates non qui autem.', b'1', b'1', b'1', '夏日芒果優格冰棒 P000004', 'P000004', 27, 'TAXABLE', '2025-07-01 14:29:56.953491', 1, 6, NULL, 2),
(5, 267.45, 'AVERAGE', '2025-07-01 14:29:56.954590', 1, 'Doloremque excepturi facere sit hic fugiat explicabo sunt. Explicabo mollitia laudantium quasi. Sapiente beatae quidem et sit dolorem exercitationem. Dicta rerum sed similique facere.', b'1', b'1', b'1', '鮮果咖啡冰棒 P000005', 'P000005', 52, 'TAXABLE', '2025-07-01 14:29:56.954590', 1, 5, NULL, 4),
(6, 305.98, 'AVERAGE', '2025-07-01 14:29:56.955500', 1, 'Sed quo ipsum cumque omnis. Eum ut recusandae itaque ratione et quo qui.', b'1', b'1', b'1', '夏日蘭姆葡萄雪酪 P000006', 'P000006', 85, 'TAXABLE', '2025-07-01 14:29:56.955500', 1, 5, NULL, 7),
(7, 131.06, 'AVERAGE', '2025-07-01 14:29:56.956377', 1, 'Ut consequatur qui dolore qui. Deleniti aut rerum beatae occaecati voluptate qui veritatis. Id repellendus iste fuga labore doloremque aut praesentium. Nobis aspernatur et eveniet non consequatur nemo sint.', b'1', b'1', b'1', '經典芒果優格雪糕 P000007', 'P000007', 67, 'TAXABLE', '2025-07-01 14:29:56.956377', 1, 7, NULL, 2),
(8, 277.20, 'AVERAGE', '2025-07-01 14:29:56.957302', 1, 'Aspernatur ab beatae nihil. Cumque quidem deleniti laudantium. Laboriosam rerum voluptatem est. Labore deleniti quis non corrupti voluptates.', b'1', b'1', b'1', '夏日藍莓冰棒 P000008', 'P000008', 72, 'TAXABLE', '2025-07-01 14:29:56.957302', 1, 2, NULL, 5),
(9, 255.87, 'AVERAGE', '2025-07-01 14:29:56.958159', 1, 'Architecto minus ea ratione cumque. Sapiente nobis ut.', b'1', b'1', b'1', '雪藏香草冰淇淋 P000009', 'P000009', 55, 'TAXABLE', '2025-07-01 14:29:56.958159', 1, 6, NULL, 5),
(10, 253.32, 'AVERAGE', '2025-07-01 14:29:56.958970', 1, 'Debitis pariatur reiciendis et dolorem veritatis. Sed qui dicta error quia ut. Modi sint iste maiores ducimus. Modi voluptate autem fuga qui quaerat.', b'1', b'1', b'1', '經典蘭姆葡萄雪酪 P000010', 'P000010', 38, 'TAXABLE', '2025-07-01 14:29:56.958970', 1, 7, NULL, 6),
(11, 60.25, 'AVERAGE', '2025-07-01 14:29:56.959801', 1, 'Inventore aut ut. Aperiam repellat eos quo culpa aliquam cum.', b'1', b'1', b'1', '純濃藍莓聖代 P000011', 'P000011', 54, 'TAXABLE', '2025-07-01 14:29:56.959801', 1, 7, NULL, 7),
(12, 349.90, 'AVERAGE', '2025-07-01 14:29:56.960925', 1, 'Esse est vitae dicta ratione praesentium fugit maiores. Hic quo rerum.', b'1', b'1', b'1', '經典OREO雪酪 P000012', 'P000012', 39, 'TAXABLE', '2025-07-01 14:29:56.960925', 1, 2, NULL, 5),
(13, 206.37, 'AVERAGE', '2025-07-01 14:29:56.961819', 1, 'Eos earum sed autem explicabo nihil. Molestiae libero maxime cupiditate minus et. Vero officiis rerum voluptatem odio dolore dolores.', b'1', b'1', b'1', '純濃薄荷巧克力雪糕 P000013', 'P000013', 80, 'TAXABLE', '2025-07-01 14:29:56.961819', 1, 4, NULL, 3),
(14, 66.43, 'AVERAGE', '2025-07-01 14:29:56.962652', 1, 'Odio ducimus incidunt repellendus rerum pariatur perspiciatis qui. Et amet necessitatibus debitis. Tempore repellat sint beatae officiis ut est.', b'1', b'1', b'1', '鮮果蘭姆葡萄冰棒 P000014', 'P000014', 64, 'TAXABLE', '2025-07-01 14:29:56.962652', 1, 4, NULL, 7),
(15, 208.30, 'AVERAGE', '2025-07-01 14:29:56.963431', 1, 'Voluptatum optio laboriosam aperiam voluptatum. Aut sint possimus voluptate molestiae. Culpa ea eos maiores.', b'1', b'1', b'1', '極致咖啡雪糕 P000015', 'P000015', 97, 'TAXABLE', '2025-07-01 14:29:56.963431', 1, 4, NULL, 5),
(16, 300.71, 'AVERAGE', '2025-07-01 14:29:56.964407', 1, 'Et quia aliquid alias animi magnam saepe. Quos placeat cupiditate architecto. A qui nobis blanditiis blanditiis non rem. Voluptas natus in qui sed sit.', b'1', b'1', b'1', '夏日海鹽焦糖雪酪 P000016', 'P000016', 51, 'TAXABLE', '2025-07-01 14:29:56.964407', 1, 3, NULL, 2),
(17, 84.15, 'AVERAGE', '2025-07-01 14:29:56.965355', 1, 'Quos officia veniam nihil odio aut nostrum distinctio. Accusantium ab et error odio. Nemo quidem dolores doloremque.', b'1', b'1', b'1', '夏日香蕉冰淇淋 P000017', 'P000017', 31, 'TAXABLE', '2025-07-01 14:29:56.965355', 1, 7, NULL, 3),
(18, 317.90, 'AVERAGE', '2025-07-01 14:29:56.966227', 1, 'Dolor nisi omnis optio voluptatem est cumque. Animi excepturi optio doloremque dolores voluptate officiis quaerat.', b'1', b'1', b'1', '極致草莓冰棒 P000018', 'P000018', 48, 'TAXABLE', '2025-07-01 14:29:56.966227', 1, 3, NULL, 6),
(19, 321.56, 'AVERAGE', '2025-07-01 14:29:56.966906', 1, 'Sint consequatur ducimus natus molestiae. Qui corrupti in. Voluptas reprehenderit esse amet pariatur ullam.', b'1', b'1', b'1', '純濃花生雪酪 P000019', 'P000019', 18, 'TAXABLE', '2025-07-01 14:29:56.966906', 1, 5, NULL, 5),
(20, 333.03, 'AVERAGE', '2025-07-01 14:29:56.967720', 1, 'Molestiae qui molestiae quia. Eveniet dignissimos ab sit.', b'1', b'1', b'1', '夏日海鹽焦糖雪酪 P000020', 'P000020', 10, 'TAXABLE', '2025-07-01 14:29:56.967720', 1, 6, NULL, 7),
(21, 102.94, 'AVERAGE', '2025-07-01 14:29:56.968479', 1, 'Alias necessitatibus neque odit doloremque deleniti eius. Voluptatem adipisci dolorem aut.', b'1', b'1', b'1', '鮮果香草雪糕 P000021', 'P000021', 64, 'TAXABLE', '2025-07-01 14:29:56.968479', 1, 3, NULL, 1),
(22, 156.60, 'AVERAGE', '2025-07-01 14:29:56.969569', 1, 'Rem qui tempora et. Fugiat tempora eum. Eaque quasi architecto ullam nihil. Similique quia illo dolores officiis dignissimos quod.', b'1', b'1', b'1', '夏日蘭姆葡萄雪糕 P000022', 'P000022', 38, 'TAXABLE', '2025-07-01 14:29:56.969569', 1, 8, NULL, 6),
(23, 72.80, 'AVERAGE', '2025-07-01 14:29:56.970369', 1, 'Et magni voluptas. Velit qui suscipit facere dolorem.', b'1', b'1', b'1', '經典香蕉冰棒 P000023', 'P000023', 39, 'TAXABLE', '2025-07-01 14:29:56.970369', 1, 2, NULL, 7),
(24, 267.74, 'AVERAGE', '2025-07-01 14:29:56.971254', 1, 'Ipsa sit libero. Animi quis maxime ut quo sunt esse.', b'1', b'1', b'1', '雪藏蘭姆葡萄雪糕 P000024', 'P000024', 35, 'TAXABLE', '2025-07-01 14:29:56.971254', 1, 1, NULL, 2),
(25, 329.68, 'AVERAGE', '2025-07-01 14:29:56.972517', 1, 'Qui repellat illum recusandae. Molestiae rerum laborum.', b'1', b'1', b'1', '夏日花生聖代 P000025', 'P000025', 42, 'TAXABLE', '2025-07-01 14:29:56.972517', 1, 5, NULL, 5),
(26, 236.19, 'AVERAGE', '2025-07-01 14:29:56.973682', 1, 'Quas neque aspernatur temporibus quidem illo fuga. Animi ab ut. Ratione a dolor debitis eligendi. Debitis et enim nemo praesentium deserunt aut eveniet.', b'1', b'1', b'1', '雪藏蘭姆葡萄雪糕 P000026', 'P000026', 34, 'TAXABLE', '2025-07-01 14:29:56.973682', 1, 6, NULL, 1),
(27, 173.83, 'AVERAGE', '2025-07-01 14:29:56.974660', 1, 'Velit suscipit minima non saepe. Illo sit harum culpa sunt.', b'1', b'1', b'1', '夏日薄荷巧克力雪酪 P000027', 'P000027', 30, 'TAXABLE', '2025-07-01 14:29:56.974660', 1, 4, NULL, 3),
(28, 178.32, 'AVERAGE', '2025-07-01 14:29:56.975747', 1, 'Saepe quis dolores voluptatum rem. Vel ullam rem qui asperiores.', b'1', b'1', b'1', '夏日咖啡冰淇淋 P000028', 'P000028', 62, 'TAXABLE', '2025-07-01 14:29:56.975747', 1, 5, NULL, 6),
(29, 329.75, 'AVERAGE', '2025-07-01 14:29:56.979564', 1, 'In molestiae voluptas non molestiae et. Odio dolor et minus.', b'1', b'1', b'1', '莊園海鹽焦糖雪酪 P000029', 'P000029', 53, 'TAXABLE', '2025-07-01 14:29:56.979564', 1, 6, NULL, 2),
(30, 107.62, 'AVERAGE', '2025-07-01 14:29:56.981052', 1, 'Velit dolor iure ullam facere iste. Minus non quis nisi veniam natus et vel.', b'1', b'1', b'1', '夏日花生冰棒 P000030', 'P000030', 92, 'TAXABLE', '2025-07-01 14:29:56.981052', 1, 8, NULL, 6),
(31, 118.94, 'AVERAGE', '2025-07-01 14:29:56.982120', 1, 'Est cupiditate quod doloremque saepe et iste. Omnis facere consequatur accusantium.', b'1', b'1', b'1', '鮮果抹茶聖代 P000031', 'P000031', 63, 'TAXABLE', '2025-07-01 14:29:56.982120', 1, 6, NULL, 1),
(32, 104.08, 'AVERAGE', '2025-07-01 14:29:56.983203', 1, 'Velit et ut est dolores veniam. Sapiente voluptatibus fugiat nam dignissimos porro facere.', b'1', b'1', b'1', '夏日OREO冰淇淋 P000032', 'P000032', 59, 'TAXABLE', '2025-07-01 14:29:56.983203', 1, 3, NULL, 6),
(33, 309.23, 'AVERAGE', '2025-07-01 14:29:56.984062', 1, 'Provident repellendus adipisci quibusdam. Rerum saepe et perspiciatis ducimus qui quo iure.', b'1', b'1', b'1', '雪藏咖啡雪酪 P000033', 'P000033', 32, 'TAXABLE', '2025-07-01 14:29:56.984062', 1, 7, NULL, 6),
(34, 213.69, 'AVERAGE', '2025-07-01 14:29:56.985013', 1, 'Minima dolore omnis mollitia. Vero at officiis sunt illum sed. Consequatur reprehenderit saepe non aspernatur aut cum debitis.', b'1', b'1', b'1', '純濃海鹽焦糖冰棒 P000034', 'P000034', 72, 'TAXABLE', '2025-07-01 14:29:56.985013', 1, 4, NULL, 4),
(35, 234.74, 'AVERAGE', '2025-07-01 14:29:56.987004', 1, 'Eos distinctio repellendus placeat harum earum dolorum velit. Eos laudantium repudiandae temporibus voluptatibus. Molestias necessitatibus possimus id occaecati optio itaque. Asperiores ducimus dolores est quia.', b'1', b'1', b'1', '經典咖啡冰淇淋 P000035', 'P000035', 41, 'TAXABLE', '2025-07-01 14:29:56.987004', 1, 6, NULL, 5),
(36, 173.56, 'AVERAGE', '2025-07-01 14:29:56.988161', 1, 'Accusamus earum temporibus dolorem est aut nulla qui. Culpa ipsam pariatur.', b'1', b'1', b'1', '極致香草聖代 P000036', 'P000036', 98, 'TAXABLE', '2025-07-01 14:29:56.988161', 1, 5, NULL, 4),
(37, 197.37, 'AVERAGE', '2025-07-01 14:29:56.989934', 1, 'Est hic voluptatem quod ut suscipit. Assumenda reiciendis quia.', b'1', b'1', b'1', '夏日香蕉冰淇淋 P000037', 'P000037', 79, 'TAXABLE', '2025-07-01 14:29:56.989934', 1, 1, NULL, 5),
(38, 84.29, 'AVERAGE', '2025-07-01 14:29:56.990877', 1, 'Tempora eum dolorum necessitatibus est nihil voluptatibus. Consequuntur temporibus deleniti quisquam et et aliquam quam. Quam ea officiis ea. Officiis quia est quam voluptates officiis quo exercitationem.', b'1', b'1', b'1', '經典巧克力雪酪 P000038', 'P000038', 50, 'TAXABLE', '2025-07-01 14:29:56.990877', 1, 2, NULL, 1),
(39, 111.02, 'AVERAGE', '2025-07-01 14:29:56.991696', 1, 'Dignissimos porro unde laboriosam ea qui modi. Fugit maxime aliquam voluptatem totam alias qui iste. Nihil earum placeat.', b'1', b'1', b'1', '雪藏抹茶雪酪 P000039', 'P000039', 41, 'TAXABLE', '2025-07-01 14:29:56.991696', 1, 7, NULL, 1),
(40, 49.24, 'AVERAGE', '2025-07-01 14:29:56.992762', 1, 'Cumque qui facere hic qui officiis quia tempora. Distinctio voluptas et assumenda voluptatum minima quas dolorem.', b'1', b'1', b'1', '鮮果抹茶雪酪 P000040', 'P000040', 81, 'TAXABLE', '2025-07-01 14:29:56.992762', 1, 4, NULL, 4),
(41, 166.71, 'AVERAGE', '2025-07-01 14:29:56.994065', 1, 'Est quo vel. Aut repellendus sunt.', b'1', b'1', b'1', '莊園香蕉聖代 P000041', 'P000041', 76, 'TAXABLE', '2025-07-01 14:29:56.994065', 1, 6, NULL, 7),
(42, 190.22, 'AVERAGE', '2025-07-01 14:29:56.995269', 1, 'Quia quasi sed culpa et dolorum ducimus repellat. Id enim omnis doloribus labore ullam. Sit est nihil est occaecati. Eligendi iure reiciendis possimus rerum autem.', b'1', b'1', b'1', '夏日香蕉冰棒 P000042', 'P000042', 66, 'TAXABLE', '2025-07-01 14:29:56.995269', 1, 3, NULL, 5),
(43, 247.14, 'AVERAGE', '2025-07-01 14:29:56.996088', 1, 'Possimus dolores autem dolor. Rerum asperiores modi qui officiis fuga.', b'1', b'1', b'1', '雪藏藍莓雪酪 P000043', 'P000043', 29, 'TAXABLE', '2025-07-01 14:29:56.996088', 1, 6, NULL, 7),
(44, 84.11, 'AVERAGE', '2025-07-01 14:29:56.996967', 1, 'Consequatur sit dolorum vel. Aut vel quidem voluptatem doloribus voluptatem maiores ut. Dignissimos quod est.', b'1', b'1', b'1', '雪藏草莓聖代 P000044', 'P000044', 67, 'TAXABLE', '2025-07-01 14:29:56.996967', 1, 5, NULL, 1),
(45, 335.06, 'AVERAGE', '2025-07-01 14:29:56.997648', 1, 'Temporibus ad aut. Dolores beatae ipsum ipsum quas omnis dolores consequatur.', b'1', b'1', b'1', '莊園燕麥奶冰棒 P000045', 'P000045', 43, 'TAXABLE', '2025-07-01 14:29:56.997648', 1, 7, NULL, 7),
(46, 331.23, 'AVERAGE', '2025-07-01 14:29:56.998321', 1, 'Excepturi minus ad suscipit molestias possimus aliquam. Repellendus cupiditate ut dolor. Non ut ea sed amet.', b'1', b'1', b'1', '雪藏花生雪糕 P000046', 'P000046', 77, 'TAXABLE', '2025-07-01 14:29:56.998321', 1, 3, NULL, 3),
(47, 272.55, 'AVERAGE', '2025-07-01 14:29:56.998983', 1, 'Est fugiat laborum incidunt dignissimos eum numquam. Explicabo ea iste suscipit sit beatae delectus commodi. Rem maxime fugiat sit ex quia pariatur beatae. Quas est a ex aut autem labore.', b'1', b'1', b'1', '雪藏香蕉雪酪 P000047', 'P000047', 54, 'TAXABLE', '2025-07-01 14:29:56.998983', 1, 6, NULL, 2),
(48, 185.82, 'AVERAGE', '2025-07-01 14:29:56.999635', 1, 'Qui vel tempore tenetur eos. Magni ducimus eveniet vel nihil. Quia laudantium ut aut autem et cumque sit. Repudiandae enim rerum.', b'1', b'1', b'1', '雪藏抹茶冰淇淋 P000048', 'P000048', 33, 'TAXABLE', '2025-07-01 14:29:56.999635', 1, 6, NULL, 7),
(49, 327.87, 'AVERAGE', '2025-07-01 14:29:57.000329', 1, 'Harum maiores cupiditate amet quis facere in ex. Dolores reiciendis porro labore. Modi sed enim. Perspiciatis vitae culpa magnam debitis dolores.', b'1', b'1', b'1', '純濃蘭姆葡萄冰棒 P000049', 'P000049', 52, 'TAXABLE', '2025-07-01 14:29:57.000329', 1, 5, NULL, 3),
(50, 230.99, 'AVERAGE', '2025-07-01 14:29:57.001028', 1, 'Dignissimos mollitia quasi. Voluptatibus est sapiente magnam labore corporis voluptatum.', b'1', b'1', b'1', '極致草莓雪糕 P000050', 'P000050', 43, 'TAXABLE', '2025-07-01 14:29:57.001028', 1, 4, NULL, 7);

-- --------------------------------------------------------

--
-- 資料表結構 `product_categories`
--

CREATE TABLE `product_categories` (
  `category_id` bigint NOT NULL,
  `created_by` bigint NOT NULL,
  `name` varchar(100) NOT NULL,
  `updated_by` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 傾印資料表的資料 `product_categories`
--

INSERT INTO `product_categories` (`category_id`, `created_by`, `name`, `updated_by`) VALUES
(1, 1, '經典冰淇淋', 1),
(2, 1, '水果雪酪', 1),
(3, 1, '雪糕系列', 1),
(4, 1, '巧酥雪糕系列', 1),
(5, 1, '季節限定', 1),
(6, 1, '純素系列', 1),
(7, 1, '品牌聯名系列', 1),
(8, 1, '週邊商品', 1);

-- --------------------------------------------------------

--
-- 資料表結構 `purchase_orders`
--

CREATE TABLE `purchase_orders` (
  `purchase_order_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `currency` varchar(10) NOT NULL,
  `order_date` date NOT NULL,
  `order_number` varchar(50) NOT NULL,
  `remarks` text,
  `status` enum('CANCELLED','CONFIRMED','DRAFT','PARTIALLY_RECEIVED','RECEIVED') NOT NULL,
  `total_amount` decimal(18,2) NOT NULL,
  `total_cost_amount` decimal(18,2) NOT NULL,
  `total_net_amount` decimal(18,2) NOT NULL,
  `total_tax_amount` decimal(18,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `updated_by` bigint NOT NULL,
  `warehouse_id` bigint DEFAULT NULL,
  `supplier_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `purchase_order_details`
--

CREATE TABLE `purchase_order_details` (
  `item_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `is_gift` bit(1) NOT NULL,
  `item_amount` decimal(18,2) NOT NULL,
  `item_net_amount` decimal(18,2) NOT NULL,
  `item_tax_amount` decimal(18,2) NOT NULL,
  `quantity` decimal(18,2) NOT NULL,
  `unit_price` decimal(18,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `updated_by` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `purchase_order_id` bigint NOT NULL,
  `warehouse_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `sales_orders`
--

CREATE TABLE `sales_orders` (
  `sales_order_id` bigint NOT NULL,
  `contact_person_id` bigint DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `currency` varchar(10) NOT NULL,
  `estimated_delivery_date` date DEFAULT NULL,
  `order_date` date NOT NULL,
  `order_number` varchar(50) NOT NULL,
  `order_status` enum('CANCELLED','COMPLETED','CONFIRMED','DRAFT','PARTIAL_SHIPPED','PENDING','SHIPPED') NOT NULL,
  `payment_method` varchar(50) NOT NULL,
  `payment_status` enum('PAID','REFUNDED','REFUNDING','UNPAID') NOT NULL,
  `quotation_id` bigint DEFAULT NULL,
  `remarks` text,
  `shipping_address` varchar(500) NOT NULL,
  `shipping_method` varchar(50) NOT NULL,
  `total_amount` decimal(18,2) NOT NULL,
  `total_net_amount` decimal(18,2) NOT NULL,
  `total_tax_amount` decimal(18,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `updated_by` bigint NOT NULL,
  `customer_id` bigint DEFAULT NULL,
  `warehouse_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `sales_order_details`
--

CREATE TABLE `sales_order_details` (
  `item_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `discount_rate` decimal(18,2) DEFAULT NULL,
  `item_amount` decimal(18,2) NOT NULL,
  `item_net_amount` decimal(18,2) NOT NULL,
  `item_sequence` int NOT NULL,
  `item_tax_amount` decimal(18,2) NOT NULL,
  `quantity` decimal(18,2) NOT NULL,
  `remarks` text,
  `specification` varchar(255) DEFAULT NULL,
  `transaction_number` varchar(50) DEFAULT NULL,
  `unit_id` bigint NOT NULL,
  `unit_price` decimal(18,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `updated_by` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `sales_order_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `sales_shipments`
--

CREATE TABLE `sales_shipments` (
  `shipment_id` bigint NOT NULL,
  `status` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `shipment_date` date NOT NULL,
  `shipment_number` varchar(255) NOT NULL,
  `shipping_method` varchar(255) NOT NULL,
  `tracking_number` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `updated_by` bigint NOT NULL,
  `customer_id` bigint NOT NULL,
  `sales_order_id` bigint DEFAULT NULL,
  `warehouse_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `sales_shipment_details`
--

CREATE TABLE `sales_shipment_details` (
  `item_id` bigint NOT NULL,
  `sales_order_item_id` bigint DEFAULT NULL,
  `shipped_quantity` decimal(38,2) NOT NULL,
  `product_id` bigint NOT NULL,
  `shipment_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `suppliers`
--

CREATE TABLE `suppliers` (
  `supplier_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `is_active` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `supplier_code` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `tags`
--

CREATE TABLE `tags` (
  `tag_id` bigint NOT NULL,
  `color` varchar(100) DEFAULT NULL,
  `tag_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 傾印資料表的資料 `tags`
--

INSERT INTO `tags` (`tag_id`, `color`, `tag_name`) VALUES
(1, '#D4EDDA', '新客戶'),
(2, '#FFF3CD', '潛在客戶'),
(3, '#E9ECEF', 'VIP客戶'),
(4, '#D1ECF1', '一般客戶'),
(5, '#F8D7DA', '戰略合作');

-- --------------------------------------------------------

--
-- 資料表結構 `units`
--

CREATE TABLE `units` (
  `unit_id` bigint NOT NULL,
  `created_by` bigint NOT NULL,
  `name` varchar(50) NOT NULL,
  `updated_by` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 傾印資料表的資料 `units`
--

INSERT INTO `units` (`unit_id`, `created_by`, `name`, `updated_by`) VALUES
(1, 1, '個', 1),
(2, 1, '箱', 1),
(3, 1, '打', 1),
(4, 1, '公克', 1),
(5, 1, '支', 1),
(6, 1, '杯', 1),
(7, 1, '盒', 1);

-- --------------------------------------------------------

--
-- 資料表結構 `user`
--

CREATE TABLE `user` (
  `user_id` bigint NOT NULL,
  `access_end_date` date DEFAULT NULL,
  `access_start_date` date DEFAULT NULL,
  `account` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `is_deleted` bit(1) NOT NULL,
  `last_login` datetime(6) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `role_name` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `user_name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 傾印資料表的資料 `user`
--

INSERT INTO `user` (`user_id`, `access_end_date`, `access_start_date`, `account`, `created_at`, `email`, `is_active`, `is_deleted`, `last_login`, `password`, `role_name`, `updated_at`, `user_name`) VALUES
(1, '2035-07-01', '2025-07-01', 'admin', '2025-07-01 14:29:56.163461', 'admin@system.com', b'1', b'0', NULL, '$2a$10$RLsYI0iCA4N7PJJbp9ITi.tBzcptJtGvhlRd4BXgfyVDUZRepEd.q', 'ADMIN', '2025-07-01 14:29:56.163462', '超級管理員');

-- --------------------------------------------------------

--
-- 資料表結構 `userlogaction`
--

CREATE TABLE `userlogaction` (
  `log_id` bigint NOT NULL,
  `action_type` enum('CREATE_CONTENT','CREATE_ORDER','DELETE_CONTENT','DELETE_CUSTOMER','DELETE_ORDER','FORCE_RESET_PASSWORD','LOGIN','LOGOUT','OTHER','UPDATE_CONTENT','UPDATE_CUSTOMER','UPDATE_ORDER','VIEW_REPORT') NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `target_id` varchar(255) DEFAULT NULL,
  `target_table` varchar(255) DEFAULT NULL,
  `user_agent` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- --------------------------------------------------------

--
-- 資料表結構 `user_authority`
--

CREATE TABLE `user_authority` (
  `user_id` bigint NOT NULL,
  `authority_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 傾印資料表的資料 `user_authority`
--

INSERT INTO `user_authority` (`user_id`, `authority_id`) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(1, 7),
(1, 8),
(1, 9),
(1, 10),
(1, 11),
(1, 12),
(1, 13),
(1, 14),
(1, 15),
(1, 16),
(1, 17),
(1, 18),
(1, 19);

-- --------------------------------------------------------

--
-- 資料表結構 `viplevel`
--

CREATE TABLE `viplevel` (
  `level` varchar(255) NOT NULL,
  `bonus_description` varchar(255) DEFAULT NULL,
  `downgrade_threshold` double NOT NULL,
  `upgrade_threshold` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 傾印資料表的資料 `viplevel`
--

INSERT INTO `viplevel` (`level`, `bonus_description`, `downgrade_threshold`, `upgrade_threshold`) VALUES
('BRONZE', '歡迎使用者', 0, 0),
('GOLD', '9折優惠', 20000, 50000),
('SILVER', '滿萬免運', 5000, 10000);

-- --------------------------------------------------------

--
-- 資料表結構 `warehouses`
--

CREATE TABLE `warehouses` (
  `warehouse_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `is_active` bit(1) NOT NULL,
  `name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

--
-- 已傾印資料表的索引
--

--
-- 資料表索引 `activities`
--
ALTER TABLE `activities`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKmdpx4pw34wl4uv1pnuiqfapgv` (`contact_id`),
  ADD KEY `FKtehic8fiattwna4dgrmw35w60` (`opportunity_id`);

--
-- 資料表索引 `authority`
--
ALTER TABLE `authority`
  ADD PRIMARY KEY (`authority_id`),
  ADD UNIQUE KEY `UKh5tdku9skqaaxxljo3vqxvju0` (`code`);

--
-- 資料表索引 `b_customers`
--
ALTER TABLE `b_customers`
  ADD PRIMARY KEY (`customer_id`),
  ADD UNIQUE KEY `UK1kuhvki1434eep06kfadk0qf0` (`tin_number`);

--
-- 資料表索引 `b_customer_tags`
--
ALTER TABLE `b_customer_tags`
  ADD PRIMARY KEY (`b_customer_id`,`tag_id`),
  ADD KEY `FKlk5ti2osykjuke10a14byem3m` (`tag_id`);

--
-- 資料表索引 `cartdetails`
--
ALTER TABLE `cartdetails`
  ADD PRIMARY KEY (`cartdetailid`),
  ADD KEY `FK5d6sesjcf708fol2awjfkp5m2` (`cartid`),
  ADD KEY `FK33ahqfskwgdsbbwe4023lg0io` (`productid`);

--
-- 資料表索引 `carts`
--
ALTER TABLE `carts`
  ADD PRIMARY KEY (`cartid`),
  ADD UNIQUE KEY `UK88sv4i13lo80s74ox7rsb5a2c` (`customer_id`);

--
-- 資料表索引 `contacts`
--
ALTER TABLE `contacts`
  ADD PRIMARY KEY (`contact_id`),
  ADD KEY `FKegxv6x070lx2cqqmjpki5yoko` (`customer_id`);

--
-- 資料表索引 `contracts`
--
ALTER TABLE `contracts`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKbx9jyu2cccdntb3ehrf0ojpfd` (`contract_number`),
  ADD UNIQUE KEY `UKokulr0jeofpww3j8dv8rv6ks8` (`opportunity_id`),
  ADD KEY `FKc4rwpitwmdksdm08ll7ogxqhk` (`contact_id`);

--
-- 資料表索引 `coupon_templates`
--
ALTER TABLE `coupon_templates`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK9iq8r9g4b82yayyhcxgewj2vs` (`code`),
  ADD KEY `FK23aje09vrafass4ap2eysj2it` (`target_vip_level`);

--
-- 資料表索引 `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`customer_id`),
  ADD UNIQUE KEY `UKrq3uk4qw27cjdp3nio09461ca` (`account`),
  ADD KEY `FKqhc0ik3v3o94pqbqdx5v430n7` (`vip_level`);

--
-- 資料表索引 `customer_address`
--
ALTER TABLE `customer_address`
  ADD PRIMARY KEY (`addressid`),
  ADD KEY `FKr9ofa0ydsgbaqmt9leb3v5eii` (`customer_id`);

--
-- 資料表索引 `customer_base`
--
ALTER TABLE `customer_base`
  ADD PRIMARY KEY (`customer_id`),
  ADD UNIQUE KEY `UK4da2fid51400x99sq5u5080p` (`customer_code`);

--
-- 資料表索引 `customer_coupons`
--
ALTER TABLE `customer_coupons`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKfesneqm9491j79s6x300o3o0g` (`order_id`),
  ADD KEY `FKsqw2cyvpv6kontx3bgk98312v` (`coupon_template_id`),
  ADD KEY `FK2xh7flxxfqpn6prhw5n06l4nn` (`customer_id`);

--
-- 資料表索引 `employees`
--
ALTER TABLE `employees`
  ADD PRIMARY KEY (`employeeid`);

--
-- 資料表索引 `inventories`
--
ALTER TABLE `inventories`
  ADD PRIMARY KEY (`inventory_id`),
  ADD KEY `FK8drmqyx629j3oo8ct9jnc5y3y` (`product_id`),
  ADD KEY `FKoipfe4s81wodvutx9i0rlmoyi` (`warehouse_id`);

--
-- 資料表索引 `inventory_adjustments`
--
ALTER TABLE `inventory_adjustments`
  ADD PRIMARY KEY (`adjustment_id`),
  ADD UNIQUE KEY `UKg5gf65kcswr83kibc64h4vi5y` (`adjustment_number`),
  ADD KEY `FKh6sr665dmelt6l0ul71dtxmn3` (`warehouse_id`);

--
-- 資料表索引 `inventory_adjustment_details`
--
ALTER TABLE `inventory_adjustment_details`
  ADD PRIMARY KEY (`item_id`),
  ADD KEY `FKlucryot3co8b4kvbq7yyd9mx2` (`adjustment_id`),
  ADD KEY `FKoi70v3livt91gwc1g8y1em5ww` (`product_id`);

--
-- 資料表索引 `inventory_movements`
--
ALTER TABLE `inventory_movements`
  ADD PRIMARY KEY (`movement_id`),
  ADD KEY `FK7lws1ve8g6b9itc054wj06uit` (`product_id`),
  ADD KEY `FKnlai5v2tcj5netbq0drcgdmg6` (`recorded_by`),
  ADD KEY `FK81bnkiybh5iegjy19inmbach0` (`warehouse_id`);

--
-- 資料表索引 `message`
--
ALTER TABLE `message`
  ADD PRIMARY KEY (`message_id`),
  ADD KEY `FKg2pmh6bgwe0dqobetgwdrv47d` (`customer_id`);

--
-- 資料表索引 `message_reply`
--
ALTER TABLE `message_reply`
  ADD PRIMARY KEY (`reply_id`),
  ADD KEY `FK90b8vnf08va1ry61qud7ehq1r` (`customer_id`),
  ADD KEY `FKhantgjt6w2sfw3s28v3n5lvid` (`message_id`),
  ADD KEY `FK2ukemcrx8wqyg7e27ab8gd2d2` (`user_id`);

--
-- 資料表索引 `opportunities`
--
ALTER TABLE `opportunities`
  ADD PRIMARY KEY (`opportunity_id`),
  ADD KEY `FK8wo5vk4ua84l6jraboeviqpf5` (`customer_id`),
  ADD KEY `FK5gqptcylo65yohtvwxgxoiks2` (`contact_id`);

--
-- 資料表索引 `opportunity_tags`
--
ALTER TABLE `opportunity_tags`
  ADD PRIMARY KEY (`opportunity_id`,`tag_id`),
  ADD KEY `FKl2sqju4kyjb0wdajff9ofupqb` (`tag_id`);

--
-- 資料表索引 `orderdetails`
--
ALTER TABLE `orderdetails`
  ADD PRIMARY KEY (`orderid`,`productid`),
  ADD KEY `FKaltatpxipsjtcih4d1h6bn0xr` (`productid`);

--
-- 資料表索引 `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`orderid`),
  ADD UNIQUE KEY `UK9k1cmmljc8hejrfa1ona2p85t` (`merchant_trade_no`),
  ADD KEY `FK624gtjin3po807j3vix093tlf` (`customer_id`),
  ADD KEY `FKd8djrcs4tdtenhcpompfwda0a` (`addressid`),
  ADD KEY `FKk6yq5p77trsqsk88eebguoyey` (`platformid`);

--
-- 資料表索引 `password_reset_token`
--
ALTER TABLE `password_reset_token`
  ADD PRIMARY KEY (`id`);

--
-- 資料表索引 `platforms`
--
ALTER TABLE `platforms`
  ADD PRIMARY KEY (`platformid`),
  ADD KEY `FK5y2g5mglrsja1xuwxfoj3koo4` (`employeeid`);

--
-- 資料表索引 `productimgs`
--
ALTER TABLE `productimgs`
  ADD PRIMARY KEY (`imgid`),
  ADD KEY `FKc9qa259om9qdfifq11q7m88qj` (`productid`);

--
-- 資料表索引 `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`product_id`),
  ADD UNIQUE KEY `UK922x4t23nx64422orei4meb2y` (`product_code`),
  ADD KEY `FK6t5dtw6tyo83ywljwohuc6g7k` (`category_id`),
  ADD KEY `FKbco7oo78jbvr88y7myi2llwvg` (`created_by`),
  ADD KEY `FK6i174ixi9087gcvvut45em7fd` (`supplier_id`),
  ADD KEY `FKeex0i50vfsa5imebrfdiyhmp9` (`unit_id`),
  ADD KEY `FKlddmkmfh5tq2egaospdtn4fy5` (`updated_by`);

--
-- 資料表索引 `product_categories`
--
ALTER TABLE `product_categories`
  ADD PRIMARY KEY (`category_id`),
  ADD UNIQUE KEY `UKfl075bwasjwsxybk4x174befx` (`name`);

--
-- 資料表索引 `purchase_orders`
--
ALTER TABLE `purchase_orders`
  ADD PRIMARY KEY (`purchase_order_id`),
  ADD UNIQUE KEY `UKnqsdqb8p2iobsmeaa2jxxw7k` (`order_number`),
  ADD KEY `FKrpdasmb8y8xs5tiy4369xpinq` (`supplier_id`);

--
-- 資料表索引 `purchase_order_details`
--
ALTER TABLE `purchase_order_details`
  ADD PRIMARY KEY (`item_id`),
  ADD KEY `FKpouou78xjt37rqmveufppyhtp` (`product_id`),
  ADD KEY `FK7k5h72ashr7waatbffpug92ei` (`purchase_order_id`),
  ADD KEY `FKkxd14v33nprv58sla1vrhfi2p` (`warehouse_id`);

--
-- 資料表索引 `sales_orders`
--
ALTER TABLE `sales_orders`
  ADD PRIMARY KEY (`sales_order_id`),
  ADD UNIQUE KEY `UK710tqn7k0rkp3ubriqlh0woyp` (`order_number`),
  ADD KEY `FKr7yhnihih2sge0tsenxvdt1dy` (`customer_id`),
  ADD KEY `FK7dfsnse1mhb9m2pbmvu842sxu` (`warehouse_id`);

--
-- 資料表索引 `sales_order_details`
--
ALTER TABLE `sales_order_details`
  ADD PRIMARY KEY (`item_id`),
  ADD UNIQUE KEY `UK2fci7bf0rrmlvhcytegxxm5pd` (`transaction_number`),
  ADD KEY `FKncwh5cjveg0gvgko7m77qawva` (`product_id`),
  ADD KEY `FKpy7rkbtos35e8ajjplvcp836v` (`sales_order_id`);

--
-- 資料表索引 `sales_shipments`
--
ALTER TABLE `sales_shipments`
  ADD PRIMARY KEY (`shipment_id`),
  ADD UNIQUE KEY `UK26hk9bmcqflwx94rmdbbcojyn` (`shipment_number`),
  ADD KEY `FKjv431n38r6rknovu8ne1gq4ud` (`customer_id`),
  ADD KEY `FKd82xyktlw82wsy97u6i6epe57` (`sales_order_id`),
  ADD KEY `FKqwjqo5yqf5kwnub7191kesqwe` (`warehouse_id`);

--
-- 資料表索引 `sales_shipment_details`
--
ALTER TABLE `sales_shipment_details`
  ADD PRIMARY KEY (`item_id`),
  ADD KEY `FKlrf6gbglckg2b0smh0jbbpmi2` (`product_id`),
  ADD KEY `FK79jt6cfdlrd0tfgdfvd07hypj` (`shipment_id`);

--
-- 資料表索引 `suppliers`
--
ALTER TABLE `suppliers`
  ADD PRIMARY KEY (`supplier_id`),
  ADD UNIQUE KEY `UKqlclyj0vn5vwtb86objyhmlkx` (`supplier_code`);

--
-- 資料表索引 `tags`
--
ALTER TABLE `tags`
  ADD PRIMARY KEY (`tag_id`),
  ADD UNIQUE KEY `UK2c6s9hekidseaj5vbgb3pgy3k` (`tag_name`);

--
-- 資料表索引 `units`
--
ALTER TABLE `units`
  ADD PRIMARY KEY (`unit_id`),
  ADD UNIQUE KEY `UKetw07nfppovq9p7ov8hcb38wy` (`name`);

--
-- 資料表索引 `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `UKdnq7r8jcmlft7l8l4j79l1h74` (`account`),
  ADD UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`);

--
-- 資料表索引 `userlogaction`
--
ALTER TABLE `userlogaction`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `FKgtidi4yjf3vgwimfll6tp1e22` (`user_id`);

--
-- 資料表索引 `user_authority`
--
ALTER TABLE `user_authority`
  ADD KEY `FKgvxjs381k6f48d5d2yi11uh89` (`authority_id`),
  ADD KEY `FKpqlsjpkybgos9w2svcri7j8xy` (`user_id`);

--
-- 資料表索引 `viplevel`
--
ALTER TABLE `viplevel`
  ADD PRIMARY KEY (`level`);

--
-- 資料表索引 `warehouses`
--
ALTER TABLE `warehouses`
  ADD PRIMARY KEY (`warehouse_id`),
  ADD UNIQUE KEY `UK2qm0l82n5ivhyqwmgejxxefm1` (`name`);

--
-- 在傾印的資料表使用自動遞增(AUTO_INCREMENT)
--

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `activities`
--
ALTER TABLE `activities`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `authority`
--
ALTER TABLE `authority`
  MODIFY `authority_id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `cartdetails`
--
ALTER TABLE `cartdetails`
  MODIFY `cartdetailid` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `carts`
--
ALTER TABLE `carts`
  MODIFY `cartid` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `contacts`
--
ALTER TABLE `contacts`
  MODIFY `contact_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `contracts`
--
ALTER TABLE `contracts`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `coupon_templates`
--
ALTER TABLE `coupon_templates`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `customer_address`
--
ALTER TABLE `customer_address`
  MODIFY `addressid` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `customer_base`
--
ALTER TABLE `customer_base`
  MODIFY `customer_id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `customer_coupons`
--
ALTER TABLE `customer_coupons`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `employees`
--
ALTER TABLE `employees`
  MODIFY `employeeid` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `inventories`
--
ALTER TABLE `inventories`
  MODIFY `inventory_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `inventory_adjustments`
--
ALTER TABLE `inventory_adjustments`
  MODIFY `adjustment_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `inventory_adjustment_details`
--
ALTER TABLE `inventory_adjustment_details`
  MODIFY `item_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `inventory_movements`
--
ALTER TABLE `inventory_movements`
  MODIFY `movement_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `message`
--
ALTER TABLE `message`
  MODIFY `message_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `message_reply`
--
ALTER TABLE `message_reply`
  MODIFY `reply_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `opportunities`
--
ALTER TABLE `opportunities`
  MODIFY `opportunity_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `orders`
--
ALTER TABLE `orders`
  MODIFY `orderid` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `password_reset_token`
--
ALTER TABLE `password_reset_token`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `platforms`
--
ALTER TABLE `platforms`
  MODIFY `platformid` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `productimgs`
--
ALTER TABLE `productimgs`
  MODIFY `imgid` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `products`
--
ALTER TABLE `products`
  MODIFY `product_id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=51;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `product_categories`
--
ALTER TABLE `product_categories`
  MODIFY `category_id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `purchase_orders`
--
ALTER TABLE `purchase_orders`
  MODIFY `purchase_order_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `purchase_order_details`
--
ALTER TABLE `purchase_order_details`
  MODIFY `item_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `sales_orders`
--
ALTER TABLE `sales_orders`
  MODIFY `sales_order_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `sales_order_details`
--
ALTER TABLE `sales_order_details`
  MODIFY `item_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `sales_shipments`
--
ALTER TABLE `sales_shipments`
  MODIFY `shipment_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `sales_shipment_details`
--
ALTER TABLE `sales_shipment_details`
  MODIFY `item_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `suppliers`
--
ALTER TABLE `suppliers`
  MODIFY `supplier_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `tags`
--
ALTER TABLE `tags`
  MODIFY `tag_id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `units`
--
ALTER TABLE `units`
  MODIFY `unit_id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `user`
--
ALTER TABLE `user`
  MODIFY `user_id` bigint NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `userlogaction`
--
ALTER TABLE `userlogaction`
  MODIFY `log_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 使用資料表自動遞增(AUTO_INCREMENT) `warehouses`
--
ALTER TABLE `warehouses`
  MODIFY `warehouse_id` bigint NOT NULL AUTO_INCREMENT;

--
-- 已傾印資料表的限制式
--

--
-- 資料表的限制式 `activities`
--
ALTER TABLE `activities`
  ADD CONSTRAINT `FKmdpx4pw34wl4uv1pnuiqfapgv` FOREIGN KEY (`contact_id`) REFERENCES `contacts` (`contact_id`),
  ADD CONSTRAINT `FKtehic8fiattwna4dgrmw35w60` FOREIGN KEY (`opportunity_id`) REFERENCES `opportunities` (`opportunity_id`);

--
-- 資料表的限制式 `b_customers`
--
ALTER TABLE `b_customers`
  ADD CONSTRAINT `FKmghkjohsqhihvce1o3156hf98` FOREIGN KEY (`customer_id`) REFERENCES `customer_base` (`customer_id`);

--
-- 資料表的限制式 `b_customer_tags`
--
ALTER TABLE `b_customer_tags`
  ADD CONSTRAINT `FKb219xh2p0ntu812ac3u1ult4o` FOREIGN KEY (`b_customer_id`) REFERENCES `b_customers` (`customer_id`),
  ADD CONSTRAINT `FKlk5ti2osykjuke10a14byem3m` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`tag_id`);

--
-- 資料表的限制式 `cartdetails`
--
ALTER TABLE `cartdetails`
  ADD CONSTRAINT `FK33ahqfskwgdsbbwe4023lg0io` FOREIGN KEY (`productid`) REFERENCES `products` (`product_id`),
  ADD CONSTRAINT `FK5d6sesjcf708fol2awjfkp5m2` FOREIGN KEY (`cartid`) REFERENCES `carts` (`cartid`);

--
-- 資料表的限制式 `carts`
--
ALTER TABLE `carts`
  ADD CONSTRAINT `FK7ltuqgyyak6nuuddwlsy93uje` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`);

--
-- 資料表的限制式 `contacts`
--
ALTER TABLE `contacts`
  ADD CONSTRAINT `FKegxv6x070lx2cqqmjpki5yoko` FOREIGN KEY (`customer_id`) REFERENCES `b_customers` (`customer_id`);

--
-- 資料表的限制式 `contracts`
--
ALTER TABLE `contracts`
  ADD CONSTRAINT `FK9dd3tgcxk5lmj35gfjkwemiab` FOREIGN KEY (`opportunity_id`) REFERENCES `opportunities` (`opportunity_id`),
  ADD CONSTRAINT `FKc4rwpitwmdksdm08ll7ogxqhk` FOREIGN KEY (`contact_id`) REFERENCES `contacts` (`contact_id`);

--
-- 資料表的限制式 `coupon_templates`
--
ALTER TABLE `coupon_templates`
  ADD CONSTRAINT `FK23aje09vrafass4ap2eysj2it` FOREIGN KEY (`target_vip_level`) REFERENCES `viplevel` (`level`);

--
-- 資料表的限制式 `customer`
--
ALTER TABLE `customer`
  ADD CONSTRAINT `FKefbmdnftcpj21yldmyw2wgut2` FOREIGN KEY (`customer_id`) REFERENCES `customer_base` (`customer_id`),
  ADD CONSTRAINT `FKqhc0ik3v3o94pqbqdx5v430n7` FOREIGN KEY (`vip_level`) REFERENCES `viplevel` (`level`);

--
-- 資料表的限制式 `customer_address`
--
ALTER TABLE `customer_address`
  ADD CONSTRAINT `FKr9ofa0ydsgbaqmt9leb3v5eii` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`);

--
-- 資料表的限制式 `customer_coupons`
--
ALTER TABLE `customer_coupons`
  ADD CONSTRAINT `FK2xh7flxxfqpn6prhw5n06l4nn` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  ADD CONSTRAINT `FKjaaw8atga6khwyv2ofcvp57l9` FOREIGN KEY (`order_id`) REFERENCES `orders` (`orderid`),
  ADD CONSTRAINT `FKsqw2cyvpv6kontx3bgk98312v` FOREIGN KEY (`coupon_template_id`) REFERENCES `coupon_templates` (`id`);

--
-- 資料表的限制式 `inventories`
--
ALTER TABLE `inventories`
  ADD CONSTRAINT `FK8drmqyx629j3oo8ct9jnc5y3y` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  ADD CONSTRAINT `FKoipfe4s81wodvutx9i0rlmoyi` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`);

--
-- 資料表的限制式 `inventory_adjustments`
--
ALTER TABLE `inventory_adjustments`
  ADD CONSTRAINT `FKh6sr665dmelt6l0ul71dtxmn3` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`);

--
-- 資料表的限制式 `inventory_adjustment_details`
--
ALTER TABLE `inventory_adjustment_details`
  ADD CONSTRAINT `FKlucryot3co8b4kvbq7yyd9mx2` FOREIGN KEY (`adjustment_id`) REFERENCES `inventory_adjustments` (`adjustment_id`),
  ADD CONSTRAINT `FKoi70v3livt91gwc1g8y1em5ww` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- 資料表的限制式 `inventory_movements`
--
ALTER TABLE `inventory_movements`
  ADD CONSTRAINT `FK7lws1ve8g6b9itc054wj06uit` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  ADD CONSTRAINT `FK81bnkiybh5iegjy19inmbach0` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`),
  ADD CONSTRAINT `FKnlai5v2tcj5netbq0drcgdmg6` FOREIGN KEY (`recorded_by`) REFERENCES `user` (`user_id`);

--
-- 資料表的限制式 `message`
--
ALTER TABLE `message`
  ADD CONSTRAINT `FKg2pmh6bgwe0dqobetgwdrv47d` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`);

--
-- 資料表的限制式 `message_reply`
--
ALTER TABLE `message_reply`
  ADD CONSTRAINT `FK2ukemcrx8wqyg7e27ab8gd2d2` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  ADD CONSTRAINT `FK90b8vnf08va1ry61qud7ehq1r` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  ADD CONSTRAINT `FKhantgjt6w2sfw3s28v3n5lvid` FOREIGN KEY (`message_id`) REFERENCES `message` (`message_id`);

--
-- 資料表的限制式 `opportunities`
--
ALTER TABLE `opportunities`
  ADD CONSTRAINT `FK5gqptcylo65yohtvwxgxoiks2` FOREIGN KEY (`contact_id`) REFERENCES `contacts` (`contact_id`),
  ADD CONSTRAINT `FK8wo5vk4ua84l6jraboeviqpf5` FOREIGN KEY (`customer_id`) REFERENCES `b_customers` (`customer_id`);

--
-- 資料表的限制式 `opportunity_tags`
--
ALTER TABLE `opportunity_tags`
  ADD CONSTRAINT `FKa1flhnurbado7yvfkqefkcdem` FOREIGN KEY (`opportunity_id`) REFERENCES `opportunities` (`opportunity_id`),
  ADD CONSTRAINT `FKl2sqju4kyjb0wdajff9ofupqb` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`tag_id`);

--
-- 資料表的限制式 `orderdetails`
--
ALTER TABLE `orderdetails`
  ADD CONSTRAINT `FKaltatpxipsjtcih4d1h6bn0xr` FOREIGN KEY (`productid`) REFERENCES `products` (`product_id`),
  ADD CONSTRAINT `FKj4gc2ja2otvwemf4rho2lp3s8` FOREIGN KEY (`orderid`) REFERENCES `orders` (`orderid`);

--
-- 資料表的限制式 `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `FK624gtjin3po807j3vix093tlf` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  ADD CONSTRAINT `FKd8djrcs4tdtenhcpompfwda0a` FOREIGN KEY (`addressid`) REFERENCES `customer_address` (`addressid`),
  ADD CONSTRAINT `FKk6yq5p77trsqsk88eebguoyey` FOREIGN KEY (`platformid`) REFERENCES `platforms` (`platformid`);

--
-- 資料表的限制式 `platforms`
--
ALTER TABLE `platforms`
  ADD CONSTRAINT `FK5y2g5mglrsja1xuwxfoj3koo4` FOREIGN KEY (`employeeid`) REFERENCES `employees` (`employeeid`);

--
-- 資料表的限制式 `productimgs`
--
ALTER TABLE `productimgs`
  ADD CONSTRAINT `FKc9qa259om9qdfifq11q7m88qj` FOREIGN KEY (`productid`) REFERENCES `products` (`product_id`);

--
-- 資料表的限制式 `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `FK6i174ixi9087gcvvut45em7fd` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`),
  ADD CONSTRAINT `FK6t5dtw6tyo83ywljwohuc6g7k` FOREIGN KEY (`category_id`) REFERENCES `product_categories` (`category_id`),
  ADD CONSTRAINT `FKbco7oo78jbvr88y7myi2llwvg` FOREIGN KEY (`created_by`) REFERENCES `user` (`user_id`),
  ADD CONSTRAINT `FKeex0i50vfsa5imebrfdiyhmp9` FOREIGN KEY (`unit_id`) REFERENCES `units` (`unit_id`),
  ADD CONSTRAINT `FKlddmkmfh5tq2egaospdtn4fy5` FOREIGN KEY (`updated_by`) REFERENCES `user` (`user_id`);

--
-- 資料表的限制式 `purchase_orders`
--
ALTER TABLE `purchase_orders`
  ADD CONSTRAINT `FKrpdasmb8y8xs5tiy4369xpinq` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`);

--
-- 資料表的限制式 `purchase_order_details`
--
ALTER TABLE `purchase_order_details`
  ADD CONSTRAINT `FK7k5h72ashr7waatbffpug92ei` FOREIGN KEY (`purchase_order_id`) REFERENCES `purchase_orders` (`purchase_order_id`),
  ADD CONSTRAINT `FKkxd14v33nprv58sla1vrhfi2p` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`),
  ADD CONSTRAINT `FKpouou78xjt37rqmveufppyhtp` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- 資料表的限制式 `sales_orders`
--
ALTER TABLE `sales_orders`
  ADD CONSTRAINT `FK7dfsnse1mhb9m2pbmvu842sxu` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`),
  ADD CONSTRAINT `FKr7yhnihih2sge0tsenxvdt1dy` FOREIGN KEY (`customer_id`) REFERENCES `customer_base` (`customer_id`);

--
-- 資料表的限制式 `sales_order_details`
--
ALTER TABLE `sales_order_details`
  ADD CONSTRAINT `FKncwh5cjveg0gvgko7m77qawva` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  ADD CONSTRAINT `FKpy7rkbtos35e8ajjplvcp836v` FOREIGN KEY (`sales_order_id`) REFERENCES `sales_orders` (`sales_order_id`);

--
-- 資料表的限制式 `sales_shipments`
--
ALTER TABLE `sales_shipments`
  ADD CONSTRAINT `FKd82xyktlw82wsy97u6i6epe57` FOREIGN KEY (`sales_order_id`) REFERENCES `sales_orders` (`sales_order_id`),
  ADD CONSTRAINT `FKjv431n38r6rknovu8ne1gq4ud` FOREIGN KEY (`customer_id`) REFERENCES `customer_base` (`customer_id`),
  ADD CONSTRAINT `FKqwjqo5yqf5kwnub7191kesqwe` FOREIGN KEY (`warehouse_id`) REFERENCES `warehouses` (`warehouse_id`);

--
-- 資料表的限制式 `sales_shipment_details`
--
ALTER TABLE `sales_shipment_details`
  ADD CONSTRAINT `FK79jt6cfdlrd0tfgdfvd07hypj` FOREIGN KEY (`shipment_id`) REFERENCES `sales_shipments` (`shipment_id`),
  ADD CONSTRAINT `FKlrf6gbglckg2b0smh0jbbpmi2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- 資料表的限制式 `userlogaction`
--
ALTER TABLE `userlogaction`
  ADD CONSTRAINT `FKgtidi4yjf3vgwimfll6tp1e22` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- 資料表的限制式 `user_authority`
--
ALTER TABLE `user_authority`
  ADD CONSTRAINT `FKgvxjs381k6f48d5d2yi11uh89` FOREIGN KEY (`authority_id`) REFERENCES `authority` (`authority_id`),
  ADD CONSTRAINT `FKpqlsjpkybgos9w2svcri7j8xy` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
