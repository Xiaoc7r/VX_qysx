课程考勤系统接口列表：

登录与注册：
/auth/login       登录 
/auth/register   注册

课程管理：
/course/list              获取全部课程列表
/course/my/{userId}/{userType}    获取我的课程列表
/course/{id}         获取课程详情
/course/{courseId}/students         获取课程学生列表

签到管理：
/attendance/create              创建签到
/attendance/list/{courseId}    获取课程签到列表
/attendance/detail/{id}         获取签到详情（教师）
/attendance/student/detail/{id}         获取签到详情（学生）
/attendance/myActive                获取我的活跃签到
/attendance/myHistory               获取我的历史签到
/attendance/stats/{courseId}              获取课程签到统计
/attendance/student/stats               获取学生个人签到统计









-- 创建数据库
CREATE DATABASE IF NOT EXISTS course_attendance DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE course_attendance;

DROP TABLE IF EXISTS `attendance`;
CREATE TABLE `attendance` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '签到ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '签到标题',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '签到类型：1-普通签到，2-位置签到，3-二维码签到',
  `sign_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '签到码',
  `latitude` decimal(10,8) DEFAULT NULL COMMENT '纬度（位置签到）',
  `longitude` decimal(11,8) DEFAULT NULL COMMENT '经度（位置签到）',
  `radius` int DEFAULT '100' COMMENT '有效半径（米）',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-进行中，0-已结束',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_sign_code` (`sign_code`),
  KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='签到表';

DROP TABLE IF EXISTS `attendance_record`;
CREATE TABLE `attendance_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `attendance_id` bigint NOT NULL COMMENT '签到ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `sign_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
  `latitude` decimal(10,8) DEFAULT NULL COMMENT '签到纬度',
  `longitude` decimal(11,8) DEFAULT NULL COMMENT '签到经度',
  `distance` decimal(10,2) DEFAULT NULL COMMENT '距离（米）',
  `result` tinyint NOT NULL DEFAULT '1' COMMENT '签到结果：1-成功，2-迟到，3-位置不符，4-已过期',
  `ip_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `device_info` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备信息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_attendance_student` (`attendance_id`,`student_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_sign_time` (`sign_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='签到记录表';

DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程代码',
  `course_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `teacher_id` bigint NOT NULL COMMENT '教师ID',
  `term` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学期',
  `class_time` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '上课时间',
  `location` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '上课地点',
  `credit` decimal(3,1) DEFAULT '2.0' COMMENT '学分',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '课程描述',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-进行中，0-已结束',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_code` (`course_code`),
  KEY `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

DROP TABLE IF EXISTS `course_student`;
CREATE TABLE `course_student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `join_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-在修，0-退课',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_student` (`course_id`,`student_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生选课表';

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `openid` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信openid',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `user_type` tinyint NOT NULL DEFAULT '1' COMMENT '用户类型：1-学生，2-教师，3-管理员',
  `student_id` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学号',
  `teacher_id` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '工号',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
  `gender` tinyint DEFAULT '0' COMMENT '性别：0-未知，1-男，2-女',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-正常，0-禁用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_openid` (`openid`),
  UNIQUE KEY `uk_student_id` (`student_id`),
  UNIQUE KEY `uk_teacher_id` (`teacher_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';


INSERT INTO `sys_user` VALUES (1,NULL,'zu','e10adc3949ba59abbe56e057f20f883e','张教授',2,NULL,'T001','13800138001','/images/icon/teacher.png',0,1,'2026-03-05 22:53:53','2026-01-13 18:39:01','2026-03-04 11:23:40'),(2,NULL,'student001','e10adc3949ba59abbe56e057f20f883e','张三',1,'2023001',NULL,'13800138002','/images/iconstudent.png',0,1,NULL,'2026-01-13 18:39:01','2026-01-18 11:04:29'),(3,NULL,'student002','e10adc3949ba59abbe56e057f20f883e','李四',1,'2023002',NULL,'13800138003','/images/iconstudent.png',0,1,NULL,'2026-01-13 18:39:01','2026-01-18 11:04:29'),(4,NULL,'admin','e10adc3949ba59abbe56e057f20f883e','系统管理员',3,NULL,NULL,'13800138000','/images/iconadmin.png',0,1,NULL,'2026-01-13 18:39:01','2026-01-18 11:04:29'),(5,NULL,'xiaowei','e10adc3949ba59abbe56e057f20f883e','xiaowei',1,'001',NULL,'',NULL,0,1,'2026-03-05 22:29:56','2026-01-16 23:37:09','2026-01-16 23:37:09');

INSERT INTO `course` VALUES (1,'CS101','Spring程序设计',1,'2024春季学期','周一 8:00-10:00','教学楼A101',3.0,'Java编程基础与面向对象思想',1,'2026-01-13 18:39:01','2026-03-05 15:31:39'),(2,'CS102','编程思想',1,'2024春季学期','周三 10:00-12:00','教学楼B201',3.5,'数据库设计与SQL语言',1,'2026-01-13 18:39:01','2026-03-05 15:31:39'),(3,'CS201','MyBatis框架技术',1,'2024春季学期','周五 14:00-16:00','实验楼C301',2.5,'前后端分离开发技术',1,'2026-01-13 18:39:01','2026-03-05 15:31:39');

INSERT INTO `course_student` VALUES (1,1,5,'2026-01-13 18:39:01',1),(2,1,3,'2026-01-13 18:39:01',1),(3,2,5,'2026-01-13 18:39:01',1),(4,2,3,'2026-01-13 18:39:01',1),(5,3,5,'2026-01-13 18:39:01',1);

INSERT INTO `attendance` VALUES (1,1,'第一次课堂签到',1,'ABC123',43.86414000,125.35043000,100,'2026-01-13 18:39:01','2026-01-13 18:49:01',0,'2026-01-13 18:39:01'),(2,1,'位置签到-教学楼A区',2,'XYZ789',43.86414000,125.35043000,100,'2026-03-09 10:30:01','2026-03-10 18:54:01',1,'2026-01-13 18:39:01');

INSERT INTO `attendance_record` VALUES (1,1,5,'2026-01-13 18:39:01',43.86414000,125.35043000,6.00,1,NULL,NULL),(2,1,3,'2026-01-13 18:39:01',NULL,NULL,12.00,2,NULL,NULL);



