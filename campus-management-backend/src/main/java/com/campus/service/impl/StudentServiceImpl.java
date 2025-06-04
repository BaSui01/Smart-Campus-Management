package com.campus.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.campus.entity.Student;
import com.campus.repository.StudentRepository;
import com.campus.repository.StudentRepository.StudentGradeCount;
import com.campus.repository.StudentRepository.StudentWithUser;
import com.campus.service.StudentService;

/**
 * 学生服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-03
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentRepository, Student> implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Optional<Student> findByStudentNo(String studentNo) {
        return studentRepository.findByStudentNo(studentNo);
    }

    @Override
    public Optional<Student> findByUserId(Long userId) {
        return studentRepository.findByUserId(userId);
    }

    @Override
    public List<Student> findByClassId(Long classId) {
        return studentRepository.findByClassId(classId);
    }

    @Override
    public List<Student> findByGrade(String grade) {
        return studentRepository.findByGrade(grade);
    }

    @Override
    public boolean existsByStudentNo(String studentNo) {
        return studentRepository.existsByStudentNo(studentNo);
    }

    @Override
    public Optional<StudentWithUser> findStudentWithUser(Long studentId) {
        return studentRepository.findStudentWithUser(studentId);
    }

    @Override
    public List<StudentWithUser> searchStudents(String keyword) {
        return studentRepository.searchStudents(keyword);
    }

    @Override
    public List<StudentGradeCount> countStudentsByGrade() {
        return studentRepository.countStudentsByGrade();
    }

    @Override
    public IPage<Student> findStudentsByPage(int page, int size, Map<String, Object> params) {
        Page<Student> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        if (params != null) {
            // 根据学号查询
            if (params.containsKey("studentNo")) {
                queryWrapper.like(Student::getStudentNo, params.get("studentNo"));
            }

            // 根据年级查询
            if (params.containsKey("grade")) {
                queryWrapper.eq(Student::getGrade, params.get("grade"));
            }

            // 根据班级ID查询
            if (params.containsKey("classId")) {
                queryWrapper.eq(Student::getClassId, params.get("classId"));
            }

            // 根据状态查询
            if (params.containsKey("status")) {
                queryWrapper.eq(Student::getStatus, params.get("status"));
            }
        }

        // 默认按学号排序
        queryWrapper.orderByAsc(Student::getStudentNo);

        return page(pageRequest, queryWrapper);
    }

    @Override
    @Transactional
    public Student createStudent(Student student) {
        // 检查学号是否已存在
        if (existsByStudentNo(student.getStudentNo())) {
            throw new IllegalArgumentException("学号已存在：" + student.getStudentNo());
        }

        // 保存学生信息
        save(student);
        return student;
    }

    @Override
    @Transactional
    public boolean updateStudent(Student student) {
        // 检查学生是否存在
        Student existingStudent = getById(student.getId());
        if (existingStudent == null) {
            return false;
        }

        // 如果学号变更，检查新学号是否已存在
        if (!existingStudent.getStudentNo().equals(student.getStudentNo())
                && existsByStudentNo(student.getStudentNo())) {
            throw new IllegalArgumentException("学号已存在：" + student.getStudentNo());
        }

        // 更新学生信息
        return updateById(student);
    }

    @Override
    @Transactional
    public boolean deleteStudent(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean batchDeleteStudents(List<Long> ids) {
        return removeBatchByIds(ids);
    }

    @Override
    @Transactional
    public Map<String, Object> importStudents(List<Student> studentList) {
        Map<String, Object> result = new HashMap<>();
        List<String> errors = new ArrayList<>();
        int successCount = 0;

        for (Student student : studentList) {
            try {
                // 检查必填字段
                if (student.getStudentNo() == null || student.getStudentNo().isEmpty()) {
                    errors.add("学号不能为空");
                    continue;
                }

                if (student.getGrade() == null || student.getGrade().isEmpty()) {
                    errors.add("年级不能为空：" + student.getStudentNo());
                    continue;
                }

                if (student.getUserId() == null) {
                    errors.add("用户ID不能为空：" + student.getStudentNo());
                    continue;
                }

                // 检查学号是否已存在
                if (existsByStudentNo(student.getStudentNo())) {
                    errors.add("学号已存在：" + student.getStudentNo());
                    continue;
                }

                // 保存学生信息
                save(student);
                successCount++;
            } catch (Exception e) {
                errors.add("导入失败：" + student.getStudentNo() + "，原因：" + e.getMessage());
            }
        }

        result.put("success", successCount);
        result.put("errors", errors);
        result.put("total", studentList.size());

        return result;
    }

    @Override
    public List<Student> exportStudents(Map<String, Object> params) {
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        if (params != null) {
            // 根据年级查询
            if (params.containsKey("grade")) {
                queryWrapper.eq(Student::getGrade, params.get("grade"));
            }

            // 根据班级ID查询
            if (params.containsKey("classId")) {
                queryWrapper.eq(Student::getClassId, params.get("classId"));
            }

            // 根据状态查询
            if (params.containsKey("status")) {
                queryWrapper.eq(Student::getStatus, params.get("status"));
            }
        }

        // 默认按学号排序
        queryWrapper.orderByAsc(Student::getStudentNo);

        return list(queryWrapper);
    }
}
