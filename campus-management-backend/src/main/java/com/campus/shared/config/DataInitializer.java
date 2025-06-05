package com.campus.shared.config;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


/**
 * 数据初始化器
 * 在应用启动时初始化必要的基础数据
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        logger.info("开始初始化基础数据...");
        
        try {
            // 初始化角色数据
            initRoles();
            
            // 初始化权限数据
            initPermissions();
            
            // 初始化角色权限关联
            initRolePermissions();
            
            // 初始化管理员用户
            initAdminUser();
            
            // 初始化用户角色关联
            initUserRoles();
            
            logger.info("基础数据初始化完成！");
            
        } catch (Exception e) {
            logger.error("数据初始化失败：", e);
        }
    }

    /**
     * 初始化角色数据
     */
    private void initRoles() {
        logger.info("初始化角色数据...");
        
        // 检查是否已存在角色数据
        Integer roleCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tb_role", Integer.class);
        if (roleCount != null && roleCount > 0) {
            logger.info("角色数据已存在，跳过初始化");
            return;
        }
        
        // 插入基础角色
        String sql = "INSERT INTO tb_role (role_name, role_key, role_sort, status, remark, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(sql, "超级管理员", "ADMIN", 1, 1, "系统超级管理员", now, now);
        jdbcTemplate.update(sql, "教师", "TEACHER", 2, 1, "教师角色", now, now);
        jdbcTemplate.update(sql, "学生", "STUDENT", 3, 1, "学生角色", now, now);
        jdbcTemplate.update(sql, "财务", "FINANCE", 4, 1, "财务角色", now, now);
        
        logger.info("角色数据初始化完成");
    }

    /**
     * 初始化权限数据
     */
    private void initPermissions() {
        logger.info("初始化权限数据...");
        
        // 检查是否已存在权限数据
        Integer permCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tb_permission", Integer.class);
        if (permCount != null && permCount > 0) {
            logger.info("权限数据已存在，跳过初始化");
            return;
        }
        
        String sql = "INSERT INTO tb_permission (permission_name, permission_key, permission_type, sort_order, status, remark, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        LocalDateTime now = LocalDateTime.now();
        
        // 系统管理权限
        jdbcTemplate.update(sql, "系统管理", "system:manage", "menu", 1, 1, "系统管理菜单", now, now);
        jdbcTemplate.update(sql, "用户管理", "user:manage", "menu", 2, 1, "用户管理", now, now);
        jdbcTemplate.update(sql, "角色管理", "role:manage", "menu", 3, 1, "角色管理", now, now);
        jdbcTemplate.update(sql, "权限管理", "permission:manage", "menu", 4, 1, "权限管理", now, now);
        
        // 学生管理权限
        jdbcTemplate.update(sql, "学生管理", "student:manage", "menu", 5, 1, "学生管理", now, now);
        jdbcTemplate.update(sql, "学生查看", "student:view", "button", 6, 1, "查看学生信息", now, now);
        jdbcTemplate.update(sql, "学生新增", "student:add", "button", 7, 1, "新增学生", now, now);
        jdbcTemplate.update(sql, "学生编辑", "student:edit", "button", 8, 1, "编辑学生信息", now, now);
        jdbcTemplate.update(sql, "学生删除", "student:delete", "button", 9, 1, "删除学生", now, now);
        
        // 课程管理权限
        jdbcTemplate.update(sql, "课程管理", "course:manage", "menu", 10, 1, "课程管理", now, now);
        jdbcTemplate.update(sql, "课程查看", "course:view", "button", 11, 1, "查看课程信息", now, now);
        jdbcTemplate.update(sql, "课程新增", "course:add", "button", 12, 1, "新增课程", now, now);
        jdbcTemplate.update(sql, "课程编辑", "course:edit", "button", 13, 1, "编辑课程信息", now, now);
        jdbcTemplate.update(sql, "课程删除", "course:delete", "button", 14, 1, "删除课程", now, now);
        
        // 班级管理权限
        jdbcTemplate.update(sql, "班级管理", "class:manage", "menu", 15, 1, "班级管理", now, now);
        jdbcTemplate.update(sql, "班级查看", "class:view", "button", 16, 1, "查看班级信息", now, now);
        jdbcTemplate.update(sql, "班级新增", "class:add", "button", 17, 1, "新增班级", now, now);
        jdbcTemplate.update(sql, "班级编辑", "class:edit", "button", 18, 1, "编辑班级信息", now, now);
        jdbcTemplate.update(sql, "班级删除", "class:delete", "button", 19, 1, "删除班级", now, now);
        
        // 成绩管理权限
        jdbcTemplate.update(sql, "成绩管理", "grade:manage", "menu", 20, 1, "成绩管理", now, now);
        jdbcTemplate.update(sql, "成绩查看", "grade:view", "button", 21, 1, "查看成绩", now, now);
        jdbcTemplate.update(sql, "成绩录入", "grade:add", "button", 22, 1, "录入成绩", now, now);
        jdbcTemplate.update(sql, "成绩修改", "grade:edit", "button", 23, 1, "修改成绩", now, now);
        
        logger.info("权限数据初始化完成");
    }

    /**
     * 初始化角色权限关联
     */
    private void initRolePermissions() {
        logger.info("初始化角色权限关联...");
        
        // 检查是否已存在关联数据
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tb_role_permission", Integer.class);
        if (count != null && count > 0) {
            logger.info("角色权限关联已存在，跳过初始化");
            return;
        }
        
        // 获取ADMIN角色ID
        Integer adminRoleId = jdbcTemplate.queryForObject("SELECT id FROM tb_role WHERE role_key = 'ADMIN'", Integer.class);
        
        if (adminRoleId != null) {
            // 为ADMIN角色分配所有权限
            String sql = "INSERT INTO tb_role_permission (role_id, permission_id, created_at) SELECT ?, id, ? FROM tb_permission";
            jdbcTemplate.update(sql, adminRoleId, LocalDateTime.now());
            logger.info("为ADMIN角色分配了所有权限");
        }
    }

    /**
     * 初始化管理员用户
     */
    private void initAdminUser() {
        logger.info("初始化管理员用户...");
        
        // 检查admin用户是否存在
        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tb_user WHERE username = 'admin'", Integer.class);
        if (userCount != null && userCount > 0) {
            logger.info("admin用户已存在，更新密码...");
            // 更新密码
            String encodedPassword = passwordEncoder.encode("admin123");
            jdbcTemplate.update("UPDATE tb_user SET password = ?, updated_at = ? WHERE username = 'admin'",
                               encodedPassword, LocalDateTime.now());
            return;
        }
        
        // 创建admin用户
        String sql = "INSERT INTO tb_user (username, password, real_name, email, phone, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String encodedPassword = passwordEncoder.encode("admin123");
        LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.update(sql, "admin", encodedPassword, "系统管理员", "admin@campus.com", "13800138000", 1, now, now);
        
        logger.info("管理员用户创建完成");
    }

    /**
     * 初始化用户角色关联
     */
    private void initUserRoles() {
        logger.info("初始化用户角色关联...");
        
        // 检查admin用户是否已有角色
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM tb_user_role ur JOIN tb_user u ON ur.user_id = u.id WHERE u.username = 'admin'",
            Integer.class);

        if (count != null && count > 0) {
            logger.info("admin用户角色关联已存在，跳过初始化");
            return;
        }
        
        // 获取admin用户ID和ADMIN角色ID
        Integer adminUserId = jdbcTemplate.queryForObject("SELECT id FROM tb_user WHERE username = 'admin'", Integer.class);
        Integer adminRoleId = jdbcTemplate.queryForObject("SELECT id FROM tb_role WHERE role_key = 'ADMIN'", Integer.class);
        
        if (adminUserId != null && adminRoleId != null) {
            String sql = "INSERT INTO tb_user_role (user_id, role_id, created_at) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, adminUserId, adminRoleId, LocalDateTime.now());
            logger.info("为admin用户分配了ADMIN角色");
        }
    }
}
