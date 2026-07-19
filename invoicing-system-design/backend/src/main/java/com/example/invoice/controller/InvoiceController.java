package com.example.invoice.controller;

import com.example.invoice.dto.request.InvoiceCreateRequest;
import com.example.invoice.dto.response.Result;
import com.example.invoice.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 发票控制器，提供发票的增删改查、开具、交付等REST接口
 */
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;

    /**
     * 查询发票列表，支持按客户ID、状态、种类筛选
     * @param customerId 客户ID（可选）
     * @param status 发票状态（可选）
     * @param kind 发票种类（可选）
     * @return 发票列表结果
     */
    @GetMapping
    public Result getAllInvoices(@RequestParam(required = false) Long customerId,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(required = false) String kind) {
        if (customerId != null) {
            return invoiceService.getInvoicesByCustomer(customerId);
        }
        if (status != null && !status.isEmpty()) {
            return invoiceService.getInvoicesByStatus(status);
        }
        if (kind != null && !kind.isEmpty()) {
            return invoiceService.getInvoicesByKind(kind);
        }
        return invoiceService.getAllInvoices();
    }

    /**
     * 根据ID查询发票
     * @param id 发票ID
     * @return 发票详情结果
     */
    @GetMapping("/{id}")
    public Result getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);
    }

    /**
     * 查询发票详情（含明细行）
     * @param id 发票ID
     * @return 发票及明细结果
     */
    @GetMapping("/{id}/detail")
    public Result getInvoiceDetailWithDetails(@PathVariable Long id) {
        return invoiceService.getInvoiceDetailWithDetails(id);
    }

    /**
     * 根据发票编号查询发票
     * @param invoiceNo 发票编号
     * @return 发票详情结果
     */
    @GetMapping("/no/{invoiceNo}")
    public Result getInvoiceByNo(@PathVariable String invoiceNo) {
        return invoiceService.getInvoiceByNo(invoiceNo);
    }

    /**
     * 创建发票
     * @param request 发票创建请求体
     * @return 创建结果
     */
    @PostMapping
    public Result createInvoice(@RequestBody InvoiceCreateRequest request) {
        return invoiceService.createInvoice(request);
    }

    /**
     * 提交审核
     *
     * @param id 发票ID
     * @return 提交结果
     */
    @PostMapping("/{id}/submit-review")
    public Result submitForReview(@PathVariable Long id) {
        return invoiceService.submitForReview(id);
    }

    /**
     * 审核发票
     *
     * @param id 发票ID
     * @param pass 是否通过
     * @param rejectReason 驳回原因
     * @return 审核结果
     */
    @PostMapping("/{id}/review")
    public Result reviewInvoice(@PathVariable Long id,
                                 @RequestParam boolean pass,
                                 @RequestParam(required = false) String rejectReason) {
        return invoiceService.reviewInvoice(id, pass, rejectReason);
    }

    /**
     * 开具发票
     * @param id 发票ID
     * @return 开具结果
     */
    @PostMapping("/{id}/issue")
    public Result issueInvoice(@PathVariable Long id) {
        return invoiceService.issueInvoice(id);
    }

    /**
     * 交付发票
     * @param id 发票ID
     * @return 交付结果
     */
    @PostMapping("/{id}/deliver")
    public Result deliverInvoice(@PathVariable Long id) {
        return invoiceService.deliverInvoice(id);
    }

    /**
     * 删除发票
     * @param id 发票ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result deleteInvoice(@PathVariable Long id) {
        return invoiceService.deleteInvoice(id);
    }

    /**
     * 批量创建发票
     * @param requests 发票创建请求列表
     * @return 批量创建结果
     */
    @PostMapping("/batch")
    public Result batchCreateInvoice(@RequestBody List<InvoiceCreateRequest> requests) {
        return invoiceService.batchCreateInvoice(requests);
    }

    /**
     * 开票失败重试
     *
     * @param id 发票ID
     * @return 重试结果
     */
    @PostMapping("/{id}/retry")
    public Result retryInvoice(@PathVariable Long id) {
        return invoiceService.retryInvoice(id);
    }
}
