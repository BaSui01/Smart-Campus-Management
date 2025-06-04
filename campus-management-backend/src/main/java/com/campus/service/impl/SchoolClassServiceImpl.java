package com.campus.service.impl;

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
import com.campus.entity.SchoolClass;
import com.campus.entity.Student;
import com.campus.repository.SchoolClassRepository;
import com.campus.repository.SchoolClassRepository.ClassDetail;
import com.campus.repository.SchoolClassRepository.ClassGradeCount;
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
public class SchoolClassServiceImpl extends ServiceImpl<SchoolClassRepository, SchoolClass> implements SchoolClassService {

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Optional<SchoolClass> findByClassCode(String classCode) {
        return schoolClassRepository.findByClassCode(classCode);
    }

    @Override
    public List<SchoolClass> findByGrade(String grade) {
        return schoolClassRepository.findByGrade(grade);
    }

    @Override
    public List<SchoolClass> findByDepartmentId(Long departmentId) {
        return schoolClassRepository.findByDepartmentId(departmentId);
    }

    @Override
    public List<SchoolClass> findByHeadTeacherId(Long headTeacherId) {
        return schoolClassRepository.findByHeadTeacherId(headTeacherId);
    }

    @Override
    public boolean existsByClassCode(String classCode) {
        return schoolClassRepository.existsByClassCode(classCode);
    }

    @Override
    public Optional<ClassDetail> findClassDetailById(Long classId) {
        return schoolClassRepository.findClassDetailById(classId);
    }

    @Override
    public List<ClassGradeCount> countClassesByGrade() {
        return schoolClassRepository.countClassesByGrade();
    }

    @Override
    public IPage<SchoolClass> findClassesByPage(int page, int size, Map<String, Object> params) {
        Page<SchoolClass> pageRequest = new Page<>(page, size);
        LambdaQueryWrapper<SchoolClass> queryWrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        if (params != null) {
            // 根据班级名称查询
            if (params.containsKey("className")) {
                queryWrapper.like(SchoolClass::getClassName, params.get("className"));
            }

            // 根据班级代码查询
            if (params.containsKey("classCode")) {
                queryWrapper.like(SchoolClass::getClassCode, params.get("classCode"));
            }

            // 根据年级查询
            if (params.containsKey("grade")) {
                queryWrapper.eq(SchoolClass::getGrade, params.get("grade"));
            }

            // 根据部门ID查询
            if (params.containsKey("departmentId")) {
                queryWrapper.eq(SchoolClass::getDepartmentId, params.get("departmentId"));
            }

            // 根据班主任ID查询
            if (params.containsKey("headTeacherId")) {
                queryWrapper.eq(SchoolClass::getHeadTeacherId, params.get("headTeacherId"));
            }

            // 根据状态查询
            if (params.containsKey("status")) {
                queryWrapper.eq(SchoolClass::getStatus, params.get("status"));
            }
        }

        // 默认按年级和班级代码排序
        queryWrapper.orderByAsc(SchoolClass::getGrade)
                   .orderByAsc(SchoolClass::getClassCode);

        return page(pageRequest, queryWrapper);
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
        SchoolClass existingClass = getById(schoolClass.getId());
        if (existingClass == null) {
            return false;
        }

        // 如果班级代码变更，检查新代码是否已存在
        if (!existingClass.getClassCode().equals(schoolClass.getClassCode())
                && existsByClassCode(schoolClass.getClassCode())) {
            throw new IllegalArgumentException("班级代码已存在：" + schoolClass.getClassCode());
        }

        // 更新班级信息
        return updateById(schoolClass);
    }

    @Override
    @Transactional
    public boolean deleteClass(Long id) {
        // 检查班级是否有关联的学生
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getClassId, id);
        long count = studentRepository.selectCount(queryWrapper);
        if (count > 0) {
            throw new IllegalStateException("班级存在关联的学生，无法删除");
        }

        // 删除班级
        return removeById(id);
    }

    @Override
    @Transactional
    public boolean batchDeleteClasses(List<Long> ids) {
        // 检查班级是否有关联的学生
        for (Long id : ids) {
            LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Student::getClassId, id);
            long count = studentRepository.selectCount(queryWrapper);
            if (count > 0) {
                throw new IllegalStateException("班级ID " + id + " 存在关联的学生，无法删除");
            }
        }

        // 批量删除班级
        return removeBatchByIds(ids);
    }

    @Override
    @Transactional
    public boolean updateStudentCount(Long classId) {
        // 检查班级是否存在
        SchoolClass schoolClass = getById(classId);
        if (schoolClass == null) {
            return false;
        }

        // 统计班级学生数量
        LambdaQueryWrapper<Student> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Student::getClassId, classId);
        long count = studentRepository.selectCount(queryWrapper);

        // 更新班级学生数量
        schoolClass.setStudentCount((int) count);
        return updateById(schoolClass);
    }
}
