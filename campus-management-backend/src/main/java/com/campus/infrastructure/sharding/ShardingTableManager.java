package com.campus.infrastructure.sharding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 分表管理服务
 * 负责自动创建分表、管理分表生命周期
 *
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.shardingsphere.enabled", havingValue = "true", matchIfMissing = true)
public class ShardingTableManager {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 自动创建学生分表
     *
     * @param enrollmentYear 入学年份
     */
    @Transactional
    public void createStudentTable(int enrollmentYear) {
        String tableName = "tb_student_" + enrollmentYear;

        if (tableExists(tableName)) {
            log.info("学生分表已存在: {}", tableName);
            return;
        }

        try {
            String createTableSql = String.format("""
                CREATE TABLE %s (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学生ID',
                    user_id BIGINT NOT NULL UNIQUE COMMENT '关联的用户ID',
                    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
                    grade VARCHAR(50) NOT NULL COMMENT '年级/专业',
                    major VARCHAR(100) COMMENT '专业',
                    class_id BIGINT COMMENT '班级ID',
                    enrollment_year INT NOT NULL COMMENT '入学年份',
                    enrollment_date DATE COMMENT '入学日期',
                    graduation_date DATE COMMENT '毕业日期',
                    student_status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '学生状态',
                    academic_advisor_id BIGINT COMMENT '学术导师ID',
                    emergency_contact VARCHAR(100) COMMENT '紧急联系人',
                    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
                    dormitory_info VARCHAR(200) COMMENT '宿舍信息',
                    scholarship_info TEXT COMMENT '奖学金信息',
                    notes TEXT COMMENT '备注',
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',

                    INDEX idx_user_id (user_id),
                    INDEX idx_student_no (student_no),
                    INDEX idx_class_id (class_id),
                    INDEX idx_enrollment_year (enrollment_year),
                    INDEX idx_student_status (student_status, deleted),
                    INDEX idx_created_at (created_at),

                    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE RESTRICT,
                    FOREIGN KEY (class_id) REFERENCES tb_class(id) ON DELETE SET NULL,
                    FOREIGN KEY (academic_advisor_id) REFERENCES tb_user(id) ON DELETE SET NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='%d年入学学生表'
                """, tableName, enrollmentYear);

            jdbcTemplate.execute(createTableSql);
            log.info("成功创建学生分表: {}", tableName);

        } catch (Exception e) {
            log.error("创建学生分表失败: {}", tableName, e);
            throw new RuntimeException("创建学生分表失败: " + tableName, e);
        }
    }

    /**
     * 自动创建成绩分表
     *
     * @param semester 学期 (格式: 2024-1)
     */
    @Transactional
    public void createGradeTable(String semester) {
        if (!GradeTableShardingAlgorithm.isValidSemester(semester)) {
            throw new IllegalArgumentException("无效的学期格式: " + semester);
        }

        String tableName = "tb_grade_" + semester.replace("-", "_");

        if (tableExists(tableName)) {
            log.info("成绩分表已存在: {}", tableName);
            return;
        }

        try {
            String createTableSql = String.format("""
                CREATE TABLE %s (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '成绩ID',
                    student_id BIGINT NOT NULL COMMENT '学生ID',
                    course_id BIGINT NOT NULL COMMENT '课程ID',
                    exam_type VARCHAR(20) NOT NULL COMMENT '考试类型',
                    score DECIMAL(5,2) NOT NULL COMMENT '成绩分数',
                    grade_level VARCHAR(10) COMMENT '成绩等级',
                    semester VARCHAR(20) NOT NULL COMMENT '学期',
                    academic_year VARCHAR(20) NOT NULL COMMENT '学年',
                    exam_date DATE COMMENT '考试日期',
                    teacher_id BIGINT COMMENT '任课教师ID',
                    remarks TEXT COMMENT '备注',
                    is_retake TINYINT DEFAULT 0 COMMENT '是否补考',
                    retake_score DECIMAL(5,2) COMMENT '补考成绩',
                    final_score DECIMAL(5,2) COMMENT '最终成绩',
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',

                    INDEX idx_student_id (student_id),
                    INDEX idx_course_id (course_id),
                    INDEX idx_semester (semester),
                    INDEX idx_student_course (student_id, course_id),
                    INDEX idx_exam_type (exam_type, deleted),
                    INDEX idx_score (score),
                    INDEX idx_created_at (created_at),

                    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE RESTRICT,
                    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='%s学期成绩表'
                """, tableName, semester);

            jdbcTemplate.execute(createTableSql);
            log.info("成功创建成绩分表: {}", tableName);

        } catch (Exception e) {
            log.error("创建成绩分表失败: {}", tableName, e);
            throw new RuntimeException("创建成绩分表失败: " + tableName, e);
        }
    }

    /**
     * 检查表是否存在
     *
     * @param tableName 表名
     * @return 是否存在
     */
    public boolean tableExists(String tableName) {
        String sql = """
            SELECT COUNT(*) FROM information_schema.tables
            WHERE table_schema = DATABASE() AND table_name = ?
            """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
        return count != null && count > 0;
    }

    /**
     * 获取所有分表信息
     *
     * @return 分表信息列表
     */
    public List<Map<String, Object>> getAllShardingTables() {
        String sql = """
            SELECT
                table_name,
                table_comment,
                table_rows,
                data_length,
                index_length,
                create_time,
                update_time
            FROM information_schema.tables
            WHERE table_schema = DATABASE()
            AND (table_name LIKE 'tb_student_20%'
                 OR table_name LIKE 'tb_grade_20%'
                 OR table_name LIKE 'tb_attendance_20%')
            ORDER BY table_name
            """;
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * 获取分表统计信息
     *
     * @return 统计信息
     */
    public Map<String, Object> getShardingStatistics() {
        String sql = """
            SELECT
                COUNT(*) as total_sharding_tables,
                SUM(table_rows) as total_rows,
                SUM(data_length) as total_data_size,
                SUM(index_length) as total_index_size
            FROM information_schema.tables
            WHERE table_schema = DATABASE()
            AND (table_name LIKE 'tb_student_20%'
                 OR table_name LIKE 'tb_grade_20%'
                 OR table_name LIKE 'tb_attendance_20%')
            """;
        return jdbcTemplate.queryForMap(sql);
    }

    /**
     * 删除过期的分表
     *
     * @param tableName 表名
     */
    @Transactional
    public void dropTable(String tableName) {
        if (!tableExists(tableName)) {
            log.warn("表不存在，无法删除: {}", tableName);
            return;
        }

        try {
            String dropSql = "DROP TABLE " + tableName;
            jdbcTemplate.execute(dropSql);
            log.info("成功删除分表: {}", tableName);
        } catch (Exception e) {
            log.error("删除分表失败: {}", tableName, e);
            throw new RuntimeException("删除分表失败: " + tableName, e);
        }
    }
}
