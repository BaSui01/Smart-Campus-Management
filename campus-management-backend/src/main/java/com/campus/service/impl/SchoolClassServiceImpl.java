package com.campus.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.entity.SchoolClass;
import com.campus.entity.Student;
import com.campus.repository.SchoolClassRepository;
import com.campus.repository.StudentRepository;
import com.campus.service.SchoolClassService;

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
}
