package com.campus.application.service;

import com.campus.domain.entity.Permission;
import com.campus.domain.entity.FeeItem;
import com.campus.domain.repository.PermissionRepository;
import com.campus.domain.repository.FeeItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 数据初始化服务
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-05
 */
@Service
public class DataInitService implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitService.class);

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private FeeItemRepository feeItemRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        try {
            initPermissions();
            initFeeItems();
            logger.info("数据初始化完成");
        } catch (Exception e) {
            logger.error("数据初始化失败", e);
        }
    }

    /**
     * 初始化权限数据
     */
    private void initPermissions() {
        if (permissionRepository.count() > 0) {
            logger.info("权限数据已存在，跳过初始化");
            return;
        }

        List<Permission> permissions = Arrays.asList(
            createPermission("user:view", "查看用户", "用户管理", "查看用户列表和用户详情"),
            createPermission("user:create", "创建用户", "用户管理", "创建新用户账号"),
            createPermission("user:edit", "编辑用户", "用户管理", "编辑用户信息"),
            createPermission("user:delete", "删除用户", "用户管理", "删除用户账号"),
            
            createPermission("role:view", "查看角色", "角色管理", "查看角色列表和角色详情"),
            createPermission("role:create", "创建角色", "角色管理", "创建新角色"),
            createPermission("role:edit", "编辑角色", "角色管理", "编辑角色信息"),
            createPermission("role:delete", "删除角色", "角色管理", "删除角色"),
            
            createPermission("permission:view", "查看权限", "权限管理", "查看权限列表和权限详情"),
            createPermission("permission:create", "创建权限", "权限管理", "创建新权限"),
            createPermission("permission:edit", "编辑权限", "权限管理", "编辑权限信息"),
            createPermission("permission:delete", "删除权限", "权限管理", "删除权限"),
            
            createPermission("student:view", "查看学生", "学生管理", "查看学生列表和学生详情"),
            createPermission("student:create", "创建学生", "学生管理", "录入新学生信息"),
            createPermission("student:edit", "编辑学生", "学生管理", "编辑学生信息"),
            createPermission("student:delete", "删除学生", "学生管理", "删除学生记录"),
            
            createPermission("course:view", "查看课程", "课程管理", "查看课程列表和课程详情"),
            createPermission("course:create", "创建课程", "课程管理", "创建新课程"),
            createPermission("course:edit", "编辑课程", "课程管理", "编辑课程信息"),
            createPermission("course:delete", "删除课程", "课程管理", "删除课程"),
            
            createPermission("payment:view", "查看缴费", "财务管理", "查看缴费记录"),
            createPermission("payment:create", "录入缴费", "财务管理", "录入学生缴费记录"),
            createPermission("payment:edit", "编辑缴费", "财务管理", "编辑缴费记录"),
            createPermission("payment:delete", "删除缴费", "财务管理", "删除缴费记录"),
            
            createPermission("report:view", "查看报表", "财务管理", "查看财务统计报表"),
            createPermission("report:export", "导出报表", "财务管理", "导出财务报表数据"),
            
            createPermission("system:config", "系统配置", "系统管理", "系统配置管理"),
            createPermission("system:log", "系统日志", "系统管理", "查看系统日志")
        );

        permissionRepository.saveAll(permissions);
        logger.info("初始化权限数据完成，共 {} 条记录", permissions.size());
    }

    /**
     * 初始化费用项目数据
     */
    private void initFeeItems() {
        if (feeItemRepository.count() > 0) {
            logger.info("费用项目数据已存在，跳过初始化");
            return;
        }

        List<FeeItem> feeItems = Arrays.asList(
            createFeeItem("学费", "TUITION", new BigDecimal("5000.00"), "学期学费"),
            createFeeItem("住宿费", "ACCOMMODATION", new BigDecimal("1200.00"), "学期住宿费"),
            createFeeItem("教材费", "TEXTBOOK", new BigDecimal("800.00"), "教材购买费用"),
            createFeeItem("实验费", "EXPERIMENT", new BigDecimal("500.00"), "实验课程费用"),
            createFeeItem("活动费", "ACTIVITY", new BigDecimal("300.00"), "课外活动费用")
        );

        feeItemRepository.saveAll(feeItems);
        logger.info("初始化费用项目数据完成，共 {} 条记录", feeItems.size());
    }

    /**
     * 创建权限对象
     */
    private Permission createPermission(String code, String name, String resourceType, String description) {
        Permission permission = new Permission();
        permission.setPermissionCode(code);
        permission.setPermissionName(name);
        permission.setResourceType(resourceType);
        permission.setPermissionDesc(description);
        permission.setStatus(1);
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());
        return permission;
    }

    /**
     * 创建费用项目对象
     */
    private FeeItem createFeeItem(String name, String type, BigDecimal amount, String description) {
        FeeItem feeItem = new FeeItem();
        feeItem.setItemName(name);
        feeItem.setItemCode(type);
        feeItem.setFeeType(type);
        feeItem.setAmount(amount);
        feeItem.setDescription(description);
        feeItem.setStatus(1);
        feeItem.setCreatedTime(LocalDateTime.now());
        feeItem.setUpdatedTime(LocalDateTime.now());
        return feeItem;
    }
}