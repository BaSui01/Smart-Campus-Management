package com.campus.common.controller;

import com.campus.shared.common.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * 基础CRUD控制器
 * 提供标准的增删改查操作
 *
 * @param <T> 实体类型
 * @param <ID> 主键类型
 * @param <R> Repository类型
 *
 * @author Campus Management System
 * @since 2025-06-07
 */
public abstract class BaseCrudController<T, ID, R extends JpaRepository<T, ID> & JpaSpecificationExecutor<T>>
        extends BaseController {
    
    /**
     * 获取Repository实例
     */
    protected abstract R getRepository();
    
    /**
     * 获取实体名称（用于日志和错误消息）
     */
    protected abstract String getEntityName();
    
    /**
     * 创建搜索规范
     */
    protected abstract Specification<T> createSearchSpecification(String keyword);
    
    /**
     * 验证实体数据
     */
    protected abstract void validateEntity(T entity);
    
    /**
     * 在保存前处理实体
     */
    protected void beforeSave(T entity) {
        // 子类可以重写此方法
    }
    
    /**
     * 在保存后处理实体
     */
    protected void afterSave(T entity) {
        // 子类可以重写此方法
    }
    
    /**
     * 在删除前处理实体
     */
    protected void beforeDelete(T entity) {
        // 子类可以重写此方法
    }
    
    /**
     * 在删除后处理
     */
    protected void afterDelete(ID id) {
        // 子类可以重写此方法
    }
    
    /**
     * 获取所有记录（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<T>>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest request) {
        
        try {
            logOperation("查询" + getEntityName() + "列表", page, size, search);
            
            // 验证分页参数
            validatePageParams(page, size);
            
            // 创建分页对象
            Pageable pageable = createPageable(page, size, sortBy, sortDir);
            
            // 创建搜索条件
            Page<T> result;
            if (StringUtils.hasText(search)) {
                String keyword = processSearchKeyword(search);
                Specification<T> spec = createSearchSpecification(keyword);
                result = getRepository().findAll(spec, pageable);
            } else {
                result = getRepository().findAll(pageable);
            }
            
            return successPage(result, "查询" + getEntityName() + "列表成功");
            
        } catch (Exception e) {
            log.error("查询{}列表失败: ", getEntityName(), e);
            return error("查询" + getEntityName() + "列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取记录
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<T>> getById(@PathVariable ID id) {
        try {
            logOperation("查询" + getEntityName(), id);
            
            validateId((Long) id, getEntityName());
            
            Optional<T> entity = getRepository().findById(id);
            if (entity.isPresent()) {
                return success(entity.get(), "查询" + getEntityName() + "成功");
            } else {
                return notFound(getEntityName() + "不存在");
            }
            
        } catch (Exception e) {
            log.error("查询{}失败: ", getEntityName(), e);
            return error("查询" + getEntityName() + "失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建新记录
     */
    @PostMapping
    public ResponseEntity<ApiResponse<T>> create(@RequestBody T entity, HttpServletRequest request) {
        try {
            logOperation("创建" + getEntityName(), entity);
            
            // 验证实体数据
            validateEntity(entity);
            
            // 保存前处理
            beforeSave(entity);
            
            // 保存实体
            T savedEntity = getRepository().save(entity);
            
            // 保存后处理
            afterSave(savedEntity);
            
            return success(savedEntity, getEntityName() + "创建成功");
            
        } catch (Exception e) {
            log.error("创建{}失败: ", getEntityName(), e);
            return error("创建" + getEntityName() + "失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新记录
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<T>> update(@PathVariable ID id, @RequestBody T entity, HttpServletRequest request) {
        try {
            logOperation("更新" + getEntityName(), id, entity);
            
            validateId((Long) id, getEntityName());
            
            // 检查实体是否存在
            if (!getRepository().existsById(id)) {
                return notFound(getEntityName() + "不存在");
            }
            
            // 验证实体数据
            validateEntity(entity);
            
            // 保存前处理
            beforeSave(entity);
            
            // 保存实体
            T savedEntity = getRepository().save(entity);
            
            // 保存后处理
            afterSave(savedEntity);
            
            return success(savedEntity, getEntityName() + "更新成功");
            
        } catch (Exception e) {
            log.error("更新{}失败: ", getEntityName(), e);
            return error("更新" + getEntityName() + "失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable ID id, HttpServletRequest request) {
        try {
            logOperation("删除" + getEntityName(), id);
            
            validateId((Long) id, getEntityName());
            
            // 检查实体是否存在
            Optional<T> entity = getRepository().findById(id);
            if (!entity.isPresent()) {
                return notFound(getEntityName() + "不存在");
            }
            
            // 删除前处理
            beforeDelete(entity.get());
            
            // 删除实体
            getRepository().deleteById(id);
            
            // 删除后处理
            afterDelete(id);
            
            return success(getEntityName() + "删除成功");
            
        } catch (Exception e) {
            log.error("删除{}失败: ", getEntityName(), e);
            return error("删除" + getEntityName() + "失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量删除记录
     */
    @DeleteMapping("/batch")
    public ResponseEntity<ApiResponse<Void>> batchDelete(@RequestBody List<ID> ids, HttpServletRequest request) {
        try {
            logOperation("批量删除" + getEntityName(), ids);
            
            if (ids == null || ids.isEmpty()) {
                return error("删除ID列表不能为空");
            }
            
            // 验证所有ID
            for (ID id : ids) {
                validateId((Long) id, getEntityName());
            }
            
            // 获取所有要删除的实体
            List<T> entities = getRepository().findAllById(ids);
            
            // 删除前处理
            for (T entity : entities) {
                beforeDelete(entity);
            }
            
            // 批量删除
            getRepository().deleteAllById(ids);
            
            // 删除后处理
            for (ID id : ids) {
                afterDelete(id);
            }
            
            return success("批量删除" + getEntityName() + "成功，共删除" + entities.size() + "条记录");
            
        } catch (Exception e) {
            log.error("批量删除{}失败: ", getEntityName(), e);
            return error("批量删除" + getEntityName() + "失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索记录
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<T>>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            logOperation("搜索" + getEntityName(), keyword, page, size);
            
            if (!StringUtils.hasText(keyword)) {
                return error("搜索关键词不能为空");
            }
            
            // 验证分页参数
            validatePageParams(page, size);
            
            // 创建分页对象
            Pageable pageable = createPageable(page, size, sortBy, sortDir);
            
            // 处理搜索关键词
            String processedKeyword = processSearchKeyword(keyword);
            
            // 创建搜索规范
            Specification<T> spec = createSearchSpecification(processedKeyword);
            
            // 执行搜索
            Page<T> result = getRepository().findAll(spec, pageable);
            
            return successPage(result, "搜索" + getEntityName() + "成功");
            
        } catch (Exception e) {
            log.error("搜索{}失败: ", getEntityName(), e);
            return error("搜索" + getEntityName() + "失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取记录总数
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> count(@RequestParam(defaultValue = "") String search) {
        try {
            logOperation("统计" + getEntityName() + "数量", search);
            
            long count;
            if (StringUtils.hasText(search)) {
                String keyword = processSearchKeyword(search);
                Specification<T> spec = createSearchSpecification(keyword);
                count = getRepository().count(spec);
            } else {
                count = getRepository().count();
            }
            
            return success(count, "统计" + getEntityName() + "数量成功");
            
        } catch (Exception e) {
            log.error("统计{}数量失败: ", getEntityName(), e);
            return error("统计" + getEntityName() + "数量失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查记录是否存在
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<ApiResponse<Boolean>> exists(@PathVariable ID id) {
        try {
            logOperation("检查" + getEntityName() + "是否存在", id);
            
            validateId((Long) id, getEntityName());
            
            boolean exists = getRepository().existsById(id);
            return success(exists, "检查" + getEntityName() + "存在性成功");
            
        } catch (Exception e) {
            log.error("检查{}是否存在失败: ", getEntityName(), e);
            return error("检查" + getEntityName() + "是否存在失败: " + e.getMessage());
        }
    }
}
