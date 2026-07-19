package com.example.invoice.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * PDF导出工具类
 * 提供通用的PDF文件生成和下载功能，支持中文
 */
public class PdfExportUtil {

    /** 中文字体（使用iTextAsian内置字体） */
    private static final String CHINESE_FONT = "STSong-Light";
    private static final String CHINESE_ENCODING = "UniGB-UCS2-H";

    /**
     * 导出PDF表格数据到HTTP响应流
     * @param response HTTP响应对象
     * @param fileName 下载文件名（不含扩展名）
     * @param title 文档标题
     * @param headers 表头数组
     * @param fieldNames 字段名数组（与headers一一对应）
     * @param dataList 数据列表
     */
    public static void exportPdf(HttpServletResponse response, String fileName,
                                  String title, String[] headers,
                                  String[] fieldNames, List<Map<String, Object>> dataList) throws Exception {
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 30, 20);
        setResponseHeader(response, fileName + ".pdf");
        OutputStream out = response.getOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // 标题
        Font titleFont = getChineseFont(16, Font.BOLD);
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(15);
        document.add(titlePara);

        // 表格
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);

        // 表头
        Font headerFont = getChineseFont(10, Font.BOLD);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }

        // 数据行
        Font dataFont = getChineseFont(9, Font.NORMAL);
        for (Map<String, Object> data : dataList) {
            for (String field : fieldNames) {
                Object value = data.get(field);
                String text = value != null ? value.toString() : "";
                PdfPCell cell = new PdfPCell(new Phrase(text, dataFont));
                cell.setPadding(5);
                table.addCell(cell);
            }
        }

        document.add(table);
        document.close();
        out.flush();
    }

    /**
     * 导出发票详情PDF
     * @param response HTTP响应对象
     * @param invoiceData 发票数据
     */
    public static void exportInvoicePdf(HttpServletResponse response, String fileName,
                                         Map<String, Object> invoiceData,
                                         List<Map<String, Object>> details) throws Exception {
        Document document = new Document(PageSize.A4, 20, 20, 30, 20);
        setResponseHeader(response, fileName + ".pdf");
        OutputStream out = response.getOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = getChineseFont(18, Font.BOLD);
        Font labelFont = getChineseFont(11, Font.BOLD);
        Font valueFont = getChineseFont(11, Font.NORMAL);

        // 标题
        Paragraph title = new Paragraph("发票详情", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // 基本信息
        document.add(new Paragraph("基本信息", labelFont));
        document.add(new Paragraph(" ")); // 空行
        String[][] infoFields = {
            {"发票号码", getStringValue(invoiceData, "invoiceNo")},
            {"发票代码", getStringValue(invoiceData, "invoiceCode")},
            {"客户名称", getStringValue(invoiceData, "customerName")},
            {"发票类型", getStringValue(invoiceData, "invoiceType")},
            {"开票日期", getStringValue(invoiceData, "issueDate")},
            {"状态", getStringValue(invoiceData, "status")}
        };
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(60);
        for (String[] field : infoFields) {
            infoTable.addCell(createLabelCell(field[0], labelFont));
            infoTable.addCell(createValueCell(field[1], valueFont));
        }
        document.add(infoTable);
        document.add(new Paragraph(" "));

        // 金额信息
        document.add(new Paragraph("金额信息", labelFont));
        document.add(new Paragraph(" "));
        String[][] amountFields = {
            {"价税合计", getStringValue(invoiceData, "totalAmount")},
            {"不含税金额", getStringValue(invoiceData, "taxableAmount")},
            {"税额", getStringValue(invoiceData, "taxAmount")}
        };
        PdfPTable amountTable = new PdfPTable(2);
        amountTable.setWidthPercentage(60);
        for (String[] field : amountFields) {
            amountTable.addCell(createLabelCell(field[0], labelFont));
            amountTable.addCell(createValueCell(field[1], valueFont));
        }
        document.add(amountTable);
        document.add(new Paragraph(" "));

        // 明细表格
        if (details != null && !details.isEmpty()) {
            document.add(new Paragraph("开票明细", labelFont));
            document.add(new Paragraph(" "));
            Font detailHeaderFont = getChineseFont(10, Font.BOLD);
            Font detailDataFont = getChineseFont(9, Font.NORMAL);
            String[] detailHeaders = {"税目", "费用类型", "金额", "税率", "税额"};
            PdfPTable detailTable = new PdfPTable(detailHeaders.length);
            detailTable.setWidthPercentage(100);
            for (String h : detailHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(h, detailHeaderFont));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                detailTable.addCell(cell);
            }
            for (Map<String, Object> d : details) {
                addDetailCell(detailTable, getStringValue(d, "taxClassName"), detailDataFont);
                addDetailCell(detailTable, getStringValue(d, "feeType"), detailDataFont);
                addDetailCell(detailTable, getStringValue(d, "totalAmount"), detailDataFont);
                addDetailCell(detailTable, getStringValue(d, "taxRate") + "%", detailDataFont);
                addDetailCell(detailTable, getStringValue(d, "taxAmount"), detailDataFont);
            }
            document.add(detailTable);
        }

        document.close();
        out.flush();
    }

    /** 获取中文字体 */
    private static Font getChineseFont(int size, int style) throws Exception {
        BaseFont bfChinese = BaseFont.createFont(CHINESE_FONT, CHINESE_ENCODING, BaseFont.NOT_EMBEDDED);
        return new Font(bfChinese, size, style);
    }

    /** 创建标签单元格 */
    private static PdfPCell createLabelCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new BaseColor(240, 240, 240));
        cell.setPadding(5);
        return cell;
    }

    /** 创建值单元格 */
    private static PdfPCell createValueCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        return cell;
    }

    /** 添加明细单元格 */
    private static void addDetailCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }

    /** 安全获取Map中的字符串值 */
    private static String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }

    /** 设置HTTP响应头 */
    private static void setResponseHeader(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/pdf");
        response.setCharacterEncoding("UTF-8");
        String encodedName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedName);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
    }
}
