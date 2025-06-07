package com.campus.domain.repository;

import com.campus.domain.entity.SchoolClass;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 班级Repository接口
 * 提供班级相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface SchoolClassRepository extends BaseRepository<SchoolClass> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据班级代码查找班级
     */
    @Query("SELECT c FROM SchoolClass c WHERE c.classCode = :classCode AND c.deleted = 0")
    Optional<SchoolClass> findByClassCode(@Param("classCode") String classCode);

    /**
     * 根据班级名称查找班级
     */
    @Query("SELECT c FROM SchoolClass c WHERE c.className = :className AND c.deleted = 0")
    Optional<SchoolClass> findByClassName(@Param("className") String className);

    /**
     * 根据年级查找班级列表
     */
    @Query("SELECT c FROM SchoolClass c WHERE c.grade = :grade AND c.deleted = 0 ORDER BY c.classCode ASC")
    List<SchoolClass> findByGrade(@Param("grade") String grade);

    /**
     * 根据部门ID查找班级列表
     */
    @Query("SELECT c FROM SchoolClass c WHERE c.departmentId = :departmentId AND c.deleted = 0 ORDER BY c.grade ASC, c.classCode ASC")
    List<SchoolClass> findByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 根据班主任ID查找班级列表
     */
    @Query("SELECT c FROM SchoolClass c WHERE c.headTeacherId = :headTeacherId AND c.deleted = 0 ORDER BY c.grade ASC, c.classCode ASC")
    List<SchoolClass> findByHeadTeacherId(@Param("headTeacherId") Long headTeacherId);

    /**
     * 根据班级名称模糊查询
     */
    @Query("SELECT c FROM SchoolClass c WHERE c.className LIKE %:className% AND c.deleted = 0 ORDER BY c.grade ASC, c.classCode ASC")
    List<SchoolClass> findByClassNameContaining(@Param("className") String className);

    /**
     * 根据入学年份查找班级列表
     */
    @Query("SELECT c FROM SchoolClass c WHERE c.enrollmentYear = :enrollmentYear AND c.deleted = 0 ORDER BY c.grade ASC, c.classCode ASC")
    List<SchoolClass> findByEnrollmentYear(@Param("enrollmentYear") Integer enrollmentYear);

    /**
     * 获取班级详情（包含班主任信息和学生数量）
     */
    @Query("""
        SELECT c, u.realName,
        (SELECT COUNT(s) FROM Student s WHERE s.classId = c.id AND s.deleted = 0)
        FROM SchoolClass c
        LEFT JOIN User u ON c.headTeacherId = u.id AND u.deleted = 0
        WHERE c.id = :classId AND c.deleted = 0
        """)
    Optional<Object[]> findClassDetailById(@Param("classId") Long classId);

    /**
     * 统计班级数量按年级
     */
    @Query("""
        SELECT c.grade, COUNT(c)
        FROM SchoolClass c
        WHERE c.deleted = 0
        GROUP BY c.grade
        ORDER BY c.grade ASC
        """)
    List<Object[]> countClassesByGrade();

    /**
     * 获取所有班级的详细信息列表
     */
    @Query("""
        SELECT c, u.realName,
        (SELECT COUNT(s) FROM Student s WHERE s.classId = c.id AND s.deleted = 0)
        FROM SchoolClass c
        LEFT JOIN User u ON c.headTeacherId = u.id AND u.deleted = 0
        WHERE c.deleted = 0
        ORDER BY c.grade ASC, c.classCode ASC
        """)
    List<Object[]> findAllClassDetails();

    /**
     * 根据年级获取班级详细信息列表
     */
    @Query("""
        SELECT c, u.realName,
        (SELECT COUNT(s) FROM Student s WHERE s.classId = c.id AND s.deleted = 0)
        FROM SchoolClass c
        LEFT JOIN User u ON c.headTeacherId = u.id AND u.deleted = 0
        WHERE c.grade = :grade AND c.deleted = 0
        ORDER BY c.classCode ASC
        """)
    List<Object[]> findClassDetailsByGrade(@Param("grade") String grade);

    /**
     * 根据关键词搜索班级
     *
     * @param keyword 关键词
     * @return 班级列表
     */
    @Query("""
        SELECT c, u.realName,
        (SELECT COUNT(s) FROM Student s WHERE s.classId = c.id AND s.deleted = 0)
        FROM SchoolClass c
        LEFT JOIN User u ON c.headTeacherId = u.id AND u.deleted = 0
        WHERE c.deleted = 0
        AND (c.className LIKE %:keyword%
             OR c.classCode LIKE %:keyword%
             OR c.grade LIKE %:keyword%
             OR u.realName LIKE %:keyword%)
        ORDER BY c.grade ASC, c.classCode ASC
        """)
    List<Object[]> searchClasses(@Param("keyword") String keyword);

    /**
     * 查找没有班主任的班级（详细信息）
     */
    @Query("""
        SELECT c,
        (SELECT COUNT(s) FROM Student s WHERE s.classId = c.id AND s.deleted = 0)
        FROM SchoolClass c
        WHERE c.headTeacherId IS NULL AND c.deleted = 0
        ORDER BY c.grade ASC, c.classCode ASC
        """)
    List<Object[]> findClassesWithoutHeadTeacherDetails();

    // ================================
    // 兼容性方法（为现有Service提供支持）
    // ================================

    /**
     * 根据班级代码查找班级（兼容性方法）
     */
    default Optional<SchoolClass> findByClassCodeAndDeleted(String classCode, Integer deleted) {
        return findByClassCode(classCode);
    }

    /**
     * 根据年级查找班级列表（兼容性方法）
     */
    default List<SchoolClass> findByGradeAndDeletedOrderByClassCodeAsc(String grade, Integer deleted) {
        return findByGrade(grade);
    }

    /**
     * 根据部门ID查找班级列表（兼容性方法）
     */
    default List<SchoolClass> findByDepartmentIdAndDeletedOrderByGradeAscClassCodeAsc(Long departmentId, Integer deleted) {
        return findByDepartmentId(departmentId);
    }

    /**
     * 根据班主任ID查找班级列表（兼容性方法）
     */
    default List<SchoolClass> findByHeadTeacherIdAndDeletedOrderByGradeAscClassCodeAsc(Long headTeacherId, Integer deleted) {
        return findByHeadTeacherId(headTeacherId);
    }

    /**
     * 检查班级代码是否存在（兼容性方法）
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM SchoolClass c WHERE c.classCode = :classCode AND c.deleted = :deleted")
    boolean existsByClassCodeAndDeleted(@Param("classCode") String classCode, @Param("deleted") Integer deleted);

    /**
     * 查找没有班主任的班级（兼容性方法）
     */
    default List<Object[]> findClassesWithoutHeadTeacher() {
        return findClassesWithoutHeadTeacherDetails();
    }

}
