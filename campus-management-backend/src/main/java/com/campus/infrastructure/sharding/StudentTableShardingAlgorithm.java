package com.campus.infrastructure.sharding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生表分片算法
 * 根据入学年份进行分表，支持Java 21的现代特性
 *
 * @author Campus Management System
 * @since 2025-06-20
 */
@Slf4j
@ConditionalOnClass(name = "org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingAlgorithm")
public class StudentTableShardingAlgorithm {

    // ShardingSphere相关方法已移除，仅保留工具方法
    // 如需使用分片功能，请确保ShardingSphere依赖可用

    /**
     * 根据入学年份获取目标表名
     *
     * @param enrollmentYear 入学年份
     * @return 目标表名
     */
    public static String getTargetTableByYear(Integer enrollmentYear) {
        if (!isValidEnrollmentYear(enrollmentYear)) {
            throw new IllegalArgumentException(
                String.format("无效的入学年份: %s, 年份范围: 1900-2100", enrollmentYear));
        }
        return "tb_student_" + enrollmentYear;
    }

    /**
     * 验证入学年份是否有效
     *
     * @param enrollmentYear 入学年份
     * @return 是否有效
     */
    public static boolean isValidEnrollmentYear(Integer enrollmentYear) {
        if (enrollmentYear == null) {
            return false;
        }
        int currentYear = LocalDate.now().getYear();
        return enrollmentYear >= 1900 && enrollmentYear <= currentYear + 10; // 允许未来10年的预录取
    }

    /**
     * 获取指定年份范围内的所有学生表名
     *
     * @param startYear 开始年份
     * @param endYear 结束年份
     * @return 表名列表
     */
    public static List<String> getTableNamesByYearRange(int startYear, int endYear) {
        if (startYear > endYear) {
            throw new IllegalArgumentException("开始年份不能大于结束年份");
        }

        return java.util.stream.IntStream.rangeClosed(startYear, endYear)
                .filter(year -> isValidEnrollmentYear(year))
                .mapToObj(year -> "tb_student_" + year)
                .collect(Collectors.toList());
    }

    /**
     * 从可用表名中找到最接近指定年份的表
     *
     * @param availableTableNames 可用表名集合
     * @param targetYear 目标年份
     * @return 最接近的表名
     */
    public static String findNearestTable(Collection<String> availableTableNames, int targetYear) {
        return availableTableNames.stream()
                .filter(tableName -> tableName.startsWith("tb_student_"))
                .map(tableName -> {
                    try {
                        String yearStr = tableName.substring("tb_student_".length());
                        int year = Integer.parseInt(yearStr);
                        return new TableYearPair(tableName, year);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(pair -> pair != null)
                .min((pair1, pair2) -> {
                    int diff1 = Math.abs(pair1.year() - targetYear);
                    int diff2 = Math.abs(pair2.year() - targetYear);
                    return Integer.compare(diff1, diff2);
                })
                .map(TableYearPair::tableName)
                .orElse(availableTableNames.iterator().next());
    }

    /**
     * 表名和年份的记录类（Java 21特性）
     */
    public record TableYearPair(String tableName, int year) {}

    /**
     * 获取当前年份的学生表名
     *
     * @return 当前年份的学生表名
     */
    public static String getCurrentYearTable() {
        return getTargetTableByYear(LocalDate.now().getYear());
    }

    /**
     * 检查表名是否为学生分表
     *
     * @param tableName 表名
     * @return 是否为学生分表
     */
    public static boolean isStudentTable(String tableName) {
        if (tableName == null || !tableName.startsWith("tb_student_")) {
            return false;
        }

        try {
            String yearStr = tableName.substring("tb_student_".length());
            int year = Integer.parseInt(yearStr);
            return isValidEnrollmentYear(year);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
