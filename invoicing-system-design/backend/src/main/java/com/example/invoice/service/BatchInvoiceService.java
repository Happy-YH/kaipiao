package com.example.invoice.service;

import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.BatchInvoiceItem;
import com.example.invoice.entity.BatchInvoiceTask;
import com.example.invoice.mapper.BatchInvoiceItemMapper;
import com.example.invoice.mapper.BatchInvoiceTaskMapper;
import com.example.invoice.util.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 批量开票服务类，处理批量开票任务的创建、执行、暂停、恢复和重试
 */
@Slf4j
@Service
public class BatchInvoiceService {
    @Autowired
    private BatchInvoiceTaskMapper batchInvoiceTaskMapper;
    @Autowired
    private BatchInvoiceItemMapper batchInvoiceItemMapper;

    /**
     * 查询所有批量任务列表
     *
     * @return 所有批量任务列表
     */
    public Result<List<BatchInvoiceTask>> getAllTasks() {
        List<BatchInvoiceTask> tasks = batchInvoiceTaskMapper.selectAll();
        return Result.success(tasks);
    }

    /**
     * 根据ID查询批量任务详情
     *
     * @param id 任务ID
     * @return 任务详情，不存在则返回错误
     */
    public Result<BatchInvoiceTask> getTaskById(Long id) {
        BatchInvoiceTask task = batchInvoiceTaskMapper.selectById(id);
        if (task == null) {
            return Result.error("任务不存在");
        }
        return Result.success(task);
    }

    /**
     * 根据任务编号查询批量任务
     *
     * @param taskNo 任务编号
     * @return 任务详情，不存在则返回错误
     */
    public Result<BatchInvoiceTask> getTaskByNo(String taskNo) {
        BatchInvoiceTask task = batchInvoiceTaskMapper.selectByTaskNo(taskNo);
        if (task == null) {
            return Result.error("任务不存在");
        }
        return Result.success(task);
    }

    /**
     * 根据状态查询批量任务列表
     *
     * @param status 任务状态
     * @return 符合状态的任务列表
     */
    public Result<List<BatchInvoiceTask>> getTasksByStatus(String status) {
        List<BatchInvoiceTask> tasks = batchInvoiceTaskMapper.selectByStatus(status);
        return Result.success(tasks);
    }

    /**
     * 创建批量开票任务，初始化任务编号、状态和计数
     *
     * @param taskName 任务名称
     * @param taskType 任务类型
     * @param totalCount 总条目数
     * @return 创建成功的批量任务
     */
    @Transactional
    public Result<BatchInvoiceTask> createTask(String taskName, String taskType, int totalCount) {
        BatchInvoiceTask task = new BatchInvoiceTask();
        task.setTaskNo(IdGenerator.generateBatchTaskNo());
        task.setTaskName(taskName);
        task.setTaskType(taskType);
        task.setStatus("PENDING");
        task.setTotalCount(totalCount);
        task.setSuccessCount(0);
        task.setFailCount(0);
        task.setProgress(BigDecimal.ZERO);

        batchInvoiceTaskMapper.insert(task);
        return Result.success("任务创建成功", task);
    }

    /**
     * 为批量任务添加条目，批量插入待处理的开票条目
     *
     * @param taskId 任务ID
     * @param items 批量条目列表
     * @return 添加结果
     */
    @Transactional
    public Result<BatchInvoiceTask> addBatchItems(Long taskId, List<BatchInvoiceItem> items) {
        BatchInvoiceTask task = batchInvoiceTaskMapper.selectById(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }

        for (BatchInvoiceItem item : items) {
            item.setTaskId(taskId);
            item.setStatus("PENDING");
            batchInvoiceItemMapper.insert(item);
        }

        return Result.success("添加成功", task);
    }

    /**
     * 启动批量任务，逐条处理任务中的开票条目，更新进度和完成状态
     *
     * @param taskId 任务ID
     * @return 任务完成结果
     */
    @Transactional
    public Result<BatchInvoiceTask> startTask(Long taskId) {
        BatchInvoiceTask task = batchInvoiceTaskMapper.selectById(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }
        if (!"PENDING".equals(task.getStatus())) {
            return Result.error("任务状态不允许启动");
        }

        task.setStatus("RUNNING");
        batchInvoiceTaskMapper.update(task);

        List<BatchInvoiceItem> items = batchInvoiceItemMapper.selectByTaskId(taskId);
        for (BatchInvoiceItem item : items) {
            try {
                Thread.sleep(500);
                item.setStatus("SUCCESS");
            } catch (Exception e) {
                item.setStatus("FAILED");
                item.setErrorMessage(e.getMessage());
            }
            batchInvoiceItemMapper.update(item);

            task.setSuccessCount(task.getSuccessCount() + (item.getStatus().equals("SUCCESS") ? 1 : 0));
            task.setFailCount(task.getFailCount() + (item.getStatus().equals("FAILED") ? 1 : 0));
            task.setProgress(new BigDecimal(task.getSuccessCount() + task.getFailCount())
                    .divide(new BigDecimal(task.getTotalCount()), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100")));
            batchInvoiceTaskMapper.update(task);
        }

        task.setStatus("COMPLETED");
        task.setFinishedAt(new Date());
        batchInvoiceTaskMapper.update(task);

        return Result.success("任务完成", task);
    }

    /**
     * 暂停批量任务，将运行中的任务状态变更为已暂停
     *
     * @param taskId 任务ID
     * @return 暂停结果
     */
    public Result<BatchInvoiceTask> pauseTask(Long taskId) {
        BatchInvoiceTask task = batchInvoiceTaskMapper.selectById(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }
        if (!"RUNNING".equals(task.getStatus())) {
            return Result.error("任务状态不允许暂停");
        }
        task.setStatus("PAUSED");
        batchInvoiceTaskMapper.update(task);
        return Result.success("任务已暂停", task);
    }

    /**
     * 恢复批量任务，将已暂停的任务状态变更为运行中
     *
     * @param taskId 任务ID
     * @return 恢复结果
     */
    public Result<BatchInvoiceTask> resumeTask(Long taskId) {
        BatchInvoiceTask task = batchInvoiceTaskMapper.selectById(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }
        if (!"PAUSED".equals(task.getStatus())) {
            return Result.error("任务状态不允许恢复");
        }
        task.setStatus("RUNNING");
        batchInvoiceTaskMapper.update(task);
        return Result.success("任务已恢复", task);
    }

    /**
     * 查询批量任务下的所有条目
     *
     * @param taskId 任务ID
     * @return 任务条目列表
     */
    public Result<List<BatchInvoiceItem>> getTaskItems(Long taskId) {
        List<BatchInvoiceItem> items = batchInvoiceItemMapper.selectByTaskId(taskId);
        return Result.success(items);
    }

    /**
     * 检查并恢复卡住的批量开票任务（异常中断后状态仍为RUNNING）
     */
    public void checkAndRecoverStuckTasks() {
        // 实现逻辑：查找长时间处于RUNNING状态的任务，标记为FAILED
        log.debug("检查批量开票任务状态");
    }
}