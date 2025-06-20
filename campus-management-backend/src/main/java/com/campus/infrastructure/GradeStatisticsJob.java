package com.campus.infrastructure;

import com.campus.application.service.academic.GradeService;
import com.campus.application.service.communication.EmailService;
import com.campus.shared.util.DistributedLock;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 成绩统计分析定时任务
 * 
 * @author Campus Team
 * @since 2025-06-05
 */
@Component
public class GradeStatisticsJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(GradeStatisticsJob.class);
    
    private static final String LOCK_KEY = "grade_statistics_job";

    @Autowired
    private GradeService gradeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private DistributedLock distributedLock;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String lockValue = distributedLock.tryLock(LOCK_KEY, 600); // 10分钟锁定时间
        
        if (lockValue == null) {
            logger.warn("成绩统计分析任务正在执行中，跳过本次执行");
            return;
        }

        try {
            logger.info("开始执行成绩统计分析任务");
            long startTime = System.currentTimeMillis();
            
            // 生成统计报告
            Map<String, Object> statisticsReport = generateStatisticsReport();
            
            // 发送邮件通知
            sendStatisticsReport(statisticsReport);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            logger.info("成绩统计分析任务执行完成，耗时: {}ms", duration);
            
        } catch (Exception e) {
            logger.error("成绩统计分析任务执行失败", e);
            throw new JobExecutionException("成绩统计分析任务执行失败", e);
        } finally {
            // 释放分布式锁
            distributedLock.releaseLock(LOCK_KEY, lockValue);
        }
    }

    /**
     * 生成统计报告
     */
    private Map<String, Object> generateStatisticsReport() {
        try {
            logger.info("开始生成成绩统计报告");
            
            // 获取各种统计数据
            Map<String, Object> report = gradeService.generateComprehensiveStatistics();
            
            // 添加报告生成时间
            report.put("generateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            report.put("reportType", "weekly_statistics");
            
            logger.info("成绩统计报告生成完成，包含 {} 项统计数据", report.size());
            
            return report;
            
        } catch (Exception e) {
            logger.error("生成成绩统计报告失败", e);
            throw new RuntimeException("生成成绩统计报告失败", e);
        }
    }

    /**
     * 发送统计报告邮件
     */
    private void sendStatisticsReport(Map<String, Object> report) {
        try {
            logger.info("开始发送成绩统计报告邮件");
            
            // 构建邮件内容
            String subject = "智慧校园管理系统 - 周度成绩统计报告";
            String content = buildReportContent(report);
            
            // 发送给管理员和教务人员
            emailService.sendStatisticsReport(subject, content, report);
            
            logger.info("成绩统计报告邮件发送完成");
            
        } catch (Exception e) {
            logger.error("发送成绩统计报告邮件失败", e);
            // 邮件发送失败不应该影响整个任务的执行
        }
    }

    /**
     * 构建报告内容
     */
    private String buildReportContent(Map<String, Object> report) {
        StringBuilder content = new StringBuilder();
        
        content.append("智慧校园管理系统 - 成绩统计分析报告\n");
        content.append("==========================================\n\n");
        
        content.append("报告生成时间: ").append(report.get("generateTime")).append("\n");
        content.append("报告类型: 周度统计报告\n\n");
        
        // 总体统计
        content.append("【总体统计】\n");
        content.append("总学生数: ").append(report.getOrDefault("totalStudents", 0)).append("\n");
        content.append("总课程数: ").append(report.getOrDefault("totalCourses", 0)).append("\n");
        content.append("总成绩记录数: ").append(report.getOrDefault("totalGrades", 0)).append("\n");
        content.append("平均成绩: ").append(report.getOrDefault("averageScore", 0)).append("\n\n");
        
        // 成绩分布
        content.append("【成绩分布】\n");
        @SuppressWarnings("unchecked")
        Map<String, Integer> gradeDistribution = (Map<String, Integer>) report.get("gradeDistribution");
        if (gradeDistribution != null) {
            gradeDistribution.forEach((grade, count) -> 
                content.append(grade).append(": ").append(count).append("人\n"));
        }
        content.append("\n");
        
        // 班级排名
        content.append("【班级平均成绩排名】\n");
        @SuppressWarnings("unchecked")
        Map<String, Double> classRanking = (Map<String, Double>) report.get("classRanking");
        if (classRanking != null) {
            classRanking.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> 
                    content.append(entry.getKey()).append(": ").append(String.format("%.2f", entry.getValue())).append("\n"));
        }
        content.append("\n");
        
        // 课程统计
        content.append("【课程成绩统计】\n");
        @SuppressWarnings("unchecked")
        Map<String, Double> courseStats = (Map<String, Double>) report.get("courseAverages");
        if (courseStats != null) {
            courseStats.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> 
                    content.append(entry.getKey()).append(": ").append(String.format("%.2f", entry.getValue())).append("\n"));
        }
        content.append("\n");
        
        content.append("==========================================\n");
        content.append("此报告由智慧校园管理系统自动生成\n");
        content.append("如有疑问，请联系系统管理员\n");
        
        return content.toString();
    }
}
