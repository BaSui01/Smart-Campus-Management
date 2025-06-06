package com.campus.application.service.impl;

import com.campus.application.service.DepartmentService;
import com.campus.domain.entity.Department;
import com.campus.domain.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 院系服务实现类
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-06
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"departments", "departmentTree"}, allEntries = true)
    public Department createDepartment(Department department) {
        log.info("Creating department: {}", department.getDeptName());
        
        // 验证院系代码唯一性
        if (existsByDeptCode(department.getDeptCode())) {
            throw new IllegalArgumentException("院系代码已存在: " + department.getDeptCode());
        }

        // 验证院系名称唯一性
        if (existsByDeptName(department.getDeptName())) {
            throw new IllegalArgumentException("院系名称已存在: " + department.getDeptName());
        }

        // 验证上级院系是否存在
        if (department.getParentId() != null) {
            if (!departmentRepository.existsById(department.getParentId())) {
                throw new IllegalArgumentException("上级院系不存在");
            }
        }
        
        // 设置默认排序
        if (department.getSortOrder() == null) {
            department.setSortOrder(0);
        }
        
        Department savedDepartment = departmentRepository.save(department);
        log.info("Department created successfully with ID: {}", savedDepartment.getId());
        
        return savedDepartment;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"departments", "departmentTree"}, allEntries = true)
    public Department updateDepartment(Long id, Department department) {
        log.info("Updating department with ID: {}", id);
        
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("院系不存在，ID: " + id));
        
        // 验证院系代码唯一性（排除当前院系）
        if (StringUtils.hasText(department.getDeptCode()) && 
            !department.getDeptCode().equals(existingDepartment.getDeptCode())) {
            if (existsByDeptCodeExcludeId(department.getDeptCode(), id)) {
                throw new IllegalArgumentException("院系代码已存在: " + department.getDeptCode());
            }
        }

        // 验证院系名称唯一性（排除当前院系）
        if (StringUtils.hasText(department.getDeptName()) &&
            !department.getDeptName().equals(existingDepartment.getDeptName())) {
            if (existsByDeptNameExcludeId(department.getDeptName(), id)) {
                throw new IllegalArgumentException("院系名称已存在: " + department.getDeptName());
            }
        }

        // 验证不能将院系设置为自己的子院系
        if (department.getParentId() != null && department.getParentId().equals(id)) {
            throw new IllegalArgumentException("不能将院系设置为自己的上级院系");
        }
        
        // 更新字段
        if (StringUtils.hasText(department.getDeptName())) {
            existingDepartment.setDeptName(department.getDeptName());
        }
        if (StringUtils.hasText(department.getDeptCode())) {
            existingDepartment.setDeptCode(department.getDeptCode());
        }
        if (department.getParentId() != null) {
            existingDepartment.setParentId(department.getParentId());
        }
        if (StringUtils.hasText(department.getDeptType())) {
            existingDepartment.setDeptType(department.getDeptType());
        }
        if (department.getDeptLevel() != null) {
            existingDepartment.setDeptLevel(department.getDeptLevel());
        }
        if (department.getSortOrder() != null) {
            existingDepartment.setSortOrder(department.getSortOrder());
        }
        if (department.getLeaderId() != null) {
            existingDepartment.setLeaderId(department.getLeaderId());
        }
        if (StringUtils.hasText(department.getPhone())) {
            existingDepartment.setPhone(department.getPhone());
        }
        if (StringUtils.hasText(department.getEmail())) {
            existingDepartment.setEmail(department.getEmail());
        }
        if (StringUtils.hasText(department.getAddress())) {
            existingDepartment.setAddress(department.getAddress());
        }
        if (StringUtils.hasText(department.getDescription())) {
            existingDepartment.setDescription(department.getDescription());
        }
        if (StringUtils.hasText(department.getRemarks())) {
            existingDepartment.setRemarks(department.getRemarks());
        }
        
        Department updatedDepartment = departmentRepository.save(existingDepartment);
        log.info("Department updated successfully: {}", updatedDepartment.getId());
        
        return updatedDepartment;
    }

    @Override
    @Cacheable(value = "departments", key = "#id")
    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .filter(dept -> dept.getDeleted() == 0);
    }

    @Override
    @Cacheable(value = "departments", key = "'code:' + #deptCode")
    public Optional<Department> getDepartmentByCode(String deptCode) {
        return departmentRepository.findByDeptCodeAndDeleted(deptCode, 0);
    }

    @Override
    @Cacheable(value = "departments", key = "'all'")
    public List<Department> getAllDepartments() {
        return departmentRepository.findByDeletedOrderBySortOrderAsc(0);
    }

    @Override
    public Page<Department> getDepartments(Pageable pageable) {
        return departmentRepository.findByDeletedOrderByCreatedAtDesc(0, pageable);
    }

    @Override
    public Page<Department> getDepartmentsByConditions(String deptName, String deptCode, 
                                                     String deptType, Integer deptLevel, 
                                                     Pageable pageable) {
        return departmentRepository.findByConditions(0, deptName, deptCode, deptType, deptLevel, pageable);
    }

    @Override
    @Cacheable(value = "departmentTree")
    public List<Department> getDepartmentTree() {
        return departmentRepository.findDepartmentTree();
    }

    @Override
    @Cacheable(value = "departments", key = "'topLevel'")
    public List<Department> getTopLevelDepartments() {
        return departmentRepository.findByParentIdIsNullAndDeletedOrderBySortOrderAsc(0);
    }

    @Override
    @Cacheable(value = "departments", key = "'children:' + #parentId")
    public List<Department> getChildDepartments(Long parentId) {
        return departmentRepository.findByParentIdAndDeletedOrderBySortOrderAsc(parentId, 0);
    }

    @Override
    public List<Department> getDepartmentsByType(String deptType) {
        return departmentRepository.findByDeptTypeAndDeletedOrderBySortOrderAsc(deptType, 0);
    }

    @Override
    public List<Department> getDepartmentsByLevel(Integer deptLevel) {
        return departmentRepository.findByDeptLevelAndDeletedOrderBySortOrderAsc(deptLevel, 0);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"departments", "departmentTree"}, allEntries = true)
    public void deleteDepartment(Long id) {
        log.info("Deleting department with ID: {}", id);
        
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("院系不存在，ID: " + id));

        // 检查是否有子院系
        long childCount = countChildDepartments(id);
        if (childCount > 0) {
            throw new IllegalArgumentException("存在子院系，无法删除");
        }
        
        // 软删除
        department.markAsDeleted();
        departmentRepository.save(department);
        
        log.info("Department deleted successfully: {}", id);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"departments", "departmentTree"}, allEntries = true)
    public void deleteDepartments(List<Long> ids) {
        log.info("Batch deleting departments: {}", ids);
        
        for (Long id : ids) {
            deleteDepartment(id);
        }
        
        log.info("Batch deletion completed for {} departments", ids.size());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"departments", "departmentTree"}, allEntries = true)
    public void enableDepartment(Long id) {
        log.info("Enabling department with ID: {}", id);
        
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("院系不存在，ID: " + id));
        
        department.enable();
        departmentRepository.save(department);
        
        log.info("Department enabled successfully: {}", id);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"departments", "departmentTree"}, allEntries = true)
    public void disableDepartment(Long id) {
        log.info("Disabling department with ID: {}", id);
        
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("院系不存在，ID: " + id));
        
        department.disable();
        departmentRepository.save(department);
        
        log.info("Department disabled successfully: {}", id);
    }

    @Override
    public boolean existsByDeptCode(String deptCode) {
        return departmentRepository.existsByDeptCodeAndDeleted(deptCode, 0);
    }

    @Override
    public boolean existsByDeptName(String deptName) {
        return departmentRepository.existsByDeptNameAndDeleted(deptName, 0);
    }

    @Override
    public boolean existsByDeptCodeExcludeId(String deptCode, Long excludeId) {
        return departmentRepository.existsByDeptCodeAndDeletedAndIdNot(deptCode, 0, excludeId);
    }

    @Override
    public boolean existsByDeptNameExcludeId(String deptName, Long excludeId) {
        return departmentRepository.existsByDeptNameAndDeletedAndIdNot(deptName, 0, excludeId);
    }

    @Override
    public long countDepartments() {
        return departmentRepository.countByDeleted(0);
    }

    @Override
    public long countChildDepartments(Long parentId) {
        return departmentRepository.countByParentIdAndDeleted(parentId, 0);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"departments", "departmentTree"}, allEntries = true)
    public void updateSortOrder(Long id, Integer sortOrder) {
        log.info("Updating sort order for department {} to {}", id, sortOrder);
        
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("院系不存在，ID: " + id));
        
        department.setSortOrder(sortOrder);
        departmentRepository.save(department);
        
        log.info("Sort order updated successfully");
    }

    @Override
    @Transactional
    @CacheEvict(value = {"departments", "departmentTree"}, allEntries = true)
    public void setDepartmentLeader(Long id, Long leaderId) {
        log.info("Setting leader for department {} to user {}", id, leaderId);
        
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("院系不存在，ID: " + id));
        
        department.setLeaderId(leaderId);
        departmentRepository.save(department);
        
        log.info("Department leader set successfully");
    }

    @Override
    @Transactional
    @CacheEvict(value = {"departments", "departmentTree"}, allEntries = true)
    public void moveDepartment(Long id, Long newParentId) {
        log.info("Moving department {} to parent {}", id, newParentId);
        
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("院系不存在，ID: " + id));

        // 验证新上级院系是否存在
        if (newParentId != null && !departmentRepository.existsById(newParentId)) {
            throw new IllegalArgumentException("新上级院系不存在");
        }

        // 验证不能移动到自己的子院系下
        if (newParentId != null && newParentId.equals(id)) {
            throw new IllegalArgumentException("不能将院系移动到自己下面");
        }
        
        department.setParentId(newParentId);
        departmentRepository.save(department);
        
        log.info("Department moved successfully");
    }
}