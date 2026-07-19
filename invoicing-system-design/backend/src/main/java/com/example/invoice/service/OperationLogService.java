package com.example.invoice.service;

import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.OperationLog;
import com.example.invoice.mapper.OperationLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志服务类，记录和查询用户操作审计日志
 */
@Service
public class OperationLogService {
    @Autowired
    private OperationLogMapper operationLogMapper;

    /**
     * 记录操作日志，保存用户操作的审计信息
     *
     * @param userId 操作用户ID
     * @param username 操作用户名
     * @param operationType 操作类型
     * @param module 操作模块
     * @param targetId 操作目标ID
     * @param targetNo 操作目标编号
     * @param content 操作内容描述
     * @param oldValue 变更前的值
     * @param newValue 变更后的值
     * @param clientIp 客户端IP地址
     * @param userAgent 客户端User-Agent
     */
    public void log(Long userId, String username, String operationType, String module,
                    Long targetId, String targetNo, String content,
                    String oldValue, String newValue, String clientIp, String userAgent) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperationType(operationType);
        log.setModule(module);
        log.setTargetId(targetId);
        log.setTargetNo(targetNo);
        log.setContent(content);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setClientIp(clientIp);
        log.setUserAgent(userAgent);
        operationLogMapper.insert(log);
    }

    /**
     * 查询操作日志列表，支持按模块、操作类型、用户和时间范围筛选，分页返回
     *
     * @param module 操作模块（可为null）
     * @param operationType 操作类型（可为null）
     * @param userId 操作用户ID（可为null）
     * @param startDate 开始日期（可为null）
     * @param endDate 结束日期（可为null）
     * @param page 页码
     * @param size 每页条数
     * @return 符合条件的操作日志列表
     */
    public Result<List<OperationLog>> getLogs(String module, String operationType,
                                                Long userId, String startDate, String endDate,
                                                int page, int size) {
        int offset = (page - 1) * size;
        List<OperationLog> logs = operationLogMapper.selectByCondition(
                module, operationType, userId, startDate, endDate, offset, size);
        int total = operationLogMapper.countByCondition(module, operationType, userId, startDate, endDate);
        return Result.success(logs);
    }
}
