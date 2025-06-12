package com.campus.application.service.academic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.campus.domain.entity.academic.CourseSelectionPeriod;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 选课时间段服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
public interface CourseSelectionPeriodService {

    /**
     * 创建选课时间段
     */
    CourseSelectionPeriod createPeriod(CourseSelectionPeriod period);

    /**
     * 更新选课时间段
     */
    CourseSelectionPeriod updatePeriod(CourseSelectionPeriod period);

    /**
     * 根据ID查找选课时间段
     */
    Optional<CourseSelectionPeriod> findById(Long id);

    /**
     * 删除选课时间段
     */
    void deleteById(Long id);

    /**
     * 分页查询选课时间段
     */
    Page<CourseSelectionPeriod> findWithFilters(String semester, Integer academicYear, String selectionType, Pageable pageable);

    /**
     * 查找当前开放的选课时间段
     */
    List<CourseSelectionPeriod> getCurrentOpenPeriods();

    /**
     * 查找学生可用的选课时间段
     */
    List<CourseSelectionPeriod> getAvailablePeriodsForStudent(String grade, String major);

    /**
     * 查找学生当前可以选课的时间段
     */
    List<CourseSelectionPeriod> getCurrentOpenPeriodsForStudent(String grade, String major);

    /**
     * 根据学期和学年查找选课时间段
     */
    List<CourseSelectionPeriod> findBySemesterAndAcademicYear(String semester, Integer academicYear);

    /**
     * 查找即将开始的选课时间段
     */
    List<CourseSelectionPeriod> getUpcomingPeriods(int hours);

    /**
     * 检查时间冲突
     */
    boolean hasTimeConflict(Long excludeId, String selectionType, String semester, Integer academicYear, 
                           LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 启用选课时间段
     */
    void enablePeriod(Long id);

    /**
     * 禁用选课时间段
     */
    void disablePeriod(Long id);

    /**
     * 检查学生是否可以在指定时间段选课
     */
    boolean canStudentSelectCourse(Long periodId, String grade, String major);

    /**
     * 检查学生是否可以退课
     */
    boolean canStudentDropCourse(Long periodId);

    /**
     * 获取选课时间段统计信息
     */
    PeriodStatistics getStatistics();

    /**
     * 批量创建选课时间段
     */
    List<CourseSelectionPeriod> batchCreatePeriods(List<CourseSelectionPeriod> periods);

    /**
     * 复制选课时间段到新学期
     */
    List<CourseSelectionPeriod> copyPeriodsToNewSemester(String fromSemester, Integer fromYear, 
                                                         String toSemester, Integer toYear);

    /**
     * 查找已过期的选课时间段
     */
    List<CourseSelectionPeriod> getExpiredPeriods();

    /**
     * 自动关闭过期的选课时间段
     */
    int autoCloseExpiredPeriods();

    /**
     * 发送选课提醒通知
     */
    void sendSelectionReminders();

    /**
     * 选课时间段统计信息内部类
     */
    class PeriodStatistics {
        private long totalPeriods;
        private long activePeriods;
        private long expiredPeriods;
        private long upcomingPeriods;
        private List<TypeCount> typeStatistics;

        public PeriodStatistics() {}

        public PeriodStatistics(long totalPeriods, long activePeriods, long expiredPeriods, long upcomingPeriods) {
            this.totalPeriods = totalPeriods;
            this.activePeriods = activePeriods;
            this.expiredPeriods = expiredPeriods;
            this.upcomingPeriods = upcomingPeriods;
        }

        // Getters and Setters
        public long getTotalPeriods() {
            return totalPeriods;
        }

        public void setTotalPeriods(long totalPeriods) {
            this.totalPeriods = totalPeriods;
        }

        public long getActivePeriods() {
            return activePeriods;
        }

        public void setActivePeriods(long activePeriods) {
            this.activePeriods = activePeriods;
        }

        public long getExpiredPeriods() {
            return expiredPeriods;
        }

        public void setExpiredPeriods(long expiredPeriods) {
            this.expiredPeriods = expiredPeriods;
        }

        public long getUpcomingPeriods() {
            return upcomingPeriods;
        }

        public void setUpcomingPeriods(long upcomingPeriods) {
            this.upcomingPeriods = upcomingPeriods;
        }

        public List<TypeCount> getTypeStatistics() {
            return typeStatistics;
        }

        public void setTypeStatistics(List<TypeCount> typeStatistics) {
            this.typeStatistics = typeStatistics;
        }
    }

    /**
     * 类型统计内部类
     */
    class TypeCount {
        private String type;
        private long count;

        public TypeCount() {}

        public TypeCount(String type, long count) {
            this.type = type;
            this.count = count;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }
}
