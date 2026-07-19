CREATE DATABASE IF NOT EXISTS invoice_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE invoice_system;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role VARCHAR(20) DEFAULT 'USER' COMMENT '角色：ADMIN/USER',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '客户ID',
    customer_name VARCHAR(100) NOT NULL COMMENT '客户名称',
    tax_id VARCHAR(50) UNIQUE COMMENT '纳税人识别号',
    address VARCHAR(200) COMMENT '地址',
    phone VARCHAR(50) COMMENT '电话',
    bank_name VARCHAR(100) COMMENT '开户行',
    bank_account VARCHAR(50) COMMENT '银行账号',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_customer_name (customer_name),
    INDEX idx_tax_id (tax_id)
) COMMENT='客户信息表';

CREATE TABLE IF NOT EXISTS loan_contract (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '合同ID',
    contract_no VARCHAR(50) NOT NULL UNIQUE COMMENT '贷款合同号',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    principal DECIMAL(18,2) NOT NULL COMMENT '贷款本金',
    annual_rate DECIMAL(10,6) NOT NULL COMMENT '年利率',
    start_date DATE NOT NULL COMMENT '贷款开始日期',
    end_date DATE NOT NULL COMMENT '贷款结束日期',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/PAID_OFF/TERMINATED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    INDEX idx_contract_no (contract_no),
    INDEX idx_customer_id (customer_id)
) COMMENT='贷款合同表';

CREATE TABLE IF NOT EXISTS repayment_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '还款记录ID',
    record_no VARCHAR(50) NOT NULL UNIQUE COMMENT '还款记录号',
    contract_id BIGINT NOT NULL COMMENT '合同ID',
    repayment_date DATE NOT NULL COMMENT '还款日期',
    fee_type VARCHAR(20) NOT NULL COMMENT '费用类型：INTEREST/FEE/PENALTY/COMMITMENT/OTHER',
    principal_amount DECIMAL(18,2) COMMENT '本金金额',
    interest_amount DECIMAL(18,2) COMMENT '利息金额',
    total_amount DECIMAL(18,2) NOT NULL COMMENT '总金额',
    tax_rate DECIMAL(5,2) DEFAULT 6.00 COMMENT '税率',
    tax_amount DECIMAL(18,2) COMMENT '税额',
    taxable_amount DECIMAL(18,2) NOT NULL COMMENT '可开票总金额',
    invoiced_amount DECIMAL(18,2) DEFAULT 0 COMMENT '实际开票金额',
    remaining_amount DECIMAL(18,2) NOT NULL COMMENT '剩余可开票金额',
    invoice_status VARCHAR(20) DEFAULT 'UNINVOICED' COMMENT '开票状态：UNINVOICED/INVOICED/PARTIAL_INVOICED/RED_CANCELLED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (contract_id) REFERENCES loan_contract(id),
    INDEX idx_record_no (record_no),
    INDEX idx_contract_id (contract_id),
    INDEX idx_invoice_status (invoice_status)
) COMMENT='还款记录表';

CREATE TABLE IF NOT EXISTS invoice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '发票ID',
    invoice_no VARCHAR(50) UNIQUE COMMENT '发票号码',
    invoice_code VARCHAR(50) COMMENT '发票代码',
    invoice_type VARCHAR(20) NOT NULL COMMENT '发票类型：DIGITAL/ELECTRONIC/PAPER',
    invoice_kind VARCHAR(20) NOT NULL COMMENT '发票种类：BLUE/RED',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    total_amount DECIMAL(18,2) NOT NULL COMMENT '价税合计',
    tax_amount DECIMAL(18,2) NOT NULL COMMENT '税额',
    taxable_amount DECIMAL(18,2) NOT NULL COMMENT '不含税金额',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT/PENDING_REVIEW/REVIEWED/PENDING_ISSUE/ISSUING/ISSUED/DELIVERED/RED_CANCELLED/FAILED',
    red_confirmation_no VARCHAR(50) COMMENT '红字确认单号',
    original_invoice_id BIGINT COMMENT '原蓝字发票ID（红冲时关联）',
    remark VARCHAR(500) COMMENT '备注',
    external_invoice_id VARCHAR(50) COMMENT '金蝶发票ID',
    external_invoice_no VARCHAR(50) COMMENT '金蝶发票号码',
    external_invoice_code VARCHAR(50) COMMENT '金蝶发票代码',
    pdf_url VARCHAR(500) COMMENT 'PDF下载地址',
    xml_url VARCHAR(500) COMMENT 'XML下载地址',
    qr_code_url VARCHAR(500) COMMENT '二维码地址',
    check_code VARCHAR(50) COMMENT '校验码',
    machine_no VARCHAR(50) COMMENT '机器编号',
    fail_reason VARCHAR(500) COMMENT '开票失败原因',
    issue_date DATE COMMENT '开票日期',
    created_by BIGINT COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (original_invoice_id) REFERENCES invoice(id),
    INDEX idx_invoice_no (invoice_no),
    INDEX idx_customer_id (customer_id),
    INDEX idx_status (status),
    INDEX idx_invoice_kind (invoice_kind)
) COMMENT='发票主表';

CREATE TABLE IF NOT EXISTS invoice_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID',
    invoice_id BIGINT NOT NULL COMMENT '发票ID',
    repayment_record_id BIGINT COMMENT '还款记录ID',
    contract_id BIGINT NOT NULL COMMENT '合同ID',
    contract_no VARCHAR(50) COMMENT '合同号',
    fee_type VARCHAR(20) NOT NULL COMMENT '费用类型',
    tax_class_code VARCHAR(30) COMMENT '税收分类编码',
    tax_class_name VARCHAR(100) COMMENT '税收分类名称',
    interest_start_date DATE COMMENT '利息起始日期',
    interest_end_date DATE COMMENT '利息结束日期',
    principal_amount DECIMAL(18,2) COMMENT '本金',
    annual_rate DECIMAL(10,6) COMMENT '年利率',
    interest_amount DECIMAL(18,2) COMMENT '利息金额',
    tax_rate DECIMAL(5,2) DEFAULT 6.00 COMMENT '税率',
    tax_amount DECIMAL(18,2) COMMENT '税额',
    total_amount DECIMAL(18,2) NOT NULL COMMENT '价税合计',
    FOREIGN KEY (invoice_id) REFERENCES invoice(id),
    FOREIGN KEY (repayment_record_id) REFERENCES repayment_record(id),
    FOREIGN KEY (contract_id) REFERENCES loan_contract(id),
    INDEX idx_invoice_id (invoice_id),
    INDEX idx_repayment_record_id (repayment_record_id)
) COMMENT='发票明细表';

CREATE TABLE IF NOT EXISTS red_confirmation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '确认单ID',
    confirmation_no VARCHAR(50) NOT NULL UNIQUE COMMENT '红字确认单号',
    invoice_id BIGINT NOT NULL COMMENT '原蓝字发票ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    cancel_type VARCHAR(20) DEFAULT 'FULL' COMMENT '红冲类型：FULL/PARTIAL',
    cancel_reason VARCHAR(50) NOT NULL COMMENT '红冲原因',
    cancel_amount DECIMAL(18,2) NOT NULL COMMENT '冲销金额',
    status VARCHAR(20) DEFAULT 'PENDING_CONFIRM' COMMENT '状态：PENDING_CONFIRM/CONFIRMED/REJECTED/EXPIRED/CANCELLED',
    initiator_id BIGINT COMMENT '发起方ID',
    initiator_name VARCHAR(50) COMMENT '发起方名称',
    confirmer_id BIGINT COMMENT '确认方ID',
    confirmer_name VARCHAR(50) COMMENT '确认方名称',
    initiate_time DATETIME COMMENT '发起时间',
    confirm_time DATETIME COMMENT '确认时间',
    expire_time DATETIME COMMENT '过期时间',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (invoice_id) REFERENCES invoice(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    INDEX idx_confirmation_no (confirmation_no),
    INDEX idx_invoice_id (invoice_id),
    INDEX idx_status (status)
) COMMENT='红字确认单表';

CREATE TABLE IF NOT EXISTS batch_invoice_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    task_no VARCHAR(50) NOT NULL UNIQUE COMMENT '任务编号',
    task_name VARCHAR(100) COMMENT '任务名称',
    task_type VARCHAR(20) NOT NULL COMMENT '任务类型：INVOICE/RED_CANCEL',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING/RUNNING/COMPLETED/FAILED/PAUSED',
    total_count INT DEFAULT 0 COMMENT '总数量',
    success_count INT DEFAULT 0 COMMENT '成功数量',
    fail_count INT DEFAULT 0 COMMENT '失败数量',
    progress DECIMAL(5,2) DEFAULT 0 COMMENT '进度百分比',
    created_by BIGINT COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    finished_at DATETIME COMMENT '完成时间',
    INDEX idx_task_no (task_no),
    INDEX idx_status (status)
) COMMENT='批量开票任务表';

CREATE TABLE IF NOT EXISTS batch_invoice_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    invoice_id BIGINT COMMENT '发票ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    contract_id BIGINT COMMENT '合同ID',
    repayment_record_id BIGINT COMMENT '还款记录ID',
    item_data TEXT COMMENT '业务数据JSON',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING/SUCCESS/FAILED',
    error_message VARCHAR(500) COMMENT '错误信息',
    FOREIGN KEY (task_id) REFERENCES batch_invoice_task(id),
    FOREIGN KEY (invoice_id) REFERENCES invoice(id),
    INDEX idx_task_id (task_id),
    INDEX idx_status (status)
) COMMENT='批量开票任务明细表';

CREATE TABLE IF NOT EXISTS tax_classification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    tax_code VARCHAR(30) NOT NULL UNIQUE COMMENT '税收分类编码',
    tax_name VARCHAR(100) NOT NULL COMMENT '税收分类名称',
    fee_type VARCHAR(20) NOT NULL COMMENT '适用费用类型',
    tax_rate DECIMAL(5,2) DEFAULT 6.00 COMMENT '税率',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_fee_type (fee_type),
    INDEX idx_tax_code (tax_code)
) COMMENT='税收分类编码表';

CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT COMMENT '操作人ID',
    username VARCHAR(50) COMMENT '操作人用户名',
    operation_type VARCHAR(30) NOT NULL COMMENT '操作类型：CREATE/UPDATE/DELETE/APPROVE/REJECT/ISSUE/RED_CANCEL/DELIVER',
    module VARCHAR(30) NOT NULL COMMENT '操作模块：INVOICE/CUSTOMER/CONTRACT/REPAYMENT/RED_CONFIRMATION/BATCH/TAX_CONFIG/SYSTEM',
    target_id BIGINT COMMENT '操作目标ID',
    target_no VARCHAR(50) COMMENT '操作目标编号',
    content TEXT COMMENT '操作内容',
    old_value TEXT COMMENT '修改前值JSON',
    new_value TEXT COMMENT '修改后值JSON',
    client_ip VARCHAR(50) COMMENT '客户端IP',
    user_agent VARCHAR(500) COMMENT '客户端设备信息',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_module (module),
    INDEX idx_created_at (created_at)
) COMMENT='操作日志表';

CREATE TABLE IF NOT EXISTS invoice_rule_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    rule_key VARCHAR(50) NOT NULL UNIQUE COMMENT '规则键',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_value VARCHAR(500) NOT NULL COMMENT '规则值',
    rule_type VARCHAR(20) DEFAULT 'STRING' COMMENT '值类型：STRING/NUMBER/BOOLEAN',
    description VARCHAR(500) COMMENT '说明',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='开票规则配置表';

CREATE TABLE IF NOT EXISTS notification_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    recipient_id BIGINT COMMENT '接收人ID',
    recipient_name VARCHAR(50) COMMENT '接收人名称',
    recipient_type VARCHAR(20) COMMENT '接收人类型：INTERNAL/CUSTOMER',
    notification_type VARCHAR(20) NOT NULL COMMENT '通知类型：EMAIL/SMS/IN_APP',
    title VARCHAR(200) COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    business_type VARCHAR(30) COMMENT '业务类型：INVOICE_ISSUED/INVOICE_DELIVERED/RED_CONFIRMATION/RED_COUNTDOWN/BATCH_COMPLETE',
    business_id BIGINT COMMENT '关联业务ID',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING/SENT/READ/FAILED',
    sent_at DATETIME COMMENT '发送时间',
    error_message VARCHAR(500) COMMENT '错误信息',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_recipient_id (recipient_id),
    INDEX idx_notification_type (notification_type),
    INDEX idx_business_type (business_type),
    INDEX idx_status (status)
) COMMENT='通知记录表';

INSERT INTO sys_user (username, password, real_name, role) VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '系统管理员', 'ADMIN');

INSERT INTO customer (customer_name, tax_id, address, phone, bank_name, bank_account) VALUES 
('深圳前海科技创新有限公司', '91440300MA5F2KXQ3R', '深圳市南山区前海路168号', '0755-86543210', '中国工商银行深圳前海支行', '4000023609200123456'),
('广州天河金融服务有限公司', '91440100MA5D2KXQ2R', '广州市天河区珠江新城', '020-87654321', '中国建设银行广州天河支行', '6227003328001234567');

INSERT INTO loan_contract (contract_no, customer_id, principal, annual_rate, start_date, end_date) VALUES 
('LOAN-2024-001287', 1, 10000000.00, 4.35, '2024-01-01', '2027-12-31'),
('LOAN-2024-001288', 2, 5000000.00, 4.50, '2024-03-15', '2026-03-14');

INSERT INTO repayment_record (record_no, contract_id, repayment_date, fee_type, principal_amount, interest_amount, total_amount, tax_rate, taxable_amount, invoiced_amount, remaining_amount, invoice_status) VALUES 
('RP20260615001', 1, '2026-06-15', 'INTEREST', 10000000.00, 36164.38, 38212.82, 6.00, 38212.82, 0, 38212.82, 'UNINVOICED'),
('RP20260615002', 1, '2026-06-15', 'FEE', 10000000.00, 4166.67, 4402.52, 6.00, 4402.52, 0, 4402.52, 'UNINVOICED'),
('RP20260620003', 2, '2026-06-20', 'INTEREST', 5000000.00, 18750.00, 19875.00, 6.00, 19875.00, 10000.00, 9875.00, 'PARTIAL_INVOICED');

INSERT INTO tax_classification (tax_code, tax_name, fee_type, tax_rate) VALUES 
('3050201010000000000', '贷款服务-其他贷款服务', 'INTEREST', 6.00),
('3050201020000000000', '贷款服务-票据贴现服务', 'INTEREST', 6.00),
('3040802000000000000', '商务辅助服务-经纪代理服务', 'FEE', 6.00),
('3050203000000000000', '贷款服务-罚息', 'PENALTY', 6.00),
('3050204000000000000', '贷款服务-承诺费', 'COMMITMENT', 6.00),
('3050299000000000000', '贷款服务-其他', 'OTHER', 6.00);

INSERT INTO invoice_rule_config (rule_key, rule_name, rule_value, rule_type, description) VALUES 
('REVIEW_THRESHOLD', '审核金额阈值', '100000', 'NUMBER', '单张开票金额超过此值需要审核'),
('BATCH_MUST_REVIEW', '批量任务必审', 'true', 'BOOLEAN', '批量开票任务是否必须经过审核'),
('MAX_INVOICE_AMOUNT', '单张最大金额', '999999999.99', 'NUMBER', '单张发票最大金额上限'),
('BUSINESS_START_HOUR', '开票开始时间', '8', 'NUMBER', '工作日开票开始时间（小时）'),
('BUSINESS_END_HOUR', '开票结束时间', '20', 'NUMBER', '工作日开票结束时间（小时）'),
('AUTO_RETRY_COUNT', '自动重试次数', '3', 'NUMBER', '开票失败后自动重试次数'),
('AUTO_RETRY_INTERVAL', '重试间隔(秒)', '30', 'NUMBER', '自动重试间隔秒数'),
('RED_CONFIRM_HOURS', '红冲确认时效(小时)', '72', 'NUMBER', '红字确认单待确认超时小时数');