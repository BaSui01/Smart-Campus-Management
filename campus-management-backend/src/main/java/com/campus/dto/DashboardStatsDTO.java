package com.campus.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘统计数据传输对象
 */
public class DashboardStatsDTO {
    
    // 基础统计数据
    private Integer totalStudents;
    private Integer totalCourses;
    private Integer totalClasses;
    private String monthlyRevenue;
    private Integer totalTeachers;
    private Integer totalUsers;
    private Integer pendingPayments;
    private Integer activeSchedules;
    
    // 趋势数据
    private List<ChartDataDTO> studentTrendData;
    private List<ChartDataDTO> courseTrendData;
    private List<ChartDataDTO> revenueTrendData;
    
    // 分布数据
    private List<ChartDataDTO> courseDistribution;
    private List<ChartDataDTO> gradeDistribution;
    private List<ChartDataDTO> majorDistribution;
    
    // 最近活动
    private List<RecentActivityDTO> recentActivities;
    
    // 系统通知
    private List<SystemNotificationDTO> systemNotifications;
    
    // 快速统计
    private QuickStatsDTO quickStats;
    
    // 构造函数
    public DashboardStatsDTO() {}
    
    // Getters and Setters
    public Integer getTotalStudents() {
        return totalStudents;
    }
    
    public void setTotalStudents(Integer totalStudents) {
        this.totalStudents = totalStudents;
    }
    
    public Integer getTotalCourses() {
        return totalCourses;
    }
    
    public void setTotalCourses(Integer totalCourses) {
        this.totalCourses = totalCourses;
    }
    
    public Integer getTotalClasses() {
        return totalClasses;
    }
    
    public void setTotalClasses(Integer totalClasses) {
        this.totalClasses = totalClasses;
    }
    
    public String getMonthlyRevenue() {
        return monthlyRevenue;
    }
    
    public void setMonthlyRevenue(String monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }
    
    public Integer getTotalTeachers() {
        return totalTeachers;
    }
    
    public void setTotalTeachers(Integer totalTeachers) {
        this.totalTeachers = totalTeachers;
    }
    
    public Integer getTotalUsers() {
        return totalUsers;
    }
    
    public void setTotalUsers(Integer totalUsers) {
        this.totalUsers = totalUsers;
    }
    
    public Integer getPendingPayments() {
        return pendingPayments;
    }
    
    public void setPendingPayments(Integer pendingPayments) {
        this.pendingPayments = pendingPayments;
    }
    
    public Integer getActiveSchedules() {
        return activeSchedules;
    }
    
    public void setActiveSchedules(Integer activeSchedules) {
        this.activeSchedules = activeSchedules;
    }
    
    public List<ChartDataDTO> getStudentTrendData() {
        return studentTrendData;
    }
    
    public void setStudentTrendData(List<ChartDataDTO> studentTrendData) {
        this.studentTrendData = studentTrendData;
    }
    
    public List<ChartDataDTO> getCourseTrendData() {
        return courseTrendData;
    }
    
    public void setCourseTrendData(List<ChartDataDTO> courseTrendData) {
        this.courseTrendData = courseTrendData;
    }
    
    public List<ChartDataDTO> getRevenueTrendData() {
        return revenueTrendData;
    }
    
    public void setRevenueTrendData(List<ChartDataDTO> revenueTrendData) {
        this.revenueTrendData = revenueTrendData;
    }
    
    public List<ChartDataDTO> getCourseDistribution() {
        return courseDistribution;
    }
    
    public void setCourseDistribution(List<ChartDataDTO> courseDistribution) {
        this.courseDistribution = courseDistribution;
    }
    
    public List<ChartDataDTO> getGradeDistribution() {
        return gradeDistribution;
    }
    
    public void setGradeDistribution(List<ChartDataDTO> gradeDistribution) {
        this.gradeDistribution = gradeDistribution;
    }
    
    public List<ChartDataDTO> getMajorDistribution() {
        return majorDistribution;
    }
    
    public void setMajorDistribution(List<ChartDataDTO> majorDistribution) {
        this.majorDistribution = majorDistribution;
    }
    
    public List<RecentActivityDTO> getRecentActivities() {
        return recentActivities;
    }
    
    public void setRecentActivities(List<RecentActivityDTO> recentActivities) {
        this.recentActivities = recentActivities;
    }
    
    public List<SystemNotificationDTO> getSystemNotifications() {
        return systemNotifications;
    }
    
    public void setSystemNotifications(List<SystemNotificationDTO> systemNotifications) {
        this.systemNotifications = systemNotifications;
    }
    
    public QuickStatsDTO getQuickStats() {
        return quickStats;
    }
    
    public void setQuickStats(QuickStatsDTO quickStats) {
        this.quickStats = quickStats;
    }
    
    /**
     * 图表数据传输对象
     */
    public static class ChartDataDTO {
        private String label;
        private Object value;
        private String color;
        private String date;
        
        public ChartDataDTO() {}
        
        public ChartDataDTO(String label, Object value) {
            this.label = label;
            this.value = value;
        }
        
        public ChartDataDTO(String label, Object value, String color) {
            this.label = label;
            this.value = value;
            this.color = color;
        }
        
        // Getters and Setters
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
    }
    
    /**
     * 最近活动传输对象
     */
    public static class RecentActivityDTO {
        private String type;
        private String description;
        private String user;
        private LocalDateTime timestamp;
        private String icon;
        private String color;
        
        public RecentActivityDTO() {}
        
        public RecentActivityDTO(String type, String description, String user, LocalDateTime timestamp) {
            this.type = type;
            this.description = description;
            this.user = user;
            this.timestamp = timestamp;
        }
        
        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getUser() { return user; }
        public void setUser(String user) { this.user = user; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
    }
    
    /**
     * 系统通知传输对象
     */
    public static class SystemNotificationDTO {
        private String title;
        private String content;
        private String sender;
        private LocalDateTime createTime;
        private String type;
        private Boolean isRead;
        
        public SystemNotificationDTO() {}
        
        public SystemNotificationDTO(String title, String content, String sender, LocalDateTime createTime) {
            this.title = title;
            this.content = content;
            this.sender = sender;
            this.createTime = createTime;
        }
        
        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        public LocalDateTime getCreateTime() { return createTime; }
        public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Boolean getIsRead() { return isRead; }
        public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    }
    
    /**
     * 快速统计传输对象
     */
    public static class QuickStatsDTO {
        private Integer todayNewStudents;
        private Integer todayPayments;
        private BigDecimal todayRevenue;
        private Integer onlineUsers;
        private Integer systemAlerts;
        
        public QuickStatsDTO() {}
        
        // Getters and Setters
        public Integer getTodayNewStudents() { return todayNewStudents; }
        public void setTodayNewStudents(Integer todayNewStudents) { this.todayNewStudents = todayNewStudents; }
        public Integer getTodayPayments() { return todayPayments; }
        public void setTodayPayments(Integer todayPayments) { this.todayPayments = todayPayments; }
        public BigDecimal getTodayRevenue() { return todayRevenue; }
        public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }
        public Integer getOnlineUsers() { return onlineUsers; }
        public void setOnlineUsers(Integer onlineUsers) { this.onlineUsers = onlineUsers; }
        public Integer getSystemAlerts() { return systemAlerts; }
        public void setSystemAlerts(Integer systemAlerts) { this.systemAlerts = systemAlerts; }
    }
}
