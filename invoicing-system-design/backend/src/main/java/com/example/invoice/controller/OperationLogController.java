package com.example.invoice.controller;

import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.OperationLog;
import com.example.invoice.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志控制器，提供操作日志的查询接口
 */
@RestController
@RequestMapping("/api/operation-logs")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 分页查询操作日志
     *
     * @param module 模块筛选
     * @param operationType 操作类型筛选
     * @param userId 操作人ID筛选
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param page 页码
     * @param size 每页大小
     * @return 日志列表
     */
    @GetMapping
    public Result<List<OperationLog>> getLogs(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return operationLogService.getLogs(module, operationType, userId, startDate, endDate, page, size);
    }
}
