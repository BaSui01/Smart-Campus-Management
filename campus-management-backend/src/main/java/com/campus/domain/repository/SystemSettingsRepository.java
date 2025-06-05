package com.campus.domain.repository;

import com.campus.domain.entity.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 系统设置数据访问接口
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-05
 */
@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long> {
    
    /**
     * 根据设置键查找设置
     */
    Optional<SystemSettings> findBySettingKey(String settingKey);
    
    /**
     * 检查设置键是否存在
     */
    boolean existsBySettingKey(String settingKey);
    
    /**
     * 根据设置类型查找设置列表
     */
    List<SystemSettings> findBySettingType(String settingType);
    
    /**
     * 根据设置键模糊搜索
     */
    @Query("SELECT s FROM SystemSettings s WHERE s.settingKey LIKE %:keyword% OR s.settingDescription LIKE %:keyword%")
    List<SystemSettings> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * 获取所有基本设置
     */
    @Query("SELECT s FROM SystemSettings s WHERE s.settingKey IN ('system.name', 'system.version', 'system.logo', 'contact.email', 'contact.phone')")
    List<SystemSettings> findBasicSettings();
    
    /**
     * 获取所有安全设置
     */
    @Query("SELECT s FROM SystemSettings s WHERE s.settingKey LIKE 'security.%'")
    List<SystemSettings> findSecuritySettings();
    
    /**
     * 获取所有通知设置
     */
    @Query("SELECT s FROM SystemSettings s WHERE s.settingKey LIKE 'notification.%'")
    List<SystemSettings> findNotificationSettings();
}
