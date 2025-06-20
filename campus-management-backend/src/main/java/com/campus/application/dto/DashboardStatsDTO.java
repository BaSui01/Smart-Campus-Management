package com.campus.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 仪表板统计数据传输对象
 * 用于封装仪表板显示的各种统计信息
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-18
 */
public class DashboardStatsDTO {

    // 基础统计数据
    private Long totalUsers;
    private Long totalStudents;
    private Long totalTeachers;
    private Long totalCourses;
    private Long totalClasses;
    private Long totalAssignments;
    private Long totalExams;
    private Long totalAnnouncements;

    // 活跃度统计
    private Long activeUsersToday;
    private Long activeUsersThisWeek;
    private Long activeUsersThisMonth;
    private Long onlineUsers;

    // 学术统计
    private Double averageGrade;
    private Long submittedAssignments;
    private Long pendingAssignments;
    private Long completedExams;
    private Long upcomingExams;

    // 系统统计
    private Long totalLogins;
    private Long failedLogins;
    private Long systemErrors;
    private Long systemWarnings;

    // 财务统计
    private Double totalRevenue;
    private Double monthlyRevenue;
    private Long paidStudents;
    private Long unpaidStudents;

    // 时间信息
    private LocalDateTime lastUpdated;
    private String updateFrequency;

    // 图表数据
    private List<ChartDataDTO> trendData;
    private List<ChartDataDTO> courseData;
    private List<ChartDataDTO> gradeData;
    private List<ChartDataDTO> revenueData;

    // 扩展的图表数据
    private List<ChartDataDTO> studentTrendData;
    private List<ChartDataDTO> courseTrendData;
    private List<ChartDataDTO> revenueTrendData;
    private List<ChartDataDTO> courseDistribution;
    private List<ChartDataDTO> gradeDistribution;
    private List<ChartDataDTO> majorDistribution;

    // 快速操作统计
    private QuickStatsDTO quickStats;
    private List<Map<String, Object>> recentActivities;
    private List<Map<String, Object>> systemAlerts;
    private List<SystemNotificationDTO> systemNotifications;

    // 额外的统计字段
    private Integer activeSchedules;
    private Integer pendingPayments;

    // 构造函数
    public DashboardStatsDTO() {
        this.lastUpdated = LocalDateTime.now();
        this.updateFrequency = "实时";
    }

    // Getter 和 Setter 方法

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Long getTotalTeachers() {
        return totalTeachers;
    }

    public void setTotalTeachers(Long totalTeachers) {
        this.totalTeachers = totalTeachers;
    }

    public Long getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(Long totalCourses) {
        this.totalCourses = totalCourses;
    }

    public Long getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(Long totalClasses) {
        this.totalClasses = totalClasses;
    }

    public Long getTotalAssignments() {
        return totalAssignments;
    }

    public void setTotalAssignments(Long totalAssignments) {
        this.totalAssignments = totalAssignments;
    }

    public Long getTotalExams() {
        return totalExams;
    }

    public void setTotalExams(Long totalExams) {
        this.totalExams = totalExams;
    }

    public Long getTotalAnnouncements() {
        return totalAnnouncements;
    }

    public void setTotalAnnouncements(Long totalAnnouncements) {
        this.totalAnnouncements = totalAnnouncements;
    }

    public Long getActiveUsersToday() {
        return activeUsersToday;
    }

    public void setActiveUsersToday(Long activeUsersToday) {
        this.activeUsersToday = activeUsersToday;
    }

    public Long getActiveUsersThisWeek() {
        return activeUsersThisWeek;
    }

    public void setActiveUsersThisWeek(Long activeUsersThisWeek) {
        this.activeUsersThisWeek = activeUsersThisWeek;
    }

    public Long getActiveUsersThisMonth() {
        return activeUsersThisMonth;
    }

    public void setActiveUsersThisMonth(Long activeUsersThisMonth) {
        this.activeUsersThisMonth = activeUsersThisMonth;
    }

    public Long getOnlineUsers() {
        return onlineUsers;
    }

    public void setOnlineUsers(Long onlineUsers) {
        this.onlineUsers = onlineUsers;
    }

    public Double getAverageGrade() {
        return averageGrade;
    }

    public void setAverageGrade(Double averageGrade) {
        this.averageGrade = averageGrade;
    }

    public Long getSubmittedAssignments() {
        return submittedAssignments;
    }

    public void setSubmittedAssignments(Long submittedAssignments) {
        this.submittedAssignments = submittedAssignments;
    }

    public Long getPendingAssignments() {
        return pendingAssignments;
    }

    public void setPendingAssignments(Long pendingAssignments) {
        this.pendingAssignments = pendingAssignments;
    }

    public Long getCompletedExams() {
        return completedExams;
    }

    public void setCompletedExams(Long completedExams) {
        this.completedExams = completedExams;
    }

    public Long getUpcomingExams() {
        return upcomingExams;
    }

    public void setUpcomingExams(Long upcomingExams) {
        this.upcomingExams = upcomingExams;
    }

    public Long getTotalLogins() {
        return totalLogins;
    }

    public void setTotalLogins(Long totalLogins) {
        this.totalLogins = totalLogins;
    }

    public Long getFailedLogins() {
        return failedLogins;
    }

    public void setFailedLogins(Long failedLogins) {
        this.failedLogins = failedLogins;
    }

    public Long getSystemErrors() {
        return systemErrors;
    }

    public void setSystemErrors(Long systemErrors) {
        this.systemErrors = systemErrors;
    }

    public Long getSystemWarnings() {
        return systemWarnings;
    }

    public void setSystemWarnings(Long systemWarnings) {
        this.systemWarnings = systemWarnings;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Double getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(Double monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public Long getPaidStudents() {
        return paidStudents;
    }

    public void setPaidStudents(Long paidStudents) {
        this.paidStudents = paidStudents;
    }

    public Long getUnpaidStudents() {
        return unpaidStudents;
    }

    public void setUnpaidStudents(Long unpaidStudents) {
        this.unpaidStudents = unpaidStudents;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getUpdateFrequency() {
        return updateFrequency;
    }

    public void setUpdateFrequency(String updateFrequency) {
        this.updateFrequency = updateFrequency;
    }

    public List<ChartDataDTO> getTrendData() {
        return trendData;
    }

    public void setTrendData(List<ChartDataDTO> trendData) {
        this.trendData = trendData;
    }

    public List<ChartDataDTO> getCourseData() {
        return courseData;
    }

    public void setCourseData(List<ChartDataDTO> courseData) {
        this.courseData = courseData;
    }

    public List<ChartDataDTO> getGradeData() {
        return gradeData;
    }

    public void setGradeData(List<ChartDataDTO> gradeData) {
        this.gradeData = gradeData;
    }

    public List<ChartDataDTO> getRevenueData() {
        return revenueData;
    }

    public void setRevenueData(List<ChartDataDTO> revenueData) {
        this.revenueData = revenueData;
    }

    public QuickStatsDTO getQuickStats() {
        return quickStats;
    }

    public void setQuickStats(QuickStatsDTO quickStats) {
        this.quickStats = quickStats;
    }

    public List<Map<String, Object>> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<Map<String, Object>> recentActivities) {
        this.recentActivities = recentActivities;
    }

    public List<Map<String, Object>> getSystemAlerts() {
        return systemAlerts;
    }

    public void setSystemAlerts(List<Map<String, Object>> systemAlerts) {
        this.systemAlerts = systemAlerts;
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

    public List<SystemNotificationDTO> getSystemNotifications() {
        return systemNotifications;
    }

    public void setSystemNotifications(List<SystemNotificationDTO> systemNotifications) {
        this.systemNotifications = systemNotifications;
    }

    public Integer getActiveSchedules() {
        return activeSchedules;
    }

    public void setActiveSchedules(Integer activeSchedules) {
        this.activeSchedules = activeSchedules;
    }

    public Integer getPendingPayments() {
        return pendingPayments;
    }

    public void setPendingPayments(Integer pendingPayments) {
        this.pendingPayments = pendingPayments;
    }

    @Override
    public String toString() {
        return "DashboardStatsDTO{" +
                "totalUsers=" + totalUsers +
                ", totalStudents=" + totalStudents +
                ", totalTeachers=" + totalTeachers +
                ", totalCourses=" + totalCourses +
                ", activeUsersToday=" + activeUsersToday +
                ", averageGrade=" + averageGrade +
                ", lastUpdated=" + lastUpdated +
                '}';
    }

    /**
     * 系统通知DTO
     */
    public static class SystemNotificationDTO {
        private String title;
        private String content;
        private String sender;
        private String createTime;
        private String type;
        private Boolean isRead;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Boolean getIsRead() {
            return isRead;
        }

        public void setIsRead(Boolean isRead) {
            this.isRead = isRead;
        }
    }

    /**
     * 快速统计DTO
     */
    public static class QuickStatsDTO {
        private Integer todayNewStudents;
        private Integer todayPayments;
        private String todayRevenue;
        private Integer onlineUsers;
        private Integer systemAlerts;

        public Integer getTodayNewStudents() {
            return todayNewStudents;
        }

        public void setTodayNewStudents(Integer todayNewStudents) {
            this.todayNewStudents = todayNewStudents;
        }

        public Integer getTodayPayments() {
            return todayPayments;
        }

        public void setTodayPayments(Integer todayPayments) {
            this.todayPayments = todayPayments;
        }

        public String getTodayRevenue() {
            return todayRevenue;
        }

        public void setTodayRevenue(String todayRevenue) {
            this.todayRevenue = todayRevenue;
        }

        public Integer getOnlineUsers() {
            return onlineUsers;
        }

        public void setOnlineUsers(Integer onlineUsers) {
            this.onlineUsers = onlineUsers;
        }

        public Integer getSystemAlerts() {
            return systemAlerts;
        }

        public void setSystemAlerts(Integer systemAlerts) {
            this.systemAlerts = systemAlerts;
        }
    }
}
