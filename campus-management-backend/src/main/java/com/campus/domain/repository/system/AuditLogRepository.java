package com.campus.domain.repository.system;

import com.campus.domain.entity.system.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志仓储接口
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>, JpaSpecificationExecutor<AuditLog> {

    /**
     * 根据用户ID查询审计日志
     * 
     * @param userId 用户ID
     * @return 审计日志列表
     */
    List<AuditLog> findByUserIdOrderByOperationTimeDesc(Long userId);

    /**
     * 根据操作类型查询审计日志
     * 
     * @param operationType 操作类型
     * @return 审计日志列表
     */
    List<AuditLog> findByOperationTypeOrderByOperationTimeDesc(String operationType);

    /**
     * 根据时间范围查询审计日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    List<AuditLog> findByOperationTimeBetweenOrderByOperationTimeDesc(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据客户端IP查询审计日志
     * 
     * @param clientIp 客户端IP
     * @return 审计日志列表
     */
    List<AuditLog> findByClientIpOrderByOperationTimeDesc(String clientIp);

    /**
     * 根据操作状态查询审计日志
     * 
     * @param operationStatus 操作状态
     * @return 审计日志列表
     */
    List<AuditLog> findByOperationStatusOrderByOperationTimeDesc(Integer operationStatus);

    /**
     * 根据风险等级查询审计日志
     * 
     * @param riskLevel 风险等级
     * @return 审计日志列表
     */
    List<AuditLog> findByRiskLevelOrderByOperationTimeDesc(Integer riskLevel);

    /**
     * 查询失败的操作日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 失败操作日志列表
     */
    @Query("SELECT a FROM AuditLog a WHERE a.operationStatus = 0 AND a.operationTime BETWEEN :startTime AND :endTime ORDER BY a.operationTime DESC")
    List<AuditLog> findFailedOperations(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 查询高风险操作日志
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 高风险操作日志列表
     */
    @Query("SELECT a FROM AuditLog a WHERE a.riskLevel = 3 AND a.operationTime BETWEEN :startTime AND :endTime ORDER BY a.operationTime DESC")
    List<AuditLog> findHighRiskOperations(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户操作次数
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作次数
     */
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.userId = :userId AND a.operationTime BETWEEN :startTime AND :endTime")
    Long countUserOperations(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计操作类型分布
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作类型统计
     */
    @Query("SELECT a.operationType, COUNT(a) FROM AuditLog a WHERE a.operationTime BETWEEN :startTime AND :endTime GROUP BY a.operationType")
    List<Object[]> countOperationTypes(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 统计模块访问次数
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 模块访问统计
     */
    @Query("SELECT a.module, COUNT(a) FROM AuditLog a WHERE a.operationTime BETWEEN :startTime AND :endTime GROUP BY a.module")
    List<Object[]> countModuleAccess(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 查询最近的登录日志
     * 
     * @param limit 限制数量
     * @return 登录日志列表
     */
    @Query("SELECT a FROM AuditLog a WHERE a.operationType = 'LOGIN' ORDER BY a.operationTime DESC")
    List<AuditLog> findRecentLogins(@Param("limit") int limit);

    /**
     * 查询异常IP访问
     * 
     * @param threshold 访问次数阈值
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 异常IP列表
     */
    @Query("SELECT a.clientIp, COUNT(a) FROM AuditLog a WHERE a.operationTime BETWEEN :startTime AND :endTime GROUP BY a.clientIp HAVING COUNT(a) > :threshold")
    List<Object[]> findAbnormalIpAccess(@Param("threshold") Long threshold, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 删除过期的审计日志
     * 
     * @param expireTime 过期时间
     * @return 删除数量
     */
    @Query("DELETE FROM AuditLog a WHERE a.operationTime < :expireTime")
    int deleteExpiredLogs(@Param("expireTime") LocalDateTime expireTime);
}
