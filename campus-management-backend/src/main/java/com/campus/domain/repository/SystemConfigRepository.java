package com.campus.domain.repository;

import com.campus.domain.entity.SystemConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 系统配置Repository接口
 * 提供系统配置相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface SystemConfigRepository extends BaseRepository<SystemConfig> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据配置键查找配置
     */
    @Query("SELECT sc FROM SystemConfig sc WHERE sc.configKey = :configKey AND sc.deleted = 0")
    Optional<SystemConfig> findByConfigKey(@Param("configKey") String configKey);

    /**
     * 根据配置分组查找配置列表
     */
    @Query("SELECT sc FROM SystemConfig sc WHERE sc.configGroup = :configGroup AND sc.deleted = 0 ORDER BY sc.sortOrder ASC")
    List<SystemConfig> findByConfigGroup(@Param("configGroup") String configGroup);

    /**
     * 分页根据配置分组查找配置列表
     */
    @Query("SELECT sc FROM SystemConfig sc WHERE sc.configGroup = :configGroup AND sc.deleted = 0")
    Page<SystemConfig> findByConfigGroup(@Param("configGroup") String configGroup, Pageable pageable);

    /**
     * 根据配置类型查找配置列表
     */
    @Query("SELECT sc FROM SystemConfig sc WHERE sc.configType = :configType AND sc.deleted = 0 ORDER BY sc.configGroup ASC, sc.sortOrder ASC")
    List<SystemConfig> findByConfigType(@Param("configType") String configType);

    /**
     * 根据配置名称模糊查询
     */
    @Query("SELECT sc FROM SystemConfig sc WHERE sc.configName LIKE %:configName% AND sc.deleted = 0 ORDER BY sc.configGroup ASC, sc.sortOrder ASC")
    List<SystemConfig> findByConfigNameContaining(@Param("configName") String configName);

    /**
     * 获取所有启用的配置
     */
    @Query("SELECT sc FROM SystemConfig sc WHERE sc.status = 1 AND sc.deleted = 0 ORDER BY sc.configGroup ASC, sc.sortOrder ASC")
    List<SystemConfig> findAllActiveConfigs();

    /**
     * 分页获取所有启用的配置
     */
    @Query("SELECT sc FROM SystemConfig sc WHERE sc.status = 1 AND sc.deleted = 0")
    Page<SystemConfig> findAllActiveConfigs(Pageable pageable);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找配置
     */
    @Query("SELECT sc FROM SystemConfig sc WHERE " +
           "(:configGroup IS NULL OR sc.configGroup = :configGroup) AND " +
           "(:configType IS NULL OR sc.configType = :configType) AND " +
           "(:configName IS NULL OR sc.configName LIKE %:configName%) AND " +
           "(:status IS NULL OR sc.status = :status) AND " +
           "sc.deleted = 0")
    Page<SystemConfig> findByMultipleConditions(@Param("configGroup") String configGroup,
                                               @Param("configType") String configType,
                                               @Param("configName") String configName,
                                               @Param("status") Integer status,
                                               Pageable pageable);

    /**
     * 搜索配置（根据配置名称、配置键、描述等关键词）
     */
    @Query("SELECT sc FROM SystemConfig sc WHERE " +
           "(sc.configName LIKE %:keyword% OR " +
           "sc.configKey LIKE %:keyword% OR " +
           "sc.description LIKE %:keyword%) AND " +
           "sc.deleted = 0 ORDER BY sc.configGroup ASC, sc.sortOrder ASC")
    List<SystemConfig> searchConfigs(@Param("keyword") String keyword);

    /**
     * 分页搜索配置
     */
    @Query("SELECT sc FROM SystemConfig sc WHERE " +
           "(sc.configName LIKE %:keyword% OR " +
           "sc.configKey LIKE %:keyword% OR " +
           "sc.description LIKE %:keyword%) AND " +
           "sc.deleted = 0")
    Page<SystemConfig> searchConfigs(@Param("keyword") String keyword, Pageable pageable);

    // ================================
    // 配置值查询方法
    // ================================

    /**
     * 根据配置键获取配置值
     */
    @Query("SELECT sc.configValue FROM SystemConfig sc WHERE sc.configKey = :configKey AND sc.status = 1 AND sc.deleted = 0")
    Optional<String> getConfigValue(@Param("configKey") String configKey);

    /**
     * 根据配置键获取配置值（带默认值）
     */
    default String getConfigValue(String configKey, String defaultValue) {
        return getConfigValue(configKey).orElse(defaultValue);
    }

    /**
     * 批量获取配置值
     */
    @Query("SELECT sc.configKey, sc.configValue FROM SystemConfig sc WHERE sc.configKey IN :configKeys AND sc.status = 1 AND sc.deleted = 0")
    List<Object[]> getConfigValues(@Param("configKeys") List<String> configKeys);

    /**
     * 获取指定分组的所有配置
     */
    @Query("SELECT sc.configKey, sc.configValue FROM SystemConfig sc WHERE sc.configGroup = :configGroup AND sc.status = 1 AND sc.deleted = 0 ORDER BY sc.sortOrder ASC")
    List<Object[]> getConfigsByGroup(@Param("configGroup") String configGroup);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据配置分组统计配置数量
     */
    @Query("SELECT sc.configGroup, COUNT(sc) FROM SystemConfig sc WHERE sc.deleted = 0 GROUP BY sc.configGroup ORDER BY sc.configGroup")
    List<Object[]> countByConfigGroup();

    /**
     * 根据配置类型统计配置数量
     */
    @Query("SELECT sc.configType, COUNT(sc) FROM SystemConfig sc WHERE sc.deleted = 0 GROUP BY sc.configType ORDER BY sc.configType")
    List<Object[]> countByConfigType();

    /**
     * 根据状态统计配置数量
     */
    @Query("SELECT sc.status, COUNT(sc) FROM SystemConfig sc WHERE sc.deleted = 0 GROUP BY sc.status")
    List<Object[]> countByStatus();

    /**
     * 统计指定分组的配置数量
     */
    @Query("SELECT COUNT(sc) FROM SystemConfig sc WHERE sc.configGroup = :configGroup AND sc.deleted = 0")
    long countByConfigGroup(@Param("configGroup") String configGroup);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查配置键是否存在
     */
    @Query("SELECT CASE WHEN COUNT(sc) > 0 THEN true ELSE false END FROM SystemConfig sc WHERE sc.configKey = :configKey AND sc.deleted = 0")
    boolean existsByConfigKey(@Param("configKey") String configKey);

    /**
     * 检查配置键是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(sc) > 0 THEN true ELSE false END FROM SystemConfig sc WHERE sc.configKey = :configKey AND sc.id != :excludeId AND sc.deleted = 0")
    boolean existsByConfigKeyAndIdNot(@Param("configKey") String configKey, @Param("excludeId") Long excludeId);

    /**
     * 检查配置名称是否存在
     */
    @Query("SELECT CASE WHEN COUNT(sc) > 0 THEN true ELSE false END FROM SystemConfig sc WHERE sc.configName = :configName AND sc.deleted = 0")
    boolean existsByConfigName(@Param("configName") String configName);

    /**
     * 检查配置名称是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(sc) > 0 THEN true ELSE false END FROM SystemConfig sc WHERE sc.configName = :configName AND sc.id != :excludeId AND sc.deleted = 0")
    boolean existsByConfigNameAndIdNot(@Param("configName") String configName, @Param("excludeId") Long excludeId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新配置值
     */
    @Modifying
    @Query("UPDATE SystemConfig sc SET sc.configValue = :configValue, sc.updatedAt = CURRENT_TIMESTAMP WHERE sc.configKey = :configKey")
    int updateConfigValue(@Param("configKey") String configKey, @Param("configValue") String configValue);

    /**
     * 批量更新配置值
     */
    @Modifying
    @Query("UPDATE SystemConfig sc SET sc.configValue = :configValue, sc.updatedAt = CURRENT_TIMESTAMP WHERE sc.configKey IN :configKeys")
    int batchUpdateConfigValue(@Param("configKeys") List<String> configKeys, @Param("configValue") String configValue);

    /**
     * 更新配置状态
     */
    @Modifying
    @Query("UPDATE SystemConfig sc SET sc.status = :status, sc.updatedAt = CURRENT_TIMESTAMP WHERE sc.id = :configId")
    int updateConfigStatus(@Param("configId") Long configId, @Param("status") Integer status);

    /**
     * 更新配置排序
     */
    @Modifying
    @Query("UPDATE SystemConfig sc SET sc.sortOrder = :sortOrder, sc.updatedAt = CURRENT_TIMESTAMP WHERE sc.id = :configId")
    int updateSortOrder(@Param("configId") Long configId, @Param("sortOrder") Integer sortOrder);

    /**
     * 批量更新配置排序
     */
    @Modifying
    @Query("UPDATE SystemConfig sc SET sc.sortOrder = :sortOrder, sc.updatedAt = CURRENT_TIMESTAMP WHERE sc.id IN :configIds")
    int batchUpdateSortOrder(@Param("configIds") List<Long> configIds, @Param("sortOrder") Integer sortOrder);

    // ================================
    // 配置管理方法
    // ================================

    /**
     * 获取所有配置分组
     */
    @Query("SELECT DISTINCT sc.configGroup FROM SystemConfig sc WHERE sc.deleted = 0 ORDER BY sc.configGroup")
    List<String> findAllConfigGroups();

    /**
     * 获取所有配置类型
     */
    @Query("SELECT DISTINCT sc.configType FROM SystemConfig sc WHERE sc.deleted = 0 ORDER BY sc.configType")
    List<String> findAllConfigTypes();

    /**
     * 获取指定分组的最大排序值
     */
    @Query("SELECT COALESCE(MAX(sc.sortOrder), 0) FROM SystemConfig sc WHERE sc.configGroup = :configGroup AND sc.deleted = 0")
    Integer getMaxSortOrderByGroup(@Param("configGroup") String configGroup);

    /**
     * 重置指定分组的排序
     */
    @Modifying
    @Query("UPDATE SystemConfig sc SET sc.sortOrder = (SELECT COUNT(sc2) FROM SystemConfig sc2 WHERE sc2.configGroup = sc.configGroup AND sc2.id <= sc.id AND sc2.deleted = 0), sc.updatedAt = CURRENT_TIMESTAMP WHERE sc.configGroup = :configGroup AND sc.deleted = 0")
    int resetSortOrderByGroup(@Param("configGroup") String configGroup);

}
