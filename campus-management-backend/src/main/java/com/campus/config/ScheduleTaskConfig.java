package com.campus.config;

import com.campus.job.AutoScheduleJob;
import com.campus.job.GradeStatisticsJob;
import com.campus.job.PaymentReminderJob;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 定时任务配置和启动类
 * 
 * @author Campus Team
 * @since 2025-06-05
 */
@Component
public class ScheduleTaskConfig implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleTaskConfig.class);

    @Autowired
    private Scheduler scheduler;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("开始配置定时任务...");
        
        try {
            // 配置自动排课任务
            configureAutoScheduleJob();
            
            // 配置成绩统计任务
            configureGradeStatisticsJob();
            
            // 配置缴费提醒任务
            configurePaymentReminderJob();
            
            logger.info("所有定时任务配置完成");
            
        } catch (Exception e) {
            logger.error("定时任务配置失败", e);
            throw e;
        }
    }

    /**
     * 配置自动排课任务
     * 每天凌晨2点执行
     */
    private void configureAutoScheduleJob() throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(AutoScheduleJob.class)
            .withIdentity("autoScheduleJob", "campus")
            .withDescription("自动排课任务")
            .storeDurably()
            .build();

        // 每天凌晨2点执行
        CronTrigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("autoScheduleTrigger", "campus")
            .withDescription("自动排课触发器")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 * * ?"))
            .build();

        if (scheduler.checkExists(jobDetail.getKey())) {
            scheduler.deleteJob(jobDetail.getKey());
        }

        scheduler.scheduleJob(jobDetail, trigger);
        logger.info("自动排课任务配置完成 - 执行时间: 每天凌晨2点");
    }

    /**
     * 配置成绩统计任务
     * 每周日晚上10点执行
     */
    private void configureGradeStatisticsJob() throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(GradeStatisticsJob.class)
            .withIdentity("gradeStatisticsJob", "campus")
            .withDescription("成绩统计分析任务")
            .storeDurably()
            .build();

        // 每周日晚上10点执行
        CronTrigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("gradeStatisticsTrigger", "campus")
            .withDescription("成绩统计分析触发器")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 22 ? * SUN"))
            .build();

        if (scheduler.checkExists(jobDetail.getKey())) {
            scheduler.deleteJob(jobDetail.getKey());
        }

        scheduler.scheduleJob(jobDetail, trigger);
        logger.info("成绩统计分析任务配置完成 - 执行时间: 每周日晚上10点");
    }

    /**
     * 配置缴费提醒任务
     * 每月1号和15号上午9点执行
     */
    private void configurePaymentReminderJob() throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(PaymentReminderJob.class)
            .withIdentity("paymentReminderJob", "campus")
            .withDescription("缴费提醒任务")
            .storeDurably()
            .build();

        // 每月1号和15号上午9点执行
        CronTrigger trigger = TriggerBuilder.newTrigger()
            .withIdentity("paymentReminderTrigger", "campus")
            .withDescription("缴费提醒触发器")
            .withSchedule(CronScheduleBuilder.cronSchedule("0 0 9 1,15 * ?"))
            .build();

        if (scheduler.checkExists(jobDetail.getKey())) {
            scheduler.deleteJob(jobDetail.getKey());
        }

        scheduler.scheduleJob(jobDetail, trigger);
        logger.info("缴费提醒任务配置完成 - 执行时间: 每月1号和15号上午9点");
    }

    /**
     * 手动触发自动排课任务
     */
    public void triggerAutoScheduleJob() throws SchedulerException {
        JobKey jobKey = new JobKey("autoScheduleJob", "campus");
        scheduler.triggerJob(jobKey);
        logger.info("手动触发自动排课任务");
    }

    /**
     * 手动触发成绩统计任务
     */
    public void triggerGradeStatisticsJob() throws SchedulerException {
        JobKey jobKey = new JobKey("gradeStatisticsJob", "campus");
        scheduler.triggerJob(jobKey);
        logger.info("手动触发成绩统计任务");
    }

    /**
     * 手动触发缴费提醒任务
     */
    public void triggerPaymentReminderJob() throws SchedulerException {
        JobKey jobKey = new JobKey("paymentReminderJob", "campus");
        scheduler.triggerJob(jobKey);
        logger.info("手动触发缴费提醒任务");
    }

    /**
     * 暂停任务
     */
    public void pauseJob(String jobName, String groupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, groupName);
        scheduler.pauseJob(jobKey);
        logger.info("暂停任务: {}.{}", groupName, jobName);
    }

    /**
     * 恢复任务
     */
    public void resumeJob(String jobName, String groupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, groupName);
        scheduler.resumeJob(jobKey);
        logger.info("恢复任务: {}.{}", groupName, jobName);
    }

    /**
     * 删除任务
     */
    public void deleteJob(String jobName, String groupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, groupName);
        scheduler.deleteJob(jobKey);
        logger.info("删除任务: {}.{}", groupName, jobName);
    }

    /**
     * 获取任务状态
     */
    public String getJobStatus(String jobName, String groupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, groupName);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        
        if (jobDetail == null) {
            return "NOT_EXISTS";
        }
        
        Trigger.TriggerState triggerState = scheduler.getTriggerState(
            new TriggerKey(jobName + "Trigger", groupName));
        
        return triggerState.name();
    }
}
