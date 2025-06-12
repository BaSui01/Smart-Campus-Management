package com.campus.domain.repository.system;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campus.domain.entity.system.SystemSettings;

import java.util.List;
import java.util.Optional;

/**
 * 系统设置Repository接口
 * 提供系统设置相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface SystemSettingsRepository extends JpaRepository<SystemSettings, Long> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据设置键查找设置
     */
    @Query("SELECT ss FROM SystemSettings ss WHERE ss.settingKey = :settingKey")
    Optional<SystemSettings> findBySettingKey(@Param("settingKey") String settingKey);

    /**
     * 根据设置类型查找设置列表
     */
    @Query("SELECT ss FROM SystemSettings ss WHERE ss.settingType = :settingType ORDER BY ss.settingKey ASC")
    List<SystemSettings> findBySettingType(@Param("settingType") String settingType);

    /**
     * 分页根据设置类型查找设置列表
     */
    @Query("SELECT ss FROM SystemSettings ss WHERE ss.settingType = :settingType")
    Page<SystemSettings> findBySettingType(@Param("settingType") String settingType, Pageable pageable);

    /**
     * 根据设置键前缀查找设置列表
     */
    @Query("SELECT ss FROM SystemSettings ss WHERE ss.settingKey LIKE :keyPrefix% ORDER BY ss.settingKey ASC")
    List<SystemSettings> findBySettingKeyPrefix(@Param("keyPrefix") String keyPrefix);

    /**
     * 根据设置描述模糊查询
     */
    @Query("SELECT ss FROM SystemSettings ss WHERE ss.settingDescription LIKE %:description% ORDER BY ss.settingKey ASC")
    List<SystemSettings> findBySettingDescriptionContaining(@Param("description") String description);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找设置
     */
    @Query("SELECT ss FROM SystemSettings ss WHERE " +
           "(:settingType IS NULL OR ss.settingType = :settingType) AND " +
           "(:keyword IS NULL OR ss.settingKey LIKE %:keyword% OR ss.settingDescription LIKE %:keyword%)")
    Page<SystemSettings> findByMultipleConditions(@Param("settingType") String settingType,
                                                 @Param("keyword") String keyword,
                                                 Pageable pageable);

    /**
     * 搜索设置（根据设置键、描述等关键词）
     */
    @Query("SELECT ss FROM SystemSettings ss WHERE " +
           "(ss.settingKey LIKE %:keyword% OR " +
           "ss.settingDescription LIKE %:keyword%) ORDER BY ss.settingKey ASC")
    List<SystemSettings> searchSettings(@Param("keyword") String keyword);

    /**
     * 分页搜索设置
     */
    @Query("SELECT ss FROM SystemSettings ss WHERE " +
           "(ss.settingKey LIKE %:keyword% OR " +
           "ss.settingDescription LIKE %:keyword%)")
    Page<SystemSettings> searchSettings(@Param("keyword") String keyword, Pageable pageable);

    // ================================
    // 设置值查询方法
    // ================================

    /**
     * 根据设置键获取设置值
     */
    @Query("SELECT ss.settingValue FROM SystemSettings ss WHERE ss.settingKey = :settingKey")
    Optional<String> getSettingValue(@Param("settingKey") String settingKey);

    /**
     * 根据设置键获取设置值（带默认值）
     */
    default String getSettingValue(String settingKey, String defaultValue) {
        return getSettingValue(settingKey).orElse(defaultValue);
    }

    /**
     * 批量获取设置值
     */
    @Query("SELECT ss.settingKey, ss.settingValue FROM SystemSettings ss WHERE ss.settingKey IN :settingKeys")
    List<Object[]> getSettingValues(@Param("settingKeys") List<String> settingKeys);

    /**
     * 获取指定类型的所有设置
     */
    @Query("SELECT ss.settingKey, ss.settingValue FROM SystemSettings ss WHERE ss.settingType = :settingType ORDER BY ss.settingKey ASC")
    List<Object[]> getSettingsByType(@Param("settingType") String settingType);

    /**
     * 获取指定前缀的所有设置
     */
    @Query("SELECT ss.settingKey, ss.settingValue FROM SystemSettings ss WHERE ss.settingKey LIKE :keyPrefix% ORDER BY ss.settingKey ASC")
    List<Object[]> getSettingsByKeyPrefix(@Param("keyPrefix") String keyPrefix);

    // ================================
    // 预定义设置查询
    // ================================

    /**
     * 获取所有基本设置
     */
    @Query("SELECT ss FROM SystemSettings ss WHERE ss.settingKey IN ('system.name', 'system.version', 'system.logo', 'contact.email', 'contact.phone')")
    List<SystemSettings> findBasicSettings();

    /**
     * 获取所有安全设置
     */
    @Query("SELECT ss FROM SystemSettings ss WHERE ss.settingKey LIKE 'security.%'")
    List<SystemSettings> findSecuritySettings();

    /**
     * 获取所有通知设置
     */
    @Query("SELECT ss FROM SystemSettings ss WHERE ss.settingKey LIKE 'notification.%'")
    List<SystemSettings> findNotificationSettings();

    /**
     * 获取所有邮件设置
     */
    @Query("SELECT ss FROM SystemSettings ss WHERE ss.settingKey LIKE 'email.%'")
    List<SystemSettings> findEmailSettings();

    /**
     * 获取所有数据库设置
     */
    @Query("SELECT ss FROM SystemSettings ss WHERE ss.settingKey LIKE 'database.%'")
    List<SystemSettings> findDatabaseSettings();

    /**
     * 获取所有缓存设置
     */
    @Query("SELECT ss FROM SystemSettings ss WHERE ss.settingKey LIKE 'cache.%'")
    List<SystemSettings> findCacheSettings();

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据设置类型统计数量
     */
    @Query("SELECT ss.settingType, COUNT(ss) FROM SystemSettings ss GROUP BY ss.settingType ORDER BY ss.settingType")
    List<Object[]> countBySettingType();

    /**
     * 统计指定类型的设置数量
     */
    @Query("SELECT COUNT(ss) FROM SystemSettings ss WHERE ss.settingType = :settingType")
    long countBySettingType(@Param("settingType") String settingType);

    /**
     * 统计指定前缀的设置数量
     */
    @Query("SELECT COUNT(ss) FROM SystemSettings ss WHERE ss.settingKey LIKE :keyPrefix%")
    long countBySettingKeyPrefix(@Param("keyPrefix") String keyPrefix);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查设置键是否存在
     */
    @Query("SELECT CASE WHEN COUNT(ss) > 0 THEN true ELSE false END FROM SystemSettings ss WHERE ss.settingKey = :settingKey")
    boolean existsBySettingKey(@Param("settingKey") String settingKey);

    /**
     * 检查设置键是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(ss) > 0 THEN true ELSE false END FROM SystemSettings ss WHERE ss.settingKey = :settingKey AND ss.id != :excludeId")
    boolean existsBySettingKeyAndIdNot(@Param("settingKey") String settingKey, @Param("excludeId") Long excludeId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新设置值
     */
    @Modifying
    @Query("UPDATE SystemSettings ss SET ss.settingValue = :settingValue, ss.updatedAt = CURRENT_TIMESTAMP WHERE ss.settingKey = :settingKey")
    int updateSettingValue(@Param("settingKey") String settingKey, @Param("settingValue") String settingValue);

    /**
     * 批量更新设置值
     */
    @Modifying
    @Query("UPDATE SystemSettings ss SET ss.settingValue = :settingValue, ss.updatedAt = CURRENT_TIMESTAMP WHERE ss.settingKey IN :settingKeys")
    int batchUpdateSettingValue(@Param("settingKeys") List<String> settingKeys, @Param("settingValue") String settingValue);

    // ================================
    // 设置管理方法
    // ================================

    /**
     * 获取所有设置类型
     */
    @Query("SELECT DISTINCT ss.settingType FROM SystemSettings ss ORDER BY ss.settingType")
    List<String> findAllSettingTypes();

    /**
     * 获取所有设置键前缀
     */
    @Query("SELECT DISTINCT SUBSTRING(ss.settingKey, 1, LOCATE('.', ss.settingKey) - 1) FROM SystemSettings ss WHERE LOCATE('.', ss.settingKey) > 0 ORDER BY SUBSTRING(ss.settingKey, 1, LOCATE('.', ss.settingKey) - 1)")
    List<String> findAllSettingKeyPrefixes();

    // ================================
    // 兼容性方法（为现有Service提供支持）
    // ================================

    /**
     * 根据设置键模糊搜索（兼容性方法）
     */
    default List<SystemSettings> searchByKeyword(String keyword) {
        return searchSettings(keyword);
    }

}
