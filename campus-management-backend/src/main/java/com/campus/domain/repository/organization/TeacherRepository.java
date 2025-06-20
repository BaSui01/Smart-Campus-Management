package com.campus.domain.repository.organization;

import com.campus.domain.entity.organization.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 教师数据访问接口
 *
 * @author Campus Management System
 * @since 2025-06-20
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    /**
     * 根据工号查找教师
     *
     * @param employeeNo 工号
     * @return 教师信息
     */
    Optional<Teacher> findByEmployeeNo(String employeeNo);

    /**
     * 检查工号是否存在
     *
     * @param employeeNo 工号
     * @return 是否存在
     */
    boolean existsByEmployeeNo(String employeeNo);

    /**
     * 根据部门查找教师
     *
     * @param department 部门
     * @return 教师列表
     */
    List<Teacher> findByDepartment(String department);

    /**
     * 根据职称查找教师
     *
     * @param title 职称
     * @return 教师列表
     */
    List<Teacher> findByTitle(String title);

    /**
     * 根据教师状态查找教师
     *
     * @param status 状态
     * @return 教师列表
     */
    List<Teacher> findByTeacherStatus(String status);

    /**
     * 根据姓名模糊查询教师
     *
     * @param name 姓名关键字
     * @return 教师列表
     */
    @Query("SELECT t FROM Teacher t WHERE t.realName LIKE %:name%")
    List<Teacher> findByRealNameContaining(@Param("name") String name);

    /**
     * 统计部门教师数量
     *
     * @param department 部门
     * @return 教师数量
     */
    @Query("SELECT COUNT(t) FROM Teacher t WHERE t.department = :department")
    long countByDepartment(@Param("department") String department);
}
