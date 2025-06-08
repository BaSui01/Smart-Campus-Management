package com.campus.domain.repository;

import com.campus.domain.entity.CourseSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 课程安排Repository接口
 * 提供课程安排相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface CourseScheduleRepository extends BaseRepository<CourseSchedule> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据课程ID查找课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.courseId = :courseId AND cs.deleted = 0 ORDER BY cs.dayOfWeek, cs.periodNumber")
    List<CourseSchedule> findByCourseId(@Param("courseId") Long courseId);

    /**
     * 分页根据课程ID查找课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.courseId = :courseId AND cs.deleted = 0")
    Page<CourseSchedule> findByCourseId(@Param("courseId") Long courseId, Pageable pageable);

    /**
     * 根据教师ID查找课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.teacherId = :teacherId AND cs.deleted = 0 ORDER BY cs.dayOfWeek, cs.periodNumber")
    List<CourseSchedule> findByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 分页根据教师ID查找课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.teacherId = :teacherId AND cs.deleted = 0")
    Page<CourseSchedule> findByTeacherId(@Param("teacherId") Long teacherId, Pageable pageable);

    /**
     * 根据教室ID查找课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.classroomId = :classroomId AND cs.deleted = 0 ORDER BY cs.dayOfWeek, cs.periodNumber")
    List<CourseSchedule> findByClassroomId(@Param("classroomId") Long classroomId);

    /**
     * 根据学期查找课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.semester = :semester AND cs.deleted = 0 ORDER BY cs.dayOfWeek, cs.periodNumber")
    List<CourseSchedule> findBySemester(@Param("semester") String semester);

    /**
     * 根据学年查找课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.academicYear = :academicYear AND cs.deleted = 0 ORDER BY cs.dayOfWeek, cs.periodNumber")
    List<CourseSchedule> findByAcademicYear(@Param("academicYear") Integer academicYear);

    /**
     * 根据星期几查找课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.dayOfWeek = :dayOfWeek AND cs.deleted = 0 ORDER BY cs.periodNumber")
    List<CourseSchedule> findByDayOfWeek(@Param("dayOfWeek") Integer dayOfWeek);

    /**
     * 根据节次查找课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.periodNumber = :periodNumber AND cs.deleted = 0 ORDER BY cs.dayOfWeek")
    List<CourseSchedule> findByPeriodNumber(@Param("periodNumber") Integer periodNumber);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据课程ID和学期查找课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.courseId = :courseId AND cs.semester = :semester AND cs.deleted = 0 ORDER BY cs.dayOfWeek, cs.periodNumber")
    List<CourseSchedule> findByCourseIdAndSemester(@Param("courseId") Long courseId, @Param("semester") String semester);

    /**
     * 根据教师ID和学期查找课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.teacherId = :teacherId AND cs.semester = :semester AND cs.deleted = 0 ORDER BY cs.dayOfWeek, cs.periodNumber")
    List<CourseSchedule> findByTeacherIdAndSemester(@Param("teacherId") Long teacherId, @Param("semester") String semester);

    /**
     * 根据学期和学年查找课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.semester = :semester AND cs.academicYear = :academicYear AND cs.deleted = 0 ORDER BY cs.dayOfWeek, cs.periodNumber")
    List<CourseSchedule> findBySemesterAndAcademicYear(@Param("semester") String semester, @Param("academicYear") Integer academicYear);

    /**
     * 获取课程表详情（包含课程和教师信息）
     */
    @Query("""
        SELECT cs, c.courseName, c.courseCode, u.realName
        FROM CourseSchedule cs
        LEFT JOIN Course c ON cs.courseId = c.id AND c.deleted = 0
        LEFT JOIN User u ON cs.teacherId = u.id AND u.deleted = 0
        WHERE cs.id = :scheduleId AND cs.deleted = 0
        """)
    Optional<Object[]> findScheduleDetailById(@Param("scheduleId") Long scheduleId);

    /**
     * 检查教室在指定时间是否被占用
     *
     * @param classroom 教室
     * @param dayOfWeek 星期几
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param semester 学期
     * @param excludeId 排除的课程表ID（用于更新时）
     * @return 是否被占用
     */
    @Query("""
        SELECT COUNT(cs) > 0
        FROM CourseSchedule cs
        WHERE cs.classroom = :classroom
        AND cs.dayOfWeek = :dayOfWeek
        AND cs.semester = :semester
        AND cs.deleted = 0
        AND cs.id != :excludeId
        AND (
            (cs.startTime <= :startTime AND cs.endTime > :startTime)
            OR (cs.startTime < :endTime AND cs.endTime >= :endTime)
            OR (cs.startTime >= :startTime AND cs.endTime <= :endTime)
        )
        """)
    boolean isClassroomOccupied(@Param("classroom") String classroom,
                               @Param("dayOfWeek") Integer dayOfWeek,
                               @Param("startTime") String startTime,
                               @Param("endTime") String endTime,
                               @Param("semester") String semester,
                               @Param("excludeId") Long excludeId);

    /**
     * 检查教师在指定时间是否有其他课程
     *
     * @param teacherId 教师ID
     * @param dayOfWeek 星期几
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param semester 学期
     * @param excludeId 排除的课程表ID（用于更新时）
     * @return 是否有冲突
     */
    @Query("""
        SELECT COUNT(cs) > 0
        FROM CourseSchedule cs
        WHERE cs.teacherId = :teacherId
        AND cs.dayOfWeek = :dayOfWeek
        AND cs.semester = :semester
        AND cs.deleted = 0
        AND cs.id != :excludeId
        AND (
            (cs.startTime <= :startTime AND cs.endTime > :startTime)
            OR (cs.startTime < :endTime AND cs.endTime >= :endTime)
            OR (cs.startTime >= :startTime AND cs.endTime <= :endTime)
        )
        """)
    boolean isTeacherOccupied(@Param("teacherId") Long teacherId,
                             @Param("dayOfWeek") Integer dayOfWeek,
                             @Param("startTime") String startTime,
                             @Param("endTime") String endTime,
                             @Param("semester") String semester,
                             @Param("excludeId") Long excludeId);

    /**
     * 根据教师ID、学期和学年查找课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.teacherId = :teacherId AND cs.semester = :semester AND cs.academicYear = :academicYear AND cs.deleted = 0 ORDER BY cs.dayOfWeek, cs.periodNumber")
    List<CourseSchedule> findByTeacherIdAndSemesterAndAcademicYear(@Param("teacherId") Long teacherId, @Param("semester") String semester, @Param("academicYear") Integer academicYear);

    /**
     * 根据教室ID、学期和学年查找课程安排
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.classroomId = :classroomId AND cs.semester = :semester AND cs.academicYear = :academicYear AND cs.deleted = 0 ORDER BY cs.dayOfWeek, cs.periodNumber")
    List<CourseSchedule> findByClassroomIdAndSemesterAndAcademicYear(@Param("classroomId") Long classroomId, @Param("semester") String semester, @Param("academicYear") Integer academicYear);

    /**
     * 根据学期和学年删除课程安排
     */
    @Modifying
    @Query("DELETE FROM CourseSchedule cs WHERE cs.semester = :semester AND cs.academicYear = :academicYear")
    void deleteBySemesterAndAcademicYear(@Param("semester") String semester, @Param("academicYear") Integer academicYear);

    /**
     * 软删除：根据学期和学年标记删除
     */
    @Modifying
    @Query("UPDATE CourseSchedule cs SET cs.deleted = 1 WHERE cs.semester = :semester AND cs.academicYear = :academicYear")
    int softDeleteBySemesterAndAcademicYear(@Param("semester") String semester, @Param("academicYear") Integer academicYear);

    /**
     * 根据课程ID列表查找课程安排
     */
    List<CourseSchedule> findByCourseIdIn(List<Long> courseIds);

    /**
     * 根据教室ID列表查找课程安排
     */
    List<CourseSchedule> findByClassroomIdIn(List<Long> classroomIds);

    /**
     * 根据时间段ID列表查找课程安排
     */
    List<CourseSchedule> findByTimeSlotIdIn(List<Long> timeSlotIds);

    /**
     * 检查指定时间段是否有课程安排
     */
    @Query("SELECT COUNT(cs) > 0 FROM CourseSchedule cs WHERE " +
           "cs.dayOfWeek = :dayOfWeek AND cs.periodNumber = :periodNumber AND " +
           "cs.semester = :semester AND cs.academicYear = :academicYear AND cs.deleted = 0")
    boolean existsByTimeSlot(@Param("dayOfWeek") Integer dayOfWeek,
                            @Param("periodNumber") Integer periodNumber,
                            @Param("semester") String semester,
                            @Param("academicYear") Integer academicYear);

    /**
     * 统计指定学期的课程安排数量
     */
    @Query("SELECT COUNT(cs) FROM CourseSchedule cs WHERE " +
           "cs.semester = :semester AND cs.academicYear = :academicYear AND cs.deleted = 0")
    long countBySemesterAndAcademicYear(@Param("semester") String semester, @Param("academicYear") Integer academicYear);

    /**
     * 获取指定教师的课程安排统计
     */
    @Query("SELECT cs.dayOfWeek, COUNT(cs) FROM CourseSchedule cs WHERE " +
           "cs.teacherId = :teacherId AND cs.semester = :semester AND " +
           "cs.academicYear = :academicYear AND cs.deleted = 0 GROUP BY cs.dayOfWeek")
    List<Object[]> getTeacherScheduleStats(@Param("teacherId") Long teacherId,
                                          @Param("semester") String semester,
                                          @Param("academicYear") Integer academicYear);

    /**
     * 获取指定教室的使用统计
     */
    @Query("SELECT cs.dayOfWeek, COUNT(cs) FROM CourseSchedule cs WHERE " +
           "cs.classroomId = :classroomId AND cs.semester = :semester AND " +
           "cs.academicYear = :academicYear AND cs.deleted = 0 GROUP BY cs.dayOfWeek")
    List<Object[]> getClassroomUsageStats(@Param("classroomId") Long classroomId,
                                         @Param("semester") String semester,
                                         @Param("academicYear") Integer academicYear);

    /**
     * 查找冲突的课程安排
     */
    @Query("SELECT cs1, cs2 FROM CourseSchedule cs1, CourseSchedule cs2 WHERE " +
           "cs1.id < cs2.id AND cs1.deleted = 0 AND cs2.deleted = 0 AND " +
           "cs1.semester = :semester AND cs1.academicYear = :academicYear AND " +
           "cs2.semester = :semester AND cs2.academicYear = :academicYear AND " +
           "cs1.dayOfWeek = cs2.dayOfWeek AND cs1.periodNumber = cs2.periodNumber AND " +
           "(cs1.teacherId = cs2.teacherId OR cs1.classroomId = cs2.classroomId)")
    List<Object[]> findConflictingSchedules(@Param("semester") String semester,
                                           @Param("academicYear") Integer academicYear);

    /**
     * 按时间段分组统计课程安排
     */
    @Query("SELECT cs.dayOfWeek, cs.periodNumber, COUNT(cs) FROM CourseSchedule cs WHERE " +
           "cs.semester = :semester AND cs.academicYear = :academicYear AND cs.deleted = 0 " +
           "GROUP BY cs.dayOfWeek, cs.periodNumber ORDER BY cs.dayOfWeek, cs.periodNumber")
    List<Object[]> getTimeSlotUsageStats(@Param("semester") String semester,
                                        @Param("academicYear") Integer academicYear);

    // ================================
    // 兼容性方法（为现有Service提供支持）
    // ================================

    /**
     * 根据课程ID查找课程安排（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.courseId = :courseId AND cs.deleted = :deleted")
    List<CourseSchedule> findByCourseIdAndDeleted(@Param("courseId") Long courseId, @Param("deleted") Integer deleted);

    /**
     * 根据教师ID查找课程安排（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.teacherId = :teacherId AND cs.deleted = :deleted")
    List<CourseSchedule> findByTeacherIdAndDeleted(@Param("teacherId") Long teacherId, @Param("deleted") Integer deleted);

    /**
     * 根据班级ID查找课程安排（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.classList LIKE CONCAT('%', :className, '%') AND cs.deleted = :deleted")
    List<CourseSchedule> findByClassIdAndDeleted(@Param("className") String className, @Param("deleted") Integer deleted);

    /**
     * 根据学期查找课程安排（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.semester = :semester AND cs.deleted = :deleted")
    List<CourseSchedule> findBySemesterAndDeleted(@Param("semester") String semester, @Param("deleted") Integer deleted);

    /**
     * 根据教室查找课程安排（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.classroomId = :classroomId AND cs.deleted = :deleted")
    List<CourseSchedule> findByClassroomAndDeleted(@Param("classroomId") Long classroomId, @Param("deleted") Integer deleted);

    /**
     * 根据课程ID和学期查找课程安排（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.courseId = :courseId AND cs.semester = :semester AND cs.deleted = :deleted")
    List<CourseSchedule> findByCourseIdAndSemesterAndDeleted(@Param("courseId") Long courseId, @Param("semester") String semester, @Param("deleted") Integer deleted);

    /**
     * 根据教师ID和学期查找课程安排（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.teacherId = :teacherId AND cs.semester = :semester AND cs.deleted = :deleted")
    List<CourseSchedule> findByTeacherIdAndSemesterAndDeleted(@Param("teacherId") Long teacherId, @Param("semester") String semester, @Param("deleted") Integer deleted);

    /**
     * 根据教室ID和删除状态查找课程安排（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.classroomId = :classroomId AND cs.deleted = :deleted")
    List<CourseSchedule> findByClassroomIdAndDeleted(@Param("classroomId") Long classroomId, @Param("deleted") Integer deleted);

    /**
     * 根据计划日期范围和删除状态查找课程安排（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.scheduleDate BETWEEN :startDate AND :endDate AND cs.deleted = :deleted")
    List<CourseSchedule> findByScheduleDateBetweenAndDeleted(@Param("startDate") java.time.LocalDate startDate, @Param("endDate") java.time.LocalDate endDate, @Param("deleted") Integer deleted);

    /**
     * 根据星期几和删除状态查找课程安排（兼容性方法）
     */
    @Query("SELECT cs FROM CourseSchedule cs WHERE cs.dayOfWeek = :dayOfWeek AND cs.deleted = :deleted")
    List<CourseSchedule> findByDayOfWeekAndDeleted(@Param("dayOfWeek") Integer dayOfWeek, @Param("deleted") Integer deleted);

    /**
     * 根据状态和删除状态统计课程安排数量（兼容性方法）
     */
    @Query("SELECT COUNT(cs) FROM CourseSchedule cs WHERE cs.status = :status AND cs.deleted = :deleted")
    long countByStatusAndDeleted(@Param("status") Integer status, @Param("deleted") Integer deleted);

}
