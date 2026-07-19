package com.example.invoice.controller;

import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.RepaymentRecord;
import com.example.invoice.service.RepaymentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 还款记录控制器，提供还款记录的增删改查及开票状态管理接口
 */
@RestController
@RequestMapping("/api/repayments")
public class RepaymentRecordController {
    @Autowired
    private RepaymentRecordService repaymentRecordService;

    /**
     * 查询还款记录列表，支持按合同ID、客户ID、状态筛选
     * @param contractId 合同ID（可选）
     * @param customerId 客户ID（可选）
     * @param status 开票状态（可选）
     * @return 还款记录列表结果
     */
    @GetMapping
    public Result getAllRecords(@RequestParam(required = false) Long contractId,
                                @RequestParam(required = false) Long customerId,
                                @RequestParam(required = false) String status) {
        if (contractId != null) {
            return repaymentRecordService.getRecordsByContract(contractId);
        }
        if (customerId != null) {
            return repaymentRecordService.getRecordsByCustomer(customerId);
        }
        if (status != null && !status.isEmpty()) {
            return repaymentRecordService.getRecordsByStatus(status);
        }
        return repaymentRecordService.getAllRecords();
    }

    /**
     * 根据ID查询还款记录
     * @param id 记录ID
     * @return 还款记录详情结果
     */
    @GetMapping("/{id}")
    public Result getRecordById(@PathVariable Long id) {
        return repaymentRecordService.getRecordById(id);
    }

    /**
     * 创建还款记录
     * @param record 还款记录信息
     * @return 创建结果
     */
    @PostMapping
    public Result createRecord(@RequestBody RepaymentRecord record) {
        return repaymentRecordService.createRecord(record);
    }

    /**
     * 更新还款记录
     * @param id 记录ID
     * @param record 还款记录信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result updateRecord(@PathVariable Long id, @RequestBody RepaymentRecord record) {
        record.setId(id);
        return repaymentRecordService.updateRecord(record);
    }

    /**
     * 更新还款记录的开票状态
     * @param id 记录ID
     * @param status 开票状态
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public Result updateInvoiceStatus(@PathVariable Long id, @RequestParam String status) {
        return repaymentRecordService.updateInvoiceStatus(id, status);
    }
}
