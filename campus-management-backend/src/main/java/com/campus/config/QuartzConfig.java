package com.campus.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Quartz 定时任务配置类
 * 
 * @author Campus Team
 * @since 2025-06-05
 */
@Configuration
public class QuartzConfig {

    /**
     * 自动装配的 Job 工厂
     * SpringBeanJobFactory 已经实现了 ApplicationContextAware，所以不需要重复实现
     */
    public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {

        private transient AutowireCapableBeanFactory beanFactory;

        @Override
        public void setApplicationContext(@NonNull ApplicationContext context) {
            super.setApplicationContext(context);
            beanFactory = context.getAutowireCapableBeanFactory();
        }

        @Override
        @NonNull
        protected Object createJobInstance(@NonNull TriggerFiredBundle bundle) throws Exception {
            Object job = super.createJobInstance(bundle);
            if (beanFactory != null) {
                beanFactory.autowireBean(job);
            }
            return job;
        }
    }

    /**
     * 配置 SchedulerFactoryBean
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        
        // 设置数据源
        factory.setDataSource(dataSource);
        
        // 设置 Quartz 属性
        factory.setQuartzProperties(quartzProperties());
        
        // 设置 Job 工厂
        factory.setJobFactory(new AutowiringSpringBeanJobFactory());
        
        // 等待作业完成后再关闭
        factory.setWaitForJobsToCompleteOnShutdown(true);
        
        // 覆盖已存在的作业
        factory.setOverwriteExistingJobs(true);
        
        // 延迟启动
        factory.setStartupDelay(30);
        
        // 设置调度器名称
        factory.setSchedulerName("CampusScheduler");
        
        return factory;
    }

    /**
     * Quartz 属性配置
     */
    private Properties quartzProperties() {
        Properties properties = new Properties();
        
        // 调度器配置
        properties.setProperty("org.quartz.scheduler.instanceName", "CampusScheduler");
        properties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        properties.setProperty("org.quartz.scheduler.skipUpdateCheck", "true");
        
        // 线程池配置
        properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        properties.setProperty("org.quartz.threadPool.threadCount", "10");
        properties.setProperty("org.quartz.threadPool.threadPriority", "5");
        properties.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
        
        // 作业存储配置
        properties.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        properties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        properties.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
        properties.setProperty("org.quartz.jobStore.useProperties", "false");
        
        // 集群配置
        properties.setProperty("org.quartz.jobStore.isClustered", "true");
        properties.setProperty("org.quartz.jobStore.clusterCheckinInterval", "20000");
        
        // 数据源配置
        properties.setProperty("org.quartz.jobStore.dataSource", "quartzDataSource");
        
        // 失火处理
        properties.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
        
        return properties;
    }
}
