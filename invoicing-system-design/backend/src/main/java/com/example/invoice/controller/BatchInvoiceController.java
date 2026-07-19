package com.example.invoice.controller;

import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.BatchInvoiceItem;
import com.example.invoice.service.BatchInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 批量开票控制器，提供批量开票任务的管理和执行接口
 */
@RestController
@RequestMapping("/api/batch")
public class BatchInvoiceController {
    @Autowired
    private BatchInvoiceService batchInvoiceService;

    /**
     * 查询批量开票任务列表，支持按状态筛选
     * @param status 任务状态（可选）
     * @return 任务列表结果
     */
    @GetMapping("/tasks")
    public Result getAllTasks(@RequestParam(required = false) String status) {
        if (status != null && !status.isEmpty()) {
            return batchInvoiceService.getTasksByStatus(status);
        }
        return batchInvoiceService.getAllTasks();
    }

    /**
     * 根据ID查询批量开票任务
     * @param id 任务ID
     * @return 任务详情结果
     */
    @GetMapping("/tasks/{id}")
    public Result getTaskById(@PathVariable Long id) {
        return batchInvoiceService.getTaskById(id);
    }

    /**
     * 查询批量开票任务的明细项列表
     * @param id 任务ID
     * @return 明细项列表结果
     */
    @GetMapping("/tasks/{id}/items")
    public Result getTaskItems(@PathVariable Long id) {
        return batchInvoiceService.getTaskItems(id);
    }

    /**
     * 创建批量开票任务
     * @param taskName 任务名称
     * @param taskType 任务类型
     * @param totalCount 总数量
     * @return 创建结果
     */
    @PostMapping("/tasks")
    public Result createTask(@RequestParam String taskName,
                             @RequestParam String taskType,
                             @RequestParam int totalCount) {
        return batchInvoiceService.createTask(taskName, taskType, totalCount);
    }

    /**
     * 向批量开票任务添加明细项
     * @param id 任务ID
     * @param items 明细项列表
     * @return 添加结果
     */
    @PostMapping("/tasks/{id}/items")
    public Result addBatchItems(@PathVariable Long id, @RequestBody List<BatchInvoiceItem> items) {
        return batchInvoiceService.addBatchItems(id, items);
    }

    /**
     * 启动批量开票任务
     * @param id 任务ID
     * @return 启动结果
     */
    @PostMapping("/tasks/{id}/start")
    public Result startTask(@PathVariable Long id) {
        return batchInvoiceService.startTask(id);
    }

    /**
     * 暂停批量开票任务
     * @param id 任务ID
     * @return 暂停结果
     */
    @PostMapping("/tasks/{id}/pause")
    public Result pauseTask(@PathVariable Long id) {
        return batchInvoiceService.pauseTask(id);
    }

    /**
     * 恢复批量开票任务
     * @param id 任务ID
     * @return 恢复结果
     */
    @PostMapping("/tasks/{id}/resume")
    public Result resumeTask(@PathVariable Long id) {
        return batchInvoiceService.resumeTask(id);
    }
}
