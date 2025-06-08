package com.campus.domain.repository;

import com.campus.domain.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课表Repository接口
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    /**
     * 根据学期查找课表
     */
    List<Schedule> findBySemesterOrderByWeekdayAscStartTimeAsc(String semester);

    /**
     * 根据教师ID和学期查找课表
     */
    List<Schedule> findByTeacherIdAndSemesterOrderByWeekdayAscStartTimeAsc(Long teacherId, String semester);

    /**
     * 根据教室ID和学期查找课表
     */
    List<Schedule> findByClassroomIdAndSemesterOrderByWeekdayAscStartTimeAsc(Long classroomId, String semester);

    /**
     * 根据状态查找课表
     */
    List<Schedule> findByStatus(Integer status);

    /**
     * 查找有冲突的课表
     */
    List<Schedule> findByHasConflictTrueAndSemester(String semester);

    /**
     * 根据课程ID查找课表
     */
    List<Schedule> findByCourseId(Long courseId);
}
