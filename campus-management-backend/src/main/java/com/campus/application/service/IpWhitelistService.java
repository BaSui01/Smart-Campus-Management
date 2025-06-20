package com.campus.application.service;

import com.campus.domain.entity.system.IpWhitelist;

import java.util.List;

/**
 * IP白名单服务接口
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
public interface IpWhitelistService {
    
    /**
     * 检查IP是否在白名单中
     * 
     * @param ipAddress IP地址
     * @return 是否在白名单中
     */
    boolean isIpInWhitelist(String ipAddress);
    
    /**
     * 添加IP到白名单
     * 
     * @param ipWhitelist IP白名单对象
     * @return 保存后的对象
     */
    IpWhitelist addToWhitelist(IpWhitelist ipWhitelist);
    
    /**
     * 从白名单中移除IP
     * 
     * @param id 白名单ID
     * @return 是否移除成功
     */
    boolean removeFromWhitelist(Long id);
    
    /**
     * 更新白名单
     * 
     * @param ipWhitelist IP白名单对象
     * @return 更新后的对象
     */
    IpWhitelist updateWhitelist(IpWhitelist ipWhitelist);
    
    /**
     * 启用/禁用白名单项
     * 
     * @param id 白名单ID
     * @param status 状态
     * @return 是否操作成功
     */
    boolean updateStatus(Long id, Integer status);
    
    /**
     * 获取所有白名单
     * 
     * @return 白名单列表
     */
    List<IpWhitelist> getAllWhitelists();
    
    /**
     * 获取启用的白名单
     * 
     * @return 启用的白名单列表
     */
    List<IpWhitelist> getEnabledWhitelists();
    
    /**
     * 根据ID获取白名单
     * 
     * @param id 白名单ID
     * @return 白名单对象
     */
    IpWhitelist getWhitelistById(Long id);
    
    /**
     * 批量导入IP白名单
     * 
     * @param ipAddresses IP地址列表
     * @param whitelistName 白名单名称
     * @param description 描述
     * @return 导入结果
     */
    int batchImportWhitelist(List<String> ipAddresses, String whitelistName, String description);
    
    /**
     * 清理过期的白名单
     * 
     * @return 清理数量
     */
    int cleanExpiredWhitelists();
    
    /**
     * 记录IP访问
     * 
     * @param ipAddress IP地址
     */
    void recordIpAccess(String ipAddress);
    
    /**
     * 获取IP访问统计
     * 
     * @return 访问统计
     */
    List<IpWhitelist> getAccessStatistics();
}
