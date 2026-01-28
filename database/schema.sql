-- ============================================
-- 大理旅游线路规划平台 - 数据库建表脚本
-- 版本: v1.0
-- 创建日期: 2026-01-28
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS trip_planner
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE trip_planner;

-- ============================================
-- 1. 用户表
-- ============================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL COMMENT 'BCrypt加密',
    avatar VARCHAR(500) DEFAULT NULL,
    role ENUM('user', 'admin') NOT NULL DEFAULT 'user',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0禁用 1正常',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 2. POI表（景点）
-- ============================================
CREATE TABLE pois (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '景点名称',
    lng DECIMAL(10, 7) NOT NULL COMMENT '经度',
    lat DECIMAL(10, 7) NOT NULL COMMENT '纬度',
    address VARCHAR(255) DEFAULT NULL COMMENT '详细地址',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    area VARCHAR(50) DEFAULT NULL COMMENT '区域',
    tags JSON DEFAULT NULL COMMENT '标签数组',
    brief VARCHAR(500) DEFAULT NULL COMMENT '简介',
    cover_image VARCHAR(500) DEFAULT NULL COMMENT '封面图URL',
    visit_duration INT DEFAULT 60 COMMENT '预计游玩时长(分钟)',
    open_time VARCHAR(100) DEFAULT NULL COMMENT '营业时间描述',
    ticket_price DECIMAL(10, 2) DEFAULT NULL COMMENT '门票价格',
    best_time VARCHAR(200) DEFAULT NULL COMMENT '最佳游玩时间',
    highlights JSON DEFAULT NULL COMMENT '景点亮点数组',
    suitable_for JSON DEFAULT NULL COMMENT '适合人群数组',
    tips VARCHAR(500) DEFAULT NULL COMMENT '游玩小贴士',
    difficulty VARCHAR(20) DEFAULT NULL COMMENT '游玩难度',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0下架 1上架',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_city_status (city, status),
    KEY idx_area (area)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='景点表';

-- ============================================
-- 3. 行程表
-- ============================================
CREATE TABLE itineraries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT DEFAULT NULL COMMENT '用户ID,游客为空',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    days INT NOT NULL COMMENT '天数',
    theme VARCHAR(50) DEFAULT NULL COMMENT '主题',
    title VARCHAR(100) DEFAULT NULL COMMENT '行程标题',
    summary VARCHAR(500) DEFAULT NULL COMMENT '行程概述',
    tips JSON DEFAULT NULL COMMENT '整体贴士数组',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0删除 1正常',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_user_id (user_id),
    KEY idx_city (city),
    CONSTRAINT fk_itinerary_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程表';

-- ============================================
-- 4. 行程天表
-- ============================================
CREATE TABLE itinerary_days (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    itinerary_id BIGINT NOT NULL COMMENT '行程ID',
    day_index INT NOT NULL COMMENT '第几天,从1开始',
    theme VARCHAR(100) DEFAULT NULL COMMENT '当日主题',
    notes VARCHAR(500) DEFAULT NULL COMMENT '当日备注/建议',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_itinerary_id (itinerary_id),
    CONSTRAINT fk_day_itinerary FOREIGN KEY (itinerary_id) REFERENCES itineraries(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程天表';

-- ============================================
-- 5. 行程项表
-- ============================================
CREATE TABLE itinerary_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    day_id BIGINT NOT NULL COMMENT '行程天ID',
    poi_id BIGINT NOT NULL COMMENT 'POI ID',
    order_index INT NOT NULL COMMENT '当天顺序,从1开始',
    start_time VARCHAR(10) DEFAULT NULL COMMENT '建议开始时间',
    tips VARCHAR(500) DEFAULT NULL COMMENT '游玩建议',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_day_id (day_id),
    KEY idx_poi_id (poi_id),
    CONSTRAINT fk_item_day FOREIGN KEY (day_id) REFERENCES itinerary_days(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_poi FOREIGN KEY (poi_id) REFERENCES pois(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行程项表';

-- ============================================
-- 6. 路线缓存表
-- ============================================
CREATE TABLE route_cache (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_poi_id BIGINT NOT NULL COMMENT '起点POI ID',
    to_poi_id BIGINT NOT NULL COMMENT '终点POI ID',
    distance_meter INT DEFAULT NULL COMMENT '距离(米)',
    walk_duration_sec INT DEFAULT NULL COMMENT '步行时长(秒)',
    drive_duration_sec INT DEFAULT NULL COMMENT '驾车时长(秒)',
    transit_duration_sec INT DEFAULT NULL COMMENT '公交时长(秒)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_from_to (from_poi_id, to_poi_id),
    KEY idx_to_poi (to_poi_id),
    CONSTRAINT fk_route_from_poi FOREIGN KEY (from_poi_id) REFERENCES pois(id) ON DELETE CASCADE,
    CONSTRAINT fk_route_to_poi FOREIGN KEY (to_poi_id) REFERENCES pois(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路线缓存表';

-- ============================================
-- 7. 帖子表
-- ============================================
CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    author_id BIGINT NOT NULL COMMENT '作者ID',
    itinerary_id BIGINT NOT NULL COMMENT '关联行程ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    summary VARCHAR(500) DEFAULT NULL COMMENT '摘要',
    like_count INT NOT NULL DEFAULT 0 COMMENT '点赞数',
    favorite_count INT NOT NULL DEFAULT 0 COMMENT '收藏数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0下架 1正常',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_author_id (author_id),
    KEY idx_itinerary_id (itinerary_id),
    KEY idx_status_created (status, created_at DESC),
    KEY idx_status_like (status, like_count DESC),
    CONSTRAINT fk_post_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_post_itinerary FOREIGN KEY (itinerary_id) REFERENCES itineraries(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- ============================================
-- 8. 点赞表
-- ============================================
CREATE TABLE likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_post (user_id, post_id),
    KEY idx_post_id (post_id),
    CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_like_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- ============================================
-- 9. 收藏表
-- ============================================
CREATE TABLE favorites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_post (user_id, post_id),
    KEY idx_post_id (post_id),
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_favorite_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';
