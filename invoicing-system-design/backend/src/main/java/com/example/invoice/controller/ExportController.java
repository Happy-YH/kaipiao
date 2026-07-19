package com.example.invoice.controller;

import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.Invoice;
import com.example.invoice.entity.InvoiceDetail;
import com.example.invoice.mapper.InvoiceDetailMapper;
import com.example.invoice.mapper.InvoiceMapper;
import com.example.invoice.mapper.RepaymentRecordMapper;
import com.example.invoice.service.BatchInvoiceService;
import com.example.invoice.util.ExcelExportUtil;
import com.example.invoice.util.PdfExportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * 导出控制器，提供发票记录、还款记录的Excel/PDF导出以及批量开票Excel导入接口
 */
@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private InvoiceMapper invoiceMapper;
    @Autowired
    private InvoiceDetailMapper invoiceDetailMapper;
    @Autowired
    private RepaymentRecordMapper repaymentRecordMapper;
    @Autowired
    private BatchInvoiceService batchInvoiceService;

    /**
     * 导出发票列表为Excel
     * @param response HTTP响应
     * @param status 发票状态筛选
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    @GetMapping("/invoices/excel")
    public void exportInvoicesExcel(HttpServletResponse response,
                                     @RequestParam(required = false) String status,
                                     @RequestParam(required = false) String startDate,
                                     @RequestParam(required = false) String endDate) throws Exception {
        List<Invoice> invoices = invoiceMapper.selectByCondition(status, startDate, endDate, 0, 10000, null, null);
        String[] headers = {"发票编号", "发票类型", "发票种类", "客户名称", "价税合计", "不含税金额", "税额", "状态", "开票日期", "金蝶发票号", "备注"};
        String[] fields = {"invoiceNo", "invoiceType", "invoiceKind", "customerName", "totalAmount", "taxableAmount", "taxAmount", "status", "issueDate", "externalInvoiceNo", "remark"};

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Invoice inv : invoices) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("invoiceNo", inv.getInvoiceNo());
            row.put("invoiceType", getStatusText(inv.getInvoiceType()));
            row.put("invoiceKind", "BLUE".equals(inv.getInvoiceKind()) ? "蓝字" : "红字");
            row.put("customerName", inv.getCustomerId()); // 需要关联查询客户名
            row.put("totalAmount", inv.getTotalAmount());
            row.put("taxableAmount", inv.getTaxableAmount());
            row.put("taxAmount", inv.getTaxAmount());
            row.put("status", getStatusText(inv.getStatus()));
            row.put("issueDate", inv.getIssueDate() != null ? inv.getIssueDate().toString() : "");
            row.put("externalInvoiceNo", inv.getExternalInvoiceNo() != null ? inv.getExternalInvoiceNo() : "");
            row.put("remark", inv.getRemark() != null ? inv.getRemark() : "");
            dataList.add(row);
        }

        ExcelExportUtil.exportExcel(response, "发票记录", "发票列表", headers, fields, dataList);
    }

    /**
     * 导出发票列表为PDF
     * @param response HTTP响应
     * @param status 发票状态筛选
     * @param startDate 开始日期
     * @param endDate 结束日期
     */
    @GetMapping("/invoices/pdf")
    public void exportInvoicesPdf(HttpServletResponse response,
                                   @RequestParam(required = false) String status,
                                   @RequestParam(required = false) String startDate,
                                   @RequestParam(required = false) String endDate) throws Exception {
        List<Invoice> invoices = invoiceMapper.selectByCondition(status, startDate, endDate, 0, 10000, null, null);
        String[] headers = {"发票编号", "种类", "价税合计", "税额", "状态", "开票日期"};
        String[] fields = {"invoiceNo", "invoiceKind", "totalAmount", "taxAmount", "status", "issueDate"};

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (Invoice inv : invoices) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("invoiceNo", inv.getInvoiceNo());
            row.put("invoiceKind", "BLUE".equals(inv.getInvoiceKind()) ? "蓝字" : "红字");
            row.put("totalAmount", inv.getTotalAmount());
            row.put("taxAmount", inv.getTaxAmount());
            row.put("status", getStatusText(inv.getStatus()));
            row.put("issueDate", inv.getIssueDate() != null ? inv.getIssueDate().toString() : "");
            dataList.add(row);
        }

        PdfExportUtil.exportPdf(response, "发票记录", "发票列表", headers, fields, dataList);
    }

    /**
     * 导出发票详情为PDF
     * @param response HTTP响应
     * @param id 发票ID
     */
    @GetMapping("/invoice/{id}/pdf")
    public void exportInvoiceDetailPdf(HttpServletResponse response, @PathVariable Long id) throws Exception {
        Invoice invoice = invoiceMapper.selectById(id);
        if (invoice == null) {
            response.sendError(404, "发票不存在");
            return;
        }

        Map<String, Object> invoiceData = new LinkedHashMap<>();
        invoiceData.put("invoiceNo", invoice.getInvoiceNo());
        invoiceData.put("invoiceCode", invoice.getInvoiceCode() != null ? invoice.getInvoiceCode() : "");
        invoiceData.put("customerName", "");
        invoiceData.put("invoiceType", "SPECIAL".equals(invoice.getInvoiceType()) ? "增值税专用发票" : "增值税普通发票");
        invoiceData.put("issueDate", invoice.getIssueDate() != null ? invoice.getIssueDate().toString() : "");
        invoiceData.put("status", getStatusText(invoice.getStatus()));
        invoiceData.put("totalAmount", invoice.getTotalAmount() != null ? invoice.getTotalAmount().toString() : "0");
        invoiceData.put("taxableAmount", invoice.getTaxableAmount() != null ? invoice.getTaxableAmount().toString() : "0");
        invoiceData.put("taxAmount", invoice.getTaxAmount() != null ? invoice.getTaxAmount().toString() : "0");

        List<InvoiceDetail> details = invoiceDetailMapper.selectByInvoiceId(id);
        List<Map<String, Object>> detailList = new ArrayList<>();
        for (InvoiceDetail d : details) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("taxClassName", d.getTaxClassName() != null ? d.getTaxClassName() : "");
            row.put("feeType", d.getFeeType() != null ? d.getFeeType() : "");
            row.put("totalAmount", d.getTotalAmount() != null ? d.getTotalAmount().toString() : "0");
            row.put("taxRate", d.getTaxRate() != null ? d.getTaxRate().toString() : "0");
            row.put("taxAmount", d.getTaxAmount() != null ? d.getTaxAmount().toString() : "0");
            detailList.add(row);
        }

        PdfExportUtil.exportInvoicePdf(response, "发票_" + invoice.getInvoiceNo(), invoiceData, detailList);
    }

    /**
     * 导入批量开票Excel文件
     * @param file 上传的Excel文件
     * @return 解析后的数据预览
     */
    @PostMapping("/batch-invoice/import")
    public Result<Map<String, Object>> importBatchInvoice(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".xls"))) {
            return Result.error("仅支持xlsx和xls格式的Excel文件");
        }

        List<Map<String, String>> dataList = com.example.invoice.util.ExcelImportUtil.parseExcel(file, 0);

        Map<String, Object> result = new HashMap<>();
        result.put("fileName", fileName);
        result.put("totalCount", dataList.size());

        // 数据校验
        List<Map<String, Object>> validList = new ArrayList<>();
        List<Map<String, Object>> errorList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, String> row = dataList.get(i);
            List<String> missing = com.example.invoice.util.ExcelImportUtil.validateRequired(row, "合同编号", "客户名称", "费用类型", "金额");
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("rowNumber", row.getOrDefault("_rowNumber", String.valueOf(i + 2)));
            item.put("contractNo", row.getOrDefault("合同编号", ""));
            item.put("customerName", row.getOrDefault("客户名称", ""));
            item.put("feeType", row.getOrDefault("费用类型", ""));
            item.put("amount", row.getOrDefault("金额", ""));
            item.put("taxRate", row.getOrDefault("税率", "6"));
            item.put("remark", row.getOrDefault("备注", ""));

            if (missing.isEmpty()) {
                item.put("valid", true);
                validList.add(item);
            } else {
                item.put("valid", false);
                item.put("errors", "缺少必填字段：" + String.join("、", missing));
                errorList.add(item);
            }
        }

        result.put("validCount", validList.size());
        result.put("errorCount", errorList.size());
        result.put("validList", validList);
        result.put("errorList", errorList);

        return Result.success("文件解析成功", result);
    }

    /**
     * 下载批量开票导入模板
     * @param response HTTP响应
     */
    @GetMapping("/batch-invoice/template")
    public void downloadBatchTemplate(HttpServletResponse response) throws Exception {
        String[] headers = {"合同编号", "客户名称", "费用类型", "金额", "税率", "计息开始日期", "计息结束日期", "备注"};
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"HT20230001", "示例客户有限公司", "利息", "10000.00", "6", "2023-01-01", "2023-03-31", ""});

        ExcelExportUtil.exportSimpleExcel(response, "批量开票导入模板", "开票数据", headers, rows);
    }

    /**
     * 状态文本转换
     * @param status 状态编码
     * @return 状态中文名称
     */
    private String getStatusText(String status) {
        if (status == null) return "";
        switch (status) {
            case "DRAFT": return "草稿";
            case "PENDING_REVIEW": return "待审核";
            case "REVIEWED": return "已审核";
            case "ISSUING": return "开票中";
            case "ISSUED": return "已开具";
            case "DELIVERED": return "已交付";
            case "RED_CANCELLED": return "已红冲";
            case "FAILED": return "开票失败";
            case "DELETED": return "已删除";
            default: return status;
        }
    }
}
