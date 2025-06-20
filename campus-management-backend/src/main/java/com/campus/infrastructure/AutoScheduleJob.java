package com.campus.infrastructure;

import com.campus.application.service.academic.CourseScheduleService;
import com.campus.domain.entity.academic.Course;
import com.campus.shared.util.DistributedLock;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自动排课定时任务
 * 
 * @author Campus Team
 * @since 2025-06-05
 */
@Component
public class AutoScheduleJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(AutoScheduleJob.class);
    
    private static final String LOCK_KEY = "auto_schedule_job";

    @Autowired
    private CourseScheduleService courseScheduleService;

    @Autowired
    private DistributedLock distributedLock;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String lockValue = distributedLock.tryLock(LOCK_KEY, 300); // 5分钟锁定时间
        
        if (lockValue == null) {
            logger.warn("自动排课任务正在执行中，跳过本次执行");
            return;
        }

        try {
            logger.info("开始执行自动排课任务");
            long startTime = System.currentTimeMillis();
            
            // 获取需要排课的课程
            List<Course> pendingCourses = courseScheduleService.findPendingCourses();
            logger.info("找到待排课程数量: {}", pendingCourses.size());
            
            int successCount = 0;
            int failCount = 0;
            
            // 执行智能排课算法
            for (Course course : pendingCourses) {
                try {
                    boolean success = courseScheduleService.autoScheduleCourse(course);
                    if (success) {
                        successCount++;
                        logger.debug("课程排课成功: {} - {}", course.getCourseCode(), course.getCourseName());
                    } else {
                        failCount++;
                        logger.warn("课程排课失败: {} - {}", course.getCourseCode(), course.getCourseName());
                    }
                } catch (Exception e) {
                    failCount++;
                    logger.error("课程排课异常: {} - {}", course.getCourseCode(), course.getCourseName(), e);
                }
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            logger.info("自动排课任务执行完成 - 总课程数: {}, 成功: {}, 失败: {}, 耗时: {}ms", 
                       pendingCourses.size(), successCount, failCount, duration);
            
            // 如果有失败的课程，记录详细信息
            if (failCount > 0) {
                logger.warn("自动排课任务存在失败课程，建议检查课程配置和教室资源");
            }
            
        } catch (Exception e) {
            logger.error("自动排课任务执行失败", e);
            throw new JobExecutionException("自动排课任务执行失败", e);
        } finally {
            // 释放分布式锁
            distributedLock.releaseLock(LOCK_KEY, lockValue);
        }
    }
}
