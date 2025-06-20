package com.campus.domain.repository;

import com.campus.domain.entity.system.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 文件信息仓储接口
 * 
 * @author Campus Management System
 * @since 2025-06-20
 */
@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long>, JpaSpecificationExecutor<FileInfo> {

    /**
     * 根据MD5查找文件
     * 
     * @param md5Hash MD5值
     * @return 文件信息
     */
    Optional<FileInfo> findByMd5HashAndDeleted(String md5Hash, Integer deleted);

    /**
     * 根据业务类型和业务ID查找文件
     * 
     * @param businessType 业务类型
     * @param businessId 业务ID
     * @return 文件列表
     */
    List<FileInfo> findByBusinessTypeAndBusinessIdAndDeletedOrderByCreatedAtDesc(
            String businessType, String businessId, Integer deleted);

    /**
     * 根据上传用户查找文件
     * 
     * @param uploadUserId 上传用户ID
     * @param deleted 是否删除
     * @return 文件列表
     */
    List<FileInfo> findByUploadUserIdAndDeletedOrderByCreatedAtDesc(Long uploadUserId, Integer deleted);

    /**
     * 根据文件状态查找文件
     * 
     * @param fileStatus 文件状态
     * @param deleted 是否删除
     * @return 文件列表
     */
    List<FileInfo> findByFileStatusAndDeleted(Integer fileStatus, Integer deleted);

    /**
     * 查找需要病毒扫描的文件
     * 
     * @param virusScanStatus 病毒扫描状态
     * @param deleted 是否删除
     * @return 文件列表
     */
    List<FileInfo> findByVirusScanStatusAndDeleted(Integer virusScanStatus, Integer deleted);

    /**
     * 查找过期的临时文件
     * 
     * @param expireTime 过期时间
     * @param businessType 业务类型
     * @return 文件列表
     */
    @Query("SELECT f FROM FileInfo f WHERE f.createdAt < :expireTime AND f.businessType = :businessType AND f.deleted = 0")
    List<FileInfo> findExpiredTempFiles(@Param("expireTime") LocalDateTime expireTime, 
                                       @Param("businessType") String businessType);

    /**
     * 统计用户上传的文件数量
     * 
     * @param uploadUserId 上传用户ID
     * @return 文件数量
     */
    @Query("SELECT COUNT(f) FROM FileInfo f WHERE f.uploadUserId = :uploadUserId AND f.deleted = 0")
    Long countByUploadUserId(@Param("uploadUserId") Long uploadUserId);

    /**
     * 统计用户上传的文件总大小
     * 
     * @param uploadUserId 上传用户ID
     * @return 文件总大小
     */
    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM FileInfo f WHERE f.uploadUserId = :uploadUserId AND f.deleted = 0")
    Long sumFileSizeByUploadUserId(@Param("uploadUserId") Long uploadUserId);

    /**
     * 根据文件类型统计文件数量
     * 
     * @return 统计结果
     */
    @Query("SELECT f.fileType, COUNT(f) FROM FileInfo f WHERE f.deleted = 0 GROUP BY f.fileType")
    List<Object[]> countByFileType();

    /**
     * 查找最近上传的文件
     * 
     * @param limit 限制数量
     * @return 文件列表
     */
    @Query("SELECT f FROM FileInfo f WHERE f.deleted = 0 ORDER BY f.createdAt DESC")
    List<FileInfo> findRecentFiles(@Param("limit") int limit);

    /**
     * 查找热门文件（下载次数最多）
     * 
     * @param limit 限制数量
     * @return 文件列表
     */
    @Query("SELECT f FROM FileInfo f WHERE f.deleted = 0 ORDER BY f.downloadCount DESC")
    List<FileInfo> findPopularFiles(@Param("limit") int limit);
}
