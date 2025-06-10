package com.campus.application.service.impl;

import com.campus.application.service.GradeService;
import com.campus.domain.entity.Grade;
import com.campus.domain.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import org.springframework.data.domain.PageImpl;

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

    @Autowired
    private GradeRepository gradeRepository;

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
        // TODO: 实现分页查询
        return gradeRepository.findAll(pageable);
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
        // TODO: 实现总成绩计算逻辑
        return grade;
    }

    @Override
    public Grade calculateGradePointAndLevel(Grade grade) {
        // TODO: 实现绩点和等级计算逻辑
        return grade;
    }

    @Override
    public Grade createGradeFromSelection(Long selectionId) {
        // TODO: 实现从选课记录创建成绩记录
        return new Grade();
    }

    @Override
    public int batchCreateGradesFromSchedule(Long scheduleId) {
        // TODO: 实现批量创建成绩记录
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> generateComprehensiveStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalGrades", count());
        // TODO: 添加更多统计信息
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> searchGrades(String keyword) {
        // TODO: 实现成绩搜索
        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getStudentGradeStatistics(Long studentId) {
        // TODO: 实现学生成绩统计
        return new HashMap<>();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getCourseGradeStatistics(Long courseId) {
        // TODO: 实现课程成绩统计
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> importGrades(List<Grade> grades) {
        // TODO: 实现成绩导入
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", 0);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> exportGrades(Map<String, Object> params) {
        // TODO: 实现成绩导出
        return Collections.emptyList();
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
            // 模拟学期数据
            return Arrays.asList("2024-2025-1", "2024-2025-2", "2023-2024-1", "2023-2024-2", "2022-2023-1", "2022-2023-2");
        } catch (Exception e) {
            System.err.println("获取所有学期失败: " + e.getMessage());
            return Arrays.asList("2024-2025-1", "2024-2025-2");
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
                .filter(grade -> grade.getFinalScore() != null)
                .mapToDouble(grade -> grade.getFinalScore().doubleValue())
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
            // 模拟计算总学分
            List<Grade> grades = gradeRepository.findByStudentIdAndDeleted(studentId);
            return grades.size() * 3; // 假设每门课程3学分
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
                .filter(grade -> grade.getFinalScore() != null)
                .mapToDouble(grade -> grade.getFinalScore().doubleValue())
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
                .filter(grade -> grade.getFinalScore() != null)
                .filter(grade -> grade.getFinalScore().doubleValue() >= 60.0)
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
                .filter(grade -> grade.getFinalScore() != null)
                .filter(grade -> grade.getFinalScore().doubleValue() < 60.0)
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
                .filter(grade -> grade.getFinalScore() != null)
                .mapToDouble(grade -> grade.getFinalScore().doubleValue())
                .sum();

            long count = allGrades.stream()
                .filter(grade -> grade.getFinalScore() != null)
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

            // 模拟成绩分布数据
            distribution.put("excellent", 25); // 90-100分
            distribution.put("good", 35);      // 80-89分
            distribution.put("average", 30);   // 70-79分
            distribution.put("poor", 10);      // 60-69分
            distribution.put("fail", 5);       // 60分以下

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
}
