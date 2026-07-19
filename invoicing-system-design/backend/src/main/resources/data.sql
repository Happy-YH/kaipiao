-- 初始用户（密码明文，演示用）
MERGE INTO sys_user (username, password, real_name, role, status) KEY(username) VALUES ('admin', '123456', '系统管理员', 'ADMIN', 1);
MERGE INTO sys_user (username, password, real_name, role, status) KEY(username) VALUES ('operator', '123456', '开票员', 'OPERATOR', 1);
MERGE INTO sys_user (username, password, real_name, role, status) KEY(username) VALUES ('reviewer', '123456', '审核员', 'REVIEWER', 1);

-- 客户数据
MERGE INTO customer (id, customer_name, tax_id, address, phone, bank_name, bank_account, email, contact_name, contact_phone) KEY(id) VALUES
(1, '深圳前海科技创新有限公司', '91440300MA5F2KXQ3R', '深圳市南山区前海路168号', '0755-86543210', '中国工商银行深圳前海支行', '4000023609200123456', 'finance@qhkc.com', '张经理', '13800138001'),
(2, '广州天河金融服务有限公司', '91440100MA5D2KXQ2R', '广州市天河区珠江新城', '020-87654321', '中国建设银行广州天河支行', '6227003328001234567', 'finance@thjr.com', '李经理', '13900139002'),
(3, '上海浦东发展有限公司', '91310115MA1H8KXQ5N', '上海市浦东新区陆家嘴', '021-62345678', '中国银行上海浦东支行', '6217808001234567890', 'finance@pdjr.com', '王总监', '13700137003');

-- 贷款合同
MERGE INTO loan_contract (id, contract_no, customer_id, principal, annual_rate, start_date, end_date, status) KEY(id) VALUES
(1, 'LOAN-2024-001287', 1, 10000000.00, 4.350000, '2024-01-01', '2027-12-31', 'ACTIVE'),
(2, 'LOAN-2024-001288', 2, 5000000.00, 4.500000, '2024-03-15', '2026-03-14', 'ACTIVE'),
(3, 'LOAN-2025-000512', 3, 8000000.00, 4.200000, '2025-01-01', '2028-12-31', 'ACTIVE');

-- 还款记录
MERGE INTO repayment_record (id, record_no, contract_id, repayment_date, fee_type, principal_amount, interest_amount, total_amount, tax_rate, taxable_amount, invoiced_amount, remaining_amount, invoice_status) KEY(id) VALUES
(1, 'RP20260615001', 1, '2026-06-15', 'INTEREST', 10000000.00, 36164.38, 38212.82, 6.00, 38212.82, 0, 38212.82, 'UNINVOICED'),
(2, 'RP20260615002', 1, '2026-06-15', 'FEE', 10000000.00, 4166.67, 4402.52, 6.00, 4402.52, 0, 4402.52, 'UNINVOICED'),
(3, 'RP20260620003', 2, '2026-06-20', 'INTEREST', 5000000.00, 18750.00, 19875.00, 6.00, 19875.00, 10000.00, 9875.00, 'PARTIAL_INVOICED'),
(4, 'RP20260701004', 2, '2026-07-01', 'INTEREST', 5000000.00, 18750.00, 19875.00, 6.00, 19875.00, 0, 19875.00, 'UNINVOICED'),
(5, 'RP20260701005', 3, '2026-07-01', 'INTEREST', 8000000.00, 28000.00, 29680.00, 6.00, 29680.00, 29680.00, 0, 'INVOICED'),
(6, 'RP20260701006', 3, '2026-07-01', 'FEE', 8000000.00, 3333.33, 3527.78, 6.00, 3527.78, 0, 3527.78, 'UNINVOICED');

-- 税目分类
MERGE INTO tax_classification (id, tax_code, tax_name, fee_type, tax_rate) KEY(id) VALUES
(1, '3050201010000000000', '贷款服务-其他贷款服务', 'INTEREST', 6.00),
(2, '3050201020000000000', '贷款服务-票据贴现服务', 'INTEREST', 6.00),
(3, '3040802000000000000', '商务辅助服务-经纪代理服务', 'FEE', 6.00),
(4, '3050203000000000000', '贷款服务-罚息', 'PENALTY', 6.00),
(5, '3050204000000000000', '贷款服务-承诺费', 'COMMITMENT', 6.00),
(6, '3050299000000000000', '贷款服务-其他', 'OTHER', 6.00);

-- 开票规则配置
MERGE INTO invoice_rule_config (id, rule_key, rule_name, rule_value, rule_type, description) KEY(id) VALUES
(1, 'REVIEW_THRESHOLD', '审核金额阈值', '100000', 'NUMBER', '单张开票金额超过此值需要审核'),
(2, 'BATCH_MUST_REVIEW', '批量任务必审', 'true', 'BOOLEAN', '批量开票任务是否必须经过审核'),
(3, 'MAX_INVOICE_AMOUNT', '单张最大金额', '999999999.99', 'NUMBER', '单张发票最大金额上限'),
(4, 'BUSINESS_START_HOUR', '开票开始时间', '8', 'NUMBER', '工作日开票开始时间（小时）'),
(5, 'BUSINESS_END_HOUR', '开票结束时间', '20', 'NUMBER', '工作日开票结束时间（小时）'),
(6, 'AUTO_RETRY_COUNT', '自动重试次数', '3', 'NUMBER', '开票失败后自动重试次数'),
(7, 'AUTO_RETRY_INTERVAL', '重试间隔(秒)', '30', 'NUMBER', '自动重试间隔秒数'),
(8, 'RED_CONFIRM_HOURS', '红冲确认时效(小时)', '72', 'NUMBER', '红字确认单待确认超时小时数');

-- 已开发票示例
MERGE INTO invoice (id, invoice_no, invoice_type, invoice_kind, customer_id, total_amount, tax_amount, taxable_amount, status, issue_date, external_invoice_no, external_invoice_code, pdf_url, check_code) KEY(id) VALUES
(1, 'FP20260701001', 'SPECIAL', 'BLUE', 3, 29680.00, 1680.00, 28000.00, 'ISSUED', '2026-07-01', 'HZ20260701000001', '044123456', 'https://mock-kingdee/invoice/HZ20260701000001.pdf', '12345678901234567890');

-- 发票明细
MERGE INTO invoice_detail (id, invoice_id, repayment_record_id, contract_id, contract_no, fee_type, tax_class_code, tax_class_name, interest_amount, tax_rate, tax_amount, total_amount, interest_start_date, interest_end_date) KEY(id) VALUES
(1, 1, 5, 3, 'LOAN-2025-000512', 'INTEREST', '3050201010000000000', '贷款服务-其他贷款服务', 28000.00, 6.00, 1680.00, 29680.00, '2026-06-01', '2026-06-30');
