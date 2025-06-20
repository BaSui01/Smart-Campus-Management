package com.campus.infrastructure.sharding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 分表定时任务调度器
 * 负责自动创建未来的分表
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.shardingsphere.enabled", havingValue = "true", matchIfMissing = true)
public class ShardingTableScheduler {
    
    private final ShardingTableManager shardingTableManager;
    
    /**
     * 每月1号凌晨2点自动创建下个月的考勤表
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    public void createNextMonthAttendanceTable() {
        LocalDate nextMonth = LocalDate.now().plusMonths(1);
        log.info("开始创建下个月考勤分表: {}", nextMonth);
        
        try {
            shardingTableManager.createAttendanceTable(nextMonth);
            log.info("成功创建下个月考勤分表");
        } catch (Exception e) {
            log.error("创建下个月考勤分表失败", e);
        }
    }
    
    /**
     * 每年7月1号凌晨2点自动创建下一年的学生表
     */
    @Scheduled(cron = "0 0 2 1 7 ?")
    public void createNextYearStudentTable() {
        int nextYear = LocalDate.now().getYear() + 1;
        log.info("开始创建下一年学生分表: {}", nextYear);
        
        try {
            shardingTableManager.createStudentTable(nextYear);
            log.info("成功创建下一年学生分表");
        } catch (Exception e) {
            log.error("创建下一年学生分表失败", e);
        }
    }
    
    /**
     * 每年1月1号和7月1号凌晨2点创建新学期成绩表
     */
    @Scheduled(cron = "0 0 2 1 1,7 ?")
    public void createNewSemesterGradeTables() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        
        // 1月创建第2学期表，7月创建第1学期表
        String semester;
        if (month == 1) {
            // 创建当年第2学期表
            semester = year + "-2";
        } else {
            // 创建当年第1学期表
            semester = year + "-1";
        }
        
        log.info("开始创建新学期成绩分表: {}", semester);
        
        try {
            shardingTableManager.createGradeTable(semester);
            log.info("成功创建新学期成绩分表: {}", semester);
        } catch (Exception e) {
            log.error("创建新学期成绩分表失败: {}", semester, e);
        }
    }
    
    /**
     * 每天凌晨3点检查并创建当前月份的考勤表（防止遗漏）
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void ensureCurrentMonthAttendanceTable() {
        LocalDate today = LocalDate.now();
        
        try {
            shardingTableManager.createAttendanceTable(today);
            log.debug("确保当前月份考勤分表存在: {}", today.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMM")));
        } catch (Exception e) {
            log.error("确保当前月份考勤分表存在失败", e);
        }
    }
    
    /**
     * 每周日凌晨4点输出分表统计信息
     */
    @Scheduled(cron = "0 0 4 * * SUN")
    public void logShardingStatistics() {
        try {
            var statistics = shardingTableManager.getShardingStatistics();
            var tables = shardingTableManager.getAllShardingTables();
            
            log.info("=== 分表统计信息 ===");
            log.info("总分表数量: {}", statistics.get("total_sharding_tables"));
            log.info("总记录数: {}", statistics.get("total_rows"));
            log.info("总数据大小: {} bytes", statistics.get("total_data_size"));
            log.info("总索引大小: {} bytes", statistics.get("total_index_size"));
            
            log.info("=== 分表详情 ===");
            tables.forEach(table -> {
                log.info("表名: {}, 记录数: {}, 注释: {}", 
                        table.get("table_name"), 
                        table.get("table_rows"), 
                        table.get("table_comment"));
            });
            log.info("==================");
            
        } catch (Exception e) {
            log.error("输出分表统计信息失败", e);
        }
    }
    
    /**
     * 手动触发创建指定年份的学生表
     * 
     * @param year 年份
     */
    public void manualCreateStudentTable(int year) {
        log.info("手动创建学生分表: {}", year);
        try {
            shardingTableManager.createStudentTable(year);
            log.info("手动创建学生分表成功: {}", year);
        } catch (Exception e) {
            log.error("手动创建学生分表失败: {}", year, e);
            throw e;
        }
    }
    
    /**
     * 手动触发创建指定学期的成绩表
     * 
     * @param semester 学期 (格式: 2024-1)
     */
    public void manualCreateGradeTable(String semester) {
        log.info("手动创建成绩分表: {}", semester);
        try {
            shardingTableManager.createGradeTable(semester);
            log.info("手动创建成绩分表成功: {}", semester);
        } catch (Exception e) {
            log.error("手动创建成绩分表失败: {}", semester, e);
            throw e;
        }
    }
    
    /**
     * 手动触发创建指定月份的考勤表
     * 
     * @param date 日期
     */
    public void manualCreateAttendanceTable(LocalDate date) {
        log.info("手动创建考勤分表: {}", date);
        try {
            shardingTableManager.createAttendanceTable(date);
            log.info("手动创建考勤分表成功: {}", date);
        } catch (Exception e) {
            log.error("手动创建考勤分表失败: {}", date, e);
            throw e;
        }
    }
}
