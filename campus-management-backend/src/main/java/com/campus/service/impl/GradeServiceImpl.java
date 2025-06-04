package com.campus.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.entity.CourseSchedule;
import com.campus.entity.CourseSelection;
import com.campus.entity.Grade;
import com.campus.repository.CourseScheduleRepository;
import com.campus.repository.CourseSelectionRepository;
import com.campus.repository.GradeRepository;
import com.campus.repository.GradeRepository.CourseGradeDetail;
import com.campus.repository.GradeRepository.GradeDetail;
import com.campus.repository.GradeRepository.StudentGradeDetail;
import com.campus.service.GradeService;

/**
 * 成绩服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Service
public class GradeServiceImpl extends ServiceImpl<GradeRepository, Grade> implements GradeService {

    @Autowired
    private GradeRepository gradeRepository;


    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    @Autowired
    private CourseSelectionRepository courseSelectionRepository;

    @Override
    public List<Grade> findByStudentId(Long studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    @Override
    public List<Grade> findByCourseId(Long courseId) {
        return gradeRepository.findByCourseId(courseId);
    }

    @Override
    public List<Grade> findByScheduleId(Long scheduleId) {
        return gradeRepository.findByScheduleId(scheduleId);
    }

    @Override
    public Optional<Grade> findBySelectionId(Long selectionId) {
        return gradeRepository.findBySelectionId(selectionId);
    }

    @Override
    public List<Grade> findBySemester(String semester) {
        return gradeRepository.findBySemester(semester);
    }

    @Override
    public List<Grade> findByStudentIdAndCourseId(Long studentId, Long courseId) {
        return gradeRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    public List<Grade> findByStudentIdAndSemester(Long studentId, String semester) {
        return gradeRepository.findByStudentIdAndSemester(studentId, semester);
    }

    @Override
    public Optional<GradeDetail> findGradeDetailById(Long gradeId) {
        return gradeRepository.findGradeDetailById(gradeId);
    }

    @Override
    public List<StudentGradeDetail> findStudentGradeDetails(Long studentId, String semester) {
        return gradeRepository.findStudentGradeDetails(studentId, semester);
    }

    @Override
    public List<CourseGradeDetail> findCourseGradeDetails(Long courseId, Long scheduleId) {
        return gradeRepository.findCourseGradeDetails(courseId, scheduleId);
    }

    @Override
    public Double calculateStudentGPA(Long studentId, String semester) {
        return gradeRepository.calculateStudentGPA(studentId, semester);
    }

    @Override
    public Double calculateClassAverageScore(Long classId, Long courseId) {
        return gradeRepository.calculateClassAverageScore(classId, courseId);
    }

    @Override
    public IPage<Grade> findGradesByPage(int page, int size, Map<String, Object> params) {
        Page<Grade> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<Grade> queryWrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        if (params != null) {
            // 根据学生ID查询
            if (params.containsKey("studentId")) {
                queryWrapper.eq(Grade::getStudentId, params.get("studentId"));
            }

            // 根据课程ID查询
            if (params.containsKey("courseId")) {
                queryWrapper.eq(Grade::getCourseId, params.get("courseId"));
            }

            // 根据课程表ID查询
            if (params.containsKey("scheduleId")) {
                queryWrapper.eq(Grade::getScheduleId, params.get("scheduleId"));
            }

            // 根据选课ID查询
            if (params.containsKey("selectionId")) {
                queryWrapper.eq(Grade::getSelectionId, params.get("selectionId"));
            }

            // 根据学期查询
            if (params.containsKey("semester")) {
                queryWrapper.eq(Grade::getSemester, params.get("semester"));
            }

            // 根据教师ID查询
            if (params.containsKey("teacherId")) {
                queryWrapper.eq(Grade::getTeacherId, params.get("teacherId"));
            }

            // 根据状态查询
            if (params.containsKey("status")) {
                queryWrapper.eq(Grade::getStatus, params.get("status"));
            }

            // 根据补考标记查询
            if (params.containsKey("isMakeup")) {
                queryWrapper.eq(Grade::getIsMakeup, params.get("isMakeup"));
            }

            // 根据重修标记查询
            if (params.containsKey("isRetake")) {
                queryWrapper.eq(Grade::getIsRetake, params.get("isRetake"));
            }
        }

        // 默认按学生ID和课程ID排序
        queryWrapper.orderByAsc(Grade::getStudentId)
                   .orderByAsc(Grade::getCourseId);

        return page(pageRequest, queryWrapper);
    }

    @Override
    @Transactional
    public Grade createGrade(Grade grade) {
        // 检查学生、课程和课程表是否存在
        // 这里可以添加更多的业务逻辑检查

        // 如果提供了平时成绩、期中成绩和期末成绩，计算总成绩
        if (grade.getRegularScore() != null || grade.getMidtermScore() != null || grade.getFinalScore() != null) {
            grade = calculateFinalScore(grade);
        }

        // 如果提供了总成绩，计算绩点和等级
        if (grade.getScore() != null) {
            grade = calculateGradePointAndLevel(grade);
        }

        // 保存成绩信息
        save(grade);
        return grade;
    }

    @Override
    @Transactional
    public boolean updateGrade(Grade grade) {
        // 检查成绩是否存在
        Grade existingGrade = getById(grade.getId());
        if (existingGrade == null) {
            return false;
        }

        // 如果提供了平时成绩、期中成绩和期末成绩，计算总成绩
        if (grade.getRegularScore() != null || grade.getMidtermScore() != null || grade.getFinalScore() != null) {
            grade = calculateFinalScore(grade);
        }

        // 如果提供了总成绩，计算绩点和等级
        if (grade.getScore() != null) {
            grade = calculateGradePointAndLevel(grade);
        }

        // 更新成绩信息
        return updateById(grade);
    }

    @Override
    @Transactional
    public boolean batchUpdateGrades(List<Grade> grades) {
        // 遍历成绩列表，计算总成绩、绩点和等级
        for (Grade grade : grades) {
            // 如果提供了平时成绩、期中成绩和期末成绩，计算总成绩
            if (grade.getRegularScore() != null || grade.getMidtermScore() != null || grade.getFinalScore() != null) {
                calculateFinalScore(grade);
            }

            // 如果提供了总成绩，计算绩点和等级
            if (grade.getScore() != null) {
                calculateGradePointAndLevel(grade);
            }
        }

        // 批量更新成绩
        return updateBatchById(grades);
    }

    @Override
    @Transactional
    public boolean deleteGrade(Long id) {
        // 删除成绩
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean batchDeleteGrades(List<Long> ids) {
        // 批量删除成绩
        return removeBatchByIds(ids);
    }

    @Override
    public Grade calculateFinalScore(Grade grade) {
        // 获取各部分成绩
        BigDecimal regularScore = grade.getRegularScore();
        BigDecimal midtermScore = grade.getMidtermScore();
        BigDecimal finalScore = grade.getFinalScore();

        // 默认权重（可以根据业务需求调整）
        BigDecimal regularWeight = new BigDecimal("0.3"); // 平时成绩占30%
        BigDecimal midtermWeight = new BigDecimal("0.2"); // 期中成绩占20%
        BigDecimal finalWeight = new BigDecimal("0.5");   // 期末成绩占50%

        // 计算总成绩
        BigDecimal totalScore = BigDecimal.ZERO;

        if (regularScore != null) {
            totalScore = totalScore.add(regularScore.multiply(regularWeight));
        }

        if (midtermScore != null) {
            totalScore = totalScore.add(midtermScore.multiply(midtermWeight));
        }

        if (finalScore != null) {
            totalScore = totalScore.add(finalScore.multiply(finalWeight));
        }

        // 四舍五入到两位小数
        totalScore = totalScore.setScale(2, RoundingMode.HALF_UP);

        // 设置总成绩
        grade.setScore(totalScore);

        return grade;
    }

    @Override
    public Grade calculateGradePointAndLevel(Grade grade) {
        // 获取总成绩
        BigDecimal score = grade.getScore();
        if (score == null) {
            return grade;
        }

        // 计算绩点（4分制）
        BigDecimal gradePoint;
        String level;

        if (score.compareTo(new BigDecimal("90")) >= 0) {
            gradePoint = new BigDecimal("4.0");
            level = "A";
        } else if (score.compareTo(new BigDecimal("85")) >= 0) {
            gradePoint = new BigDecimal("3.7");
            level = "A-";
        } else if (score.compareTo(new BigDecimal("80")) >= 0) {
            gradePoint = new BigDecimal("3.3");
            level = "B+";
        } else if (score.compareTo(new BigDecimal("75")) >= 0) {
            gradePoint = new BigDecimal("3.0");
            level = "B";
        } else if (score.compareTo(new BigDecimal("70")) >= 0) {
            gradePoint = new BigDecimal("2.7");
            level = "B-";
        } else if (score.compareTo(new BigDecimal("65")) >= 0) {
            gradePoint = new BigDecimal("2.3");
            level = "C+";
        } else if (score.compareTo(new BigDecimal("60")) >= 0) {
            gradePoint = new BigDecimal("2.0");
            level = "C";
        } else {
            gradePoint = new BigDecimal("0.0");
            level = "F";
        }

        // 设置绩点和等级
        grade.setGradePoint(gradePoint);
        grade.setLevel(level);

        return grade;
    }

    @Override
    @Transactional
    public Grade createGradeFromSelection(Long selectionId) {
        // 获取选课记录
        CourseSelection selection = courseSelectionRepository.selectById(selectionId);
        if (selection == null) {
            throw new IllegalArgumentException("选课记录不存在：" + selectionId);
        }

        // 检查是否已存在成绩记录
        Optional<Grade> existingGrade = findBySelectionId(selectionId);
        if (existingGrade.isPresent()) {
            return existingGrade.get();
        }

        // 创建成绩记录
        Grade grade = new Grade();
        grade.setStudentId(selection.getStudentId());
        grade.setCourseId(selection.getCourseId());
        grade.setScheduleId(selection.getScheduleId());
        grade.setSelectionId(selection.getId());
        grade.setSemester(selection.getSemester());
        grade.setStatus(0); // 默认状态：未录入

        // 保存成绩记录
        save(grade);
        return grade;
    }

    @Override
    @Transactional
    public int batchCreateGradesFromSchedule(Long scheduleId) {
        // 获取课程表
        CourseSchedule schedule = courseScheduleRepository.selectById(scheduleId);
        if (schedule == null) {
            throw new IllegalArgumentException("课程表不存在：" + scheduleId);
        }

        // 获取该课程表的所有选课记录
        List<CourseSelection> selections = courseSelectionRepository.findByScheduleId(scheduleId);
        if (selections.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (CourseSelection selection : selections) {
            // 检查是否已存在成绩记录
            Optional<Grade> existingGrade = findBySelectionId(selection.getId());
            if (!existingGrade.isPresent()) {
                // 创建成绩记录
                Grade grade = new Grade();
                grade.setStudentId(selection.getStudentId());
                grade.setCourseId(selection.getCourseId());
                grade.setScheduleId(selection.getScheduleId());
                grade.setSelectionId(selection.getId());
                grade.setSemester(selection.getSemester());
                grade.setStatus(0); // 默认状态：未录入

                // 保存成绩记录
                save(grade);
                count++;
            }
        }

        return count;
    }
}
