package com.campus.infrastructure.sharding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 考勤表分片算法
 * 根据考勤日期按年月进行分表
 *
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
@ConditionalOnClass(name = "org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingAlgorithm")
public class AttendanceTableShardingAlgorithm {
    
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    
    // ShardingSphere相关方法已移除，仅保留工具方法
    // 如需使用分片功能，请确保ShardingSphere依赖可用
    
    /**
     * 根据日期字符串获取目标表名
     * 
     * @param dateString 日期字符串 (格式: yyyy-MM-dd)
     * @return 目标表名
     */
    public static String getTargetTableByDateString(String dateString) {
        try {
            LocalDate date = LocalDate.parse(dateString);
            String yearMonth = date.format(YEAR_MONTH_FORMATTER);
            return "tb_attendance_" + yearMonth;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("无效的日期格式: " + dateString + ", 期望格式: yyyy-MM-dd", e);
        }
    }
    
    /**
     * 根据年月获取目标表名
     * 
     * @param year 年份
     * @param month 月份 (1-12)
     * @return 目标表名
     */
    public static String getTargetTableByYearMonth(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("无效的月份: " + month + ", 月份范围: 1-12");
        }
        
        String yearMonth = String.format("%04d%02d", year, month);
        return "tb_attendance_" + yearMonth;
    }
    
    /**
     * 获取当前月份的目标表名
     * 
     * @return 当前月份的目标表名
     */
    public static String getCurrentMonthTargetTable() {
        LocalDate now = LocalDate.now();
        String yearMonth = now.format(YEAR_MONTH_FORMATTER);
        return "tb_attendance_" + yearMonth;
    }
    
    /**
     * 获取下个月的目标表名
     * 
     * @return 下个月的目标表名
     */
    public static String getNextMonthTargetTable() {
        LocalDate nextMonth = LocalDate.now().plusMonths(1);
        String yearMonth = nextMonth.format(YEAR_MONTH_FORMATTER);
        return "tb_attendance_" + yearMonth;
    }
    
    /**
     * 解析表名获取年月
     * 
     * @param tableName 表名 (如: tb_attendance_202406)
     * @return 年月字符串 (如: 202406)
     */
    public static String parseYearMonthFromTableName(String tableName) {
        if (tableName == null || !tableName.startsWith("tb_attendance_")) {
            throw new IllegalArgumentException("无效的考勤表名: " + tableName);
        }
        
        String yearMonth = tableName.substring("tb_attendance_".length());
        if (!yearMonth.matches("\\d{6}")) {
            throw new IllegalArgumentException("无效的考勤表名格式: " + tableName);
        }
        
        return yearMonth;
    }
    
    /**
     * 验证表名是否为有效的考勤分表名
     * 
     * @param tableName 表名
     * @return 是否有效
     */
    public static boolean isValidAttendanceTableName(String tableName) {
        try {
            parseYearMonthFromTableName(tableName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
