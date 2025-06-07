package com.campus.application.service;

import com.campus.domain.entity.Course;
import com.campus.domain.entity.CourseSchedule;
import com.campus.domain.entity.Classroom;
import com.campus.domain.entity.TimeSlot;

import java.util.List;
import java.util.Map;

/**
 * 自动排课服务接口
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
public interface AutoScheduleService {

    /**
     * 排课请求参数
     */
    class ScheduleRequest {
        private String semester;           // 学期
        private Integer academicYear;      // 学年
        private List<Long> courseIds;      // 需要排课的课程ID列表
        private List<Long> classroomIds;   // 可用教室ID列表
        private List<Long> timeSlotIds;    // 可用时间段ID列表
        private Integer startWeek;         // 开始周次
        private Integer endWeek;           // 结束周次
        private String weekType;           // 单双周限制
        private Boolean allowCombinedClass; // 是否允许合班教学
        private Integer maxClassSize;      // 最大班级规模
        private Map<String, Object> preferences; // 排课偏好设置

        // 构造函数和getter/setter
        public ScheduleRequest() {}

        public ScheduleRequest(String semester, Integer academicYear, List<Long> courseIds) {
            this.semester = semester;
            this.academicYear = academicYear;
            this.courseIds = courseIds;
        }

        // Getter 和 Setter 方法
        public String getSemester() { return semester; }
        public void setSemester(String semester) { this.semester = semester; }

        public Integer getAcademicYear() { return academicYear; }
        public void setAcademicYear(Integer academicYear) { this.academicYear = academicYear; }

        public List<Long> getCourseIds() { return courseIds; }
        public void setCourseIds(List<Long> courseIds) { this.courseIds = courseIds; }

        public List<Long> getClassroomIds() { return classroomIds; }
        public void setClassroomIds(List<Long> classroomIds) { this.classroomIds = classroomIds; }

        public List<Long> getTimeSlotIds() { return timeSlotIds; }
        public void setTimeSlotIds(List<Long> timeSlotIds) { this.timeSlotIds = timeSlotIds; }

        public Integer getStartWeek() { return startWeek; }
        public void setStartWeek(Integer startWeek) { this.startWeek = startWeek; }

        public Integer getEndWeek() { return endWeek; }
        public void setEndWeek(Integer endWeek) { this.endWeek = endWeek; }

        public String getWeekType() { return weekType; }
        public void setWeekType(String weekType) { this.weekType = weekType; }

        public Boolean getAllowCombinedClass() { return allowCombinedClass; }
        public void setAllowCombinedClass(Boolean allowCombinedClass) { this.allowCombinedClass = allowCombinedClass; }

        public Integer getMaxClassSize() { return maxClassSize; }
        public void setMaxClassSize(Integer maxClassSize) { this.maxClassSize = maxClassSize; }

        public Map<String, Object> getPreferences() { return preferences; }
        public void setPreferences(Map<String, Object> preferences) { this.preferences = preferences; }
    }

    /**
     * 排课结果
     */
    class ScheduleResult {
        private boolean success;                    // 是否成功
        private String message;                     // 结果消息
        private List<CourseSchedule> schedules;     // 生成的课程安排
        private List<ConflictInfo> conflicts;       // 冲突信息
        private ScheduleStatistics statistics;      // 排课统计

        public ScheduleResult() {}

        public ScheduleResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        // Getter 和 Setter 方法
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public List<CourseSchedule> getSchedules() { return schedules; }
        public void setSchedules(List<CourseSchedule> schedules) { this.schedules = schedules; }

        public List<ConflictInfo> getConflicts() { return conflicts; }
        public void setConflicts(List<ConflictInfo> conflicts) { this.conflicts = conflicts; }

        public ScheduleStatistics getStatistics() { return statistics; }
        public void setStatistics(ScheduleStatistics statistics) { this.statistics = statistics; }
    }

    /**
     * 冲突信息
     */
    class ConflictInfo {
        private String type;              // 冲突类型：teacher, classroom, student
        private String description;       // 冲突描述
        private Long courseId1;          // 冲突课程1
        private Long courseId2;          // 冲突课程2
        private String suggestion;       // 解决建议

        public ConflictInfo() {}

        public ConflictInfo(String type, String description) {
            this.type = type;
            this.description = description;
        }

        // Getter 和 Setter 方法
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Long getCourseId1() { return courseId1; }
        public void setCourseId1(Long courseId1) { this.courseId1 = courseId1; }

        public Long getCourseId2() { return courseId2; }
        public void setCourseId2(Long courseId2) { this.courseId2 = courseId2; }

        public String getSuggestion() { return suggestion; }
        public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    }

    /**
     * 排课统计
     */
    class ScheduleStatistics {
        private int totalCourses;         // 总课程数
        private int scheduledCourses;     // 已排课程数
        private int unscheduledCourses;   // 未排课程数
        private int totalConflicts;       // 总冲突数
        private int teacherConflicts;     // 教师冲突数
        private int classroomConflicts;   // 教室冲突数
        private int studentConflicts;     // 学生冲突数
        private double successRate;       // 成功率

        public ScheduleStatistics() {}

        // Getter 和 Setter 方法
        public int getTotalCourses() { return totalCourses; }
        public void setTotalCourses(int totalCourses) { this.totalCourses = totalCourses; }

        public int getScheduledCourses() { return scheduledCourses; }
        public void setScheduledCourses(int scheduledCourses) { this.scheduledCourses = scheduledCourses; }

        public int getUnscheduledCourses() { return unscheduledCourses; }
        public void setUnscheduledCourses(int unscheduledCourses) { this.unscheduledCourses = unscheduledCourses; }

        public int getTotalConflicts() { return totalConflicts; }
        public void setTotalConflicts(int totalConflicts) { this.totalConflicts = totalConflicts; }

        public int getTeacherConflicts() { return teacherConflicts; }
        public void setTeacherConflicts(int teacherConflicts) { this.teacherConflicts = teacherConflicts; }

        public int getClassroomConflicts() { return classroomConflicts; }
        public void setClassroomConflicts(int classroomConflicts) { this.classroomConflicts = classroomConflicts; }

        public int getStudentConflicts() { return studentConflicts; }
        public void setStudentConflicts(int studentConflicts) { this.studentConflicts = studentConflicts; }

        public double getSuccessRate() { return successRate; }
        public void setSuccessRate(double successRate) { this.successRate = successRate; }
    }

    /**
     * 自动排课
     * 
     * @param request 排课请求参数
     * @return 排课结果
     */
    ScheduleResult autoSchedule(ScheduleRequest request);

    /**
     * 检查课程安排冲突
     * 
     * @param schedule 课程安排
     * @param existingSchedules 已有的课程安排
     * @return 冲突列表
     */
    List<ConflictInfo> checkConflicts(CourseSchedule schedule, List<CourseSchedule> existingSchedules);

    /**
     * 验证排课方案
     * 
     * @param schedules 课程安排列表
     * @return 验证结果
     */
    ScheduleResult validateSchedule(List<CourseSchedule> schedules);

    /**
     * 优化排课方案
     * 
     * @param schedules 现有的课程安排
     * @param request 优化参数
     * @return 优化后的排课结果
     */
    ScheduleResult optimizeSchedule(List<CourseSchedule> schedules, ScheduleRequest request);

    /**
     * 获取可用时间段
     * 
     * @param courseId 课程ID
     * @param classroomId 教室ID
     * @param teacherId 教师ID
     * @param semester 学期
     * @param academicYear 学年
     * @return 可用时间段列表
     */
    List<TimeSlot> getAvailableTimeSlots(Long courseId, Long classroomId, Long teacherId, 
                                        String semester, Integer academicYear);

    /**
     * 获取推荐教室
     * 
     * @param course 课程
     * @param timeSlot 时间段
     * @param studentCount 学生数量
     * @return 推荐教室列表
     */
    List<Classroom> getRecommendedClassrooms(Course course, TimeSlot timeSlot, Integer studentCount);

    /**
     * 生成排课报告
     * 
     * @param semester 学期
     * @param academicYear 学年
     * @return 排课报告
     */
    Map<String, Object> generateScheduleReport(String semester, Integer academicYear);

    /**
     * 批量导入排课
     * 
     * @param schedules 课程安排列表
     * @return 导入结果
     */
    ScheduleResult batchImportSchedule(List<CourseSchedule> schedules);

    /**
     * 清空指定学期的排课
     * 
     * @param semester 学期
     * @param academicYear 学年
     * @return 是否成功
     */
    boolean clearSchedule(String semester, Integer academicYear);

    /**
     * 复制排课方案
     * 
     * @param sourceSemester 源学期
     * @param sourceAcademicYear 源学年
     * @param targetSemester 目标学期
     * @param targetAcademicYear 目标学年
     * @return 复制结果
     */
    ScheduleResult copySchedule(String sourceSemester, Integer sourceAcademicYear, 
                               String targetSemester, Integer targetAcademicYear);

    /**
     * 获取排课统计信息
     * 
     * @param semester 学期
     * @param academicYear 学年
     * @return 统计信息
     */
    ScheduleStatistics getScheduleStatistics(String semester, Integer academicYear);

    /**
     * 检查教师时间冲突
     * 
     * @param teacherId 教师ID
     * @param timeSlot 时间段
     * @param semester 学期
     * @param academicYear 学年
     * @return 是否有冲突
     */
    boolean hasTeacherConflict(Long teacherId, TimeSlot timeSlot, String semester, Integer academicYear);

    /**
     * 检查教室时间冲突
     * 
     * @param classroomId 教室ID
     * @param timeSlot 时间段
     * @param semester 学期
     * @param academicYear 学年
     * @return 是否有冲突
     */
    boolean hasClassroomConflict(Long classroomId, TimeSlot timeSlot, String semester, Integer academicYear);

    /**
     * 检查学生时间冲突
     * 
     * @param classList 班级列表
     * @param timeSlot 时间段
     * @param semester 学期
     * @param academicYear 学年
     * @return 是否有冲突
     */
    boolean hasStudentConflict(List<String> classList, TimeSlot timeSlot, String semester, Integer academicYear);
}