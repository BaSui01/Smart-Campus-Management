package com.campus.domain.repository.organization;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.domain.entity.organization.Student;
import com.campus.domain.repository.infrastructure.BaseRepository;

import java.util.List;
import java.util.Optional;

/**
 * 学生Repository接口
 * 提供学生相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface StudentRepository extends BaseRepository<Student> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据学号查找学生
     */
    @Query("SELECT s FROM Student s WHERE s.studentNo = :studentNo AND s.deleted = 0")
    Optional<Student> findByStudentNo(@Param("studentNo") String studentNo);

    /**
     * 根据用户ID查找学生
     */
    @Query("SELECT s FROM Student s WHERE s.userId = :userId AND s.deleted = 0")
    Optional<Student> findByUserId(@Param("userId") Long userId);

    /**
     * 根据班级ID查找学生列表
     */
    @Query("SELECT s FROM Student s WHERE s.classId = :classId AND s.deleted = 0 ORDER BY s.studentNo ASC")
    List<Student> findByClassId(@Param("classId") Long classId);

    /**
     * 分页根据班级ID查找学生列表
     */
    @Query("SELECT s FROM Student s WHERE s.classId = :classId AND s.deleted = 0")
    Page<Student> findByClassId(@Param("classId") Long classId, Pageable pageable);

    /**
     * 根据年级查找学生列表
     */
    @Query("SELECT s FROM Student s WHERE s.grade = :grade AND s.deleted = 0 ORDER BY s.studentNo ASC")
    List<Student> findByGrade(@Param("grade") String grade);

    /**
     * 分页根据年级查找学生列表
     */
    @Query("SELECT s FROM Student s WHERE s.grade = :grade AND s.deleted = 0")
    Page<Student> findByGrade(@Param("grade") String grade, Pageable pageable);

    /**
     * 根据专业查找学生列表
     */
    @Query("SELECT s FROM Student s WHERE s.major = :major AND s.deleted = 0 ORDER BY s.studentNo ASC")
    List<Student> findByMajor(@Param("major") String major);

    /**
     * 根据年级和专业查找学生列表
     */
    @Query("SELECT s FROM Student s WHERE s.grade = :grade AND s.major = :major AND s.deleted = 0 ORDER BY s.studentNo ASC")
    List<Student> findByGradeAndMajor(@Param("grade") String grade, @Param("major") String major);

    /**
     * 根据入学年份查找学生列表
     */
    @Query("SELECT s FROM Student s WHERE s.enrollmentYear = :year AND s.deleted = 0 ORDER BY s.studentNo ASC")
    List<Student> findByEnrollmentYear(@Param("year") Integer year);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找学生
     */
    @Query("SELECT s FROM Student s WHERE " +
           "(:grade IS NULL OR s.grade = :grade) AND " +
           "(:major IS NULL OR s.major = :major) AND " +
           "(:classId IS NULL OR s.classId = :classId) AND " +
           "(:enrollmentYear IS NULL OR s.enrollmentYear = :enrollmentYear) AND " +
           "s.deleted = 0")
    Page<Student> findByMultipleConditions(@Param("grade") String grade,
                                          @Param("major") String major,
                                          @Param("classId") Long classId,
                                          @Param("enrollmentYear") Integer enrollmentYear,
                                          Pageable pageable);

    /**
     * 分页搜索学生
     */
    @Query("SELECT s FROM Student s LEFT JOIN s.user u WHERE " +
           "(s.studentNo LIKE %:keyword% OR " +
           "u.realName LIKE %:keyword% OR " +
           "u.username LIKE %:keyword% OR " +
           "u.email LIKE %:keyword% OR " +
           "u.phone LIKE %:keyword%) AND " +
           "s.deleted = 0")
    Page<Student> searchStudentsPage(@Param("keyword") String keyword, Pageable pageable);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找学生并预加载用户信息
     */
    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.user WHERE s.deleted = 0")
    List<Student> findAllWithUser();

    /**
     * 查找学生并预加载班级信息
     */
    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.schoolClass WHERE s.deleted = 0")
    List<Student> findAllWithClass();

    /**
     * 查找学生并预加载所有关联信息
     */
    @Query("SELECT DISTINCT s FROM Student s " +
           "LEFT JOIN FETCH s.user u " +
           "LEFT JOIN FETCH s.schoolClass sc " +
           "WHERE s.deleted = 0")
    List<Student> findAllWithAssociations();

    /**
     * 分页查找学生并预加载所有关联信息
     */
    @Query("SELECT DISTINCT s FROM Student s " +
           "LEFT JOIN FETCH s.user u " +
           "LEFT JOIN FETCH s.schoolClass sc " +
           "WHERE s.deleted = 0")
    Page<Student> findAllWithAssociations(Pageable pageable);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据年级统计学生数量
     */
    @Query("SELECT s.grade, COUNT(s) FROM Student s WHERE s.deleted = 0 GROUP BY s.grade ORDER BY s.grade")
    List<Object[]> countByGrade();

    /**
     * 根据专业统计学生数量
     */
    @Query("SELECT s.major, COUNT(s) FROM Student s WHERE s.deleted = 0 GROUP BY s.major ORDER BY s.major")
    List<Object[]> countByMajor();

    /**
     * 根据班级统计学生数量
     */
    @Query("SELECT sc.className, COUNT(s) FROM Student s LEFT JOIN s.schoolClass sc WHERE s.deleted = 0 GROUP BY s.classId, sc.className ORDER BY sc.className")
    List<Object[]> countByClass();

    /**
     * 根据入学年份统计学生数量
     */
    @Query("SELECT s.enrollmentYear, COUNT(s) FROM Student s WHERE s.deleted = 0 GROUP BY s.enrollmentYear ORDER BY s.enrollmentYear")
    List<Object[]> countByEnrollmentYear();

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查学号是否存在
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Student s WHERE s.studentNo = :studentNo AND s.deleted = 0")
    boolean existsByStudentNo(@Param("studentNo") String studentNo);

    /**
     * 检查学号是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Student s WHERE s.studentNo = :studentNo AND s.id != :excludeId AND s.deleted = 0")
    boolean existsByStudentNoAndIdNot(@Param("studentNo") String studentNo, @Param("excludeId") Long excludeId);

    /**
     * 检查用户ID是否已关联学生
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Student s WHERE s.userId = :userId AND s.deleted = 0")
    boolean existsByUserId(@Param("userId") Long userId);

    /**
     * 检查用户ID是否已关联学生（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Student s WHERE s.userId = :userId AND s.id != :excludeId AND s.deleted = 0")
    boolean existsByUserIdAndIdNot(@Param("userId") Long userId, @Param("excludeId") Long excludeId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新学生班级
     */
    @Modifying
    @Query("UPDATE Student s SET s.classId = :classId, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id = :studentId")
    int updateClass(@Param("studentId") Long studentId, @Param("classId") Long classId);

    /**
     * 批量更新学生班级
     */
    @Modifying
    @Query("UPDATE Student s SET s.classId = :classId, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id IN :studentIds")
    int batchUpdateClass(@Param("studentIds") List<Long> studentIds, @Param("classId") Long classId);

    /**
     * 更新学生年级
     */
    @Modifying
    @Query("UPDATE Student s SET s.grade = :grade, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id = :studentId")
    int updateGrade(@Param("studentId") Long studentId, @Param("grade") String grade);

    /**
     * 批量更新学生年级
     */
    @Modifying
    @Query("UPDATE Student s SET s.grade = :grade, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id IN :studentIds")
    int batchUpdateGrade(@Param("studentIds") List<Long> studentIds, @Param("grade") String grade);

    // ================================
    // 兼容性方法（为现有Service提供支持）
    // ================================

    /**
     * 根据学号和删除状态查找学生（兼容性方法）
     */
    default Optional<Student> findByStudentNoAndDeleted(String studentNo, Integer deleted) {
        if (deleted == 0) {
            return findByStudentNo(studentNo);
        }
        return Optional.empty();
    }

    /**
     * 根据用户ID和删除状态查找学生（兼容性方法）
     */
    default Optional<Student> findByUserIdAndDeleted(Long userId, Integer deleted) {
        if (deleted == 0) {
            return findByUserId(userId);
        }
        return Optional.empty();
    }

    /**
     * 根据班级ID和删除状态查找学生列表（兼容性方法）
     */
    default List<Student> findByClassIdAndDeletedOrderByStudentNoAsc(Long classId, Integer deleted) {
        if (deleted == 0) {
            return findByClassId(classId);
        }
        return List.of();
    }

    /**
     * 分页根据班级ID和删除状态查找学生列表（兼容性方法）
     */
    default Page<Student> findByClassIdAndDeletedOrderByStudentNoAsc(Long classId, Integer deleted, Pageable pageable) {
        if (deleted == 0) {
            return findByClassId(classId, pageable);
        }
        return Page.empty(pageable);
    }

    /**
     * 根据年级和删除状态查找学生列表（兼容性方法）
     */
    default List<Student> findByGradeAndDeletedOrderByStudentNoAsc(String grade, Integer deleted) {
        if (deleted == 0) {
            return findByGrade(grade);
        }
        return List.of();
    }

    /**
     * 分页根据年级和删除状态查找学生列表（兼容性方法）
     */
    default Page<Student> findByGradeAndDeletedOrderByStudentNoAsc(String grade, Integer deleted, Pageable pageable) {
        if (deleted == 0) {
            return findByGrade(grade, pageable);
        }
        return Page.empty(pageable);
    }

    /**
     * 根据状态和删除状态查找学生列表（兼容性方法）
     */
    default Page<Student> findByStatusAndDeletedOrderByStudentNoAsc(Integer status, Integer deleted, Pageable pageable) {
        if (deleted == 0) {
            return findByMultipleConditions(null, null, null, null, pageable);
        }
        return Page.empty(pageable);
    }

    /**
     * 检查学号是否存在（兼容性方法）
     */
    default boolean existsByStudentNoAndDeleted(String studentNo, Integer deleted) {
        if (deleted == 0) {
            return existsByStudentNo(studentNo);
        }
        return false;
    }

    /**
     * 查找学生详情（兼容性方法）
     */
    @Query("SELECT s, u.username, u.realName, u.email, u.phone, sc.className FROM Student s " +
           "LEFT JOIN User u ON s.userId = u.id " +
           "LEFT JOIN SchoolClass sc ON s.classId = sc.id " +
           "WHERE s.id = :studentId AND s.deleted = 0")
    Optional<Object[]> findStudentWithUser(@Param("studentId") Long studentId);

    /**
     * 根据删除状态查找学生并预加载关联信息（兼容性方法）
     */
    default Page<Student> findByDeletedWithAssociationsOrderByStudentNoAsc(Integer deleted, Pageable pageable) {
        if (deleted == 0) {
            return findAllWithAssociations(pageable);
        }
        return Page.empty(pageable);
    }

    /**
     * 根据搜索条件查找学生并预加载关联信息（兼容性方法）
     */
    default Page<Student> findBySearchWithAssociationsOrderByStudentNoAsc(String keyword, Integer deleted, Pageable pageable) {
        if (deleted == 0) {
            return searchStudentsPage(keyword, pageable);
        }
        return Page.empty(pageable);
    }

    /**
     * 统计学生数量按年级（兼容性方法）
     */
    @Query("SELECT s.grade, COUNT(s) FROM Student s WHERE s.deleted = 0 GROUP BY s.grade ORDER BY s.grade")
    List<Object[]> countStudentsByGrade();

    /**
     * 搜索学生（兼容性方法）
     */
    @Query("SELECT s, u.username, u.realName, u.email, u.phone FROM Student s " +
           "LEFT JOIN User u ON s.userId = u.id AND u.deleted = 0 " +
           "WHERE (s.studentNo LIKE %:keyword% OR u.realName LIKE %:keyword% OR u.username LIKE %:keyword% OR u.email LIKE %:keyword%) " +
           "AND s.deleted = 0 ORDER BY s.studentNo ASC")
    List<Object[]> searchStudents(@Param("keyword") String keyword);

}

