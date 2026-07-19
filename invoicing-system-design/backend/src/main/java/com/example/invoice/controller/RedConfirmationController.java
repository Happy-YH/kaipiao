package com.example.invoice.controller;

import com.example.invoice.dto.request.RedCancelRequest;
import com.example.invoice.dto.response.Result;
import com.example.invoice.service.RedConfirmationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 红冲确认控制器，提供红字确认单的管理和确认操作接口
 */
@RestController
@RequestMapping("/api/red-confirmations")
public class RedConfirmationController {
    @Autowired
    private RedConfirmationService redConfirmationService;

    /**
     * 查询红字确认单列表，支持按发票ID、状态筛选
     * @param invoiceId 关联发票ID（可选）
     * @param status 确认单状态（可选）
     * @return 确认单列表结果
     */
    @GetMapping
    public Result getAllConfirmations(@RequestParam(required = false) Long invoiceId,
                                      @RequestParam(required = false) String status) {
        if (invoiceId != null) {
            return redConfirmationService.getConfirmationsByInvoice(invoiceId);
        }
        if (status != null && !status.isEmpty()) {
            return redConfirmationService.getConfirmationsByStatus(status);
        }
        return redConfirmationService.getAllConfirmations();
    }

    /**
     * 根据ID查询红字确认单
     * @param id 确认单ID
     * @return 确认单详情结果
     */
    @GetMapping("/{id}")
    public Result getConfirmationById(@PathVariable Long id) {
        return redConfirmationService.getConfirmationById(id);
    }

    /**
     * 根据确认单编号查询红字确认单
     * @param confirmationNo 确认单编号
     * @return 确认单详情结果
     */
    @GetMapping("/no/{confirmationNo}")
    public Result getConfirmationByNo(@PathVariable String confirmationNo) {
        return redConfirmationService.getConfirmationByNo(confirmationNo);
    }

    /**
     * 创建红字确认单
     * @param request 红冲请求体
     * @return 创建结果
     */
    @PostMapping
    public Result createRedConfirmation(@RequestBody RedCancelRequest request) {
        return redConfirmationService.createRedConfirmation(request);
    }

    /**
     * 确认红冲操作
     * @param id 确认单ID
     * @return 确认结果
     */
    @PostMapping("/{id}/confirm")
    public Result confirmRed(@PathVariable Long id) {
        return redConfirmationService.confirmRed(id);
    }

    /**
     * 取消红冲操作
     * @param id 确认单ID
     * @return 取消结果
     */
    @PostMapping("/{id}/cancel")
    public Result cancelRed(@PathVariable Long id) {
        return redConfirmationService.cancelRed(id);
    }

    /**
     * 拒绝红字确认单
     *
     * @param id 确认单ID
     * @param rejectReason 拒绝原因
     * @return 拒绝结果
     */
    @PostMapping("/{id}/reject")
    public Result rejectRed(@PathVariable Long id,
                            @RequestParam(required = false) String rejectReason) {
        return redConfirmationService.rejectRed(id, rejectReason);
    }

    /**
     * 手动触发超时确认单处理（用于测试/管理）
     *
     * @return 处理的记录数
     */
    @PostMapping("/process-expired")
    public Result<Integer> processExpired() {
        int count = redConfirmationService.processExpiredConfirmations();
        return Result.success("处理完成", count);
    }
}
