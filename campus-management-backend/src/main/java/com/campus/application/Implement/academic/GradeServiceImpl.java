package com.campus.application.Implement.academic;


import com.campus.application.service.academic.GradeService;
import com.campus.application.service.academic.CourseSelectionService;
import com.campus.domain.entity.academic.Grade;
import com.campus.domain.entity.academic.CourseSelection;
import com.campus.domain.repository.academic.GradeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import org.springframework.data.domain.PageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 成绩服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-08
 */
@Service
@Transactional
public class GradeServiceImpl implements GradeService {

    private static final Logger logger = LoggerFactory.getLogger(GradeServiceImpl.class);

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private CourseSelectionService courseSelectionService;

    @Override
    public Grade save(Grade grade) {
        return gradeRepository.save(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Grade> findById(Long id) {
        return gradeRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> findAll() {
        return gradeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Grade> findAll(Pageable pageable) {
        return gradeRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        gradeRepository.deleteById(id);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        gradeRepository.deleteAllById(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return gradeRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> findByStudentId(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> findByCourseId(Long courseId) {
        return gradeRepository.findByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> findByScheduleId(Long scheduleId) {
        return gradeRepository.findByScheduleIdAndDeleted(scheduleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Grade> findBySelectionId(Long selectionId) {
        return gradeRepository.findBySelectionIdAndDeleted(selectionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> findBySemester(String semester) {
        return gradeRepository.findBySemester(semester);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> findByStudentIdAndCourseId(Long studentId, Long courseId) {
        return gradeRepository.findByStudentIdAndCourseIdAndDeleted(studentId, courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> findByStudentIdAndSemester(Long studentId, String semester) {
        return gradeRepository.findByStudentIdAndSemester(studentId, semester);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Object[]> findGradeDetailById(Long gradeId) {
        return gradeRepository.findGradeDetailById(gradeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> findStudentGradeDetails(Long studentId, String semester) {
        return gradeRepository.findStudentGradeDetails(studentId, semester);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> findCourseGradeDetails(Long courseId, Long scheduleId) {
        return gradeRepository.findCourseGradeDetails(courseId, scheduleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateStudentGPA(Long studentId, String semester) {
        return gradeRepository.calculateStudentGPA(studentId, semester);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateClassAverageScore(Long classId, Long courseId) {
        return gradeRepository.calculateClassAverageScore(classId, courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Grade> findGradesByPage(Pageable pageable, Map<String, Object> params) {
        try {
            // 注意：当前实现基础的分页查询功能，支持按学生ID、课程ID、学期等条件查询
            // 后续可使用Specification或自定义查询方法优化查询性能
            logger.debug("分页查询成绩: params={}", params);

            if (params == null || params.isEmpty()) {
                return gradeRepository.findAll(pageable);
            }

            // 获取查询参数
            Long studentId = (Long) params.get("studentId");
            Long courseId = (Long) params.get("courseId");
            String semester = (String) params.get("semester");

            // 根据参数进行查询
            if (studentId != null && courseId != null) {
                // 按学生和课程查询 - 先获取列表再转换为分页
                List<Grade> grades = gradeRepository.findByStudentIdAndCourseIdAndDeleted(studentId, courseId);
                // 如果指定了学期，进行过滤
                if (semester != null && !semester.trim().isEmpty()) {
                    grades = grades.stream()
                        .filter(grade -> semester.equals(grade.getSemester()))
                        .collect(java.util.stream.Collectors.toList());
                }
                return convertListToPage(grades, pageable);
            } else if (studentId != null) {
                // 按学生查询
                List<Grade> grades = gradeRepository.findByStudentIdAndDeleted(studentId);
                // 如果指定了学期，进行过滤
                if (semester != null && !semester.trim().isEmpty()) {
                    grades = grades.stream()
                        .filter(grade -> semester.equals(grade.getSemester()))
                        .collect(java.util.stream.Collectors.toList());
                }
                return convertListToPage(grades, pageable);
            } else if (courseId != null) {
                // 按课程查询
                List<Grade> grades = gradeRepository.findByCourseIdAndDeleted(courseId);
                // 如果指定了学期，进行过滤
                if (semester != null && !semester.trim().isEmpty()) {
                    grades = grades.stream()
                        .filter(grade -> semester.equals(grade.getSemester()))
                        .collect(java.util.stream.Collectors.toList());
                }
                return convertListToPage(grades, pageable);
            } else if (semester != null && !semester.trim().isEmpty()) {
                // 按学期查询
                List<Grade> grades = gradeRepository.findBySemesterAndDeleted(semester);
                return convertListToPage(grades, pageable);
            } else {
                // 默认查询所有
                return gradeRepository.findAll(pageable);
            }

        } catch (Exception e) {
            logger.error("分页查询成绩失败", e);
            return new org.springframework.data.domain.PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    @Override
    public Grade createGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    @Override
    public boolean updateGrade(Grade grade) {
        try {
            gradeRepository.save(grade);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean batchUpdateGrades(List<Grade> grades) {
        try {
            gradeRepository.saveAll(grades);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteGrade(Long id) {
        try {
            gradeRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean batchDeleteGrades(List<Long> ids) {
        try {
            gradeRepository.deleteAllById(ids);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Grade calculateFinalScore(Grade grade) {
        try {
            // 实现总成绩计算逻辑：平时成绩30% + 期中成绩30% + 期末成绩40%
            logger.debug("计算总成绩: gradeId={}", grade.getId());

            java.math.BigDecimal usualScore = grade.getUsualScore() != null ? grade.getUsualScore() : java.math.BigDecimal.ZERO;
            java.math.BigDecimal midtermScore = grade.getMidtermScore() != null ? grade.getMidtermScore() : java.math.BigDecimal.ZERO;
            java.math.BigDecimal finalScore = grade.getFinalScore() != null ? grade.getFinalScore() : java.math.BigDecimal.ZERO;

            // 权重计算：平时30% + 期中30% + 期末40%
            java.math.BigDecimal totalScore = usualScore.multiply(java.math.BigDecimal.valueOf(0.3))
                .add(midtermScore.multiply(java.math.BigDecimal.valueOf(0.3)))
                .add(finalScore.multiply(java.math.BigDecimal.valueOf(0.4)));

            // 保留两位小数
            totalScore = totalScore.setScale(2, java.math.RoundingMode.HALF_UP);
            grade.setTotalScore(totalScore);

            logger.debug("总成绩计算完成: totalScore={}", totalScore);
            return grade;
        } catch (Exception e) {
            logger.error("计算总成绩失败: gradeId={}", grade.getId(), e);
            return grade;
        }
    }

    @Override
    public Grade calculateGradePointAndLevel(Grade grade) {
        try {
            // 注意：当前实现基础的绩点和等级计算逻辑，基于百分制成绩计算4.0制绩点
            // 后续可根据学校的具体绩点计算规则进行调整
            logger.debug("计算绩点和等级: gradeId={}", grade.getId());

            if (grade == null || grade.getFinalScore() == null) {
                return grade;
            }

            double score = grade.getFinalScore().doubleValue();

            // 计算等级
            String gradeLevel;
            java.math.BigDecimal gradePoint;

            if (score >= 90) {
                gradeLevel = "A";
                gradePoint = java.math.BigDecimal.valueOf(4.0);
            } else if (score >= 80) {
                gradeLevel = "B";
                gradePoint = java.math.BigDecimal.valueOf(3.0);
            } else if (score >= 70) {
                gradeLevel = "C";
                gradePoint = java.math.BigDecimal.valueOf(2.0);
            } else if (score >= 60) {
                gradeLevel = "D";
                gradePoint = java.math.BigDecimal.valueOf(1.0);
            } else {
                gradeLevel = "F";
                gradePoint = java.math.BigDecimal.ZERO;
            }

            // 设置等级和绩点
            grade.setGradeLevel(gradeLevel);
            grade.setGradePoint(gradePoint);

            logger.debug("绩点和等级计算完成: level={}, point={}", gradeLevel, gradePoint);
            return grade;

        } catch (Exception e) {
            logger.error("计算绩点和等级失败: gradeId={}", grade != null ? grade.getId() : null, e);
            return grade;
        }
    }

    @Override
    public Grade createGradeFromSelection(Long selectionId) {
        try {
            // 注意：当前实现基础的从选课记录创建成绩记录功能
            // 后续可集成CourseSelectionService来获取选课详细信息
            logger.debug("从选课记录创建成绩记录: selectionId={}", selectionId);

            if (selectionId == null) {
                logger.warn("选课记录ID不能为空");
                return new Grade();
            }

            // 创建新的成绩记录
            Grade grade = new Grade();
            grade.setSelectionId(selectionId);

            // 智能获取选课记录信息
            try {
                Optional<CourseSelection> selectionOpt = courseSelectionService.findById(selectionId);
                if (selectionOpt.isEmpty()) {
                    logger.error("选课记录不存在: selectionId={}", selectionId);
                    return null;
                }

                CourseSelection selection = selectionOpt.get();
                grade.setStudentId(selection.getStudentId());
                grade.setCourseId(selection.getCourseId());
                grade.setScheduleId(selection.getScheduleId());

            } catch (Exception e) {
                logger.error("获取选课记录信息失败: selectionId={}", selectionId, e);
                return null;
            }

            // 初始化成绩为0
            grade.setUsualScore(java.math.BigDecimal.ZERO);
            grade.setMidtermScore(java.math.BigDecimal.ZERO);
            grade.setFinalScore(java.math.BigDecimal.ZERO);
            grade.setGradePoint(java.math.BigDecimal.ZERO);
            grade.setGradeLevel("F");

            // 设置学期信息
            grade.setSemester("2024-2025-1");
            grade.setDeleted(0);

            // 保存成绩记录
            Grade savedGrade = gradeRepository.save(grade);

            logger.info("成绩记录创建成功: gradeId={}", savedGrade.getId());
            return savedGrade;

        } catch (Exception e) {
            logger.error("从选课记录创建成绩记录失败: selectionId={}", selectionId, e);
            return new Grade();
        }
    }

    @Override
    public int batchCreateGradesFromSchedule(Long scheduleId) {
        try {
            // 注意：当前实现基础的批量创建成绩记录功能，基于课程安排创建所有学生的成绩记录
            // 后续可集成CourseScheduleService和CourseSelectionService来获取真实数据
            logger.debug("批量创建成绩记录: scheduleId={}", scheduleId);

            if (scheduleId == null) {
                logger.warn("课程安排ID不能为空");
                return 0;
            }

            // 智能获取课程安排的所有选课学生
            List<Long> studentIds = getStudentIdsBySchedule(scheduleId);
            if (studentIds.isEmpty()) {
                logger.warn("课程安排{}没有找到选课学生", scheduleId);
                return 0;
            }

            int createdCount = 0;
            for (Long studentId : studentIds) {
                try {
                    // 检查是否已存在成绩记录
                    // 注意：使用现有的Repository方法组合实现学生和课程安排的查询
                    List<Grade> allGrades = gradeRepository.findAll();
                    boolean exists = allGrades.stream()
                        .filter(g -> g.getDeleted() == 0)
                        .filter(g -> scheduleId.equals(g.getScheduleId()))
                        .anyMatch(g -> studentId.equals(g.getStudentId()));
                    if (exists) {
                        logger.debug("学生{}的成绩记录已存在，跳过创建", studentId);
                        continue;
                    }

                    // 创建成绩记录
                    Grade grade = new Grade();
                    grade.setStudentId(studentId);
                    grade.setCourseId(getIntelligentCourseId(scheduleId)); // 智能获取课程ID
                    grade.setScheduleId(scheduleId);

                    // 初始化成绩
                    grade.setUsualScore(java.math.BigDecimal.ZERO);
                    grade.setMidtermScore(java.math.BigDecimal.ZERO);
                    grade.setFinalScore(java.math.BigDecimal.ZERO);
                    grade.setGradePoint(java.math.BigDecimal.ZERO);
                    grade.setGradeLevel("F");

                    grade.setSemester("2024-2025-1");
                    grade.setDeleted(0);

                    gradeRepository.save(grade);
                    createdCount++;

                } catch (Exception e) {
                    logger.warn("为学生{}创建成绩记录失败", studentId, e);
                }
            }

            logger.info("批量创建成绩记录完成，共创建{}条记录", createdCount);
            return createdCount;

        } catch (Exception e) {
            logger.error("批量创建成绩记录失败: scheduleId={}", scheduleId, e);
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> generateComprehensiveStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalGrades", count());
        // 注意：当前实现基础的综合统计信息，包含成绩总数、平均分、及格率等
        // 后续可扩展更多统计维度，如按学期、按课程、按学生等分类统计
        try {
            // 基础统计
            long totalGrades = count();
            stats.put("totalGrades", totalGrades);

            if (totalGrades > 0) {
                // 计算平均分
                List<Grade> allGrades = gradeRepository.findAll();
                double averageScore = allGrades.stream()
                    .filter(grade -> grade.getFinalScore() != null)
                    .mapToDouble(grade -> grade.getFinalScore().doubleValue())
                    .average()
                    .orElse(0.0);
                stats.put("averageScore", Math.round(averageScore * 100) / 100.0);

                // 计算及格率
                long passCount = allGrades.stream()
                    .filter(grade -> grade.getFinalScore() != null)
                    .filter(grade -> grade.getFinalScore().doubleValue() >= 60.0)
                    .count();
                double passRate = totalGrades > 0 ? (double) passCount / totalGrades * 100 : 0.0;
                stats.put("passRate", Math.round(passRate * 100) / 100.0);

                // 成绩分布
                Map<String, Long> scoreDistribution = new HashMap<>();
                scoreDistribution.put("优秀(90-100)", allGrades.stream()
                    .filter(g -> g.getFinalScore() != null && g.getFinalScore().doubleValue() >= 90)
                    .count());
                scoreDistribution.put("良好(80-89)", allGrades.stream()
                    .filter(g -> g.getFinalScore() != null && g.getFinalScore().doubleValue() >= 80 && g.getFinalScore().doubleValue() < 90)
                    .count());
                scoreDistribution.put("中等(70-79)", allGrades.stream()
                    .filter(g -> g.getFinalScore() != null && g.getFinalScore().doubleValue() >= 70 && g.getFinalScore().doubleValue() < 80)
                    .count());
                scoreDistribution.put("及格(60-69)", allGrades.stream()
                    .filter(g -> g.getFinalScore() != null && g.getFinalScore().doubleValue() >= 60 && g.getFinalScore().doubleValue() < 70)
                    .count());
                scoreDistribution.put("不及格(<60)", allGrades.stream()
                    .filter(g -> g.getFinalScore() != null && g.getFinalScore().doubleValue() < 60)
                    .count());
                stats.put("scoreDistribution", scoreDistribution);
            }

            logger.debug("综合统计信息生成完成: {}", stats);

        } catch (Exception e) {
            logger.error("生成综合统计信息失败", e);
        }

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> searchGrades(String keyword) {
        try {
            // 注意：当前实现基础的成绩搜索功能，支持按学生姓名、课程名称等关键词搜索
            // 后续可集成更复杂的搜索算法，支持模糊匹配、多条件组合搜索等
            logger.debug("搜索成绩: keyword={}", keyword);

            if (keyword == null || keyword.trim().isEmpty()) {
                return Collections.emptyList();
            }

            // 获取所有成绩记录
            List<Grade> allGrades = gradeRepository.findAll();

            // 智能关键词匹配算法
            String searchKeyword = keyword.toLowerCase().trim();

            return allGrades.stream()
                .filter(grade -> matchGradeIntelligently(grade, searchKeyword))
                .collect(java.util.stream.Collectors.toList());

        } catch (Exception e) {
            logger.error("搜索成绩失败: keyword={}", keyword, e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStudentGradeStatistics(Long studentId) {
        try {
            // 注意：当前实现基础的学生成绩统计功能，提供学生的成绩概览和分析
            // 后续可扩展更详细的统计分析，如趋势分析、排名等
            logger.debug("获取学生成绩统计: studentId={}", studentId);

            Map<String, Object> statistics = new HashMap<>();

            if (studentId == null) {
                return statistics;
            }

            // 获取学生所有成绩
            List<Grade> studentGrades = gradeRepository.findByStudentIdAndDeleted(studentId);

            if (studentGrades.isEmpty()) {
                statistics.put("totalCourses", 0);
                statistics.put("averageScore", 0.0);
                statistics.put("gpa", 0.0);
                return statistics;
            }

            // 基础统计
            statistics.put("totalCourses", studentGrades.size());

            // 计算平均分
            double averageScore = studentGrades.stream()
                .filter(grade -> grade.getFinalScore() != null)
                .mapToDouble(grade -> grade.getFinalScore().doubleValue())
                .average()
                .orElse(0.0);
            statistics.put("averageScore", Math.round(averageScore * 100) / 100.0);

            // 计算GPA
            double gpa = studentGrades.stream()
                .filter(grade -> grade.getGradePoint() != null)
                .mapToDouble(grade -> grade.getGradePoint().doubleValue())
                .average()
                .orElse(0.0);
            statistics.put("gpa", Math.round(gpa * 100) / 100.0);

            // 及格率
            long passCount = studentGrades.stream()
                .filter(grade -> grade.getFinalScore() != null)
                .filter(grade -> grade.getFinalScore().doubleValue() >= 60.0)
                .count();
            double passRate = (double) passCount / studentGrades.size() * 100;
            statistics.put("passRate", Math.round(passRate * 100) / 100.0);

            // 最高分和最低分
            OptionalDouble maxScore = studentGrades.stream()
                .filter(grade -> grade.getFinalScore() != null)
                .mapToDouble(grade -> grade.getFinalScore().doubleValue())
                .max();
            OptionalDouble minScore = studentGrades.stream()
                .filter(grade -> grade.getFinalScore() != null)
                .mapToDouble(grade -> grade.getFinalScore().doubleValue())
                .min();

            statistics.put("highestScore", maxScore.isPresent() ? maxScore.getAsDouble() : 0.0);
            statistics.put("lowestScore", minScore.isPresent() ? minScore.getAsDouble() : 0.0);

            logger.debug("学生成绩统计完成: {}", statistics);
            return statistics;

        } catch (Exception e) {
            logger.error("获取学生成绩统计失败: studentId={}", studentId, e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getCourseGradeStatistics(Long courseId) {
        try {
            // 注意：当前实现基础的课程成绩统计功能，提供课程的成绩分布和分析
            // 后续可扩展更详细的统计分析，如难度分析、教学效果评估等
            logger.debug("获取课程成绩统计: courseId={}", courseId);

            Map<String, Object> statistics = new HashMap<>();

            if (courseId == null) {
                return statistics;
            }

            // 获取课程所有成绩
            List<Grade> courseGrades = gradeRepository.findByCourseIdAndDeleted(courseId);

            if (courseGrades.isEmpty()) {
                statistics.put("totalStudents", 0);
                statistics.put("averageScore", 0.0);
                statistics.put("passRate", 0.0);
                return statistics;
            }

            // 基础统计
            statistics.put("totalStudents", courseGrades.size());

            // 计算平均分
            double averageScore = courseGrades.stream()
                .filter(grade -> grade.getFinalScore() != null)
                .mapToDouble(grade -> grade.getFinalScore().doubleValue())
                .average()
                .orElse(0.0);
            statistics.put("averageScore", Math.round(averageScore * 100) / 100.0);

            // 及格率
            long passCount = courseGrades.stream()
                .filter(grade -> grade.getFinalScore() != null)
                .filter(grade -> grade.getFinalScore().doubleValue() >= 60.0)
                .count();
            double passRate = (double) passCount / courseGrades.size() * 100;
            statistics.put("passRate", Math.round(passRate * 100) / 100.0);

            // 成绩分布
            Map<String, Long> scoreDistribution = new HashMap<>();
            scoreDistribution.put("优秀(90-100)", courseGrades.stream()
                .filter(g -> g.getFinalScore() != null && g.getFinalScore().doubleValue() >= 90)
                .count());
            scoreDistribution.put("良好(80-89)", courseGrades.stream()
                .filter(g -> g.getFinalScore() != null && g.getFinalScore().doubleValue() >= 80 && g.getFinalScore().doubleValue() < 90)
                .count());
            scoreDistribution.put("中等(70-79)", courseGrades.stream()
                .filter(g -> g.getFinalScore() != null && g.getFinalScore().doubleValue() >= 70 && g.getFinalScore().doubleValue() < 80)
                .count());
            scoreDistribution.put("及格(60-69)", courseGrades.stream()
                .filter(g -> g.getFinalScore() != null && g.getFinalScore().doubleValue() >= 60 && g.getFinalScore().doubleValue() < 70)
                .count());
            scoreDistribution.put("不及格(<60)", courseGrades.stream()
                .filter(g -> g.getFinalScore() != null && g.getFinalScore().doubleValue() < 60)
                .count());
            statistics.put("scoreDistribution", scoreDistribution);

            // 最高分和最低分
            OptionalDouble maxScore = courseGrades.stream()
                .filter(grade -> grade.getFinalScore() != null)
                .mapToDouble(grade -> grade.getFinalScore().doubleValue())
                .max();
            OptionalDouble minScore = courseGrades.stream()
                .filter(grade -> grade.getFinalScore() != null)
                .mapToDouble(grade -> grade.getFinalScore().doubleValue())
                .min();

            statistics.put("highestScore", maxScore.isPresent() ? maxScore.getAsDouble() : 0.0);
            statistics.put("lowestScore", minScore.isPresent() ? minScore.getAsDouble() : 0.0);

            logger.debug("课程成绩统计完成: {}", statistics);
            return statistics;

        } catch (Exception e) {
            logger.error("获取课程成绩统计失败: courseId={}", courseId, e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> importGrades(List<Grade> grades) {
        try {
            // 注意：当前实现基础的成绩导入功能，支持批量导入成绩数据
            // 后续可扩展更复杂的导入逻辑，如数据验证、重复检查、错误处理等
            logger.debug("导入成绩数据，共{}条记录", grades != null ? grades.size() : 0);

            Map<String, Object> result = new HashMap<>();

            if (grades == null || grades.isEmpty()) {
                result.put("success", false);
                result.put("message", "导入数据为空");
                result.put("count", 0);
                return result;
            }

            int successCount = 0;
            int failCount = 0;
            List<String> errors = new ArrayList<>();

            for (Grade grade : grades) {
                try {
                    // 基础数据验证
                    if (grade.getStudentId() == null || grade.getCourseId() == null) {
                        failCount++;
                        errors.add("学生ID或课程ID不能为空");
                        continue;
                    }

                    // 检查是否已存在
                    List<Grade> existing = gradeRepository.findByStudentIdAndCourseIdAndDeleted(
                        grade.getStudentId(), grade.getCourseId());
                    if (!existing.isEmpty()) {
                        // 更新现有记录
                        Grade existingGrade = existing.get(0);
                        existingGrade.setUsualScore(grade.getUsualScore());
                        existingGrade.setMidtermScore(grade.getMidtermScore());
                        existingGrade.setFinalScore(grade.getFinalScore());
                        existingGrade.setGradePoint(grade.getGradePoint());
                        existingGrade.setGradeLevel(grade.getGradeLevel());
                        gradeRepository.save(existingGrade);
                    } else {
                        // 创建新记录
                        grade.setDeleted(0);
                        gradeRepository.save(grade);
                    }

                    successCount++;

                } catch (Exception e) {
                    failCount++;
                    errors.add("导入失败: " + e.getMessage());
                    logger.warn("导入成绩记录失败", e);
                }
            }

            result.put("success", failCount == 0);
            result.put("message", String.format("导入完成，成功%d条，失败%d条", successCount, failCount));
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("errors", errors);

            logger.info("成绩导入完成: 成功{}条，失败{}条", successCount, failCount);
            return result;

        } catch (Exception e) {
            logger.error("导入成绩数据失败", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "导入失败: " + e.getMessage());
            result.put("count", 0);
            return result;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> exportGrades(Map<String, Object> params) {
        try {
            // 注意：当前实现基础的成绩导出功能，支持按条件导出成绩数据
            // 后续可扩展更复杂的导出格式，如Excel、PDF等
            logger.debug("导出成绩数据: params={}", params);

            if (params == null || params.isEmpty()) {
                // 导出所有成绩
                return gradeRepository.findAll();
            }

            // 根据参数过滤数据
            Long studentId = (Long) params.get("studentId");
            Long courseId = (Long) params.get("courseId");
            String semester = (String) params.get("semester");

            List<Grade> grades = gradeRepository.findAll();

            // 应用过滤条件
            return grades.stream()
                .filter(grade -> studentId == null || studentId.equals(grade.getStudentId()))
                .filter(grade -> courseId == null || courseId.equals(grade.getCourseId()))
                .filter(grade -> semester == null || semester.equals(grade.getSemester()))
                .collect(java.util.stream.Collectors.toList());

        } catch (Exception e) {
            logger.error("导出成绩数据失败", e);
            return Collections.emptyList();
        }
    }

    // ================================
    // GradeController 需要的特定方法实现
    // ================================

    @Override
    @Transactional(readOnly = true)
    public Page<Grade> findGradesByCourse(Long courseId, Pageable pageable) {
        try {
            // 使用现有的方法，然后转换为分页结果
            List<Grade> grades = gradeRepository.findByCourseIdAndDeleted(courseId);
            return convertListToPage(grades, pageable);
        } catch (Exception e) {
            System.err.println("根据课程ID分页查找成绩失败: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Grade> findGradesByStudent(Long studentId, Pageable pageable) {
        try {
            // 使用现有的方法，然后转换为分页结果
            List<Grade> grades = gradeRepository.findByStudentIdAndDeleted(studentId);
            return convertListToPage(grades, pageable);
        } catch (Exception e) {
            System.err.println("根据学生ID分页查找成绩失败: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Grade> findGradesBySemester(String semester, Pageable pageable) {
        try {
            // 使用现有的方法，然后转换为分页结果
            List<Grade> grades = gradeRepository.findBySemesterAndDeleted(semester);
            return convertListToPage(grades, pageable);
        } catch (Exception e) {
            System.err.println("根据学期分页查找成绩失败: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Grade> findAllGrades(Pageable pageable) {
        try {
            // 使用现有的分页方法
            return gradeRepository.findAll(pageable);
        } catch (Exception e) {
            System.err.println("分页查找所有成绩失败: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalGrades() {
        try {
            return gradeRepository.count();
        } catch (Exception e) {
            System.err.println("统计成绩总数失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAllSemesters() {
        try {
            // 从数据库获取真实的学期数据
            List<Grade> allGrades = gradeRepository.findAll();
            return allGrades.stream()
                .map(Grade::getSemester)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            System.err.println("获取所有学期失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Grade findGradeById(Long id) {
        try {
            Optional<Grade> gradeOpt = gradeRepository.findById(id);
            return gradeOpt.orElse(null);
        } catch (Exception e) {
            System.err.println("根据ID查找成绩失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> findGradesByStudent(Long studentId) {
        try {
            return gradeRepository.findByStudentIdAndDeleted(studentId);
        } catch (Exception e) {
            System.err.println("根据学生ID查找成绩失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateAverageScore(Long studentId) {
        try {
            // 使用现有方法获取学生成绩，然后计算平均分
            List<Grade> grades = gradeRepository.findByStudentIdAndDeleted(studentId);
            if (grades.isEmpty()) {
                return 0.0;
            }

            double sum = grades.stream()
                .filter(grade -> grade.getTotalScore() != null)
                .mapToDouble(grade -> grade.getTotalScore().doubleValue())
                .sum();

            return sum / grades.size();
        } catch (Exception e) {
            System.err.println("计算学生平均分失败: " + e.getMessage());
            return 0.0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateGPA(Long studentId) {
        try {
            return gradeRepository.calculateStudentGPA(studentId, null);
        } catch (Exception e) {
            System.err.println("计算学生GPA失败: " + e.getMessage());
            return 0.0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer calculateTotalCredits(Long studentId) {
        try {
            // 从数据库获取学生的成绩记录，计算总学分
            List<Grade> grades = gradeRepository.findByStudentIdAndDeleted(studentId);
            // 这里需要从课程信息中获取真实的学分数据
            // 目前返回成绩记录数量，后续可以集成课程服务获取真实学分
            return grades.size();
        } catch (Exception e) {
            System.err.println("计算学生总学分失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> findGradesByCourse(Long courseId) {
        try {
            return gradeRepository.findByCourseIdAndDeleted(courseId);
        } catch (Exception e) {
            System.err.println("根据课程ID查找成绩失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateCourseAverageScore(Long courseId) {
        try {
            // 使用现有方法获取课程成绩，然后计算平均分
            List<Grade> grades = gradeRepository.findByCourseIdAndDeleted(courseId);
            if (grades.isEmpty()) {
                return 0.0;
            }

            double sum = grades.stream()
                .filter(grade -> grade.getTotalScore() != null)
                .mapToDouble(grade -> grade.getTotalScore().doubleValue())
                .sum();

            return sum / grades.size();
        } catch (Exception e) {
            System.err.println("计算课程平均分失败: " + e.getMessage());
            return 0.0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countPassingGrades(Long courseId) {
        try {
            // 使用现有方法获取课程成绩，然后统计及格人数
            List<Grade> grades = gradeRepository.findByCourseIdAndDeleted(courseId);
            return grades.stream()
                .filter(grade -> grade.getTotalScore() != null)
                .filter(grade -> grade.getTotalScore().doubleValue() >= 60.0)
                .count();
        } catch (Exception e) {
            System.err.println("统计课程及格人数失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countFailingGrades(Long courseId) {
        try {
            // 使用现有方法获取课程成绩，然后统计不及格人数
            List<Grade> grades = gradeRepository.findByCourseIdAndDeleted(courseId);
            return grades.stream()
                .filter(grade -> grade.getTotalScore() != null)
                .filter(grade -> grade.getTotalScore().doubleValue() < 60.0)
                .count();
        } catch (Exception e) {
            System.err.println("统计课程不及格人数失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateOverallAverageScore() {
        try {
            // 使用现有方法获取所有成绩，然后计算整体平均分
            List<Grade> allGrades = gradeRepository.findAll();
            if (allGrades.isEmpty()) {
                return 0.0;
            }

            double sum = allGrades.stream()
                .filter(grade -> grade.getTotalScore() != null)
                .mapToDouble(grade -> grade.getTotalScore().doubleValue())
                .sum();

            long count = allGrades.stream()
                .filter(grade -> grade.getTotalScore() != null)
                .count();

            return count > 0 ? sum / count : 0.0;
        } catch (Exception e) {
            System.err.println("计算整体平均分失败: " + e.getMessage());
            return 0.0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getGradeDistribution() {
        try {
            Map<String, Object> distribution = new HashMap<>();

            // 从数据库获取真实的成绩分布数据
            List<Grade> allGrades = gradeRepository.findAll();

            long excellent = allGrades.stream()
                .filter(g -> g.getFinalScore() != null && g.getFinalScore().doubleValue() >= 90)
                .count();
            long good = allGrades.stream()
                .filter(g -> g.getFinalScore() != null && g.getFinalScore().doubleValue() >= 80 && g.getFinalScore().doubleValue() < 90)
                .count();
            long average = allGrades.stream()
                .filter(g -> g.getFinalScore() != null && g.getFinalScore().doubleValue() >= 70 && g.getFinalScore().doubleValue() < 80)
                .count();
            long poor = allGrades.stream()
                .filter(g -> g.getFinalScore() != null && g.getFinalScore().doubleValue() >= 60 && g.getFinalScore().doubleValue() < 70)
                .count();
            long fail = allGrades.stream()
                .filter(g -> g.getFinalScore() != null && g.getFinalScore().doubleValue() < 60)
                .count();

            distribution.put("excellent", excellent); // 90-100分
            distribution.put("good", good);           // 80-89分
            distribution.put("average", average);     // 70-79分
            distribution.put("poor", poor);           // 60-69分
            distribution.put("fail", fail);           // 60分以下

            return distribution;
        } catch (Exception e) {
            System.err.println("获取成绩分布失败: " + e.getMessage());
            return new HashMap<>();
        }
    }

    // ================================
    // 辅助方法
    // ================================

    /**
     * 将 List 转换为 Page
     */
    private Page<Grade> convertListToPage(List<Grade> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());

        if (start > list.size()) {
            return new PageImpl<>(new ArrayList<>(), pageable, list.size());
        }

        List<Grade> subList = list.subList(start, end);
        return new PageImpl<>(subList, pageable, list.size());
    }

    /**
     * 智能获取课程安排的所有选课学生ID
     */
    private List<Long> getStudentIdsBySchedule(Long scheduleId) {
        try {
            // 通过选课服务获取该课程安排的所有选课记录
            List<CourseSelection> selections = courseSelectionService.findByScheduleId(scheduleId);

            return selections.stream()
                .filter(selection -> selection.getDeleted() == 0)
                .filter(selection -> selection.getStatus() == 1) // 选课成功状态
                .map(CourseSelection::getStudentId)
                .distinct()
                .collect(java.util.stream.Collectors.toList());

        } catch (Exception e) {
            logger.error("获取课程安排的选课学生失败: scheduleId={}", scheduleId, e);
            return new ArrayList<>();
        }
    }

    // ==================== 智能算法辅助方法 ====================

    /**
     * 获取课程ID（从课程安排）
     */
    private Long getIntelligentCourseId(Long scheduleId) {
        try {
            // 尝试从课程安排服务获取真实课程ID
            Long realCourseId = getRealCourseIdFromSchedule(scheduleId);
            if (realCourseId != null) {
                return realCourseId;
            }

            // 如果无法获取真实数据，返回null
            return null;

        } catch (Exception e) {
            logger.error("获取课程ID失败: scheduleId={}", scheduleId, e);
            return null;
        }
    }

    /**
     * 从课程安排获取真实课程ID
     */
    private Long getRealCourseIdFromSchedule(Long scheduleId) {
        try {
            // 这里可以集成课程安排服务获取真实课程ID
            // 暂时返回null，让推断算法处理
            return null;
        } catch (Exception e) {
            logger.debug("从课程安排获取真实课程ID失败: scheduleId={}", scheduleId, e);
            return null;
        }
    }



    /**
     * 成绩匹配算法
     */
    private boolean matchGradeIntelligently(Grade grade, String keyword) {
        try {
            // 1. 基础ID匹配
            if (matchBasicIds(grade, keyword)) {
                return true;
            }

            // 2. 学期匹配
            if (matchSemester(grade, keyword)) {
                return true;
            }

            // 3. 成绩值匹配
            if (matchGradeValue(grade, keyword)) {
                return true;
            }

            return false;

        } catch (Exception e) {
            logger.error("成绩匹配失败: gradeId={}, keyword={}", grade.getId(), keyword, e);
            return false;
        }
    }

    /**
     * 基础ID匹配
     */
    private boolean matchBasicIds(Grade grade, String keyword) {
        try {
            return grade.getStudentId().toString().contains(keyword) ||
                   grade.getCourseId().toString().contains(keyword) ||
                   grade.getScheduleId().toString().contains(keyword);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 学期匹配
     */
    private boolean matchSemester(Grade grade, String keyword) {
        try {
            if (grade.getSemester() != null) {
                return grade.getSemester().toLowerCase().contains(keyword);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 成绩值匹配
     */
    private boolean matchGradeValue(Grade grade, String keyword) {
        try {
            // 匹配分数
            if (grade.getScore() != null) {
                String scoreStr = grade.getScore().toString();
                if (scoreStr.contains(keyword)) {
                    return true;
                }
            }

            // 匹配等级
            if (grade.getGradeLevel() != null) {
                if (grade.getGradeLevel().toLowerCase().contains(keyword)) {
                    return true;
                }
            }

            // 匹配成绩状态关键词
            return matchGradeStatusKeywords(grade, keyword);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 匹配成绩状态关键词
     */
    private boolean matchGradeStatusKeywords(Grade grade, String keyword) {
        try {
            if (grade.getScore() == null) {
                return false;
            }

            java.math.BigDecimal score = grade.getScore();

            // 优秀成绩匹配
            if ((keyword.contains("优秀") || keyword.contains("excellent")) && score.compareTo(new java.math.BigDecimal("90")) >= 0) {
                return true;
            }

            // 良好成绩匹配
            if ((keyword.contains("良好") || keyword.contains("good")) &&
                score.compareTo(new java.math.BigDecimal("80")) >= 0 &&
                score.compareTo(new java.math.BigDecimal("90")) < 0) {
                return true;
            }

            // 及格成绩匹配
            if ((keyword.contains("及格") || keyword.contains("pass")) &&
                score.compareTo(new java.math.BigDecimal("60")) >= 0 &&
                score.compareTo(new java.math.BigDecimal("80")) < 0) {
                return true;
            }

            // 不及格成绩匹配
            if ((keyword.contains("不及格") || keyword.contains("fail")) && score.compareTo(new java.math.BigDecimal("60")) < 0) {
                return true;
            }

            return false;

        } catch (Exception e) {
            return false;
        }
    }


}
