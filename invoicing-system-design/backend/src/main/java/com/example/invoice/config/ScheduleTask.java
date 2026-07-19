package com.example.invoice.config;

import com.example.invoice.service.BatchInvoiceService;
import com.example.invoice.service.RedConfirmationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务配置类，处理红冲超时、批量任务进度等周期性任务
 */
@Slf4j
@Configuration
@EnableScheduling
public class ScheduleTask {

    @Autowired
    private RedConfirmationService redConfirmationService;

    @Autowired
    private BatchInvoiceService batchInvoiceService;

    /**
     * 每隔5分钟检查一次超时的红字确认单
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void processExpiredRedConfirmations() {
        try {
            int count = redConfirmationService.processExpiredConfirmations();
            if (count > 0) {
                log.info("[定时任务] 红字确认单超时处理完成，共处理 {} 条", count);
            }
        } catch (Exception e) {
            log.error("[定时任务] 红字确认单超时处理异常", e);
        }
    }

    /**
     * 每隔1分钟检查批量开票任务状态（防止异常中断）
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void checkBatchInvoiceTasks() {
        try {
            batchInvoiceService.checkAndRecoverStuckTasks();
        } catch (Exception e) {
            log.error("[定时任务] 批量开票任务检查异常", e);
        }
    }
}
