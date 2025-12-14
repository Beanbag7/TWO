-- 一键初始化设备借用系统数据库脚本 (Part 1)
-- 包含：数据库创建、表结构定义、基础数据（类型、用户）
-- 作者：Antigravity Agent
-- 时间：2025-12-14

-- 1. 创建数据库
DROP DATABASE IF EXISTS equipmentloansystem;
CREATE DATABASE equipmentloansystem CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE equipmentloansystem;

-- 禁用外键检查以避免创建表时的依赖顺序问题
SET FOREIGN_KEY_CHECKS = 0;

-- 2. 创建表结构

-- 2.1 用户表
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    full_name VARCHAR(50) NOT NULL COMMENT '姓名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    employee_student_id VARCHAR(50) NOT NULL UNIQUE COMMENT '学号/工号',
    phone_number VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    department VARCHAR(100) COMMENT '部门/学院',
    user_type VARCHAR(20) NOT NULL COMMENT '用户类型：管理员/教师/学生/行政人员/研究生',
    is_blacklisted BOOLEAN DEFAULT FALSE COMMENT '是否黑名单',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) COMMENT='用户表';

-- 2.2 设备类型表
CREATE TABLE equipment_types (
    type_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '类型ID',
    type_name VARCHAR(50) NOT NULL UNIQUE COMMENT '类型名称',
    max_borrow_days INT DEFAULT 7 COMMENT '最大借用天数',
    description VARCHAR(255) COMMENT '描述'
) COMMENT='设备类型表';

-- 2.3 设备表
CREATE TABLE equipments (
    equipment_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '设备ID',
    type_id INT NOT NULL COMMENT '设备类型ID',
    equipment_name VARCHAR(100) NOT NULL COMMENT '设备名称',
    brand VARCHAR(50) COMMENT '品牌',
    model VARCHAR(50) COMMENT '型号',
    serial_number VARCHAR(100) NOT NULL UNIQUE COMMENT '序列号',
    status VARCHAR(20) DEFAULT '可用' COMMENT '状态：可用/借出/维护中/已报废',
    purchase_date DATE COMMENT '购买日期',
    price DECIMAL(10, 2) COMMENT '价格',
    storage_location VARCHAR(100) COMMENT '存放位置',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (type_id) REFERENCES equipment_types(type_id)
) COMMENT='设备表';

-- 2.4 预约表
CREATE TABLE reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '预约ID',
    user_id INT NOT NULL COMMENT '用户ID',
    equipment_id INT NOT NULL COMMENT '设备ID',
    scheduled_borrow_date DATE NOT NULL COMMENT '预计借用日期',
    scheduled_return_date DATE NOT NULL COMMENT '预计归还日期',
    status VARCHAR(20) DEFAULT '待审核' COMMENT '状态：待审核/已批准/已拒绝/已取消/已转借用',
    reservation_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
    approver_id INT COMMENT '审批人ID',
    approval_time DATETIME COMMENT '审批时间',
    rejection_reason VARCHAR(255) COMMENT '拒绝原因',
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (equipment_id) REFERENCES equipments(equipment_id),
    FOREIGN KEY (approver_id) REFERENCES users(user_id)
) COMMENT='预约记录表';

-- 2.5 借用记录表
CREATE TABLE borrow_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id INT NOT NULL COMMENT '用户ID',
    equipment_id INT NOT NULL COMMENT '设备ID',
    reservation_id INT COMMENT '关联预约ID',
    actual_borrow_time DATETIME COMMENT '实际借出时间',
    expected_return_time DATETIME COMMENT '预计归还时间',
    actual_return_time DATETIME COMMENT '实际归还时间',
    borrow_status VARCHAR(20) DEFAULT '使用中' COMMENT '状态：使用中/已归还/逾期/损坏',
    checkout_staff_id INT COMMENT '借出经手人ID',
    return_staff_id INT COMMENT '归还经手人ID',
    notes TEXT COMMENT '备注',
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (equipment_id) REFERENCES equipments(equipment_id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id),
    FOREIGN KEY (checkout_staff_id) REFERENCES users(user_id),
    FOREIGN KEY (return_staff_id) REFERENCES users(user_id)
) COMMENT='借用记录表';

-- 2.6 损坏记录表
CREATE TABLE damage_records (
    damage_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '损坏ID',
    borrow_record_id INT NOT NULL COMMENT '借用记录ID',
    reported_by INT NOT NULL COMMENT '报告人ID',
    damage_description TEXT NOT NULL COMMENT '损坏描述',
    severity VARCHAR(20) COMMENT '严重程度：轻度/中度/重度',
    repair_cost DECIMAL(10, 2) COMMENT '维修费用',
    is_resolved BOOLEAN DEFAULT FALSE COMMENT '是否已解决',
    damage_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发生/报告时间',
    resolution_notes TEXT COMMENT '解决备注',
    FOREIGN KEY (borrow_record_id) REFERENCES borrow_records(record_id),
    FOREIGN KEY (reported_by) REFERENCES users(user_id)
) COMMENT='损坏记录表';

-- 2.7 维修记录表
CREATE TABLE maintenance_records (
    maintenance_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '维修ID',
    equipment_id INT NOT NULL COMMENT '设备ID',
    damage_record_id INT COMMENT '关联损坏记录ID',
    start_date DATE NOT NULL COMMENT '开始维修日期',
    completion_date DATE COMMENT '完成日期',
    expected_completion_date DATE COMMENT '预计完成日期',
    maintenance_status VARCHAR(20) DEFAULT '维修中' COMMENT '状态：维修中/已修复/已报废',
    maintenance_description TEXT COMMENT '维修描述',
    technician VARCHAR(50) COMMENT '维修技师',
    cost DECIMAL(10, 2) COMMENT '费用',
    notes TEXT COMMENT '备注',
    FOREIGN KEY (equipment_id) REFERENCES equipments(equipment_id),
    FOREIGN KEY (damage_record_id) REFERENCES damage_records(damage_id)
) COMMENT='维修记录表';


-- 3. 插入基础数据

-- 3.1 插入设备类型数据（15种类型）
INSERT INTO equipment_types (type_name, max_borrow_days, description) VALUES
('单反相机', 3, '专业摄影设备，适合商业拍摄和艺术创作'),
('微单相机', 3, '轻便型无反相机，适合日常拍摄和旅行'),
('投影仪', 5, '办公会议演示设备，支持高清投影'),
('笔记本电脑', 7, '高性能办公笔记本，适合外出办公'),
('无人机', 7, '航拍摄像设备，用于空中拍摄'),
('录音设备', 5, '专业录音设备，适合会议和采访'),
('三脚架', 7, '相机支撑设备，稳定拍摄'),
('闪光灯', 5, '摄影补光设备，室内外拍摄必备'),
('移动硬盘', 3, '大容量存储设备，数据备份和传输'),
('打印机', 7, '办公打印设备，支持彩色和黑白打印'),
('扫描仪', 5, '文档扫描设备，高清扫描'),
('平板电脑', 7, '便携式平板设备，适合演示和展示'),
('摄像机', 5, '专业摄像设备，适合视频录制'),
('音响设备', 3, '扩音设备，适合会议和活动'),
('麦克风', 3, '收音设备，适合录音和直播');

-- 3.2 插入用户数据（45个用户）
-- 管理员用户（5个）
INSERT INTO users (full_name, password, employee_student_id, phone_number, email, department, user_type) VALUES
('系统管理员', 'e10adc3949ba59abbe56e057f20f883e', 'ADMIN001', '13800138000', 'admin@edu.cn', '信息技术中心', '管理员'),
('设备管理员', 'e10adc3949ba59abbe56e057f20f883e', 'ADMIN002', '13800138001', 'equipment_admin@edu.cn', '资产管理处', '管理员'),
('审核管理员', 'e10adc3949ba59abbe56e057f20f883e', 'ADMIN003', '13800138002', 'review_admin@edu.cn', '教务处', '管理员'),
('维修管理员', 'e10adc3949ba59abbe56e057f20f883e', 'ADMIN004', '13800138003', 'maintenance_admin@edu.cn', '后勤处', '管理员'),
('数据管理员', 'e10adc3949ba59abbe56e057f20f883e', 'ADMIN005', '13800138004', 'data_admin@edu.cn', '信息中心', '管理员');

-- 教师用户（10个）
INSERT INTO users (full_name, password, employee_student_id, phone_number, email, department, user_type) VALUES
('王教授', 'e10adc3949ba59abbe56e057f20f883e', 'T2023001', '13900139001', 'wangprof@edu.cn', '计算机科学与技术学院', '教师'),
('李副教授', 'e10adc3949ba59abbe56e057f20f883e', 'T2023002', '13900139002', 'liassociate@edu.cn', '软件工程学院', '教师'),
('张讲师', 'e10adc3949ba59abbe56e057f20f883e', 'T2023003', '13900139003', 'zhanglect@edu.cn', '人工智能学院', '教师'),
('陈助教', 'e10adc3949ba59abbe56e057f20f883e', 'T2023004', '13900139004', 'chenassist@edu.cn', '数据科学学院', '教师'),
('刘研究员', 'e10adc3949ba59abbe56e057f20f883e', 'T2023005', '13900139005', 'liuresearch@edu.cn', '网络空间安全学院', '教师'),
('杨教授', 'e10adc3949ba59abbe56e057f20f883e', 'T2023006', '13900139006', 'yangprof@edu.cn', '电子信息学院', '教师'),
('周副教授', 'e10adc3949ba59abbe56e057f20f883e', 'T2023007', '13900139007', 'zhouassociate@edu.cn', '机械工程学院', '教师'),
('吴讲师', 'e10adc3949ba59abbe56e057f20f883e', 'T2023008', '13900139008', 'wulct@edu.cn', '材料科学与工程学院', '教师'),
('郑助教', 'e10adc3949ba59abbe56e057f20f883e', 'T2023009', '13900139009', 'zhengassist@edu.cn', '化学化工学院', '教师'),
('孙研究员', 'e10adc3949ba59abbe56e057f20f883e', 'T2023010', '13900139010', 'sunresearch@edu.cn', '生命科学学院', '教师');

-- 学生用户（20个）
INSERT INTO users (full_name, password, employee_student_id, phone_number, email, department, user_type) VALUES
('张明', 'e10adc3949ba59abbe56e057f20f883e', 'S2023001', '13800138010', 'zhangming@edu.cn', '计算机科学与技术学院', '学生'),
('李华', 'e10adc3949ba59abbe56e057f20f883e', 'S2023002', '13800138011', 'lihua@edu.cn', '软件工程学院', '学生'),
('王芳', 'e10adc3949ba59abbe56e057f20f883e', 'S2023003', '13800138012', 'wangfang@edu.cn', '人工智能学院', '学生'),
('赵强', 'e10adc3949ba59abbe56e057f20f883e', 'S2023004', '13800138013', 'zhaoqiang@edu.cn', '数据科学学院', '学生'),
('刘丽', 'e10adc3949ba59abbe56e057f20f883e', 'S2023005', '13800138014', 'liuli@edu.cn', '网络空间安全学院', '学生'),
('陈伟', 'e10adc3949ba59abbe56e057f20f883e', 'S2023006', '13800138015', 'chenwei@edu.cn', '电子信息学院', '学生'),
('杨敏', 'e10adc3949ba59abbe56e057f20f883e', 'S2023007', '13800138016', 'yangmin@edu.cn', '机械工程学院', '学生'),
('周杰', 'e10adc3949ba59abbe56e057f20f883e', 'S2023008', '13800138017', 'zhoujie@edu.cn', '材料科学与工程学院', '学生'),
('吴娜', 'e10adc3949ba59abbe56e057f20f883e', 'S2023009', '13800138018', 'wuna@edu.cn', '化学化工学院', '学生'),
('郑涛', 'e10adc3949ba59abbe56e057f20f883e', 'S2023010', '13800138019', 'zhengtao@edu.cn', '生命科学学院', '学生'),
('孙倩', 'e10adc3949ba59abbe56e057f20f883e', 'S2023011', '13800138020', 'sunqian@edu.cn', '医学部', '学生'),
('马超', 'e10adc3949ba59abbe56e057f20f883e', 'S2023012', '13800138021', 'machao@edu.cn', '商学院', '学生'),
('林静', 'e10adc3949ba59abbe56e057f20f883e', 'S2023013', '13800138022', 'linjing@edu.cn', '法学院', '学生'),
('黄磊', 'e10adc3949ba59abbe56e057f20f883e', 'S2023014', '13800138023', 'huanglei@edu.cn', '文学院', '学生'),
('徐燕', 'e10adc3949ba59abbe56e057f20f883e', 'S2023015', '13800138024', 'xuyan@edu.cn', '外国语学院', '学生'),
('朱军', 'e10adc3949ba59abbe56e057f20f883e', 'S2023016', '13800138025', 'zhujun@edu.cn', '艺术学院', '学生'),
('何伟', 'e10adc3949ba59abbe56e057f20f883e', 'S2023017', '13800138026', 'hewei@edu.cn', '体育学院', '学生'),
('高敏', 'e10adc3949ba59abbe56e057f20f883e', 'S2023018', '13800138027', 'gaomin@edu.cn', '教育学院', '学生'),
('唐磊', 'e10adc3949ba59abbe56e057f20f883e', 'S2023019', '13800138028', 'tanglei@edu.cn', '新闻传播学院', '学生'),
('宋佳', 'e10adc3949ba59abbe56e057f20f883e', 'S2023020', '13800138029', 'songjia@edu.cn', '历史学院', '学生');

-- 行政人员（5个）
INSERT INTO users (full_name, password, employee_student_id, phone_number, email, department, user_type) VALUES
('赵主任', 'e10adc3949ba59abbe56e057f20f883e', 'A2023001', '13700137001', 'zhaodirector@edu.cn', '教务处', '行政人员'),
('钱处长', 'e10adc3949ba59abbe56e057f20f883e', 'A2023002', '13700137002', 'qianhead@edu.cn', '学生处', '行政人员'),
('孙科长', 'e10adc3949ba59abbe56e057f20f883e', 'A2023003', '13700137003', 'sunsection@edu.cn', '财务处', '行政人员'),
('李管理员', 'e10adc3949ba59abbe56e057f20f883e', 'A2023004', '13700137004', 'liadmin@edu.cn', '图书馆', '行政人员'),
('周秘书', 'e10adc3949ba59abbe56e057f20f883e', 'A2023005', '13700137005', 'zhousecretary@edu.cn', '校长办公室', '行政人员');

-- 研究生（5个）
INSERT INTO users (full_name, password, employee_student_id, phone_number, email, department, user_type) VALUES
('周博士', 'e10adc3949ba59abbe56e057f20f883e', 'G2023001', '13600136001', 'zhouphd@edu.cn', '研究生院', '研究生'),
('吴硕士', 'e10adc3949ba59abbe56e057f20f883e', 'G2023002', '13600136002', 'wumaster@edu.cn', '研究生院', '研究生'),
('郑研究生', 'e10adc3949ba59abbe56e057f20f883e', 'G2023003', '13600136003', 'zhenggrad@edu.cn', '研究生院', '研究生'),
('王研究生', 'e10adc3949ba59abbe56e057f20f883e', 'G2023004', '13600136004', 'wanggrad@edu.cn', '研究生院', '研究生'),
('李研究生', 'e10adc3949ba59abbe56e057f20f883e', 'G2023005', '13600136005', 'ligrad@edu.cn', '研究生院', '研究生');

-- 黑名单用户（5个）
INSERT INTO users (full_name, password, employee_student_id, phone_number, email, department, user_type, is_blacklisted) VALUES
('马逾期', 'e10adc3949ba59abbe56e057f20f883e', 'S2023021', '13800138030', 'mayuqi@edu.cn', '璁＄畻鏈虹瀛︿笌鎶€鏈闄?', '瀛︾敓', TRUE),
('鏉ㄥけ淇?, 'e10adc3949ba59abbe56e057f20f883e', 'S2023022', '13800138031', 'yangshixin@edu.cn', '杞欢宸ョ▼瀛﹂櫌', '瀛︾敓', TRUE),
('寮犳崯鍧?, 'e10adc3949ba59abbe56e057f20f883e', 'S2023023', '13800138032', 'zhangsunhuai@edu.cn', '浜哄伐鏅鸿兘瀛﹂櫌', '瀛︾敓', TRUE),
('鐜嬭繚瑙?, 'e10adc3949ba59abbe56e057f20f883e', 'S2023024', '13800138033', 'wangweigui@edu.cn', '鏁版嵁绉戝瀛﹂櫌', '瀛︾敓', TRUE),
('鏉庡け绾?, 'e10adc3949ba59abbe56e057f20f883e', 'S2023025', '13800138034', 'lishiyue@edu.cn', '缃戠粶绌洪棿瀹夊叏瀛﹂櫌', '瀛︾敓', TRUE);
