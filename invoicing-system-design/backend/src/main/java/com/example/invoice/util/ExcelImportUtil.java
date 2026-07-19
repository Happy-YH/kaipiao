package com.example.invoice.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Excel导入解析工具类
 * 支持xls和xlsx格式，提供通用的Excel文件读取和数据提取功能
 */
public class ExcelImportUtil {

    /** 日期格式化模板 */
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);
    /** 数字格式化（避免科学计数法） */
    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#.##");

    /**
     * 解析上传的Excel文件，返回每行数据的Map列表
     * @param file 上传的Excel文件
     * @param headerRow 表头所在行号（从0开始）
     * @return List<Map<String, String>> 每行数据，key为列标题，value为单元格值
     */
    public static List<Map<String, String>> parseExcel(MultipartFile file, int headerRow) throws IOException {
        Workbook workbook = createWorkbook(file.getInputStream(), file.getOriginalFilename());
        Sheet sheet = workbook.getSheetAt(0);
        return parseSheet(sheet, headerRow);
    }

    /**
     * 解析指定Sheet的Excel文件
     * @param file 上传的Excel文件
     * @param sheetIndex Sheet索引（从0开始）
     * @param headerRow 表头所在行号
     * @return 解析结果列表
     */
    public static List<Map<String, String>> parseExcel(MultipartFile file, int sheetIndex, int headerRow) throws IOException {
        Workbook workbook = createWorkbook(file.getInputStream(), file.getOriginalFilename());
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        return parseSheet(sheet, headerRow);
    }

    /**
     * 根据文件名创建对应的Workbook对象（兼容xls和xlsx）
     */
    private static Workbook createWorkbook(InputStream is, String fileName) throws IOException {
        if (fileName != null && fileName.toLowerCase().endsWith(".xlsx")) {
            return new XSSFWorkbook(is);
        } else {
            return new HSSFWorkbook(is);
        }
    }

    /**
     * 解析Sheet页数据
     * @param sheet Excel Sheet对象
     * @param headerRow 表头行号
     * @return 数据列表
     */
    private static List<Map<String, String>> parseSheet(Sheet sheet, int headerRow) {
        List<Map<String, String>> dataList = new ArrayList<>();
        Row header = sheet.getRow(headerRow);
        if (header == null) {
            return dataList;
        }

        // 读取表头
        List<String> headers = new ArrayList<>();
        for (Cell cell : header) {
            headers.add(getCellValueAsString(cell));
        }

        // 逐行读取数据
        for (int i = headerRow + 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            Map<String, String> rowData = new LinkedHashMap<>();
            boolean hasData = false;
            for (int j = 0; j < headers.size(); j++) {
                Cell cell = row.getCell(j);
                String value = cell != null ? getCellValueAsString(cell) : "";
                rowData.put(headers.get(j), value);
                if (!value.trim().isEmpty()) {
                    hasData = true;
                }
            }
            if (hasData) {
                rowData.put("_rowNumber", String.valueOf(i + 1)); // 记录原始行号
                dataList.add(rowData);
            }
        }
        return dataList;
    }

    /**
     * 获取单元格的字符串值，自动处理不同类型的单元格
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    return DATE_FORMATTER.format(date);
                } else {
                    return NUMBER_FORMAT.format(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return NUMBER_FORMAT.format(cell.getNumericCellValue());
                } catch (Exception e) {
                    return cell.getStringCellValue();
                }
            case BLANK:
                return "";
            default:
                return "";
        }
    }

    /**
     * 校验必填字段
     * @param rowData 行数据
     * @param requiredFields 必填字段名数组
     * @return 缺失字段列表，为空表示校验通过
     */
    public static List<String> validateRequired(Map<String, String> rowData, String... requiredFields) {
        List<String> missing = new ArrayList<>();
        for (String field : requiredFields) {
            String value = rowData.get(field);
            if (value == null || value.trim().isEmpty()) {
                missing.add(field);
            }
        }
        return missing;
    }
}
