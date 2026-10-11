-- =====================================================
-- 视光管理系统 - 完整数据库脚本
-- 包含所有表和测试数据
-- =====================================================

-- 删除旧数据库（如果需要重新创建）
DROP DATABASE IF EXISTS vision_care;

-- 创建数据库
CREATE DATABASE vision_care DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE vision_care;

-- =====================================================
-- 1. 系统用户表
-- =====================================================
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role ENUM(
        'ADMIN',
        'OPTOMETRIST',
        'PROCESSOR',
        'DOCTOR'
    ) NOT NULL COMMENT '角色',
    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '系统用户表';

-- =====================================================
-- 2. 患者档案表（包含软删除字段）
-- =====================================================
CREATE TABLE patient (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_no VARCHAR(50) NOT NULL UNIQUE COMMENT '患者编号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender ENUM('MALE', 'FEMALE') NOT NULL COMMENT '性别',
    birth_date DATE COMMENT '出生日期',
    phone VARCHAR(20) COMMENT '联系电话',
    id_card VARCHAR(18) COMMENT '身份证号',
    address VARCHAR(200) COMMENT '地址',
    medical_history TEXT COMMENT '病史',
    family_history TEXT COMMENT '家族史',
    deleted TINYINT DEFAULT 0 COMMENT '0:未删除 1:已删除（软删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_patient_no (patient_no),
    INDEX idx_phone (phone),
    INDEX idx_name (name),
    INDEX idx_deleted (deleted)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '患者档案表';

-- =====================================================
-- 3. 验光记录表
-- =====================================================
CREATE TABLE optometry_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    optometrist_id BIGINT COMMENT '验光师ID',
    check_date DATETIME NOT NULL COMMENT '检查时间',
    -- 裸眼视力
    vision_od_naked DECIMAL(3, 1) COMMENT '右眼裸眼视力',
    vision_os_naked DECIMAL(3, 1) COMMENT '左眼裸眼视力',
    vision_ou_naked DECIMAL(3, 1) COMMENT '双眼裸眼视力',
    -- 电脑验光
    sphere_od DECIMAL(4, 2) COMMENT '右眼球镜',
    sphere_os DECIMAL(4, 2) COMMENT '左眼球镜',
    cylinder_od DECIMAL(4, 2) COMMENT '右眼柱镜',
    cylinder_os DECIMAL(4, 2) COMMENT '左眼柱镜',
    axis_od INT COMMENT '右眼轴位',
    axis_os INT COMMENT '左眼轴位',
    add_od DECIMAL(4, 2) COMMENT '右眼下加光',
    add_os DECIMAL(4, 2) COMMENT '左眼下加光',
    -- 矫正视力
    vision_od_corrected DECIMAL(3, 1) COMMENT '右眼矫正视力',
    vision_os_corrected DECIMAL(3, 1) COMMENT '左眼矫正视力',
    vision_ou_corrected DECIMAL(3, 1) COMMENT '双眼矫正视力',
    -- 其他参数
    pd_distance DECIMAL(4, 1) COMMENT '瞳距(PD)',
    ph_distance DECIMAL(4, 1) COMMENT '瞳高(PH)',
    eye_pressure_od DECIMAL(4, 1) COMMENT '右眼眼压',
    eye_pressure_os DECIMAL(4, 1) COMMENT '左眼眼压',
    remark TEXT COMMENT '备注',
    status ENUM(
        'DRAFT',
        'CONFIRMED',
        'CANCELLED'
    ) DEFAULT 'DRAFT' COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patient (id) ON DELETE CASCADE,
    FOREIGN KEY (optometrist_id) REFERENCES sys_user (id) ON DELETE SET NULL,
    INDEX idx_patient_id (patient_id),
    INDEX idx_check_date (check_date),
    INDEX idx_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '验光记录表';

-- =====================================================
-- 4. 配镜处方表
-- =====================================================
CREATE TABLE prescription (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    prescription_no VARCHAR(50) NOT NULL UNIQUE COMMENT '处方编号',
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    optometry_id BIGINT COMMENT '验光记录ID',
    doctor_id BIGINT COMMENT '医生ID',
    -- 镜架信息
    frame_type VARCHAR(50) COMMENT '镜架类型',
    frame_brand VARCHAR(50) COMMENT '镜架品牌',
    frame_model VARCHAR(100) COMMENT '镜架型号',
    -- 镜片信息
    lens_type VARCHAR(50) COMMENT '镜片类型',
    lens_material VARCHAR(50) COMMENT '镜片材质',
    lens_index DECIMAL(3, 2) COMMENT '折射率',
    lens_coating VARCHAR(100) COMMENT '镀膜',
    -- 处方度数
    sphere_od DECIMAL(4, 2) COMMENT '右眼球镜',
    sphere_os DECIMAL(4, 2) COMMENT '左眼球镜',
    cylinder_od DECIMAL(4, 2) COMMENT '右眼柱镜',
    cylinder_os DECIMAL(4, 2) COMMENT '左眼柱镜',
    axis_od INT COMMENT '右眼轴位',
    axis_os INT COMMENT '左眼轴位',
    add_od DECIMAL(4, 2) COMMENT '右眼下加光',
    add_os DECIMAL(4, 2) COMMENT '左眼下加光',
    pd_distance DECIMAL(4, 1) COMMENT '瞳距',
    ph_distance DECIMAL(4, 1) COMMENT '瞳高',
    usage_type ENUM(
        'DISTANCE',
        'READING',
        'PROGRESSIVE',
        'COMPUTER',
        'DRIVING'
    ) DEFAULT 'DISTANCE' COMMENT '用途',
    price DECIMAL(10, 2) COMMENT '总价',
    paid_amount DECIMAL(10, 2) COMMENT '已付金额',
    payment_status ENUM('UNPAID', 'PARTIAL', 'PAID') DEFAULT 'UNPAID' COMMENT '付款状态',
    status ENUM(
        'DRAFT',
        'PENDING',
        'PROCESSING',
        'COMPLETED',
        'CANCELLED',
        'DELIVERED'
    ) DEFAULT 'DRAFT' COMMENT '处方状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patient (id) ON DELETE CASCADE,
    FOREIGN KEY (optometry_id) REFERENCES optometry_record (id) ON DELETE SET NULL,
    FOREIGN KEY (doctor_id) REFERENCES sys_user (id) ON DELETE SET NULL,
    INDEX idx_prescription_no (prescription_no),
    INDEX idx_patient_id (patient_id),
    INDEX idx_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '配镜处方表';

-- =====================================================
-- 5. 加工单表
-- =====================================================
CREATE TABLE processing_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '加工单号',
    prescription_id BIGINT NOT NULL COMMENT '处方ID',
    processor_id BIGINT COMMENT '加工师ID',
    frame_model VARCHAR(100) COMMENT '镜架型号',
    frame_brand VARCHAR(50) COMMENT '镜架品牌',
    frame_color VARCHAR(50) COMMENT '镜架颜色',
    lens_type VARCHAR(50) COMMENT '镜片类型',
    lens_index DECIMAL(3, 2) COMMENT '折射率',
    lens_design VARCHAR(50) COMMENT '镜片设计',
    status ENUM(
        'PENDING',
        'PROCESSING',
        'QUALITY_CHECK',
        'COMPLETED',
        'DELIVERED'
    ) DEFAULT 'PENDING' COMMENT '状态',
    start_time DATETIME COMMENT '开始加工时间',
    complete_time DATETIME COMMENT '完成时间',
    quality_check_result ENUM('PASS', 'FAIL', 'PENDING') DEFAULT 'PENDING' COMMENT '质检结果',
    quality_check_remark TEXT COMMENT '质检备注',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (prescription_id) REFERENCES prescription (id) ON DELETE CASCADE,
    FOREIGN KEY (processor_id) REFERENCES sys_user (id) ON DELETE SET NULL,
    INDEX idx_order_no (order_no),
    INDEX idx_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '加工单表';

-- =====================================================
-- 6. 随访计划表
-- =====================================================
CREATE TABLE follow_up_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL COMMENT '患者ID',
    prescription_id BIGINT COMMENT '关联处方ID',
    plan_date DATE NOT NULL COMMENT '计划随访日期',
    follow_type ENUM(
        'ONE_WEEK',
        'ONE_MONTH',
        'THREE_MONTHS',
        'SIX_MONTHS',
        'ONE_YEAR',
        'CUSTOM'
    ) NOT NULL COMMENT '随访类型',
    content VARCHAR(500) COMMENT '随访内容',
    status ENUM(
        'PENDING',
        'COMPLETED',
        'MISSED',
        'CANCELLED'
    ) DEFAULT 'PENDING' COMMENT '状态',
    complete_date DATETIME COMMENT '完成时间',
    follow_result TEXT COMMENT '随访结果',
    reminder_count INT DEFAULT 0 COMMENT '提醒次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patient (id) ON DELETE CASCADE,
    FOREIGN KEY (prescription_id) REFERENCES prescription (id) ON DELETE CASCADE,
    INDEX idx_patient_id (patient_id),
    INDEX idx_plan_date (plan_date),
    INDEX idx_status (status)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '随访计划表';

-- =====================================================
-- 7. 系统日志表
-- =====================================================
CREATE TABLE sys_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '操作用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(100) COMMENT '操作类型',
    method VARCHAR(200) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip VARCHAR(50) COMMENT 'IP地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '系统日志表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 密码: 123456 (BCrypt加密)
INSERT INTO
    sys_user (
        username,
        password,
        real_name,
        role,
        phone,
        email
    )
VALUES (
        'admin',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
        '系统管理员',
        'ADMIN',
        '13800138000',
        'admin@vision.com'
    ),
    (
        'optometrist1',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
        '张验光师',
        'OPTOMETRIST',
        '13800138001',
        'zhang@vision.com'
    ),
    (
        'processor1',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
        '李加工师',
        'PROCESSOR',
        '13800138002',
        'li@vision.com'
    ),
    (
        'doctor1',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
        '王医生',
        'DOCTOR',
        '13800138003',
        'wang@vision.com'
    );

-- 插入测试患者数据
INSERT INTO
    patient (
        patient_no,
        name,
        gender,
        birth_date,
        phone,
        address,
        medical_history,
        deleted
    )
VALUES (
        '202401010001',
        '张三',
        'MALE',
        '2010-05-15',
        '13912345678',
        '北京市朝阳区',
        '无特殊病史',
        0
    ),
    (
        '202401010002',
        '李四',
        'FEMALE',
        '1985-08-20',
        '13987654321',
        '上海市浦东新区',
        '高血压',
        0
    ),
    (
        '202401010003',
        '王小明',
        'MALE',
        '2015-03-10',
        '13612341234',
        '广州市天河区',
        '无',
        0
    ),
    (
        '202401010004',
        '赵丽颖',
        'FEMALE',
        '1992-11-28',
        '13855556666',
        '深圳市南山区',
        '无',
        0
    ),
    (
        '202401010005',
        '刘德华',
        'MALE',
        '1961-09-27',
        '13966667777',
        '香港九龙',
        '无',
        0
    );

-- 插入测试验光数据
INSERT INTO
    optometry_record (
        patient_id,
        optometrist_id,
        check_date,
        vision_od_naked,
        vision_os_naked,
        vision_ou_naked,
        sphere_od,
        sphere_os,
        cylinder_od,
        cylinder_os,
        axis_od,
        axis_os,
        vision_od_corrected,
        vision_os_corrected,
        vision_ou_corrected,
        pd_distance,
        status
    )
VALUES (
        1,
        2,
        '2024-01-15 10:00:00',
        0.3,
        0.3,
        0.4,
        -2.50,
        -2.25,
        -0.50,
        -0.50,
        180,
        175,
        1.0,
        1.0,
        1.0,
        62.0,
        'CONFIRMED'
    ),
    (
        1,
        2,
        '2024-03-20 14:30:00',
        0.2,
        0.3,
        0.3,
        -2.75,
        -2.50,
        -0.50,
        -0.50,
        180,
        175,
        1.0,
        1.0,
        1.0,
        62.0,
        'CONFIRMED'
    ),
    (
        1,
        2,
        '2024-06-10 09:00:00',
        0.2,
        0.2,
        0.3,
        -3.00,
        -2.75,
        -0.50,
        -0.50,
        180,
        175,
        1.0,
        1.0,
        1.0,
        62.0,
        'CONFIRMED'
    ),
    (
        2,
        2,
        '2024-01-18 09:00:00',
        0.5,
        0.4,
        0.5,
        -1.50,
        -1.75,
        -0.25,
        -0.25,
        90,
        85,
        1.0,
        1.0,
        1.0,
        64.0,
        'CONFIRMED'
    ),
    (
        3,
        2,
        '2024-02-22 11:00:00',
        0.4,
        0.4,
        0.5,
        -1.00,
        -1.25,
        0.00,
        0.00,
        0,
        0,
        1.0,
        1.0,
        1.0,
        58.0,
        'CONFIRMED'
    ),
    (
        4,
        2,
        '2024-04-01 10:00:00',
        0.5,
        0.5,
        0.6,
        -0.50,
        -0.50,
        0.00,
        0.00,
        0,
        0,
        1.2,
        1.2,
        1.2,
        60.0,
        'CONFIRMED'
    );

-- 插入测试处方数据
INSERT INTO
    prescription (
        prescription_no,
        patient_id,
        optometry_id,
        doctor_id,
        frame_type,
        frame_brand,
        lens_type,
        lens_material,
        lens_index,
        sphere_od,
        sphere_os,
        cylinder_od,
        cylinder_os,
        axis_od,
        axis_os,
        pd_distance,
        price,
        paid_amount,
        payment_status,
        status
    )
VALUES (
        'P202401150001',
        1,
        1,
        4,
        '全框',
        'RayBan',
        '单光',
        '树脂',
        1.56,
        -2.50,
        -2.25,
        -0.50,
        -0.50,
        180,
        175,
        62.0,
        680.00,
        680.00,
        'PAID',
        'COMPLETED'
    ),
    (
        'P202403200001',
        1,
        2,
        4,
        '半框',
        'Oakley',
        '防蓝光',
        '树脂',
        1.60,
        -2.75,
        -2.50,
        -0.50,
        -0.50,
        180,
        175,
        62.0,
        880.00,
        880.00,
        'PAID',
        'COMPLETED'
    ),
    (
        'P202401180001',
        2,
        4,
        4,
        '全框',
        'Gucci',
        '渐进多焦点',
        '树脂',
        1.67,
        -1.50,
        -1.75,
        -0.25,
        -0.25,
        90,
        85,
        64.0,
        1580.00,
        1000.00,
        'PARTIAL',
        'PROCESSING'
    ),
    (
        'P202402220001',
        3,
        5,
        4,
        '全框',
        'Silhouette',
        '单光',
        '树脂',
        1.56,
        -1.00,
        -1.25,
        0.00,
        0.00,
        0,
        0,
        58.0,
        580.00,
        580.00,
        'PAID',
        'COMPLETED'
    );

-- 插入测试随访计划
INSERT INTO
    follow_up_plan (
        patient_id,
        prescription_id,
        plan_date,
        follow_type,
        content,
        status
    )
VALUES (
        1,
        1,
        DATE_ADD('2024-01-15', INTERVAL 1 WEEK),
        'ONE_WEEK',
        '配镜后一周随访，检查佩戴适应情况',
        'COMPLETED'
    ),
    (
        1,
        1,
        DATE_ADD(
            '2024-01-15',
            INTERVAL 1 MONTH
        ),
        'ONE_MONTH',
        '配镜后一月随访，检查视力变化',
        'COMPLETED'
    ),
    (
        1,
        1,
        DATE_ADD(
            '2024-01-15',
            INTERVAL 3 MONTH
        ),
        'THREE_MONTHS',
        '配镜后三月随访，复查验光',
        'COMPLETED'
    ),
    (
        1,
        1,
        DATE_ADD(
            '2024-01-15',
            INTERVAL 6 MONTH
        ),
        'SIX_MONTHS',
        '半年随访，全面检查视力发展情况',
        'PENDING'
    ),
    (
        1,
        2,
        DATE_ADD('2024-03-20', INTERVAL 1 WEEK),
        'ONE_WEEK',
        '配镜后一周随访',
        'COMPLETED'
    ),
    (
        1,
        2,
        DATE_ADD(
            '2024-03-20',
            INTERVAL 1 MONTH
        ),
        'ONE_MONTH',
        '配镜后一月随访',
        'PENDING'
    ),
    (
        2,
        3,
        DATE_ADD('2024-01-18', INTERVAL 1 WEEK),
        'ONE_WEEK',
        '配镜后一周随访',
        'COMPLETED'
    ),
    (
        2,
        3,
        CURDATE(),
        'ONE_MONTH',
        '配镜后一月随访',
        'PENDING'
    ),
    (
        3,
        4,
        CURDATE(),
        'ONE_WEEK',
        '配镜后一周随访',
        'PENDING'
    );

-- 插入测试加工单
INSERT INTO
    processing_order (
        order_no,
        prescription_id,
        processor_id,
        frame_model,
        frame_brand,
        lens_type,
        lens_index,
        status,
        start_time
    )
VALUES (
        'JG202401150001',
        1,
        3,
        'RB2140',
        'RayBan',
        '单光',
        1.56,
        'DELIVERED',
        '2024-01-15 14:00:00'
    ),
    (
        'JG202403200001',
        2,
        3,
        'OX123',
        'Oakley',
        '防蓝光',
        1.60,
        'PROCESSING',
        '2024-03-20 15:30:00'
    ),
    (
        'JG202401180001',
        3,
        3,
        'GG456',
        'Gucci',
        '渐进多焦点',
        1.67,
        'QUALITY_CHECK',
        '2024-01-18 10:00:00'
    ),
    (
        'JG202402220001',
        4,
        3,
        'SL789',
        'Silhouette',
        '单光',
        1.56,
        'COMPLETED',
        '2024-02-22 09:00:00'
    );

-- 更新加工单完成时间
UPDATE processing_order
SET
    complete_time = '2024-01-16 10:00:00',
    quality_check_result = 'PASS'
WHERE
    id = 1;

UPDATE processing_order
SET
    complete_time = '2024-02-23 16:00:00',
    quality_check_result = 'PASS'
WHERE
    id = 4;

-- 更新随访完成时间
UPDATE follow_up_plan
SET
    complete_date = '2024-01-22 10:00:00',
    follow_result = '佩戴适应良好，无不适'
WHERE
    id = 1;

UPDATE follow_up_plan
SET
    complete_date = '2024-02-15 14:00:00',
    follow_result = '视力稳定，矫正效果满意'
WHERE
    id = 2;

UPDATE follow_up_plan
SET
    complete_date = '2024-04-15 11:00:00',
    follow_result = '复查验光，度数稳定'
WHERE
    id = 3;

UPDATE follow_up_plan
SET
    complete_date = '2024-03-27 09:00:00',
    follow_result = '佩戴正常'
WHERE
    id = 5;

UPDATE follow_up_plan
SET
    complete_date = '2024-01-25 15:00:00',
    follow_result = '适应良好'
WHERE
    id = 7;

-- =====================================================
-- 验证数据
-- =====================================================
SELECT '========================================' as '';

SELECT '视光管理系统数据库初始化完成！' as message;

SELECT '========================================' as '';

SELECT '====== 系统用户 ======' as '';

SELECT id, username, real_name, role FROM sys_user;

SELECT '====== 患者列表 ======' as '';

SELECT
    id,
    patient_no,
    name,
    gender,
    phone,
    deleted
FROM patient
WHERE
    deleted = 0;

SELECT '====== 数据统计 ======' as '';

SELECT (
        SELECT COUNT(*)
        FROM patient
        WHERE
            deleted = 0
    ) as patient_count,
    (
        SELECT COUNT(*)
        FROM optometry_record
    ) as optometry_count,
    (
        SELECT COUNT(*)
        FROM prescription
    ) as prescription_count,
    (
        SELECT COUNT(*)
        FROM processing_order
    ) as processing_count,
    (
        SELECT COUNT(*)
        FROM follow_up_plan
    ) as followup_count;

SELECT '========================================' as '';

SELECT '登录账号: admin / 123456' as login_info;

SELECT '========================================' as '';