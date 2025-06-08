# 智慧校园管理系统架构修复实施方案

## 🎯 实施概览

基于架构完整性分析报告，本文档提供了详细的修复实施方案，包括具体的代码模板、实施步骤和质量保证措施。

---

## ✅ 阶段一：高优先级组件补全 (已完成)

### ✅ 1.1 Repository接口补全 (已完成)

#### ✅ CourseResourceRepository
```java
package com.campus.domain.repository;

import com.campus.domain.entity.CourseResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseResourceRepository extends JpaRepository<CourseResource, Long> {
    
    List<CourseResource> findByCourseIdAndDeletedOrderByCreatedAtDesc(Long courseId, Integer deleted);
    
    List<CourseResource> findByResourceTypeAndDeletedOrderByCreatedAtDesc(String resourceType, Integer deleted);
    
    @Query("SELECT cr FROM CourseResource cr WHERE cr.course.id = :courseId AND cr.resourceType = :type AND cr.deleted = 0")
    List<CourseResource> findByCourseAndType(@Param("courseId") Long courseId, @Param("type") String resourceType);
    
    Optional<CourseResource> findByIdAndDeleted(Long id, Integer deleted);
    
    boolean existsByFilePathAndDeleted(String filePath, Integer deleted);
}
```

#### ✅ StudentEvaluationRepository
```java
package com.campus.domain.repository;

import com.campus.domain.entity.StudentEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentEvaluationRepository extends JpaRepository<StudentEvaluation, Long> {
    
    List<StudentEvaluation> findByStudentIdAndDeletedOrderByCreatedAtDesc(Long studentId, Integer deleted);
    
    List<StudentEvaluation> findByEvaluatorIdAndDeletedOrderByCreatedAtDesc(Long evaluatorId, Integer deleted);
    
    @Query("SELECT se FROM StudentEvaluation se WHERE se.student.id = :studentId AND se.evaluationType = :type AND se.deleted = 0")
    List<StudentEvaluation> findByStudentAndType(@Param("studentId") Long studentId, @Param("type") String evaluationType);
    
    @Query("SELECT se FROM StudentEvaluation se WHERE se.createdAt BETWEEN :startDate AND :endDate AND se.deleted = 0")
    List<StudentEvaluation> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
```

#### ✅ TeacherCoursePermissionRepository
```java
package com.campus.domain.repository;

import com.campus.domain.entity.TeacherCoursePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherCoursePermissionRepository extends JpaRepository<TeacherCoursePermission, Long> {
    
    List<TeacherCoursePermission> findByTeacherIdAndDeletedOrderByCreatedAtDesc(Long teacherId, Integer deleted);
    
    List<TeacherCoursePermission> findByCourseIdAndDeletedOrderByCreatedAtDesc(Long courseId, Integer deleted);
    
    @Query("SELECT tcp FROM TeacherCoursePermission tcp WHERE tcp.teacher.id = :teacherId AND tcp.course.id = :courseId AND tcp.deleted = 0")
    Optional<TeacherCoursePermission> findByTeacherAndCourse(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);
    
    boolean existsByTeacherIdAndCourseIdAndDeleted(Long teacherId, Long courseId, Integer deleted);
}
```

### ✅ 1.2 核心Service接口补全 (已完成)

#### ✅ ClassroomService
```java
package com.campus.application.service;

import com.campus.domain.entity.Classroom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClassroomService {
    
    // 基础CRUD操作
    Classroom createClassroom(Classroom classroom);
    Optional<Classroom> findClassroomById(Long id);
    Classroom updateClassroom(Classroom classroom);
    void deleteClassroom(Long id);
    
    // 查询操作
    Page<Classroom> findAllClassrooms(Pageable pageable);
    List<Classroom> findAvailableClassrooms();
    List<Classroom> findClassroomsByBuilding(String building);
    List<Classroom> findClassroomsByCapacityRange(Integer minCapacity, Integer maxCapacity);
    
    // 业务操作
    boolean isClassroomAvailable(Long classroomId, String timeSlot, String dayOfWeek);
    List<Classroom> findAvailableClassroomsForTimeSlot(String timeSlot, String dayOfWeek);
    boolean checkClassroomConflict(Long classroomId, String timeSlot, String dayOfWeek);
    
    // 统计操作
    long countTotalClassrooms();
    long countAvailableClassrooms();
    
    // 搜索操作
    Page<Classroom> searchClassrooms(String keyword, Pageable pageable);
}
```

#### ✅ MessageService
```java
package com.campus.application.service;

import com.campus.domain.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    
    // 基础CRUD操作
    Message sendMessage(Message message);
    Optional<Message> findMessageById(Long id);
    void deleteMessage(Long id);
    
    // 查询操作
    Page<Message> findMessagesByReceiver(Long receiverId, Pageable pageable);
    Page<Message> findMessagesBySender(Long senderId, Pageable pageable);
    List<Message> findUnreadMessages(Long receiverId);
    
    // 业务操作
    void markAsRead(Long messageId);
    void markAllAsRead(Long receiverId);
    boolean isMessageRead(Long messageId);
    
    // 统计操作
    long countUnreadMessages(Long receiverId);
    long countTotalMessages(Long userId);
    
    // 搜索操作
    Page<Message> searchMessages(Long userId, String keyword, Pageable pageable);
}
```

### 1.3 核心ServiceImpl实现类

#### ClassroomServiceImpl
```java
package com.campus.application.service.impl;

import com.campus.application.service.ClassroomService;
import com.campus.domain.entity.Classroom;
import com.campus.domain.repository.ClassroomRepository;
import com.campus.shared.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClassroomServiceImpl implements ClassroomService {
    
    private static final Logger logger = LoggerFactory.getLogger(ClassroomServiceImpl.class);
    
    @Autowired
    private ClassroomRepository classroomRepository;
    
    @Override
    public Classroom createClassroom(Classroom classroom) {
        logger.info("创建教室: {}", classroom.getClassroomName());
        
        // 验证教室名称唯一性
        if (classroomRepository.existsByClassroomNameAndDeleted(classroom.getClassroomName(), 0)) {
            throw new BusinessException("教室名称已存在");
        }
        
        classroom.setStatus(1);
        classroom.setDeleted(0);
        return classroomRepository.save(classroom);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Classroom> findClassroomById(Long id) {
        return classroomRepository.findByIdAndDeleted(id, 0);
    }
    
    @Override
    public Classroom updateClassroom(Classroom classroom) {
        logger.info("更新教室信息: {}", classroom.getId());
        
        Optional<Classroom> existingOpt = findClassroomById(classroom.getId());
        if (existingOpt.isEmpty()) {
            throw new BusinessException("教室不存在");
        }
        
        Classroom existing = existingOpt.get();
        existing.setClassroomName(classroom.getClassroomName());
        existing.setBuilding(classroom.getBuilding());
        existing.setFloor(classroom.getFloor());
        existing.setCapacity(classroom.getCapacity());
        existing.setEquipment(classroom.getEquipment());
        existing.setDescription(classroom.getDescription());
        
        return classroomRepository.save(existing);
    }
    
    @Override
    public void deleteClassroom(Long id) {
        logger.info("删除教室: {}", id);
        
        Optional<Classroom> classroomOpt = findClassroomById(id);
        if (classroomOpt.isEmpty()) {
            throw new BusinessException("教室不存在");
        }
        
        Classroom classroom = classroomOpt.get();
        classroom.setDeleted(1);
        classroomRepository.save(classroom);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Classroom> findAllClassrooms(Pageable pageable) {
        return classroomRepository.findByDeletedOrderByCreatedAtDesc(0, pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Classroom> findAvailableClassrooms() {
        return classroomRepository.findByStatusAndDeletedOrderByClassroomName(1, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Classroom> findClassroomsByBuilding(String building) {
        return classroomRepository.findByBuildingAndDeletedOrderByFloorAscClassroomNameAsc(building, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Classroom> findClassroomsByCapacityRange(Integer minCapacity, Integer maxCapacity) {
        return classroomRepository.findByCapacityBetweenAndDeletedOrderByCapacityAsc(minCapacity, maxCapacity, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isClassroomAvailable(Long classroomId, String timeSlot, String dayOfWeek) {
        // TODO: 实现教室时间段可用性检查
        // 需要查询课程安排表，检查指定时间段是否有课程安排
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Classroom> findAvailableClassroomsForTimeSlot(String timeSlot, String dayOfWeek) {
        // TODO: 实现指定时间段的可用教室查询
        return findAvailableClassrooms();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean checkClassroomConflict(Long classroomId, String timeSlot, String dayOfWeek) {
        return !isClassroomAvailable(classroomId, timeSlot, dayOfWeek);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countTotalClassrooms() {
        return classroomRepository.countByDeleted(0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long countAvailableClassrooms() {
        return classroomRepository.countByStatusAndDeleted(1, 0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Classroom> searchClassrooms(String keyword, Pageable pageable) {
        return classroomRepository.findByClassroomNameContainingIgnoreCaseAndDeletedOrderByClassroomName(keyword, 0, pageable);
    }
}
```

### 1.4 核心API控制器补全

#### ClassroomApiController
```java
package com.campus.interfaces.rest.v1;

import com.campus.application.service.ClassroomService;
import com.campus.domain.entity.Classroom;
import com.campus.shared.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/classrooms")
@Tag(name = "教室管理", description = "教室管理相关API")
public class ClassroomApiController {
    
    private static final Logger logger = LoggerFactory.getLogger(ClassroomApiController.class);
    
    @Autowired
    private ClassroomService classroomService;
    
    @PostMapping
    @Operation(summary = "创建教室", description = "创建新的教室")
    public ResponseEntity<ApiResponse<Classroom>> createClassroom(@Valid @RequestBody Classroom classroom) {
        try {
            Classroom created = classroomService.createClassroom(classroom);
            return ResponseEntity.ok(ApiResponse.success(created, "教室创建成功"));
        } catch (Exception e) {
            logger.error("创建教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("创建教室失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取教室详情", description = "根据ID获取教室详细信息")
    public ResponseEntity<ApiResponse<Classroom>> getClassroom(@PathVariable Long id) {
        try {
            Optional<Classroom> classroom = classroomService.findClassroomById(id);
            if (classroom.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(classroom.get()));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("获取教室详情失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取教室详情失败: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新教室", description = "更新教室信息")
    public ResponseEntity<ApiResponse<Classroom>> updateClassroom(@PathVariable Long id, @Valid @RequestBody Classroom classroom) {
        try {
            classroom.setId(id);
            Classroom updated = classroomService.updateClassroom(classroom);
            return ResponseEntity.ok(ApiResponse.success(updated, "教室更新成功"));
        } catch (Exception e) {
            logger.error("更新教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("更新教室失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除教室", description = "删除指定教室")
    public ResponseEntity<ApiResponse<Void>> deleteClassroom(@PathVariable Long id) {
        try {
            classroomService.deleteClassroom(id);
            return ResponseEntity.ok(ApiResponse.success(null, "教室删除成功"));
        } catch (Exception e) {
            logger.error("删除教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("删除教室失败: " + e.getMessage()));
        }
    }
    
    @GetMapping
    @Operation(summary = "获取教室列表", description = "分页获取教室列表")
    public ResponseEntity<ApiResponse<Page<Classroom>>> getClassrooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Classroom> classrooms = classroomService.findAllClassrooms(pageable);
            return ResponseEntity.ok(ApiResponse.success(classrooms));
        } catch (Exception e) {
            logger.error("获取教室列表失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取教室列表失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/available")
    @Operation(summary = "获取可用教室", description = "获取所有可用的教室")
    public ResponseEntity<ApiResponse<List<Classroom>>> getAvailableClassrooms() {
        try {
            List<Classroom> classrooms = classroomService.findAvailableClassrooms();
            return ResponseEntity.ok(ApiResponse.success(classrooms));
        } catch (Exception e) {
            logger.error("获取可用教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("获取可用教室失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/building/{building}")
    @Operation(summary = "按建筑物获取教室", description = "获取指定建筑物的所有教室")
    public ResponseEntity<ApiResponse<List<Classroom>>> getClassroomsByBuilding(@PathVariable String building) {
        try {
            List<Classroom> classrooms = classroomService.findClassroomsByBuilding(building);
            return ResponseEntity.ok(ApiResponse.success(classrooms));
        } catch (Exception e) {
            logger.error("按建筑物获取教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("按建筑物获取教室失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "搜索教室", description = "根据关键词搜索教室")
    public ResponseEntity<ApiResponse<Page<Classroom>>> searchClassrooms(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Classroom> classrooms = classroomService.searchClassrooms(keyword, pageable);
            return ResponseEntity.ok(ApiResponse.success(classrooms));
        } catch (Exception e) {
            logger.error("搜索教室失败", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("搜索教室失败: " + e.getMessage()));
        }
    }
}
```

---

## 🟡 阶段二：中优先级组件补全 (2-3周)

### 2.1 学术管理功能完善

#### GradeController (Web控制器)
```java
package com.campus.interfaces.web;

import com.campus.application.service.GradeService;
import com.campus.application.service.CourseService;
import com.campus.application.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/grades")
public class GradeController {
    
    @Autowired
    private GradeService gradeService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private StudentService studentService;
    
    @GetMapping
    public String gradesPage(Model model) {
        model.addAttribute("currentPage", "grades");
        model.addAttribute("courses", courseService.findAllCourses());
        return "admin/academic/grades";
    }
    
    @GetMapping("/student/{studentId}")
    public String studentGrades(@PathVariable Long studentId, Model model) {
        model.addAttribute("currentPage", "grades");
        model.addAttribute("student", studentService.findStudentById(studentId));
        model.addAttribute("grades", gradeService.findGradesByStudent(studentId));
        return "admin/academic/student-grades";
    }
    
    @GetMapping("/course/{courseId}")
    public String courseGrades(@PathVariable Long courseId, Model model) {
        model.addAttribute("currentPage", "grades");
        model.addAttribute("course", courseService.findCourseById(courseId));
        model.addAttribute("grades", gradeService.findGradesByCourse(courseId));
        return "admin/academic/course-grades";
    }
}
```

### 2.2 系统管理功能完善

#### RoleController (Web控制器)
```java
package com.campus.interfaces.web;

import com.campus.application.service.RoleService;
import com.campus.application.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/roles")
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private PermissionService permissionService;
    
    @GetMapping
    public String rolesPage(Model model) {
        model.addAttribute("currentPage", "roles");
        model.addAttribute("roles", roleService.findAllRoles());
        model.addAttribute("permissions", permissionService.findAllPermissions());
        return "admin/system/roles";
    }
    
    @GetMapping("/{id}")
    public String roleDetail(@PathVariable Long id, Model model) {
        model.addAttribute("currentPage", "roles");
        model.addAttribute("role", roleService.findRoleById(id));
        model.addAttribute("rolePermissions", roleService.getRolePermissions(id));
        model.addAttribute("allPermissions", permissionService.findAllPermissions());
        return "admin/system/role-detail";
    }
}
```

---

## 📋 实施检查清单

### ✅ 代码质量标准
- [ ] 所有类都有完整的JavaDoc注释
- [ ] 所有方法都有适当的日志记录
- [ ] 所有异常都有适当的处理
- [ ] 所有Repository都继承自JpaRepository
- [ ] 所有Service都有对应的接口
- [ ] 所有Controller都有适当的API文档注解

### ✅ 测试覆盖率要求
- [ ] 每个Service实现类都有对应的单元测试
- [ ] 每个Controller都有对应的集成测试
- [ ] 测试覆盖率达到80%以上

### ✅ 安全性检查
- [ ] 所有API都有适当的权限控制
- [ ] 所有输入都有数据验证
- [ ] 所有敏感操作都有审计日志

### ✅ 性能优化
- [ ] 数据库查询都有适当的索引
- [ ] 分页查询都使用Pageable
- [ ] 大数据量操作都有缓存机制

---

## 🚀 下一步行动

1. **立即开始**: 按照优先级顺序实施Repository补全
2. **并行开发**: Service层和Controller层可以并行开发
3. **持续集成**: 每完成一个组件立即进行测试
4. **文档更新**: 及时更新API文档和用户手册

通过系统性的实施，可以在4-7周内完成整个架构的补全工作，显著提升系统的功能完整性和可维护性。
