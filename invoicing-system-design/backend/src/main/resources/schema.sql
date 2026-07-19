-- 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    role VARCHAR(20) DEFAULT 'USER',
    status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 客户信息表
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    tax_id VARCHAR(50) UNIQUE,
    address VARCHAR(200),
    phone VARCHAR(50),
    bank_name VARCHAR(100),
    bank_account VARCHAR(50),
    email VARCHAR(100),
    contact_name VARCHAR(50),
    contact_phone VARCHAR(50),
    status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_customer_name ON customer(customer_name);

-- 贷款合同表
CREATE TABLE IF NOT EXISTS loan_contract (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_no VARCHAR(50) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL,
    principal DECIMAL(18,2) NOT NULL,
    annual_rate DECIMAL(10,6) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_lc_customer_id ON loan_contract(customer_id);

-- 还款记录表
CREATE TABLE IF NOT EXISTS repayment_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    record_no VARCHAR(50) NOT NULL UNIQUE,
    contract_id BIGINT NOT NULL,
    repayment_date DATE NOT NULL,
    fee_type VARCHAR(20) NOT NULL,
    principal_amount DECIMAL(18,2),
    interest_amount DECIMAL(18,2),
    total_amount DECIMAL(18,2) NOT NULL,
    tax_rate DECIMAL(5,2) DEFAULT 6.00,
    tax_amount DECIMAL(18,2),
    taxable_amount DECIMAL(18,2) NOT NULL,
    invoiced_amount DECIMAL(18,2) DEFAULT 0,
    remaining_amount DECIMAL(18,2) NOT NULL,
    invoice_status VARCHAR(20) DEFAULT 'UNINVOICED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_rr_contract_id ON repayment_record(contract_id);
CREATE INDEX IF NOT EXISTS idx_rr_status ON repayment_record(invoice_status);

-- 发票主表
CREATE TABLE IF NOT EXISTS invoice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_no VARCHAR(50) UNIQUE,
    invoice_code VARCHAR(50),
    invoice_type VARCHAR(20) NOT NULL,
    invoice_kind VARCHAR(20) NOT NULL,
    customer_id BIGINT NOT NULL,
    total_amount DECIMAL(18,2) NOT NULL,
    tax_amount DECIMAL(18,2) NOT NULL,
    taxable_amount DECIMAL(18,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'DRAFT',
    red_confirmation_no VARCHAR(50),
    original_invoice_id BIGINT,
    remark VARCHAR(500),
    external_invoice_id VARCHAR(50),
    external_invoice_no VARCHAR(50),
    external_invoice_code VARCHAR(50),
    pdf_url VARCHAR(500),
    xml_url VARCHAR(500),
    qr_code_url VARCHAR(500),
    check_code VARCHAR(50),
    machine_no VARCHAR(50),
    fail_reason VARCHAR(500),
    issue_date DATE,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_inv_no ON invoice(invoice_no);
CREATE INDEX IF NOT EXISTS idx_inv_customer ON invoice(customer_id);
CREATE INDEX IF NOT EXISTS idx_inv_status ON invoice(status);
CREATE INDEX IF NOT EXISTS idx_inv_kind ON invoice(invoice_kind);

-- 发票明细表
CREATE TABLE IF NOT EXISTS invoice_detail (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    repayment_record_id BIGINT,
    contract_id BIGINT NOT NULL,
    contract_no VARCHAR(50),
    fee_type VARCHAR(20) NOT NULL,
    tax_class_code VARCHAR(30),
    tax_class_name VARCHAR(100),
    interest_start_date DATE,
    interest_end_date DATE,
    principal_amount DECIMAL(18,2),
    annual_rate DECIMAL(10,6),
    interest_amount DECIMAL(18,2),
    tax_rate DECIMAL(5,2) DEFAULT 6.00,
    tax_amount DECIMAL(18,2),
    total_amount DECIMAL(18,2) NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_id_invoice ON invoice_detail(invoice_id);
CREATE INDEX IF NOT EXISTS idx_id_repayment ON invoice_detail(repayment_record_id);

-- 红字确认单表
CREATE TABLE IF NOT EXISTS red_confirmation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    confirmation_no VARCHAR(50) NOT NULL UNIQUE,
    invoice_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    cancel_type VARCHAR(20) DEFAULT 'FULL',
    cancel_reason VARCHAR(50) NOT NULL,
    cancel_amount DECIMAL(18,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING_CONFIRM',
    initiator_id BIGINT,
    initiator_name VARCHAR(50),
    confirmer_id BIGINT,
    confirmer_name VARCHAR(50),
    initiate_time TIMESTAMP,
    confirm_time TIMESTAMP,
    expire_time TIMESTAMP,
    remark VARCHAR(500),
    red_repayment_record_ids VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_rc_no ON red_confirmation(confirmation_no);
CREATE INDEX IF NOT EXISTS idx_rc_status ON red_confirmation(status);

-- 批量开票任务表
CREATE TABLE IF NOT EXISTS batch_invoice_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_no VARCHAR(50) NOT NULL UNIQUE,
    task_name VARCHAR(100),
    task_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    total_count INT DEFAULT 0,
    success_count INT DEFAULT 0,
    fail_count INT DEFAULT 0,
    progress DECIMAL(5,2) DEFAULT 0,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_bit_status ON batch_invoice_task(status);

-- 批量开票任务明细表
CREATE TABLE IF NOT EXISTS batch_invoice_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    invoice_id BIGINT,
    customer_id BIGINT NOT NULL,
    contract_id BIGINT,
    repayment_record_id BIGINT,
    item_data CLOB,
    status VARCHAR(20) DEFAULT 'PENDING',
    error_message VARCHAR(500)
);
CREATE INDEX IF NOT EXISTS idx_bii_task ON batch_invoice_item(task_id);

-- 税收分类编码表
CREATE TABLE IF NOT EXISTS tax_classification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tax_code VARCHAR(30) NOT NULL UNIQUE,
    tax_name VARCHAR(100) NOT NULL,
    fee_type VARCHAR(20) NOT NULL,
    tax_rate DECIMAL(5,2) DEFAULT 6.00,
    status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_tc_fee ON tax_classification(fee_type);

-- 操作日志表
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50),
    operation_type VARCHAR(30) NOT NULL,
    module VARCHAR(30) NOT NULL,
    target_id BIGINT,
    target_no VARCHAR(50),
    content CLOB,
    old_value CLOB,
    new_value CLOB,
    client_ip VARCHAR(50),
    user_agent VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_ol_type ON sys_operation_log(operation_type);

-- 开票规则配置表
CREATE TABLE IF NOT EXISTS invoice_rule_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_key VARCHAR(50) NOT NULL UNIQUE,
    rule_name VARCHAR(100) NOT NULL,
    rule_value VARCHAR(500) NOT NULL,
    rule_type VARCHAR(20) DEFAULT 'STRING',
    description VARCHAR(500),
    status SMALLINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 通知记录表
CREATE TABLE IF NOT EXISTS notification_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id BIGINT,
    recipient_name VARCHAR(50),
    recipient_type VARCHAR(20),
    notification_type VARCHAR(20) NOT NULL,
    title VARCHAR(200),
    content CLOB,
    business_type VARCHAR(30),
    business_id BIGINT,
    status VARCHAR(20) DEFAULT 'PENDING',
    sent_at TIMESTAMP,
    error_message VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
