package com.example.invoice.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * Excel导出工具类
 * 提供通用的Excel文件生成和下载功能
 */
public class ExcelExportUtil {

    /**
     * 导出Excel文件到HTTP响应流
     * @param response HTTP响应对象
     * @param fileName 下载文件名（不含扩展名）
     * @param sheetName Sheet名称
     * @param headers 表头列表
     * @param fieldNames 字段名列表（与headers一一对应）
     * @param dataList 数据列表
     */
    public static void exportExcel(HttpServletResponse response, String fileName,
                                    String sheetName, String[] headers,
                                    String[] fieldNames, List<Map<String, Object>> dataList) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        // 创建样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle amountStyle = createAmountStyle(workbook);

        Sheet sheet = workbook.createSheet(sheetName);

        // 创建表头行
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 18 * 256);
        }

        // 填充数据行
        for (int i = 0; i < dataList.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Map<String, Object> data = dataList.get(i);
            for (int j = 0; j < fieldNames.length; j++) {
                Cell cell = row.createCell(j);
                Object value = data.get(fieldNames[j]);
                setCellValue(cell, value);
                // 金额列使用等宽字体
                if (value instanceof Number) {
                    cell.setCellStyle(amountStyle);
                } else {
                    cell.setCellStyle(dataStyle);
                }
            }
        }

        // 写入响应流
        setResponseHeader(response, fileName + ".xlsx");
        OutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
        out.close();
        workbook.close();
    }

    /**
     * 导出简单Excel（直接传入二维数据）
     * @param response HTTP响应对象
     * @param fileName 文件名
     * @param sheetName Sheet名称
     * @param headers 表头数组
     * @param rows 数据行列表
     */
    public static void exportSimpleExcel(HttpServletResponse response, String fileName,
                                          String sheetName, String[] headers,
                                          List<String[]> rows) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        Sheet sheet = workbook.createSheet(sheetName);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(i, 18 * 256);
        }

        for (int i = 0; i < rows.size(); i++) {
            Row row = sheet.createRow(i + 1);
            String[] values = rows.get(i);
            for (int j = 0; j < values.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(values[j] != null ? values[j] : "");
                cell.setCellStyle(dataStyle);
            }
        }

        setResponseHeader(response, fileName + ".xlsx");
        OutputStream out = response.getOutputStream();
        workbook.write(out);
        out.flush();
        out.close();
        workbook.close();
    }

    /**
     * 创建表头样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("微软雅黑");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * 创建数据单元格样式
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("微软雅黑");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * 创建金额列样式（等宽字体，右对齐）
     */
    private static CellStyle createAmountStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Consolas");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /**
     * 设置单元格值（根据类型自动处理）
     */
    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 设置HTTP响应头，支持中文文件名
     */
    private static void setResponseHeader(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String encodedName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedName);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
    }
}
