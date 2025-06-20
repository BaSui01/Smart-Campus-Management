package com.campus.shared.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Excel工具类
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
public class ExcelUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 读取Excel文件
     * 
     * @param file Excel文件
     * @return 数据列表
     */
    public static List<Map<String, Object>> readExcel(MultipartFile file) throws IOException {
        List<Map<String, Object>> dataList = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = createWorkbook(file.getOriginalFilename(), inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            
            // 获取表头
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel文件表头不能为空");
            }
            
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValueAsString(cell));
            }
            
            // 读取数据行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                
                Map<String, Object> rowData = new HashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j);
                    String header = headers.get(j);
                    Object value = getCellValue(cell);
                    rowData.put(header, value);
                }
                
                // 跳过空行
                if (!isEmptyRow(rowData)) {
                    dataList.add(rowData);
                }
            }
            
            workbook.close();
        }
        
        return dataList;
    }

    /**
     * 创建Excel文件
     * 
     * @param headers 表头
     * @param dataList 数据列表
     * @return Excel文件字节数组
     */
    public static byte[] createExcel(List<String> headers, List<Map<String, Object>> dataList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("数据");
            
            // 创建表头样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }
            
            // 创建数据行
            for (int i = 0; i < dataList.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Map<String, Object> rowData = dataList.get(i);
                
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.createCell(j);
                    String header = headers.get(j);
                    Object value = rowData.get(header);
                    setCellValue(cell, value);
                }
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 创建Excel模板
     * 
     * @param headers 表头列表
     * @param sampleData 示例数据
     * @return Excel模板字节数组
     */
    public static byte[] createTemplate(List<String> headers, Map<String, Object> sampleData) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("模板");
            
            // 创建样式
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle sampleStyle = createSampleStyle(workbook);
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }
            
            // 创建示例数据行
            if (sampleData != null && !sampleData.isEmpty()) {
                Row sampleRow = sheet.createRow(1);
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = sampleRow.createCell(i);
                    String header = headers.get(i);
                    Object value = sampleData.get(header);
                    setCellValue(cell, value);
                    cell.setCellStyle(sampleStyle);
                }
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 验证Excel文件格式
     * 
     * @param file Excel文件
     * @param requiredHeaders 必需的表头
     * @return 验证结果
     */
    public static Map<String, Object> validateExcelFormat(MultipartFile file, List<String> requiredHeaders) {
        Map<String, Object> result = new HashMap<>();
        result.put("valid", false);
        
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = createWorkbook(file.getOriginalFilename(), inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            
            // 检查是否有数据
            if (sheet.getLastRowNum() < 0) {
                result.put("message", "Excel文件为空");
                return result;
            }
            
            // 获取表头
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                result.put("message", "Excel文件缺少表头");
                return result;
            }
            
            List<String> actualHeaders = new ArrayList<>();
            for (Cell cell : headerRow) {
                actualHeaders.add(getCellValueAsString(cell));
            }
            
            // 检查必需的表头
            List<String> missingHeaders = new ArrayList<>();
            for (String requiredHeader : requiredHeaders) {
                if (!actualHeaders.contains(requiredHeader)) {
                    missingHeaders.add(requiredHeader);
                }
            }
            
            if (!missingHeaders.isEmpty()) {
                result.put("message", "缺少必需的表头：" + String.join(", ", missingHeaders));
                result.put("missingHeaders", missingHeaders);
                return result;
            }
            
            // 检查数据行数
            int dataRows = sheet.getLastRowNum();
            result.put("totalRows", dataRows);
            result.put("dataRows", dataRows - 1); // 减去表头行
            
            result.put("valid", true);
            result.put("message", "Excel文件格式验证通过");
            result.put("headers", actualHeaders);
            
            workbook.close();
            
        } catch (Exception e) {
            log.error("验证Excel文件格式失败", e);
            result.put("message", "Excel文件格式验证失败：" + e.getMessage());
        }
        
        return result;
    }

    /**
     * 创建Workbook对象
     */
    private static Workbook createWorkbook(String fileName, InputStream inputStream) throws IOException {
        if (fileName.endsWith(".xlsx")) {
            return new XSSFWorkbook(inputStream);
        } else if (fileName.endsWith(".xls")) {
            return new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("不支持的文件格式，请使用.xls或.xlsx文件");
        }
    }

    /**
     * 获取单元格值
     */
    private static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    // 如果是整数，返回Long类型
                    if (numericValue == Math.floor(numericValue)) {
                        return (long) numericValue;
                    } else {
                        return numericValue;
                    }
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * 获取单元格值作为字符串
     */
    private static String getCellValueAsString(Cell cell) {
        Object value = getCellValue(cell);
        return value != null ? value.toString() : "";
    }

    /**
     * 设置单元格值
     */
    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof LocalDate) {
            cell.setCellValue(((LocalDate) value).format(DATE_FORMATTER));
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue(((LocalDateTime) value).format(DATETIME_FORMATTER));
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 创建表头样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        
        return style;
    }

    /**
     * 创建示例数据样式
     */
    private static CellStyle createSampleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        Font font = workbook.createFont();
        font.setItalic(true);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFont(font);
        
        return style;
    }

    /**
     * 检查是否为空行
     */
    private static boolean isEmptyRow(Map<String, Object> rowData) {
        return rowData.values().stream()
                .allMatch(value -> value == null || value.toString().trim().isEmpty());
    }
}
