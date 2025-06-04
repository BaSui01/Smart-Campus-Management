package com.campus.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.entity.Student;

/**
 * 学生数据访问层
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Mapper
@Repository
public interface StudentRepository extends BaseMapper<Student> {

    /**
     * 根据学号查找学生
     *
     * @param studentNo 学号
     * @return 学生信息
     */
    @Select("SELECT * FROM tb_student WHERE student_no = #{studentNo} AND deleted = 0")
    Optional<Student> findByStudentNo(@Param("studentNo") String studentNo);

    /**
     * 根据用户ID查找学生
     *
     * @param userId 用户ID
     * @return 学生信息
     */
    @Select("SELECT * FROM tb_student WHERE user_id = #{userId} AND deleted = 0")
    Optional<Student> findByUserId(@Param("userId") Long userId);

    /**
     * 根据班级ID查找学生列表
     *
     * @param classId 班级ID
     * @return 学生列表
     */
    @Select("SELECT * FROM tb_student WHERE class_id = #{classId} AND deleted = 0 ORDER BY student_no ASC")
    List<Student> findByClassId(@Param("classId") Long classId);

    /**
     * 根据年级查找学生列表
     *
     * @param grade 年级
     * @return 学生列表
     */
    @Select("SELECT * FROM tb_student WHERE grade = #{grade} AND deleted = 0 ORDER BY student_no ASC")
    List<Student> findByGrade(@Param("grade") String grade);

    /**
     * 检查学号是否存在
     *
     * @param studentNo 学号
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM tb_student WHERE student_no = #{studentNo} AND deleted = 0")
    boolean existsByStudentNo(@Param("studentNo") String studentNo);

    /**
     * 获取学生及其用户信息
     *
     * @param studentId 学生ID
     * @return 学生信息包含用户详情
     */
    @Select("""
        SELECT s.*, u.username, u.real_name, u.email, u.phone, u.avatar_url
        FROM tb_student s
        LEFT JOIN tb_user u ON s.user_id = u.id AND u.deleted = 0
        WHERE s.id = #{studentId} AND s.deleted = 0
        """)
    Optional<StudentWithUser> findStudentWithUser(@Param("studentId") Long studentId);

    /**
     * 搜索学生
     *
     * @param keyword 关键词
     * @return 学生列表
     */
    @Select("""
        SELECT s.*, u.username, u.real_name, u.email, u.phone
        FROM tb_student s
        LEFT JOIN tb_user u ON s.user_id = u.id AND u.deleted = 0
        WHERE (s.student_no LIKE CONCAT('%', #{keyword}, '%')
               OR u.real_name LIKE CONCAT('%', #{keyword}, '%')
               OR u.username LIKE CONCAT('%', #{keyword}, '%')
               OR u.email LIKE CONCAT('%', #{keyword}, '%'))
        AND s.deleted = 0
        ORDER BY s.student_no ASC
        """)
    List<StudentWithUser> searchStudents(@Param("keyword") String keyword);

    /**
     * 统计学生数量按年级
     *
     * @return 统计结果
     */
    @Select("""
        SELECT grade, COUNT(*) as count
        FROM tb_student
        WHERE deleted = 0
        GROUP BY grade
        ORDER BY grade ASC
        """)
    List<StudentGradeCount> countStudentsByGrade();

    /**
     * 学生年级统计内部类
     */
    class StudentGradeCount {
        private String grade;
        private Long count;

        public String getGrade() { return grade; }
        public void setGrade(String grade) { this.grade = grade; }
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }

    /**
     * 学生与用户信息内部类
     */
    class StudentWithUser extends Student {
        private String username;
        private String realName;
        private String email;
        private String phone;
        private String avatarUrl;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }
}
