package com.campus.application.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.application.service.SchoolClassService;
import com.campus.domain.entity.SchoolClass;
import com.campus.domain.entity.Student;
import com.campus.domain.repository.SchoolClassRepository;
import com.campus.domain.repository.StudentRepository;

/**
 * 班级服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Service
public class SchoolClassServiceImpl implements SchoolClassService {

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private StudentRepository studentRepository;

    // ==================== 基础CRUD方法 ====================

    @Override
    public SchoolClass save(SchoolClass schoolClass) {
        return schoolClassRepository.save(schoolClass);
    }

    @Override
    public Optional<SchoolClass> findById(Long id) {
        return schoolClassRepository.findById(id);
    }

    @Override
    public List<SchoolClass> findAll() {
        return schoolClassRepository.findAll();
    }

    @Override
    public Page<SchoolClass> findAll(Pageable pageable) {
        return schoolClassRepository.findAll(pageable);
    }

    @Override
    public void deleteById(Long id) {
        schoolClassRepository.deleteById(id);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        schoolClassRepository.deleteAllById(ids);
    }

    @Override
    @Cacheable(value = "class:count", unless = "#result == null")
    public long count() {
        return schoolClassRepository.count();
    }

    // ==================== 业务查询方法 ====================

    @Override
    public Optional<SchoolClass> findByClassCode(String classCode) {
        return schoolClassRepository.findByClassCodeAndDeleted(classCode, 0);
    }

    @Override
    public List<SchoolClass> findByGrade(String grade) {
        return schoolClassRepository.findByGradeAndDeletedOrderByClassCodeAsc(grade, 0);
    }

    @Override
    public List<SchoolClass> findByDepartmentId(Long departmentId) {
        return schoolClassRepository.findByDepartmentIdAndDeletedOrderByGradeAscClassCodeAsc(departmentId, 0);
    }

    @Override
    public List<SchoolClass> findByHeadTeacherId(Long headTeacherId) {
        return schoolClassRepository.findByHeadTeacherIdAndDeletedOrderByGradeAscClassCodeAsc(headTeacherId, 0);
    }

    @Override
    public boolean existsByClassCode(String classCode) {
        return schoolClassRepository.existsByClassCodeAndDeleted(classCode, 0);
    }

    @Override
    public Optional<Object[]> findClassDetailById(Long classId) {
        return schoolClassRepository.findClassDetailById(classId);
    }

    @Override
    public List<Object[]> countClassesByGrade() {
        return schoolClassRepository.countClassesByGrade();
    }

    @Override
    public Page<SchoolClass> findClassesByPage(Pageable pageable, Map<String, Object> params) {
        // 简化实现，使用基础分页查询
        // 在实际项目中，可以根据params构建Specification进行条件查询
        return schoolClassRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public SchoolClass createClass(SchoolClass schoolClass) {
        // 检查班级代码是否已存在
        if (existsByClassCode(schoolClass.getClassCode())) {
            throw new IllegalArgumentException("班级代码已存在：" + schoolClass.getClassCode());
        }

        // 保存班级信息
        save(schoolClass);
        return schoolClass;
    }

    @Override
    @Transactional
    public boolean updateClass(SchoolClass schoolClass) {
        // 检查班级是否存在
        Optional<SchoolClass> existingClassOpt = findById(schoolClass.getId());
        if (existingClassOpt.isEmpty()) {
            return false;
        }

        SchoolClass existingClass = existingClassOpt.get();

        // 如果班级代码变更，检查新代码是否已存在
        if (!existingClass.getClassCode().equals(schoolClass.getClassCode())
                && existsByClassCode(schoolClass.getClassCode())) {
            throw new IllegalArgumentException("班级代码已存在：" + schoolClass.getClassCode());
        }

        // 更新班级信息
        save(schoolClass);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteClass(Long id) {
        // 检查班级是否有关联的学生
        List<Student> students = studentRepository.findByClassIdAndDeletedOrderByStudentNoAsc(id, 0);
        if (!students.isEmpty()) {
            throw new IllegalStateException("班级存在关联的学生，无法删除");
        }

        // 删除班级
        deleteById(id);
        return true;
    }

    @Override
    @Transactional
    public boolean batchDeleteClasses(List<Long> ids) {
        // 检查班级是否有关联的学生
        for (Long id : ids) {
            List<Student> students = studentRepository.findByClassIdAndDeletedOrderByStudentNoAsc(id, 0);
            if (!students.isEmpty()) {
                throw new IllegalStateException("班级ID " + id + " 存在关联的学生，无法删除");
            }
        }

        // 批量删除班级
        deleteAllById(ids);
        return true;
    }

    @Override
    @Transactional
    public boolean updateStudentCount(Long classId) {
        // 检查班级是否存在
        Optional<SchoolClass> schoolClassOpt = findById(classId);
        if (schoolClassOpt.isEmpty()) {
            return false;
        }

        SchoolClass schoolClass = schoolClassOpt.get();

        // 统计班级学生数量
        List<Student> students = studentRepository.findByClassIdAndDeletedOrderByStudentNoAsc(classId, 0);
        long count = students.size();

        // 更新班级学生数量
        schoolClass.setStudentCount((int) count);
        save(schoolClass);
        return true;
    }

    /**
     * 获取所有班级的详细信息列表
     */
    public List<Object[]> getAllClassDetails() {
        return schoolClassRepository.findAllClassDetails();
    }

    /**
     * 根据年级获取班级详细信息列表
     */
    public List<Object[]> getClassDetailsByGrade(String grade) {
        return schoolClassRepository.findClassDetailsByGrade(grade);
    }

    /**
     * 根据关键词搜索班级
     */
    public List<Object[]> searchClasses(String keyword) {
        return schoolClassRepository.searchClasses(keyword);
    }

    /**
     * 查找没有班主任的班级
     */
    public List<Object[]> getClassesWithoutHeadTeacher() {
        return schoolClassRepository.findClassesWithoutHeadTeacher();
    }

    // ================================
    // 班级管理页面需要的方法实现
    // ================================

    @Override
    @Transactional(readOnly = true)
    public Page<SchoolClass> searchClasses(String keyword, Pageable pageable) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return schoolClassRepository.findAll(pageable);
            }

            // 使用现有的搜索方法，然后手动分页
            List<Object[]> searchResults = schoolClassRepository.searchClasses(keyword.trim());

            // 转换为 SchoolClass 对象列表
            List<SchoolClass> classes = new ArrayList<>();
            for (Object[] result : searchResults) {
                SchoolClass schoolClass = new SchoolClass();
                schoolClass.setId((Long) result[0]);
                schoolClass.setClassName((String) result[1]);
                schoolClass.setClassCode((String) result[2]);
                schoolClass.setGrade((String) result[3]);
                classes.add(schoolClass);
            }

            // 手动分页
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), classes.size());
            List<SchoolClass> pageContent = start < classes.size() ?
                classes.subList(start, end) : new ArrayList<>();

            return new PageImpl<>(pageContent, pageable, classes.size());
        } catch (Exception e) {
            System.err.println("搜索班级失败: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SchoolClass> findClassesByDepartment(Long departmentId, Pageable pageable) {
        try {
            List<SchoolClass> classes = schoolClassRepository.findByDepartmentIdAndDeletedOrderByGradeAscClassCodeAsc(departmentId, 0);

            // 手动分页
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), classes.size());
            List<SchoolClass> pageContent = start < classes.size() ?
                classes.subList(start, end) : new ArrayList<>();

            return new PageImpl<>(pageContent, pageable, classes.size());
        } catch (Exception e) {
            System.err.println("根据部门查找班级失败: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SchoolClass> findClassesByGrade(String grade, Pageable pageable) {
        try {
            List<SchoolClass> classes = schoolClassRepository.findByGradeAndDeletedOrderByClassCodeAsc(grade, 0);

            // 手动分页
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), classes.size());
            List<SchoolClass> pageContent = start < classes.size() ?
                classes.subList(start, end) : new ArrayList<>();

            return new PageImpl<>(pageContent, pageable, classes.size());
        } catch (Exception e) {
            System.err.println("根据年级查找班级失败: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SchoolClass> findAllClasses(Pageable pageable) {
        try {
            return schoolClassRepository.findAll(pageable);
        } catch (Exception e) {
            System.err.println("查找所有班级失败: " + e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalClasses() {
        try {
            return schoolClassRepository.count();
        } catch (Exception e) {
            System.err.println("统计班级总数失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveClasses() {
        try {
            return schoolClassRepository.findAll()
                .stream()
                .filter(schoolClass -> schoolClass.getStatus() == 1 && schoolClass.getDeleted() == 0)
                .count();
        } catch (Exception e) {
            System.err.println("统计活跃班级数失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAllGrades() {
        try {
            return schoolClassRepository.findAll()
                .stream()
                .map(SchoolClass::getGrade)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("获取所有年级失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SchoolClass findClassById(Long id) {
        try {
            return schoolClassRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("根据ID查找班级失败: " + e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countStudentsByClass(Long classId) {
        try {
            List<Student> students = studentRepository.findByClassIdAndDeletedOrderByStudentNoAsc(classId, 0);
            return students.size();
        } catch (Exception e) {
            System.err.println("统计班级学生数量失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countCoursesByClass(Long classId) {
        try {
            // 模拟统计班级课程数量
            // 在实际项目中，需要根据课程选择表或课程安排表来统计
            return 8; // 模拟数据
        } catch (Exception e) {
            System.err.println("统计班级课程数量失败: " + e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional
    public boolean enableClass(Long classId) {
        try {
            SchoolClass schoolClass = schoolClassRepository.findById(classId).orElse(null);
            if (schoolClass != null) {
                schoolClass.setStatus(1);
                schoolClass.setUpdatedAt(LocalDateTime.now());
                schoolClassRepository.save(schoolClass);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("启用班级失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean disableClass(Long classId) {
        try {
            SchoolClass schoolClass = schoolClassRepository.findById(classId).orElse(null);
            if (schoolClass != null) {
                schoolClass.setStatus(0);
                schoolClass.setUpdatedAt(LocalDateTime.now());
                schoolClassRepository.save(schoolClass);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("禁用班级失败: " + e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> countClassesByDepartment() {
        try {
            Map<String, Long> departmentStats = new HashMap<>();

            // 模拟按部门统计班级数量
            departmentStats.put("计算机学院", 15L);
            departmentStats.put("数学学院", 12L);
            departmentStats.put("物理学院", 10L);
            departmentStats.put("化学学院", 8L);
            departmentStats.put("生物学院", 6L);
            departmentStats.put("外语学院", 9L);

            return departmentStats;
        } catch (Exception e) {
            System.err.println("按部门统计班级数量失败: " + e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SchoolClass> findAllClasses() {
        try {
            return schoolClassRepository.findAll();
        } catch (Exception e) {
            System.err.println("获取所有班级失败: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
