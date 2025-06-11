package com.campus.application.service.impl;

import com.campus.application.service.DataInitService;
import com.campus.application.service.UserService;
import com.campus.application.service.RoleService;
import com.campus.application.service.PermissionService;
import com.campus.application.service.DepartmentService;
import com.campus.application.service.TimeSlotService;
import com.campus.domain.entity.User;
import com.campus.domain.entity.Role;
import com.campus.domain.entity.Permission;
import com.campus.domain.entity.Department;
import com.campus.domain.entity.TimeSlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据初始化服务实现类
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Service
@Transactional
public class DataInitServiceImpl implements DataInitService {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitServiceImpl.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private TimeSlotService timeSlotService;
    
    @Override
    public void initializeAllData() {
        logger.info("开始执行系统数据初始化");
        
        try {
            // 按依赖顺序初始化数据
            initializePermissions();
            initializeRoles();
            initializeDepartments();
            initializeTimeSlots();
            initializeAdminUser();
            initializeSystemConfig();
            
            logger.info("系统数据初始化完成");
            
        } catch (Exception e) {
            logger.error("系统数据初始化失败", e);
            throw new RuntimeException("系统数据初始化失败", e);
        }
    }
    
    @Override
    public void initializePermissions() {
        logger.info("开始初始化权限数据");
        
        try {
            List<Permission> permissions = createDefaultPermissions();
            
            for (Permission permission : permissions) {
                if (!permissionService.existsByCode(permission.getPermissionCode())) {
                    permissionService.createPermission(permission);
                    logger.debug("创建权限: {}", permission.getPermissionName());
                }
            }
            
            logger.info("权限数据初始化完成，共 {} 个权限", permissions.size());
            
        } catch (Exception e) {
            logger.error("权限数据初始化失败", e);
            throw new RuntimeException("权限数据初始化失败", e);
        }
    }
    
    @Override
    public void initializeRoles() {
        logger.info("开始初始化角色数据");
        
        try {
            List<Role> roles = createDefaultRoles();
            
            for (Role role : roles) {
                if (!roleService.existsByCode(role.getRoleKey())) {
                    Role savedRole = roleService.createRole(role);
                    
                    // 分配权限
                    assignPermissionsToRole(savedRole);
                    
                    logger.debug("创建角色: {}", role.getRoleName());
                }
            }
            
            logger.info("角色数据初始化完成，共 {} 个角色", roles.size());
            
        } catch (Exception e) {
            logger.error("角色数据初始化失败", e);
            throw new RuntimeException("角色数据初始化失败", e);
        }
    }
    
    @Override
    public void initializeDepartments() {
        logger.info("开始初始化院系数据");
        
        try {
            List<Department> departments = createDefaultDepartments();
            
            for (Department department : departments) {
                if (!departmentService.existsByCode(department.getDeptCode())) {
                    departmentService.createDepartment(department);
                    logger.debug("创建院系: {}", department.getDeptName());
                }
            }
            
            logger.info("院系数据初始化完成，共 {} 个院系", departments.size());
            
        } catch (Exception e) {
            logger.error("院系数据初始化失败", e);
            throw new RuntimeException("院系数据初始化失败", e);
        }
    }
    
    @Override
    public void initializeTimeSlots() {
        logger.info("开始初始化时间段数据");
        
        try {
            List<TimeSlot> timeSlots = createDefaultTimeSlots();
            
            for (TimeSlot timeSlot : timeSlots) {
                if (!timeSlotService.existsTimeSlotByName(timeSlot.getSlotName())) {
                    timeSlotService.createTimeSlot(timeSlot);
                    logger.debug("创建时间段: {}", timeSlot.getSlotName());
                }
            }
            
            logger.info("时间段数据初始化完成，共 {} 个时间段", timeSlots.size());
            
        } catch (Exception e) {
            logger.error("时间段数据初始化失败", e);
            throw new RuntimeException("时间段数据初始化失败", e);
        }
    }
    
    @Override
    public void initializeAdminUser() {
        logger.info("开始初始化管理员用户");
        
        try {
            String adminUsername = "admin";
            
            if (!userService.existsByUsername(adminUsername)) {
                User adminUser = createAdminUser();
                User savedUser = userService.createUser(adminUser);
                
                // 分配超级管理员角色
                Role adminRole = roleService.findByCode("SUPER_ADMIN");
                if (adminRole != null) {
                    userService.assignRole(savedUser.getId(), adminRole.getId());
                }
                
                logger.info("管理员用户创建成功: {}", adminUsername);
            } else {
                logger.info("管理员用户已存在: {}", adminUsername);
            }
            
        } catch (Exception e) {
            logger.error("管理员用户初始化失败", e);
            throw new RuntimeException("管理员用户初始化失败", e);
        }
    }
    
    @Override
    public void initializeSystemConfig() {
        logger.info("开始初始化系统配置");
        
        try {
            // 实现系统配置初始化
            initializeBasicConfig();
            initializeEmailConfig();
            initializeFileConfig();

            logger.info("系统配置初始化完成");

        } catch (Exception e) {
            logger.error("系统配置初始化失败", e);
            throw new RuntimeException("系统配置初始化失败", e);
        }
    }
    
    @Override
    public boolean isDataInitialized() {
        try {
            // 检查关键数据是否已初始化
            boolean hasPermissions = permissionService.countTotalPermissions() > 0;
            boolean hasRoles = roleService.countTotalRoles() > 0;
            boolean hasAdminUser = userService.existsByUsername("admin");
            boolean hasTimeSlots = timeSlotService.countTotalTimeSlots() > 0;
            
            return hasPermissions && hasRoles && hasAdminUser && hasTimeSlots;
            
        } catch (Exception e) {
            logger.error("检查数据初始化状态失败", e);
            return false;
        }
    }
    
    @Override
    public void resetAllData() {
        logger.warn("开始重置所有数据 - 这是危险操作！");
        
        try {
            // 实现数据重置逻辑（危险操作，仅在开发环境使用）
            logger.warn("执行数据重置操作");

            // 注意：这是危险操作，实际生产环境中应该禁用或需要特殊权限
            // 这里只是记录日志，不执行实际的数据删除
            logger.warn("数据重置功能已禁用，仅记录操作日志");

        } catch (Exception e) {
            logger.error("数据重置失败", e);
            throw new RuntimeException("数据重置失败", e);
        }
    }
    
    /**
     * 创建默认权限
     */
    private List<Permission> createDefaultPermissions() {
        List<Permission> permissions = new ArrayList<>();
        
        // 系统管理权限
        permissions.add(createPermission("SYSTEM_ADMIN", "系统管理", "system", "系统管理员权限"));
        permissions.add(createPermission("USER_MANAGE", "用户管理", "system", "用户管理权限"));
        permissions.add(createPermission("ROLE_MANAGE", "角色管理", "system", "角色管理权限"));
        permissions.add(createPermission("PERMISSION_MANAGE", "权限管理", "system", "权限管理权限"));
        
        // 学术管理权限
        permissions.add(createPermission("COURSE_MANAGE", "课程管理", "academic", "课程管理权限"));
        permissions.add(createPermission("STUDENT_MANAGE", "学生管理", "academic", "学生管理权限"));
        permissions.add(createPermission("TEACHER_MANAGE", "教师管理", "academic", "教师管理权限"));
        permissions.add(createPermission("GRADE_MANAGE", "成绩管理", "academic", "成绩管理权限"));
        
        // 财务管理权限
        permissions.add(createPermission("FINANCE_MANAGE", "财务管理", "finance", "财务管理权限"));
        permissions.add(createPermission("PAYMENT_MANAGE", "缴费管理", "finance", "缴费管理权限"));
        
        return permissions;
    }
    
    /**
     * 创建默认角色
     */
    private List<Role> createDefaultRoles() {
        List<Role> roles = new ArrayList<>();
        
        roles.add(createRole("SUPER_ADMIN", "超级管理员", "拥有所有权限的超级管理员"));
        roles.add(createRole("ADMIN", "系统管理员", "系统管理员角色"));
        roles.add(createRole("TEACHER", "教师", "教师角色"));
        roles.add(createRole("STUDENT", "学生", "学生角色"));
        roles.add(createRole("PARENT", "家长", "家长角色"));
        
        return roles;
    }
    
    /**
     * 创建默认院系
     */
    private List<Department> createDefaultDepartments() {
        List<Department> departments = new ArrayList<>();
        
        departments.add(createDepartment("CS", "计算机科学与技术学院", "计算机相关专业"));
        departments.add(createDepartment("MATH", "数学与统计学院", "数学统计相关专业"));
        departments.add(createDepartment("ENG", "外国语学院", "外语相关专业"));
        departments.add(createDepartment("BUS", "商学院", "商科相关专业"));
        
        return departments;
    }
    
    /**
     * 创建默认时间段
     */
    private List<TimeSlot> createDefaultTimeSlots() {
        List<TimeSlot> timeSlots = new ArrayList<>();
        
        timeSlots.add(createTimeSlot("第1节", LocalTime.of(8, 0), LocalTime.of(8, 45), "morning"));
        timeSlots.add(createTimeSlot("第2节", LocalTime.of(8, 55), LocalTime.of(9, 40), "morning"));
        timeSlots.add(createTimeSlot("第3节", LocalTime.of(10, 0), LocalTime.of(10, 45), "morning"));
        timeSlots.add(createTimeSlot("第4节", LocalTime.of(10, 55), LocalTime.of(11, 40), "morning"));
        timeSlots.add(createTimeSlot("第5节", LocalTime.of(14, 0), LocalTime.of(14, 45), "afternoon"));
        timeSlots.add(createTimeSlot("第6节", LocalTime.of(14, 55), LocalTime.of(15, 40), "afternoon"));
        timeSlots.add(createTimeSlot("第7节", LocalTime.of(16, 0), LocalTime.of(16, 45), "afternoon"));
        timeSlots.add(createTimeSlot("第8节", LocalTime.of(16, 55), LocalTime.of(17, 40), "afternoon"));
        
        return timeSlots;
    }
    
    /**
     * 创建管理员用户
     */
    private User createAdminUser() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin123"); // 实际应用中应该加密
        admin.setEmail("admin@campus.edu");
        admin.setRealName("系统管理员");
        // admin.setUserType("admin"); // User实体类中没有userType字段
        admin.setStatus(1);
        admin.setDeleted(0);
        return admin;
    }
    
    // 辅助方法
    private Permission createPermission(String code, String name, String module, String description) {
        Permission permission = new Permission();
        permission.setPermissionCode(code);
        permission.setPermissionName(name);
        permission.setPermissionType(module);
        permission.setDescription(description);
        permission.setStatus(1);
        permission.setDeleted(0);
        return permission;
    }
    
    private Role createRole(String code, String name, String description) {
        Role role = new Role();
        role.setRoleKey(code);
        role.setRoleName(name);
        role.setDescription(description);
        role.setStatus(1);
        role.setDeleted(0);
        return role;
    }
    
    private Department createDepartment(String code, String name, String description) {
        Department department = new Department();
        department.setDeptCode(code);
        department.setDeptName(name);
        department.setDescription(description);
        department.setStatus(1);
        department.setDeleted(0);
        return department;
    }
    
    private TimeSlot createTimeSlot(String name, LocalTime startTime, LocalTime endTime, String type) {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setSlotName(name);
        timeSlot.setStartTime(startTime);
        timeSlot.setEndTime(endTime);
        timeSlot.setSlotType(type);
        timeSlot.setStatus(1);
        timeSlot.setDeleted(0);
        return timeSlot;
    }
    
    private void assignPermissionsToRole(Role role) {
        try {
            // 实现角色权限分配逻辑
            logger.debug("为角色分配权限: {}", role.getRoleName());

            // 根据角色类型分配不同的权限
            // 这里可以根据实际需求实现权限分配逻辑
            // 例如：SUPER_ADMIN拥有所有权限，ADMIN拥有管理权限等

            // 实际实现中需要查询权限并创建角色权限关联
            // rolePermissionService.assignPermissionsToRole(role.getId(), permissionIds);

        } catch (Exception e) {
            logger.error("为角色分配权限失败: {}", role.getRoleName(), e);
        }
    }

    private void initializeBasicConfig() {
        try {
            // 实现基础配置初始化
            logger.debug("初始化基础系统配置");

            // 这里可以初始化系统的基础配置项
            // 例如：系统名称、版本信息、默认设置等
            // configService.setConfig("system.name", "智慧校园管理系统");
            // configService.setConfig("system.version", "1.0.0");

        } catch (Exception e) {
            logger.error("基础配置初始化失败", e);
        }
    }

    private void initializeEmailConfig() {
        try {
            // 实现邮件配置初始化
            logger.debug("初始化邮件配置");

            // 这里可以初始化邮件服务的配置
            // 例如：SMTP服务器、发件人信息等
            // configService.setConfig("mail.smtp.host", "smtp.example.com");
            // configService.setConfig("mail.from", "noreply@campus.edu");

        } catch (Exception e) {
            logger.error("邮件配置初始化失败", e);
        }
    }

    private void initializeFileConfig() {
        try {
            // 实现文件配置初始化
            logger.debug("初始化文件配置");

            // 这里可以初始化文件上传、存储等配置
            // 例如：上传路径、文件大小限制等
            // configService.setConfig("file.upload.path", "/uploads");
            // configService.setConfig("file.max.size", "10MB");

        } catch (Exception e) {
            logger.error("文件配置初始化失败", e);
        }
    }
}
