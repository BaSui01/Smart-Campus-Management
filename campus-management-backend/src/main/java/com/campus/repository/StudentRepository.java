package com.campus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.entity.Student;

/**
 * 学生数据访问层
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * 根据学号查找学生
     *
     * @param studentNo 学号
     * @return 学生信息
     */
    Optional<Student> findByStudentNoAndDeleted(String studentNo, Integer deleted);

    /**
     * 根据用户ID查找学生
     *
     * @param userId 用户ID
     * @return 学生信息
     */
    Optional<Student> findByUserIdAndDeleted(Long userId, Integer deleted);

    /**
     * 根据班级ID查找学生列表
     *
     * @param classId 班级ID
     * @return 学生列表
     */
    List<Student> findByClassIdAndDeletedOrderByStudentNoAsc(Long classId, Integer deleted);

    /**
     * 根据年级查找学生列表
     *
     * @param grade 年级
     * @return 学生列表
     */
    List<Student> findByGradeAndDeletedOrderByStudentNoAsc(String grade, Integer deleted);

    /**
     * 检查学号是否存在
     *
     * @param studentNo 学号
     * @return 是否存在
     */
    boolean existsByStudentNoAndDeleted(String studentNo, Integer deleted);

    /**
     * 获取学生及其用户信息
     *
     * @param studentId 学生ID
     * @return 学生信息包含用户详情
     */
    @Query("""
        SELECT s, u.username, u.realName, u.email, u.phone, u.avatarUrl
        FROM Student s
        LEFT JOIN User u ON s.userId = u.id AND u.deleted = 0
        WHERE s.id = :studentId AND s.deleted = 0
        """)
    Optional<Object[]> findStudentWithUser(@Param("studentId") Long studentId);

    /**
     * 搜索学生
     *
     * @param keyword 关键词
     * @return 学生列表
     */
    @Query("""
        SELECT s, u.username, u.realName, u.email, u.phone
        FROM Student s
        LEFT JOIN User u ON s.userId = u.id AND u.deleted = 0
        WHERE (s.studentNo LIKE %:keyword%
               OR u.realName LIKE %:keyword%
               OR u.username LIKE %:keyword%
               OR u.email LIKE %:keyword%)
        AND s.deleted = 0
        ORDER BY s.studentNo ASC
        """)
    List<Object[]> searchStudents(@Param("keyword") String keyword);

    /**
     * 统计学生数量按年级
     *
     * @return 统计结果
     */
    @Query("""
        SELECT s.grade, COUNT(s)
        FROM Student s
        WHERE s.deleted = 0
        GROUP BY s.grade
        ORDER BY s.grade ASC
        """)
    List<Object[]> countStudentsByGrade();

    /**
     * 统计学生数量按班级
     *
     * @return 统计结果
     */
    @Query("""
        SELECT c.className, COUNT(s.id)
        FROM Student s
        LEFT JOIN SchoolClass c ON s.classId = c.id AND c.deleted = 0
        WHERE s.deleted = 0
        GROUP BY s.classId, c.className
        ORDER BY c.className ASC
        """)
    List<Object[]> countStudentsByClass();

    /**
     * 根据班级ID和年级查找学生列表
     *
     * @param classId 班级ID
     * @param grade 年级
     * @return 学生列表
     */
    List<Student> findByClassIdAndGradeAndDeletedOrderByStudentNoAsc(Long classId, String grade, Integer deleted);

    /**
     * 获取学生的完整信息（包含班级和用户信息）
     *
     * @param studentId 学生ID
     * @return 学生完整信息
     */
    @Query("""
        SELECT s, u.username, u.realName, u.email, u.phone, u.avatarUrl,
               c.className, c.classCode
        FROM Student s
        LEFT JOIN User u ON s.userId = u.id AND u.deleted = 0
        LEFT JOIN SchoolClass c ON s.classId = c.id AND c.deleted = 0
        WHERE s.id = :studentId AND s.deleted = 0
        """)
    Optional<Object[]> findStudentFullInfo(@Param("studentId") Long studentId);
}

