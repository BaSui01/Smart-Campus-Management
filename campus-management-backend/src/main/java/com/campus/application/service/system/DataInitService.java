package com.campus.application.service.system;

/**
 * 数据初始化服务接口
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
public interface DataInitService {

    /**
     * 初始化所有数据
     */
    void initializeAllData();

    /**
     * 初始化权限数据
     */
    void initializePermissions();

    /**
     * 初始化角色数据
     */
    void initializeRoles();

    /**
     * 初始化院系数据
     */
    void initializeDepartments();

    /**
     * 初始化时间段数据
     */
    void initializeTimeSlots();

    /**
     * 初始化管理员用户
     */
    void initializeAdminUser();

    /**
     * 初始化系统配置
     */
    void initializeSystemConfig();

    /**
     * 检查数据是否已初始化
     */
    boolean isDataInitialized();

    /**
     * 重置所有数据
     */
    void resetAllData();
}