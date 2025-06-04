 package com.campus.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.SchoolClass;

/**
 * 班级数据访问层
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Mapper
@Repository
public interface SchoolClassRepository extends BaseMapper<SchoolClass> {

    /**
     * 根据班级代码查找班级
     *
     * @param classCode 班级代码
     * @return 班级信息
     */
    @Select("SELECT * FROM tb_class WHERE class_code = #{classCode} AND deleted = 0")
    Optional<SchoolClass> findByClassCode(@Param("classCode") String classCode);

    /**
     * 根据年级查找班级列表
     *
     * @param grade 年级
     * @return 班级列表
     */
    @Select("SELECT * FROM tb_class WHERE grade = #{grade} AND deleted = 0 ORDER BY class_code ASC")
    List<SchoolClass> findByGrade(@Param("grade") String grade);

    /**
     * 根据部门ID查找班级列表
     *
     * @param departmentId 部门ID
     * @return 班级列表
     */
    @Select("SELECT * FROM tb_class WHERE department_id = #{departmentId} AND deleted = 0 ORDER BY grade ASC, class_code ASC")
    List<SchoolClass> findByDepartmentId(@Param("departmentId") Long departmentId);

    /**
     * 根据班主任ID查找班级列表
     *
     * @param headTeacherId 班主任ID
     * @return 班级列表
     */
    @Select("SELECT * FROM tb_class WHERE head_teacher_id = #{headTeacherId} AND deleted = 0 ORDER BY grade ASC, class_code ASC")
    List<SchoolClass> findByHeadTeacherId(@Param("headTeacherId") Long headTeacherId);

    /**
     * 检查班级代码是否存在
     *
     * @param classCode 班级代码
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_class WHERE class_code = #{classCode} AND deleted = 0")
    boolean existsByClassCode(@Param("classCode") String classCode);

    /**
     * 获取班级详情（包含班主任信息和学生数量）
     */
    @Select("""
        SELECT c.*, t.real_name as head_teacher_name,
        (SELECT COUNT(*) FROM tb_student s WHERE s.class_id = c.id AND s.deleted = 0) as student_count
        FROM tb_class c
        LEFT JOIN tb_user t ON c.head_teacher_id = t.id AND t.deleted = 0
        WHERE c.id = #{classId} AND c.deleted = 0
        """)
    Optional<ClassDetail> findClassDetailById(@Param("classId") Long classId);

    /**
     * 统计班级数量按年级
     */
    @Select("""
        SELECT grade, COUNT(*) as count
        FROM tb_class
        WHERE deleted = 0
        GROUP BY grade
        ORDER BY grade ASC
        """)
    List<ClassGradeCount> countClassesByGrade();

    /**
     * 班级年级统计内部类
     */
    class ClassGradeCount {
        private String grade;
        private Long count;

        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }

    /**
     * 班级详情内部类
     */
    class ClassDetail extends SchoolClass {
        private String headTeacherName;
        private Integer actualStudentCount;

        public String getHeadTeacherName() { return headTeacherName; }
        public void setHeadTeacherName(String headTeacherName) { this.headTeacherName = headTeacherName; }
        public Integer getActualStudentCount() { return actualStudentCount; }
        public void setActualStudentCount(Integer actualStudentCount) { this.actualStudentCount = actualStudentCount; }
    }
}
