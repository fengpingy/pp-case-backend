/*
Navicat MySQL Data Transfer

Source Server         : pp-case
Source Server Version : 50738
Source Host           : 101.43.220.65:3306
Source Database       : pp_case_platform

Target Server Type    : MYSQL
Target Server Version : 50738
File Encoding         : 65001

Date: 2022-06-01 00:46:09
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for pp_case
-- ----------------------------
DROP TABLE IF EXISTS `pp_case`;
CREATE TABLE `pp_case` (
  `id` bigint(20) NOT NULL,
  `title` varchar(255) COLLATE utf8_bin NOT NULL,
  `code` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `step_expect` varchar(5000) COLLATE utf8_bin DEFAULT NULL,
  `type` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `level` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `precondition` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `remark` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `tester_id` bigint(20) DEFAULT NULL,
  `module_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for pp_module
-- ----------------------------
DROP TABLE IF EXISTS `pp_module`;
CREATE TABLE `pp_module` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `parent_id` bigint(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pp_test_plan
-- ----------------------------
DROP TABLE IF EXISTS `pp_test_plan`;
CREATE TABLE `pp_test_plan` (
  `id` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `creator_id` bigint(20) NOT NULL,
  `executor_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `test_plan_id_uindex` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='测试计划表';

-- ----------------------------
-- Table structure for pp_unit
-- ----------------------------
DROP TABLE IF EXISTS `pp_unit`;
CREATE TABLE `pp_unit` (
  `id` bigint(20) NOT NULL,
  `unit_name` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pp_user
-- ----------------------------
DROP TABLE IF EXISTS `pp_user`;
CREATE TABLE `pp_user` (
  `id` bigint(20) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pp_user_log
-- ----------------------------
DROP TABLE IF EXISTS `pp_user_log`;
CREATE TABLE `pp_user_log` (
  `id` bigint(20) NOT NULL,
  `operation_staff_id` bigint(20) DEFAULT NULL,
  `operation_time` datetime DEFAULT NULL,
  `operation_description` varchar(5000) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `request_id` varchar(255) DEFAULT NULL,
  `is_success` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pp_user_log
-- ----------------------------
CREATE TABLE `pp_plan_case_records` (
  `id` bigint(25) NOT NULL,
  `plan_id` bigint(20) DEFAULT NULL,
  `case_image_id` bigint(20) DEFAULT NULL,
  `is_execute` tinyint(4) DEFAULT '0',
  `execute_time` datetime DEFAULT NULL,
  `is_pass` tinyint(4) DEFAULT '0',
  `execute_name` varchar(255) DEFAULT null,
  `execute_role` varchar(20) DEFAULT null,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_delete` smallint(6) DEFAULT '0',

) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='测试计划cases执行记录表';