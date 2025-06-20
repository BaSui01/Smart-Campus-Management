package com.campus.infrastructure.sharding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 成绩表分片算法
 * 根据学期进行分表
 *
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
@ConditionalOnClass(name = "org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingAlgorithm")
public class GradeTableShardingAlgorithm {
    
    // ShardingSphere相关方法已移除，仅保留工具方法
    // 如需使用分片功能，请确保ShardingSphere依赖可用
    
    /**
     * 验证学期格式
     * 
     * @param semester 学期字符串
     * @return 是否有效
     */
    public static boolean isValidSemester(String semester) {
        return semester != null && semester.matches("\\d{4}-[12]");
    }
    
    /**
     * 解析学期年份
     * 
     * @param semester 学期字符串 (如: "2024-1")
     * @return 年份
     */
    public static int parseYear(String semester) {
        if (!isValidSemester(semester)) {
            throw new IllegalArgumentException("无效的学期格式: " + semester);
        }
        return Integer.parseInt(semester.substring(0, 4));
    }
    
    /**
     * 解析学期序号
     *
     * @param semester 学期字符串 (如: "2024-1")
     * @return 学期序号 (1 或 2)
     */
    public static int parseSemesterNumber(String semester) {
        if (!isValidSemester(semester)) {
            throw new IllegalArgumentException("无效的学期格式: " + semester);
        }
        return Integer.parseInt(semester.substring(5));
    }

    /**
     * 根据学期获取目标表名
     *
     * @param semester 学期 (格式: 2024-1)
     * @return 目标表名
     */
    public static String getTargetTableBySemester(String semester) {
        if (!isValidSemester(semester)) {
            throw new IllegalArgumentException(
                String.format("无效的学期格式: %s, 正确格式: YYYY-S (如: 2024-1)", semester));
        }
        return "tb_grade_" + semester.replace("-", "_");
    }

    /**
     * 获取当前学期
     *
     * @return 当前学期字符串
     */
    public static String getCurrentSemester() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        // 根据月份判断学期：1-7月为第二学期，8-12月为第一学期
        int semester = (month >= 8) ? 1 : 2;

        // 如果是第二学期，年份要减1（因为学年跨年）
        if (semester == 2) {
            year--;
        }

        return year + "-" + semester;
    }

    /**
     * 获取指定年份范围内的所有成绩表名
     *
     * @param startYear 开始年份
     * @param endYear 结束年份
     * @return 表名列表
     */
    public static List<String> getTableNamesByYearRange(int startYear, int endYear) {
        if (startYear > endYear) {
            throw new IllegalArgumentException("开始年份不能大于结束年份");
        }

        return IntStream.rangeClosed(startYear, endYear)
                .boxed()
                .flatMap(year -> List.of(year + "-1", year + "-2").stream())
                .filter(GradeTableShardingAlgorithm::isValidSemester)
                .map(GradeTableShardingAlgorithm::getTargetTableBySemester)
                .collect(Collectors.toList());
    }

    /**
     * 从可用表名中找到最接近指定学期的表
     *
     * @param availableTableNames 可用表名集合
     * @param targetSemester 目标学期
     * @return 最接近的表名
     */
    public static String findNearestTable(Collection<String> availableTableNames, String targetSemester) {
        if (!isValidSemester(targetSemester)) {
            return availableTableNames.iterator().next();
        }

        int targetYear = parseYear(targetSemester);
        int targetSemesterNum = parseSemesterNumber(targetSemester);

        return availableTableNames.stream()
                .filter(tableName -> tableName.startsWith("tb_grade_"))
                .filter(tableName -> {
                    try {
                        String semesterStr = tableName.substring("tb_grade_".length()).replace("_", "-");
                        return isValidSemester(semesterStr);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .min((table1, table2) -> {
                    String semester1 = table1.substring("tb_grade_".length()).replace("_", "-");
                    String semester2 = table2.substring("tb_grade_".length()).replace("_", "-");

                    int year1 = parseYear(semester1);
                    int semesterNum1 = parseSemesterNumber(semester1);
                    int year2 = parseYear(semester2);
                    int semesterNum2 = parseSemesterNumber(semester2);

                    int distance1 = Math.abs((year1 - targetYear) * 2 + (semesterNum1 - targetSemesterNum));
                    int distance2 = Math.abs((year2 - targetYear) * 2 + (semesterNum2 - targetSemesterNum));

                    return Integer.compare(distance1, distance2);
                })
                .orElse(availableTableNames.iterator().next());
    }

    /**
     * 检查表名是否为成绩分表
     *
     * @param tableName 表名
     * @return 是否为成绩分表
     */
    public static boolean isGradeTable(String tableName) {
        if (tableName == null || !tableName.startsWith("tb_grade_")) {
            return false;
        }

        try {
            String semesterStr = tableName.substring("tb_grade_".length()).replace("_", "-");
            return isValidSemester(semesterStr);
        } catch (Exception e) {
            return false;
        }
    }
}
