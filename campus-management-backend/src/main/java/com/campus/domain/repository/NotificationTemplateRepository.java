package com.campus.domain.repository;

import com.campus.domain.entity.NotificationTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 通知模板Repository接口
 * 提供通知模板相关的数据访问方法
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-07
 */
@Repository
public interface NotificationTemplateRepository extends BaseRepository<NotificationTemplate> {

    // ================================
    // 基础查询方法
    // ================================

    /**
     * 根据模板代码查找模板
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.templateCode = :templateCode AND nt.deleted = 0")
    Optional<NotificationTemplate> findByTemplateCode(@Param("templateCode") String templateCode);

    /**
     * 根据模板名称查找模板
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.templateName = :templateName AND nt.deleted = 0")
    Optional<NotificationTemplate> findByTemplateName(@Param("templateName") String templateName);

    /**
     * 根据模板类型查找模板列表
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.templateType = :templateType AND nt.deleted = 0 ORDER BY nt.sortOrder ASC")
    List<NotificationTemplate> findByTemplateType(@Param("templateType") String templateType);

    /**
     * 分页根据模板类型查找模板列表
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.templateType = :templateType AND nt.deleted = 0")
    Page<NotificationTemplate> findByTemplateType(@Param("templateType") String templateType, Pageable pageable);

    /**
     * 根据通知类型查找模板列表
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.notificationType = :notificationType AND nt.deleted = 0 ORDER BY nt.sortOrder ASC")
    List<NotificationTemplate> findByNotificationType(@Param("notificationType") String notificationType);

    /**
     * 根据模板名称模糊查询
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.templateName LIKE %:templateName% AND nt.deleted = 0 ORDER BY nt.sortOrder ASC")
    List<NotificationTemplate> findByTemplateNameContaining(@Param("templateName") String templateName);

    /**
     * 查找启用的模板
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.status = 1 AND nt.deleted = 0 ORDER BY nt.sortOrder ASC")
    List<NotificationTemplate> findActiveTemplates();

    /**
     * 分页查找启用的模板
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.status = 1 AND nt.deleted = 0")
    Page<NotificationTemplate> findActiveTemplates(Pageable pageable);

    // ================================
    // 复合查询方法
    // ================================

    /**
     * 根据多个条件查找模板
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE " +
           "(:templateName IS NULL OR nt.templateName LIKE %:templateName%) AND " +
           "(:templateType IS NULL OR nt.templateType = :templateType) AND " +
           "(:notificationType IS NULL OR nt.notificationType = :notificationType) AND " +
           "(:status IS NULL OR nt.status = :status) AND " +
           "nt.deleted = 0")
    Page<NotificationTemplate> findByMultipleConditions(@Param("templateName") String templateName,
                                                       @Param("templateType") String templateType,
                                                       @Param("notificationType") String notificationType,
                                                       @Param("status") Integer status,
                                                       Pageable pageable);

    /**
     * 搜索模板（根据模板名称、代码、描述等关键词）
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE " +
           "(nt.templateName LIKE %:keyword% OR " +
           "nt.templateCode LIKE %:keyword% OR " +
           "nt.description LIKE %:keyword%) AND " +
           "nt.deleted = 0 ORDER BY nt.sortOrder ASC")
    List<NotificationTemplate> searchTemplates(@Param("keyword") String keyword);

    /**
     * 分页搜索模板
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE " +
           "(nt.templateName LIKE %:keyword% OR " +
           "nt.templateCode LIKE %:keyword% OR " +
           "nt.description LIKE %:keyword%) AND " +
           "nt.deleted = 0")
    Page<NotificationTemplate> searchTemplates(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 根据模板类型和通知类型查找模板
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.templateType = :templateType AND nt.notificationType = :notificationType AND nt.deleted = 0 ORDER BY nt.sortOrder ASC")
    List<NotificationTemplate> findByTemplateTypeAndNotificationType(@Param("templateType") String templateType, 
                                                                    @Param("notificationType") String notificationType);

    // ================================
    // 排序相关查询
    // ================================

    /**
     * 根据排序顺序查找模板
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.deleted = 0 ORDER BY nt.sortOrder ASC, nt.createdAt DESC")
    List<NotificationTemplate> findAllOrderBySortOrder();

    /**
     * 获取指定类型的最大排序值
     */
    @Query("SELECT COALESCE(MAX(nt.sortOrder), 0) FROM NotificationTemplate nt WHERE nt.templateType = :templateType AND nt.deleted = 0")
    Integer getMaxSortOrderByTemplateType(@Param("templateType") String templateType);

    // ================================
    // 统计查询方法
    // ================================

    /**
     * 根据模板类型统计数量
     */
    @Query("SELECT nt.templateType, COUNT(nt) FROM NotificationTemplate nt WHERE nt.deleted = 0 GROUP BY nt.templateType ORDER BY nt.templateType")
    List<Object[]> countByTemplateType();

    /**
     * 根据通知类型统计数量
     */
    @Query("SELECT nt.notificationType, COUNT(nt) FROM NotificationTemplate nt WHERE nt.deleted = 0 GROUP BY nt.notificationType ORDER BY nt.notificationType")
    List<Object[]> countByNotificationType();

    /**
     * 根据状态统计数量
     */
    @Query("SELECT nt.status, COUNT(nt) FROM NotificationTemplate nt WHERE nt.deleted = 0 GROUP BY nt.status")
    List<Object[]> countByStatus();

    /**
     * 统计指定类型的模板数量
     */
    @Query("SELECT COUNT(nt) FROM NotificationTemplate nt WHERE nt.templateType = :templateType AND nt.deleted = 0")
    long countByTemplateType(@Param("templateType") String templateType);

    /**
     * 统计启用的模板数量
     */
    @Query("SELECT COUNT(nt) FROM NotificationTemplate nt WHERE nt.status = 1 AND nt.deleted = 0")
    long countActiveTemplates();

    /**
     * 统计模板使用次数
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.templateId = :templateId AND n.deleted = 0")
    long countUsageByTemplateId(@Param("templateId") Long templateId);

    // ================================
    // 存在性检查方法
    // ================================

    /**
     * 检查模板代码是否存在
     */
    @Query("SELECT CASE WHEN COUNT(nt) > 0 THEN true ELSE false END FROM NotificationTemplate nt WHERE nt.templateCode = :templateCode AND nt.deleted = 0")
    boolean existsByTemplateCode(@Param("templateCode") String templateCode);

    /**
     * 检查模板代码是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(nt) > 0 THEN true ELSE false END FROM NotificationTemplate nt WHERE nt.templateCode = :templateCode AND nt.id != :excludeId AND nt.deleted = 0")
    boolean existsByTemplateCodeAndIdNot(@Param("templateCode") String templateCode, @Param("excludeId") Long excludeId);

    /**
     * 检查模板名称是否存在
     */
    @Query("SELECT CASE WHEN COUNT(nt) > 0 THEN true ELSE false END FROM NotificationTemplate nt WHERE nt.templateName = :templateName AND nt.deleted = 0")
    boolean existsByTemplateName(@Param("templateName") String templateName);

    /**
     * 检查模板名称是否存在（排除指定ID）
     */
    @Query("SELECT CASE WHEN COUNT(nt) > 0 THEN true ELSE false END FROM NotificationTemplate nt WHERE nt.templateName = :templateName AND nt.id != :excludeId AND nt.deleted = 0")
    boolean existsByTemplateNameAndIdNot(@Param("templateName") String templateName, @Param("excludeId") Long excludeId);

    // ================================
    // 更新操作方法
    // ================================

    /**
     * 更新模板状态
     */
    @Modifying
    @Query("UPDATE NotificationTemplate nt SET nt.status = :status, nt.updatedAt = CURRENT_TIMESTAMP WHERE nt.id = :templateId")
    int updateStatus(@Param("templateId") Long templateId, @Param("status") Integer status);

    /**
     * 批量更新模板状态
     */
    @Modifying
    @Query("UPDATE NotificationTemplate nt SET nt.status = :status, nt.updatedAt = CURRENT_TIMESTAMP WHERE nt.id IN :templateIds")
    int batchUpdateStatus(@Param("templateIds") List<Long> templateIds, @Param("status") Integer status);

    /**
     * 更新模板排序
     */
    @Modifying
    @Query("UPDATE NotificationTemplate nt SET nt.sortOrder = :sortOrder, nt.updatedAt = CURRENT_TIMESTAMP WHERE nt.id = :templateId")
    int updateSortOrder(@Param("templateId") Long templateId, @Param("sortOrder") Integer sortOrder);

    /**
     * 批量更新模板排序
     */
    @Modifying
    @Query("UPDATE NotificationTemplate nt SET nt.sortOrder = :sortOrder, nt.updatedAt = CURRENT_TIMESTAMP WHERE nt.id IN :templateIds")
    int batchUpdateSortOrder(@Param("templateIds") List<Long> templateIds, @Param("sortOrder") Integer sortOrder);

    /**
     * 更新模板内容
     */
    @Modifying
    @Query("UPDATE NotificationTemplate nt SET nt.templateContent = :templateContent, nt.updatedAt = CURRENT_TIMESTAMP WHERE nt.id = :templateId")
    int updateTemplateContent(@Param("templateId") Long templateId, @Param("templateContent") String templateContent);

    /**
     * 增加使用次数
     */
    @Modifying
    @Query("UPDATE NotificationTemplate nt SET nt.usageCount = nt.usageCount + 1, nt.updatedAt = CURRENT_TIMESTAMP WHERE nt.id = :templateId")
    int incrementUsageCount(@Param("templateId") Long templateId);

    // ================================
    // 模板管理方法
    // ================================

    /**
     * 获取所有模板类型
     */
    @Query("SELECT DISTINCT nt.templateType FROM NotificationTemplate nt WHERE nt.deleted = 0 ORDER BY nt.templateType")
    List<String> findAllTemplateTypes();

    /**
     * 获取所有通知类型
     */
    @Query("SELECT DISTINCT nt.notificationType FROM NotificationTemplate nt WHERE nt.deleted = 0 ORDER BY nt.notificationType")
    List<String> findAllNotificationTypes();

    /**
     * 重置指定类型的模板排序
     */
    @Modifying
    @Query("UPDATE NotificationTemplate nt SET nt.sortOrder = (SELECT COUNT(nt2) FROM NotificationTemplate nt2 WHERE nt2.templateType = nt.templateType AND nt2.id <= nt.id AND nt2.deleted = 0), nt.updatedAt = CURRENT_TIMESTAMP WHERE nt.templateType = :templateType AND nt.deleted = 0")
    int resetSortOrderByTemplateType(@Param("templateType") String templateType);

    /**
     * 获取最常用的模板
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.deleted = 0 ORDER BY nt.usageCount DESC")
    List<NotificationTemplate> findMostUsedTemplates(Pageable pageable);

    /**
     * 获取最近创建的模板
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.deleted = 0 ORDER BY nt.createdAt DESC")
    List<NotificationTemplate> findRecentTemplates(Pageable pageable);

    /**
     * 获取默认模板
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.isDefault = true AND nt.templateType = :templateType AND nt.deleted = 0")
    Optional<NotificationTemplate> findDefaultTemplateByType(@Param("templateType") String templateType);

    /**
     * 设置默认模板
     */
    @Modifying
    @Query("UPDATE NotificationTemplate nt SET nt.isDefault = CASE WHEN nt.id = :templateId THEN true ELSE false END, nt.updatedAt = CURRENT_TIMESTAMP WHERE nt.templateType = :templateType")
    int setDefaultTemplate(@Param("templateId") Long templateId, @Param("templateType") String templateType);

}
