package com.example.invoice.service;

import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.RepaymentRecord;
import com.example.invoice.mapper.RepaymentRecordMapper;
import com.example.invoice.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 还款记录服务类，处理还款记录的增删改查及开票状态管理
 */
@Service
public class RepaymentRecordService {
    @Autowired
    private RepaymentRecordMapper repaymentRecordMapper;

    /**
     * 查询所有还款记录列表
     *
     * @return 所有还款记录列表
     */
    public Result<List<RepaymentRecord>> getAllRecords() {
        List<RepaymentRecord> records = repaymentRecordMapper.selectAll();
        return Result.success(records);
    }

    /**
     * 根据ID查询还款记录
     *
     * @param id 还款记录ID
     * @return 还款记录信息，不存在则返回错误
     */
    public Result<RepaymentRecord> getRecordById(Long id) {
        RepaymentRecord record = repaymentRecordMapper.selectById(id);
        if (record == null) {
            return Result.error("还款记录不存在");
        }
        return Result.success(record);
    }

    /**
     * 根据合同ID查询还款记录列表
     *
     * @param contractId 合同ID
     * @return 该合同关联的还款记录列表
     */
    public Result<List<RepaymentRecord>> getRecordsByContract(Long contractId) {
        List<RepaymentRecord> records = repaymentRecordMapper.selectByContractId(contractId);
        return Result.success(records);
    }

    /**
     * 根据客户ID查询还款记录列表
     *
     * @param customerId 客户ID
     * @return 该客户关联的还款记录列表
     */
    public Result<List<RepaymentRecord>> getRecordsByCustomer(Long customerId) {
        List<RepaymentRecord> records = repaymentRecordMapper.selectByCustomerId(customerId);
        return Result.success(records);
    }

    /**
     * 根据开票状态查询还款记录列表
     *
     * @param status 开票状态
     * @return 符合状态的还款记录列表
     */
    public Result<List<RepaymentRecord>> getRecordsByStatus(String status) {
        List<RepaymentRecord> records = repaymentRecordMapper.selectByInvoiceStatus(status);
        return Result.success(records);
    }

    /**
     * 创建还款记录，自动生成编号、默认税率和开票状态
     * 可开票金额 = 还款总额 - 本金部分（本金不可开票，仅利息/手续费/罚息等可开票）
     *
     * @param record 还款记录信息
     * @return 创建成功的还款记录
     */
    public Result<RepaymentRecord> createRecord(RepaymentRecord record) {
        record.setRecordNo(IdGenerator.generateRecordNo());
        if (record.getTaxRate() == null) {
            record.setTaxRate(new BigDecimal("6.00"));
        }
        if (record.getInvoiceStatus() == null) {
            record.setInvoiceStatus("UNINVOICED");
        }
        if (record.getInvoicedAmount() == null) {
            record.setInvoicedAmount(BigDecimal.ZERO);
        }
        // 计算可开票金额 = 还款总额 - 本金部分
        BigDecimal totalAmount = record.getTotalAmount() != null ? record.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal principalAmount = record.getPrincipalAmount() != null ? record.getPrincipalAmount() : BigDecimal.ZERO;
        BigDecimal taxableAmount = totalAmount.subtract(principalAmount);
        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            taxableAmount = BigDecimal.ZERO;
        }
        // 若用户未指定可开票金额则自动计算
        if (record.getTaxableAmount() == null) {
            record.setTaxableAmount(taxableAmount);
        }
        if (record.getTaxAmount() == null) {
            BigDecimal interestAmount = record.getInterestAmount() != null ? record.getInterestAmount() : BigDecimal.ZERO;
            record.setTaxAmount(interestAmount.multiply(record.getTaxRate()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP));
        }
        record.setRemainingAmount(record.getTaxableAmount().subtract(record.getInvoicedAmount()));
        repaymentRecordMapper.insert(record);
        return Result.success("创建成功", record);
    }

    /**
     * 更新还款记录，重新计算剩余金额和开票状态
     *
     * @param record 还款记录信息（含ID）
     * @return 更新成功的还款记录
     */
    public Result<RepaymentRecord> updateRecord(RepaymentRecord record) {
        RepaymentRecord existing = repaymentRecordMapper.selectById(record.getId());
        if (existing == null) {
            return Result.error("还款记录不存在");
        }
        BigDecimal taxable = record.getTaxableAmount() != null ? record.getTaxableAmount() : BigDecimal.ZERO;
        BigDecimal invoiced = record.getInvoicedAmount() != null ? record.getInvoicedAmount() : BigDecimal.ZERO;
        // 上限校验：累计开票金额不能超过可开票金额
        if (invoiced.compareTo(taxable) > 0) {
            return Result.error("累计开票金额不能超过可开票金额");
        }
        record.setRemainingAmount(taxable.subtract(invoiced));
        String status;
        if (invoiced.compareTo(BigDecimal.ZERO) == 0) {
            status = "UNINVOICED";
        } else if (invoiced.compareTo(taxable) == 0) {
            status = "INVOICED";
        } else {
            status = "PARTIAL_INVOICED";
        }
        record.setInvoiceStatus(status);
        repaymentRecordMapper.update(record);
        return Result.success("更新成功", record);
    }

    /**
     * 更新还款记录的开票状态
     *
     * @param recordId 还款记录ID
     * @param status 开票状态（UNINVOICED/PARTIAL_INVOICED/INVOICED）
     * @return 更新成功的还款记录
     */
    public Result<RepaymentRecord> updateInvoiceStatus(Long recordId, String status) {
        RepaymentRecord record = repaymentRecordMapper.selectById(recordId);
        if (record == null) {
            return Result.error("还款记录不存在");
        }
        record.setInvoiceStatus(status);
        repaymentRecordMapper.update(record);
        return Result.success("更新成功", record);
    }
}