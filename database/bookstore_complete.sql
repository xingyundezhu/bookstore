DROP DATABASE IF EXISTS bookstore;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS bookstore 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE bookstore;

-- ============================================
-- 1. 用户表 (t_user)
-- ============================================
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像URL',
    gender TINYINT DEFAULT 0 COMMENT '性别: 0-未知, 1-男, 2-女',
    birthday DATE COMMENT '生日',
    bio VARCHAR(500) COMMENT '个人简介',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: USER-普通用户, ADMIN-管理员',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_phone (phone),
    INDEX idx_status (status),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 2. 分类表 (t_category)
-- ============================================
DROP TABLE IF EXISTS t_category;
CREATE TABLE t_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort INT NOT NULL DEFAULT 0 COMMENT '排序号',
    icon VARCHAR(255) COMMENT '分类图标URL',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_sort (sort),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- ============================================
-- 3. 图书表 (t_book)
-- ============================================
DROP TABLE IF EXISTS t_book;
CREATE TABLE t_book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '图书ID',
    title VARCHAR(200) NOT NULL COMMENT '书名',
    author VARCHAR(100) NOT NULL COMMENT '作者',
    publisher VARCHAR(100) NOT NULL COMMENT '出版社',
    publish_date DATE COMMENT '出版日期',
    isbn VARCHAR(20) COMMENT 'ISBN号',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    discount DECIMAL(3,2) COMMENT '折扣',
    stock INT NOT NULL DEFAULT 0 COMMENT '库存',
    sales INT NOT NULL DEFAULT 0 COMMENT '销量',
    cover_image VARCHAR(255) COMMENT '封面图片URL',
    images TEXT COMMENT '详情图片JSON',
    description TEXT COMMENT '图书简介',
    pages INT COMMENT '页数',
    language VARCHAR(20) COMMENT '语言',
    binding VARCHAR(20) COMMENT '装帧',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
    is_hot TINYINT NOT NULL DEFAULT 0 COMMENT '是否热门: 0-否, 1-是',
    is_new TINYINT NOT NULL DEFAULT 0 COMMENT '是否新品: 0-否, 1-是',
    is_recommend TINYINT NOT NULL DEFAULT 0 COMMENT '是否推荐: 0-否, 1-是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category_id (category_id),
    INDEX idx_author (author),
    INDEX idx_isbn (isbn),
    INDEX idx_status (status),
    INDEX idx_sales (sales),
    INDEX idx_is_hot (is_hot),
    INDEX idx_is_new (is_new),
    INDEX idx_is_recommend (is_recommend),
    FULLTEXT INDEX ft_title_author (title, author),
    CONSTRAINT fk_book_category FOREIGN KEY (category_id) REFERENCES t_category(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书表';

-- ============================================
-- 4. 收货地址表 (t_address)
-- ============================================
DROP TABLE IF EXISTS t_address;
CREATE TABLE t_address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '地址ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    receiver VARCHAR(50) NOT NULL COMMENT '收货人',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区县',
    detail VARCHAR(200) NOT NULL COMMENT '详细地址',
    is_default TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认: 0-否, 1-是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收货地址表';

-- ============================================
-- 5. 购物车表 (t_cart)
-- ============================================
DROP TABLE IF EXISTS t_cart;
CREATE TABLE t_cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    selected TINYINT NOT NULL DEFAULT 1 COMMENT '是否选中: 0-否, 1-是',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_book (user_id, book_id),
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_book FOREIGN KEY (book_id) REFERENCES t_book(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- ============================================
-- 6. 订单表 (t_order)
-- ============================================
DROP TABLE IF EXISTS t_order;
CREATE TABLE t_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    pay_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    freight DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '运费',
    discount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '优惠金额',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待付款, 1-待发货, 2-待收货, 3-已完成, 4-已取消, 5-已退款',
    payment_type TINYINT NOT NULL DEFAULT 1 COMMENT '支付方式: 1-支付宝, 2-微信, 3-银行卡',
    payment_time DATETIME COMMENT '支付时间',
    delivery_time DATETIME COMMENT '发货时间',
    receive_time DATETIME COMMENT '收货时间',
    receiver VARCHAR(50) NOT NULL COMMENT '收货人',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    receiver_address VARCHAR(255) NOT NULL COMMENT '收货地址',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ============================================
-- 7. 订单项表 (t_order_item)
-- ============================================
DROP TABLE IF EXISTS t_order_item;
CREATE TABLE t_order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    book_title VARCHAR(200) NOT NULL COMMENT '图书名称',
    book_cover VARCHAR(255) COMMENT '图书封面',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL COMMENT '数量',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id),
    INDEX idx_book_id (book_id),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES t_order(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_book FOREIGN KEY (book_id) REFERENCES t_book(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';

-- ============================================
-- 8. 评价表 (t_review)
-- ============================================
DROP TABLE IF EXISTS t_review;
CREATE TABLE t_review (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    rating TINYINT NOT NULL COMMENT '评分: 1-5',
    content TEXT COMMENT '评价内容',
    images TEXT COMMENT '评价图片JSON',
    is_anonymous TINYINT NOT NULL DEFAULT 0 COMMENT '是否匿名: 0-否, 1-是',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-隐藏, 1-显示',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    INDEX idx_order_id (order_id),
    INDEX idx_rating (rating),
    CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE RESTRICT,
    CONSTRAINT fk_review_book FOREIGN KEY (book_id) REFERENCES t_book(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_order FOREIGN KEY (order_id) REFERENCES t_order(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- ============================================
-- 9. 用户行为表 (t_user_behavior)
-- 用于推荐系统
-- ============================================
DROP TABLE IF EXISTS t_user_behavior;
CREATE TABLE t_user_behavior (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '行为ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    behavior_type TINYINT NOT NULL COMMENT '行为类型: 1-浏览, 2-收藏, 3-加购, 4-购买, 5-评价',
    score DECIMAL(5,2) NOT NULL COMMENT '行为得分',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    INDEX idx_behavior_type (behavior_type),
    INDEX idx_create_time (create_time),
    CONSTRAINT fk_behavior_user FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_behavior_book FOREIGN KEY (book_id) REFERENCES t_book(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户行为表';

-- ============================================
-- 10. 图书评分统计表 (t_book_rating)
-- ============================================
DROP TABLE IF EXISTS t_book_rating;
CREATE TABLE t_book_rating (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    book_id BIGINT NOT NULL UNIQUE COMMENT '图书ID',
    avg_rating DECIMAL(3,2) NOT NULL DEFAULT 0 COMMENT '平均评分',
    rating_count INT NOT NULL DEFAULT 0 COMMENT '评价数量',
    rating_1_count INT NOT NULL DEFAULT 0 COMMENT '1星数量',
    rating_2_count INT NOT NULL DEFAULT 0 COMMENT '2星数量',
    rating_3_count INT NOT NULL DEFAULT 0 COMMENT '3星数量',
    rating_4_count INT NOT NULL DEFAULT 0 COMMENT '4星数量',
    rating_5_count INT NOT NULL DEFAULT 0 COMMENT '5星数量',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_rating_book FOREIGN KEY (book_id) REFERENCES t_book(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书评分统计表';

-- ============================================
-- 11. 收藏表 (t_favorite)
-- ============================================
DROP TABLE IF EXISTS t_favorite;
CREATE TABLE t_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_book (user_id, book_id),
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_favorite_book FOREIGN KEY (book_id) REFERENCES t_book(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- ============================================
-- 初始数据插入
-- ============================================

-- 插入管理员账户
-- 用户名: admin
-- 密码: 123456 (BCrypt加密后的值)
INSERT INTO t_user (username, password, email, nickname, role, status) VALUES
('admin', '$2a$10$EuWPzhzznDJfSnQNRQ0LIeLQ3gOxxG7guNEk2Rv27esU0zY1eE4Ha', 'admin@bookstore.com', '管理员', 'ADMIN', 1);

-- 插入分类数据
INSERT INTO t_category (name, sort, status) VALUES
('文学小说', 1, 1),
('科技编程', 2, 1),
('经济管理', 3, 1),
('人文历史', 4, 1),
('艺术设计', 5, 1),
('生活百科', 6, 1),
('教育学习', 7, 1),
('童书绘本', 8, 1);

-- 插入图书数据
INSERT INTO t_book (title, author, publisher, publish_date, category_id, price, original_price, stock, sales, cover_image, description, status, is_hot, is_new, is_recommend) VALUES
('三体', '刘慈欣', '重庆出版社', '2008-01-01', 1, 59.00, 68.00, 1000, 50000, '/uploads/三体.png', '文化大革命如火如荼进行的同时，军方探寻外星文明的绝秘计划"红岸工程"取得了突破性进展。但在按下发射键的那一刻，历经沧桑的叶文洁没有意识到，她彻底改变了人类的命运。地球文明向宇宙发出的第一声啼鸣，以太阳为中心，以光速向宇宙深处飞驰……', 1, 1, 0, 1),
('活着', '余华', '作家出版社', '2012-08-01', 1, 35.00, 45.00, 800, 40000, '/uploads/活着.png', '讲述了农村人福贵悲惨的人生遭遇。福贵本是个阔少爷，可他嗜赌如命，终于赌光了家业，一贫如洗。他的父亲被他活活气死，母亲则在穷困中患了重病，福贵前去求药，却在途中被抓了壮丁。', 1, 1, 0, 1),
('Java编程思想', 'Bruce Eckel', '机械工业出版社', '2007-06-01', 2, 108.00, 128.00, 500, 20000, '/uploads/Java编程思想.png', 'Java经典书籍，适合有一定基础的程序员阅读。本书赢得了全球程序员的广泛赞誉，即使是晦涩的概念，在Bruce Eckel的文字亲和力和小而直接的编程示例面前也会化解于无形。', 1, 1, 1, 1),
('深入理解计算机系统', 'Randal E.Bryant', '机械工业出版社', '2016-07-01', 2, 139.00, 168.00, 300, 15000, '/uploads/深入理解计算机系统.png', '从程序员的视角讲述计算机系统的本质概念。本书从程序员的视角详细阐述计算机系统的本质概念，并展示这些概念如何实实在在地影响应用程序的正确性、性能和实用性。', 1, 1, 1, 1),
('经济学原理', '曼昆', '北京大学出版社', '2015-05-01', 3, 98.00, 118.00, 400, 12000, '/uploads/经济学原理.png', '经济学入门经典教材。本书是世界上最流行的经济学教科书。本书英文版自1998年出版以来，已被翻译成二十多种语言，在全世界销售超过一百万册。', 1, 0, 1, 1),
('人类简史', '尤瓦尔·赫拉利', '中信出版社', '2014-11-01', 4, 68.00, 88.00, 600, 30000, '/uploads/人类简史.png', '从十万年前有生命迹象开始到21世纪资本、科技交织的人类发展史。这是一部宏大的人类简史，更见微知著、以小写大，让人类重新审视自己。', 1, 1, 0, 1),
('设计中的设计', '原研哉', '广西师范大学出版社', '2010-09-01', 5, 58.00, 78.00, 200, 8000, '/uploads/设计中的设计.png', '设计到底是什么？作为一名从业二十余年并且具有世界影响的设计师，原研哉对自己提出了这样一个问题。为了给出自己的答案，他走了那么长的路，做了那么多的探索。', 1, 0, 0, 1),
('断舍离', '山下英子', '广西科学技术出版社', '2013-07-01', 6, 32.00, 42.00, 700, 25000, '/uploads/断舍离.png', '断舍离，人生整理术。断=断绝不需要的东西，舍=舍弃多余的废物，离=脱离对物品的执着。', 1, 1, 0, 0),
('小王子', '圣埃克苏佩里', '人民文学出版社', '2003-08-01', 8, 22.00, 32.00, 1000, 60000, '/uploads/小王子.png', '小王子是一个超凡脱俗的仙童，住在一颗只比他大一丁点儿的小行星上。陪伴他的是一朵他非常喜爱的玫瑰花。但玫瑰花的虚荣心伤害了小王子对她的感情。小王子告别小行星，开始了遨游太空的旅行。', 1, 1, 0, 1),
('Python编程：从入门到实践', 'Eric Matthes', '人民邮电出版社', '2016-07-01', 2, 89.00, 99.00, 450, 18000, '/uploads/Python编程：从入门到实践.png', '一本针对所有层次Python读者而作的实用教程。本书是一本Python语言编程入门书，适合对Python感兴趣的任何读者阅读。', 1, 1, 1, 1),
('百年孤独', '加西亚·马尔克斯', '南海出版公司', '2011-06-01', 1, 55.00, 65.00, 550, 35000, '/uploads/百年孤独.png', '《百年孤独》是魔幻现实主义文学的代表作。描写了布恩迪亚家族七代人的传奇故事，以及加勒比海沿岸小镇马孔多的百年兴衰，反映了拉丁美洲一个世纪以来风云变幻的历史。', 1, 1, 0, 1),
('围城', '钱钟书', '人民文学出版社', '1991-02-01', 1, 39.00, 49.00, 650, 28000, '/uploads/围城.png', '《围城》是钱钟书所著的长篇小说。故事主要写抗战初期知识分子的群相。被誉为"新儒林外史"。', 1, 0, 0, 0);

-- 插入图书评分统计
INSERT INTO t_book_rating (book_id, avg_rating, rating_count, rating_5_count, rating_4_count, rating_3_count, rating_2_count, rating_1_count)
SELECT 
    id, 
    4.5, 
    FLOOR(RAND() * 1000) + 100, 
    FLOOR(RAND() * 500) + 200, 
    FLOOR(RAND() * 300) + 100,
    FLOOR(RAND() * 100) + 50,
    FLOOR(RAND() * 50) + 10,
    FLOOR(RAND() * 20) + 5
FROM t_book;

-- ============================================
-- 创建视图（可选）
-- ============================================

-- 图书详情视图（包含分类名称和评分）
CREATE OR REPLACE VIEW v_book_detail AS
SELECT 
    b.*,
    c.name AS category_name,
    IFNULL(r.avg_rating, 0) AS avg_rating,
    IFNULL(r.rating_count, 0) AS rating_count
FROM t_book b
LEFT JOIN t_category c ON b.category_id = c.id
LEFT JOIN t_book_rating r ON b.id = r.book_id;

-- 订单详情视图
CREATE OR REPLACE VIEW v_order_detail AS
SELECT 
    o.*,
    u.username,
    u.nickname AS user_nickname
FROM t_order o
LEFT JOIN t_user u ON o.user_id = u.id;

-- ============================================
-- 存储过程（可选）
-- ============================================

-- 更新图书评分统计的存储过程
DELIMITER //
CREATE PROCEDURE update_book_rating(IN p_book_id BIGINT)
BEGIN
    DECLARE v_avg_rating DECIMAL(3,2);
    DECLARE v_rating_count INT;
    DECLARE v_1_count INT;
    DECLARE v_2_count INT;
    DECLARE v_3_count INT;
    DECLARE v_4_count INT;
    DECLARE v_5_count INT;
    
    SELECT 
        AVG(rating),
        COUNT(*),
        SUM(CASE WHEN rating = 1 THEN 1 ELSE 0 END),
        SUM(CASE WHEN rating = 2 THEN 1 ELSE 0 END),
        SUM(CASE WHEN rating = 3 THEN 1 ELSE 0 END),
        SUM(CASE WHEN rating = 4 THEN 1 ELSE 0 END),
        SUM(CASE WHEN rating = 5 THEN 1 ELSE 0 END)
    INTO v_avg_rating, v_rating_count, v_1_count, v_2_count, v_3_count, v_4_count, v_5_count
    FROM t_review
    WHERE book_id = p_book_id AND status = 1;
    
    INSERT INTO t_book_rating (book_id, avg_rating, rating_count, rating_1_count, rating_2_count, rating_3_count, rating_4_count, rating_5_count)
    VALUES (p_book_id, IFNULL(v_avg_rating, 0), IFNULL(v_rating_count, 0), IFNULL(v_1_count, 0), IFNULL(v_2_count, 0), IFNULL(v_3_count, 0), IFNULL(v_4_count, 0), IFNULL(v_5_count, 0))
    ON DUPLICATE KEY UPDATE
        avg_rating = IFNULL(v_avg_rating, 0),
        rating_count = IFNULL(v_rating_count, 0),
        rating_1_count = IFNULL(v_1_count, 0),
        rating_2_count = IFNULL(v_2_count, 0),
        rating_3_count = IFNULL(v_3_count, 0),
        rating_4_count = IFNULL(v_4_count, 0),
        rating_5_count = IFNULL(v_5_count, 0);
END //
DELIMITER ;

-- ============================================
-- 触发器（可选）
-- ============================================

-- 订单创建后扣减库存
DELIMITER //
CREATE TRIGGER after_order_item_insert
AFTER INSERT ON t_order_item
FOR EACH ROW
BEGIN
    UPDATE t_book 
    SET stock = stock - NEW.quantity,
        sales = sales + NEW.quantity
    WHERE id = NEW.book_id;
END //
DELIMITER ;

-- 订单取消后恢复库存
DELIMITER //
CREATE TRIGGER after_order_cancel
AFTER UPDATE ON t_order
FOR EACH ROW
BEGIN
    IF NEW.status = 4 AND OLD.status != 4 THEN
        UPDATE t_book b
        INNER JOIN t_order_item oi ON b.id = oi.book_id
        SET b.stock = b.stock + oi.quantity,
            b.sales = b.sales - oi.quantity
        WHERE oi.order_id = NEW.id;
    END IF;
END //
DELIMITER ;

-- ============================================
-- 完成提示
-- ============================================
SELECT '数据库初始化完成!' AS message;
SELECT COUNT(*) AS user_count FROM t_user;
SELECT COUNT(*) AS category_count FROM t_category;
SELECT COUNT(*) AS book_count FROM t_book;
