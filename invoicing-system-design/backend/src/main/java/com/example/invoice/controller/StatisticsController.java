package com.example.invoice.controller;

import com.example.invoice.dto.response.Result;
import com.example.invoice.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 统计控制器，提供开票趋势、费用占比、红冲率等统计分析接口
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取月度开票趋势统计
     * @param months 统计月数（默认6个月）
     * @return 月度趋势数据结果
     */
    @GetMapping("/monthly-trend")
    public Result<Map<String, Object>> getMonthlyTrend(@RequestParam(defaultValue = "6") int months) {
        return statisticsService.getMonthlyTrend(months);
    }

    /**
     * 获取费用类型分布统计
     * @return 费用占比数据结果
     */
    @GetMapping("/fee-type-distribution")
    public Result<Map<String, Object>> getFeeTypeDistribution() {
        return statisticsService.getFeeTypeDistribution();
    }

    /**
     * 获取红冲率统计
     * @param months 统计月数（默认6个月）
     * @return 红冲率数据结果
     */
    @GetMapping("/red-cancel-rate")
    public Result<Map<String, Object>> getRedCancelRate(@RequestParam(defaultValue = "6") int months) {
        return statisticsService.getRedCancelRate(months);
    }

    /**
     * 获取仪表盘汇总统计数据
     * @return 仪表盘数据结果
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStats() {
        return statisticsService.getDashboardStats();
    }

    /**
     * 按客户维度统计开票汇总（TOP N）
     *
     * @param limit 返回前N名
     * @return 客户开票汇总列表
     */
    @GetMapping("/customer-summary")
    public Result<List<Map<String, Object>>> getCustomerSummary(
            @RequestParam(defaultValue = "10") int limit) {
        return statisticsService.getCustomerSummary(limit);
    }

    /**
     * 获取发票状态概览
     *
     * @return 各状态发票数量统计
     */
    @GetMapping("/status-overview")
    public Result<Map<String, Object>> getStatusOverview() {
        return statisticsService.getStatusOverview();
    }
}
