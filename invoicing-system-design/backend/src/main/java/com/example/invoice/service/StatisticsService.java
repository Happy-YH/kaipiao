package com.example.invoice.service;

import com.example.invoice.dto.response.Result;
import com.example.invoice.mapper.InvoiceMapper;
import com.example.invoice.mapper.InvoiceDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 统计服务类，提供开票量趋势、费用类型占比、红冲率等统计分析
 */
@Service
public class StatisticsService {
    @Autowired
    private InvoiceMapper invoiceMapper;
    @Autowired
    private InvoiceDetailMapper invoiceDetailMapper;

    /**
     * 获取月度开票趋势统计，包含蓝票和红票的数量及金额
     *
     * @param months 统计的月份数
     * @return 月度趋势数据，包含每月蓝票/红票数量和金额
     */
    public Result<Map<String, Object>> getMonthlyTrend(int months) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = months - 1; i >= 0; i--) {
            LocalDate target = now.minusMonths(i);
            String monthKey = target.format(fmt);
            Map<String, Object> item = new HashMap<>();
            item.put("month", monthKey);
            item.put("blueCount", invoiceMapper.countByMonthAndKind(monthKey, "BLUE"));
            item.put("redCount", invoiceMapper.countByMonthAndKind(monthKey, "RED"));
            item.put("blueAmount", invoiceMapper.sumAmountByMonthAndKind(monthKey, "BLUE"));
            item.put("redAmount", invoiceMapper.sumAmountByMonthAndKind(monthKey, "RED"));
            trend.add(item);
        }
        result.put("trend", trend);
        return Result.success(result);
    }

    /**
     * 获取费用类型占比统计
     *
     * @return 各费用类型的数量分布
     */
    public Result<Map<String, Object>> getFeeTypeDistribution() {
        Map<String, Object> result = new HashMap<>();
        result.put("distribution", invoiceDetailMapper.countByFeeType());
        return Result.success(result);
    }

    /**
     * 获取红冲率统计，按月计算红票数占蓝票数的比例
     *
     * @param months 统计的月份数
     * @return 各月红冲率数据，包含蓝票总数、红票数和红冲率百分比
     */
    public Result<Map<String, Object>> getRedCancelRate(int months) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> rates = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = months - 1; i >= 0; i--) {
            LocalDate target = now.minusMonths(i);
            String monthKey = target.format(fmt);
            int total = invoiceMapper.countByMonthAndKind(monthKey, "BLUE");
            int red = invoiceMapper.countByMonthAndKind(monthKey, "RED");
            Map<String, Object> item = new HashMap<>();
            item.put("month", monthKey);
            item.put("total", total);
            item.put("redCount", red);
            item.put("rate", total > 0 ? (double) red / total * 100 : 0);
            rates.add(item);
        }
        result.put("rates", rates);
        return Result.success(result);
    }

    /**
     * 获取仪表盘统计数据，包含发票总数、蓝票/红票金额、各状态发票数量
     *
     * @return 仪表盘统计概览数据
     */
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalInvoices", invoiceMapper.countAll());
        result.put("totalBlueAmount", invoiceMapper.sumAmountByKind("BLUE"));
        result.put("totalRedAmount", invoiceMapper.sumAmountByKind("RED"));
        result.put("pendingReview", invoiceMapper.countByStatus("PENDING_REVIEW"));
        result.put("issued", invoiceMapper.countByStatus("ISSUED"));
        result.put("redCancelled", invoiceMapper.countByStatus("RED_CANCELLED"));
        result.put("failed", invoiceMapper.countByStatus("FAILED"));
        return Result.success(result);
    }

    /**
     * 按客户维度统计开票汇总
     *
     * @param limit 返回前N名客户
     * @return 客户开票汇总列表
     */
    public Result<List<Map<String, Object>>> getCustomerSummary(int limit) {
        List<Map<String, Object>> list = invoiceMapper.selectCustomerSummary(limit);
        return Result.success(list);
    }

    /**
     * 获取发票数量概览（按状态分组）
     *
     * @return 各状态发票数量
     */
    public Result<Map<String, Object>> getStatusOverview() {
        Map<String, Object> result = new HashMap<>();
        result.put("draft", invoiceMapper.countByStatus("DRAFT"));
        result.put("pendingReview", invoiceMapper.countByStatus("PENDING_REVIEW"));
        result.put("reviewed", invoiceMapper.countByStatus("REVIEWED"));
        result.put("issuing", invoiceMapper.countByStatus("ISSUING"));
        result.put("issued", invoiceMapper.countByStatus("ISSUED"));
        result.put("delivered", invoiceMapper.countByStatus("DELIVERED"));
        result.put("redCancelled", invoiceMapper.countByStatus("RED_CANCELLED"));
        result.put("failed", invoiceMapper.countByStatus("FAILED"));
        return Result.success(result);
    }
}
