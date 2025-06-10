package com.campus.domain.repository;

import com.campus.domain.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 课程Repository接口
 * 提供课程相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface CourseRepository extends BaseRepository<Course> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据课程代码查找课程
     */
    @Query("SELECT c FROM Course c WHERE c.courseCode = :courseCode AND c.deleted = 0")
    Optional<Course> findByCourseCode(@Param("courseCode") String courseCode);

    /**
     * 根据课程名称模糊查找课程
     */
    @Query("SELECT c FROM Course c WHERE c.courseName LIKE %:courseName% AND c.deleted = 0 ORDER BY c.courseName")
    List<Course> findByCourseNameContaining(@Param("courseName") String courseName);

    /**
     * 分页根据课程名称模糊查找课程
     */
    @Query("SELECT c FROM Course c WHERE c.courseName LIKE %:courseName% AND c.deleted = 0")
    Page<Course> findByCourseNameContaining(@Param("courseName") String courseName, Pageable pageable);

    /**
     * 根据部门ID查找课程列表
     */
    @Query("SELECT c FROM Course c WHERE c.departmentId = :departmentId AND c.deleted = 0 ORDER BY c.courseName")
    List<Course> findByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 分页根据部门ID查找课程列表
     */
    @Query("SELECT c FROM Course c WHERE c.departmentId = :departmentId AND c.deleted = 0")
    Page<Course> findByDepartmentId(@Param("departmentId") Long departmentId, Pageable pageable);

    /**
     * 根据教师ID查找课程列表
     */
    @Query("SELECT c FROM Course c WHERE c.teacherId = :teacherId AND c.deleted = 0 ORDER BY c.courseName")
    List<Course> findByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * 分页根据教师ID查找课程列表
     */
    @Query("SELECT c FROM Course c WHERE c.teacherId = :teacherId AND c.deleted = 0")
    Page<Course> findByTeacherId(@Param("teacherId") Long teacherId, Pageable pageable);

    /**
     * 根据学期查找课程列表
     */
    @Query("SELECT c FROM Course c WHERE c.semester = :semester AND c.deleted = 0 ORDER BY c.courseName")
    List<Course> findBySemester(@Param("semester") String semester);

    /**
     * 根据课程类型查找课程列表
     */
    @Query("SELECT c FROM Course c WHERE c.courseType = :courseType AND c.deleted = 0 ORDER BY c.courseName")
    List<Course> findByCourseType(@Param("courseType") String courseType);

    /**
     * 根据学分查找课程列表
     */
    @Query("SELECT c FROM Course c WHERE c.credits = :credits AND c.deleted = 0 ORDER BY c.courseName")
    List<Course> findByCredits(@Param("credits") Integer credits);

    /**
     * 根据学分范围查找课程
     */
    @Query("SELECT c FROM Course c WHERE c.credits BETWEEN :minCredits AND :maxCredits AND c.deleted = 0 ORDER BY c.credits ASC")
    List<Course> findByCreditsBetween(@Param("minCredits") Integer minCredits, @Param("maxCredits") Integer maxCredits);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找课程
     */
    @Query("SELECT c FROM Course c WHERE " +
           "(:courseName IS NULL OR c.courseName LIKE %:courseName%) AND " +
           "(:courseType IS NULL OR c.courseType = :courseType) AND " +
           "(:semester IS NULL OR c.semester = :semester) AND " +
           "(:departmentId IS NULL OR c.departmentId = :departmentId) AND " +
           "(:teacherId IS NULL OR c.teacherId = :teacherId) AND " +
           "c.deleted = 0")
    Page<Course> findByMultipleConditions(@Param("courseName") String courseName,
                                         @Param("courseType") String courseType,
                                         @Param("semester") String semester,
                                         @Param("departmentId") Long departmentId,
                                         @Param("teacherId") Long teacherId,
                                         Pageable pageable);

    /**
     * 搜索课程（根据课程名称、课程代码、描述等关键词）
     */
    @Query("SELECT c FROM Course c WHERE " +
           "(c.courseName LIKE %:keyword% OR " +
           "c.courseCode LIKE %:keyword% OR " +
           "c.description LIKE %:keyword%) AND " +
           "c.deleted = 0 ORDER BY c.courseName")
    List<Course> searchCourses(@Param("keyword") String keyword);

    /**
     * 分页搜索课程
     */
    @Query("SELECT c FROM Course c WHERE " +
           "(c.courseName LIKE %:keyword% OR " +
           "c.courseCode LIKE %:keyword% OR " +
           "c.description LIKE %:keyword%) AND " +
           "c.deleted = 0")
    Page<Course> searchCourses(@Param("keyword") String keyword, Pageable pageable);

    // ================================
    // 关联查询方法
    // ================================

    /**
     * 查找课程并预加载教师信息
     */
    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.teacher WHERE c.deleted = 0")
    List<Course> findAllWithTeacher();

    /**
     * 查找课程并预加载部门信息
     */
    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.department WHERE c.deleted = 0")
    List<Course> findAllWithDepartment();

    /**
     * 查找课程并预加载所有关联信息
     */
    @Query("SELECT DISTINCT c FROM Course c " +
           "LEFT JOIN FETCH c.teacher t " +
           "LEFT JOIN FETCH c.department d " +
           "WHERE c.deleted = 0")
    List<Course> findAllWithAssociations();

    // ================================
    // 选课相关查询
    // ================================

    /**
     * 查找有选课学生的课程
     */
    @Query("SELECT DISTINCT c FROM Course c " +
           "INNER JOIN CourseSelection cs ON c.id = cs.courseId AND cs.deleted = 0 " +
           "WHERE c.deleted = 0 ORDER BY c.courseName")
    List<Course> findCoursesWithSelections();

    /**
     * 查找没有选课学生的课程
     */
    @Query("SELECT c FROM Course c " +
           "LEFT JOIN CourseSelection cs ON c.id = cs.courseId AND cs.deleted = 0 " +
           "WHERE c.deleted = 0 AND cs.id IS NULL ORDER BY c.courseName")
    List<Course> findCoursesWithoutSelections();

    /**
     * 根据学生ID查找已选课程
     */
    @Query("SELECT c FROM Course c " +
           "INNER JOIN CourseSelection cs ON c.id = cs.courseId " +
           "WHERE cs.studentId = :studentId AND cs.deleted = 0 AND c.deleted = 0 " +
           "ORDER BY c.courseName")
    List<Course> findCoursesByStudentId(@Param("studentId") Long studentId);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据课程类型统计课程数量
     */
    @Query("SELECT c.courseType, COUNT(c) FROM Course c WHERE c.deleted = 0 GROUP BY c.courseType ORDER BY COUNT(c) DESC")
    List<Object[]> countByCourseType();

    /**
     * 根据学期统计课程数量
     */
    @Query("SELECT c.semester, COUNT(c) FROM Course c WHERE c.deleted = 0 GROUP BY c.semester ORDER BY c.semester DESC")
    List<Object[]> countBySemester();

    /**
     * 根据部门统计课程数量
     */
    @Query("SELECT d.deptName, COUNT(c) FROM Course c LEFT JOIN c.department d WHERE c.deleted = 0 GROUP BY c.departmentId, d.deptName ORDER BY COUNT(c) DESC")
    List<Object[]> countByDepartment();

    /**
     * 根据教师统计课程数量
     */
    @Query("SELECT u.realName, COUNT(c) FROM Course c LEFT JOIN c.teacher u WHERE c.deleted = 0 GROUP BY c.teacherId, u.realName ORDER BY COUNT(c) DESC")
    List<Object[]> countByTeacher();

    /**
     * 根据学分统计课程数量
     */
    @Query("SELECT c.credits, COUNT(c) FROM Course c WHERE c.deleted = 0 GROUP BY c.credits ORDER BY c.credits")
    List<Object[]> countByCredits();

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查课程代码是否存在
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Course c WHERE c.courseCode = :courseCode AND c.deleted = 0")
    boolean existsByCourseCode(@Param("courseCode") String courseCode);

    /**
     * 检查课程代码是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Course c WHERE c.courseCode = :courseCode AND c.id != :excludeId AND c.deleted = 0")
    boolean existsByCourseCodeAndIdNot(@Param("courseCode") String courseCode, @Param("excludeId") Long excludeId);

    /**
     * 检查课程名称是否存在
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Course c WHERE c.courseName = :courseName AND c.deleted = 0")
    boolean existsByCourseName(@Param("courseName") String courseName);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新课程教师
     */
    @Modifying
    @Query("UPDATE Course c SET c.teacherId = :teacherId, c.updatedAt = CURRENT_TIMESTAMP WHERE c.id = :courseId")
    int updateTeacher(@Param("courseId") Long courseId, @Param("teacherId") Long teacherId);

    /**
     * 批量更新课程教师
     */
    @Modifying
    @Query("UPDATE Course c SET c.teacherId = :teacherId, c.updatedAt = CURRENT_TIMESTAMP WHERE c.id IN :courseIds")
    int batchUpdateTeacher(@Param("courseIds") List<Long> courseIds, @Param("teacherId") Long teacherId);

    /**
     * 更新课程学期
     */
    @Modifying
    @Query("UPDATE Course c SET c.semester = :semester, c.updatedAt = CURRENT_TIMESTAMP WHERE c.id = :courseId")
    int updateSemester(@Param("courseId") Long courseId, @Param("semester") String semester);

    /**
     * 批量更新课程学期
     */
    @Modifying
    @Query("UPDATE Course c SET c.semester = :semester, c.updatedAt = CURRENT_TIMESTAMP WHERE c.id IN :courseIds")
    int batchUpdateSemester(@Param("courseIds") List<Long> courseIds, @Param("semester") String semester);

    // ================================
    // 兼容性方法（为现有Service提供支持）
    // ================================

    /**
     * 根据课程代码和删除状态查找课程（兼容性方法）
     */
    default Optional<Course> findByCourseCodeAndDeleted(String courseCode, Integer deleted) {
        if (deleted == 0) {
            return findByCourseCode(courseCode);
        }
        return Optional.empty();
    }

    /**
     * 根据课程名称模糊查询和删除状态查找课程（兼容性方法）
     */
    default List<Course> findByCourseNameContainingAndDeleted(String courseName, Integer deleted) {
        if (deleted == 0) {
            return findByCourseNameContaining(courseName);
        }
        return List.of();
    }

    /**
     * 分页根据课程名称模糊查询和删除状态查找课程（兼容性方法）
     */
    default Page<Course> findByCourseNameContainingAndDeleted(String courseName, Integer deleted, Pageable pageable) {
        if (deleted == 0) {
            return findByCourseNameContaining(courseName, pageable);
        }
        return Page.empty(pageable);
    }

    /**
     * 根据删除状态查找课程（兼容性方法）
     */
    default List<Course> findByDeletedOrderByCourseCodeAsc(Integer deleted) {
        if (deleted == 0) {
            return findAllWithAssociations();
        }
        return List.of();
    }

    /**
     * 分页根据删除状态查找课程（兼容性方法）
     */
    default Page<Course> findByDeletedOrderByCourseCodeAsc(Integer deleted, Pageable pageable) {
        if (deleted == 0) {
            return findAll(pageable);
        }
        return Page.empty(pageable);
    }

    /**
     * 检查课程代码是否存在（兼容性方法）
     */
    default boolean existsByCourseCodeAndDeleted(String courseCode, Integer deleted) {
        if (deleted == 0) {
            return existsByCourseCode(courseCode);
        }
        return false;
    }

    /**
     * 搜索课程（兼容性方法）
     */
    default List<Course> searchCourses(String keyword, String courseType, String semester, Integer deleted) {
        if (deleted == 0) {
            return searchCourses(keyword);
        }
        return List.of();
    }

    /**
     * 分页搜索课程（兼容性方法）
     */
    default Page<Course> searchCourses(String keyword, String courseType, String semester, Integer deleted, Pageable pageable) {
        if (deleted == 0) {
            return searchCourses(keyword, pageable);
        }
        return Page.empty(pageable);
    }

    /**
     * 根据院系ID和删除状态查找课程（兼容性方法）
     */
    default List<Course> findByDepartmentIdAndDeleted(Long departmentId, Integer deleted) {
        if (deleted == 0) {
            return findByDepartmentId(departmentId);
        }
        return List.of();
    }

    /**
     * 根据教师ID和删除状态查找课程（兼容性方法）
     */
    default List<Course> findByTeacherIdAndDeleted(Long teacherId, Integer deleted) {
        if (deleted == 0) {
            return findByTeacherId(teacherId);
        }
        return List.of();
    }

    /**
     * 根据学期和删除状态查找课程（兼容性方法）
     */
    default List<Course> findBySemesterAndDeleted(String semester, Integer deleted) {
        if (deleted == 0) {
            return findBySemester(semester);
        }
        return List.of();
    }

    /**
     * 根据课程类型和删除状态查找课程（兼容性方法）
     */
    default List<Course> findByCourseTypeAndDeleted(String courseType, Integer deleted) {
        if (deleted == 0) {
            return findByCourseType(courseType);
        }
        return List.of();
    }

    /**
     * 统计课程数量按类型（兼容性方法）
     */
    @Query("SELECT c.courseType, COUNT(c) FROM Course c WHERE c.deleted = 0 GROUP BY c.courseType ORDER BY c.courseType")
    List<Object[]> countCoursesByType();

    /**
     * 统计课程数量按学期（兼容性方法）
     */
    @Query("SELECT c.semester, COUNT(c) FROM Course c WHERE c.deleted = 0 GROUP BY c.semester ORDER BY c.semester")
    List<Object[]> countCoursesBySemester();

    /**
     * 查找课程详情（兼容性方法）
     */
    @Query("SELECT c, u.realName, d.deptName FROM Course c " +
           "LEFT JOIN User u ON c.teacherId = u.id " +
           "LEFT JOIN Department d ON c.departmentId = d.id " +
           "WHERE c.id = :courseId AND c.deleted = 0")
    Optional<Object[]> findCourseDetailById(@Param("courseId") Long courseId);

    // ================================
    // CacheWarmupServiceImpl需要的方法
    // ================================

    /**
     * 根据状态和删除状态查找课程
     */
    @Query("SELECT c FROM Course c WHERE c.status = :status AND c.deleted = :deleted ORDER BY c.courseName")
    List<Course> findByStatusAndDeleted(@Param("status") Integer status, @Param("deleted") Integer deleted);

    /**
     * 根据状态和删除状态统计课程数量
     */
    @Query("SELECT COUNT(c) FROM Course c WHERE c.status = :status AND c.deleted = :deleted")
    long countByStatusAndDeleted(@Param("status") Integer status, @Param("deleted") Integer deleted);

}
