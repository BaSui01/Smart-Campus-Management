package com.campus.application.service.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询优化服务
 * 提供高性能的数据库查询优化方案
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-18
 */
@Service
public class QueryOptimizationService {

    private static final Logger logger = LoggerFactory.getLogger(QueryOptimizationService.class);

    @Autowired
    private EntityManager entityManager;

    /**
     * 构建动态查询条件
     * 
     * @param <T> 实体类型
     * @param params 查询参数
     * @return Specification查询条件
     */
    public <T> Specification<T> buildDynamicSpecification(Map<String, Object> params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (value == null) {
                    continue;
                }

                try {
                    // 处理不同类型的查询条件
                    if (key.endsWith("_like")) {
                        // 模糊查询
                        String fieldName = key.substring(0, key.length() - 5);
                        predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get(fieldName).as(String.class)),
                            "%" + value.toString().toLowerCase() + "%"
                        ));
                    } else if (key.endsWith("_in")) {
                        // IN查询
                        String fieldName = key.substring(0, key.length() - 3);
                        if (value instanceof List) {
                            predicates.add(root.get(fieldName).in((List<?>) value));
                        }
                    } else if (key.endsWith("_gt")) {
                        // 大于查询
                        String fieldName = key.substring(0, key.length() - 3);
                        if (value instanceof Comparable) {
                            @SuppressWarnings("unchecked")
                            Comparable<Object> comparableValue = (Comparable<Object>) value;
                            predicates.add(criteriaBuilder.greaterThan(
                                root.get(fieldName), comparableValue
                            ));
                        }
                    } else if (key.endsWith("_gte")) {
                        // 大于等于查询
                        String fieldName = key.substring(0, key.length() - 4);
                        if (value instanceof Comparable) {
                            @SuppressWarnings("unchecked")
                            Comparable<Object> comparableValue = (Comparable<Object>) value;
                            predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                                root.get(fieldName), comparableValue
                            ));
                        }
                    } else if (key.endsWith("_lt")) {
                        // 小于查询
                        String fieldName = key.substring(0, key.length() - 3);
                        if (value instanceof Comparable) {
                            @SuppressWarnings("unchecked")
                            Comparable<Object> comparableValue = (Comparable<Object>) value;
                            predicates.add(criteriaBuilder.lessThan(
                                root.get(fieldName), comparableValue
                            ));
                        }
                    } else if (key.endsWith("_lte")) {
                        // 小于等于查询
                        String fieldName = key.substring(0, key.length() - 4);
                        if (value instanceof Comparable) {
                            @SuppressWarnings("unchecked")
                            Comparable<Object> comparableValue = (Comparable<Object>) value;
                            predicates.add(criteriaBuilder.lessThanOrEqualTo(
                                root.get(fieldName), comparableValue
                            ));
                        }
                    } else if (key.endsWith("_ne")) {
                        // 不等于查询
                        String fieldName = key.substring(0, key.length() - 3);
                        predicates.add(criteriaBuilder.notEqual(root.get(fieldName), value));
                    } else if (key.endsWith("_null")) {
                        // 空值查询
                        String fieldName = key.substring(0, key.length() - 5);
                        if (Boolean.TRUE.equals(value)) {
                            predicates.add(criteriaBuilder.isNull(root.get(fieldName)));
                        } else {
                            predicates.add(criteriaBuilder.isNotNull(root.get(fieldName)));
                        }
                    } else {
                        // 等于查询
                        predicates.add(criteriaBuilder.equal(root.get(key), value));
                    }
                } catch (Exception e) {
                    logger.warn("构建查询条件失败，字段: {}, 值: {}", key, value, e);
                }
            }

            // 默认过滤软删除的记录
            try {
                predicates.add(criteriaBuilder.equal(root.get("deleted"), 0));
            } catch (Exception e) {
                // 如果实体没有deleted字段，忽略此条件
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 执行批量查询
     * 
     * @param sql 原生SQL查询
     * @param params 参数
     * @param batchSize 批次大小
     * @return 查询结果
     */
    public List<Object[]> executeBatchQuery(String sql, Map<String, Object> params, int batchSize) {
        logger.debug("执行批量查询，SQL: {}, 批次大小: {}", sql, batchSize);

        Query query = entityManager.createNativeQuery(sql);
        
        // 设置参数
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        // 设置批次大小
        query.setMaxResults(batchSize);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        logger.debug("批量查询完成，返回 {} 条记录", results.size());

        return results;
    }

    /**
     * 执行统计查询
     * 
     * @param entityClass 实体类
     * @param params 查询参数
     * @return 统计结果
     */
    @Cacheable(value = "queryStats", key = "#entityClass.simpleName + '_' + #params.hashCode()")
    public Long executeCountQuery(Class<?> entityClass, Map<String, Object> params) {
        logger.debug("执行统计查询，实体: {}", entityClass.getSimpleName());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<?> root = countQuery.from(entityClass);

        // 构建查询条件
        Specification<Object> spec = buildDynamicSpecification(params);
        @SuppressWarnings("unchecked")
        Root<Object> objectRoot = (Root<Object>) root;
        Predicate predicate = spec.toPredicate(objectRoot, countQuery, criteriaBuilder);

        countQuery.select(criteriaBuilder.count(root));
        if (predicate != null) {
            countQuery.where(predicate);
        }

        TypedQuery<Long> query = entityManager.createQuery(countQuery);
        Long count = query.getSingleResult();

        logger.debug("统计查询完成，总数: {}", count);
        return count;
    }

    /**
     * 执行分组统计查询
     * 
     * @param sql 原生SQL查询
     * @param params 参数
     * @return 分组统计结果
     */
    @Cacheable(value = "groupStats", key = "#sql.hashCode() + '_' + #params.hashCode()")
    public List<Object[]> executeGroupByQuery(String sql, Map<String, Object> params) {
        logger.debug("执行分组统计查询，SQL: {}", sql);

        Query query = entityManager.createNativeQuery(sql);
        
        // 设置参数
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        logger.debug("分组统计查询完成，返回 {} 个分组", results.size());

        return results;
    }

    /**
     * 执行优化的关联查询
     * 
     * @param <T> 实体类型
     * @param entityClass 实体类
     * @param fetchPaths 需要预加载的关联路径
     * @param params 查询参数
     * @param pageable 分页参数
     * @return 查询结果
     */
    public <T> List<T> executeOptimizedJoinQuery(
            Class<T> entityClass, 
            List<String> fetchPaths, 
            Map<String, Object> params,
            Pageable pageable) {
        
        logger.debug("执行优化关联查询，实体: {}, 预加载路径: {}", 
                    entityClass.getSimpleName(), fetchPaths);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);

        // 添加fetch join以避免N+1问题
        for (String fetchPath : fetchPaths) {
            try {
                root.fetch(fetchPath, JoinType.LEFT);
            } catch (Exception e) {
                logger.warn("无法预加载路径: {}", fetchPath, e);
            }
        }

        // 构建查询条件
        Specification<T> spec = buildDynamicSpecification(params);
        Predicate predicate = spec.toPredicate(root, criteriaQuery, criteriaBuilder);

        criteriaQuery.select(root);
        if (predicate != null) {
            criteriaQuery.where(predicate);
        }

        // 添加排序
        if (pageable.getSort().isSorted()) {
            List<Order> orders = new ArrayList<>();
            pageable.getSort().forEach(sortOrder -> {
                try {
                    if (sortOrder.isAscending()) {
                        orders.add(criteriaBuilder.asc(root.get(sortOrder.getProperty())));
                    } else {
                        orders.add(criteriaBuilder.desc(root.get(sortOrder.getProperty())));
                    }
                } catch (Exception e) {
                    logger.warn("无法添加排序字段: {}", sortOrder.getProperty(), e);
                }
            });
            criteriaQuery.orderBy(orders);
        }

        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        
        // 设置分页
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<T> results = query.getResultList();
        logger.debug("优化关联查询完成，返回 {} 条记录", results.size());

        return results;
    }

    /**
     * 执行原生SQL查询并映射到DTO
     * 
     * @param <T> DTO类型
     * @param sql 原生SQL
     * @param resultClass 结果类型
     * @param params 参数
     * @return 查询结果
     */
    public <T> List<T> executeNativeQueryWithMapping(
            String sql, 
            Class<T> resultClass, 
            Map<String, Object> params) {
        
        logger.debug("执行原生SQL查询，结果类型: {}", resultClass.getSimpleName());

        Query query = entityManager.createNativeQuery(sql, resultClass);
        
        // 设置参数
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        @SuppressWarnings("unchecked")
        List<T> results = query.getResultList();
        logger.debug("原生SQL查询完成，返回 {} 条记录", results.size());

        return results;
    }

    /**
     * 清理查询缓存
     */
    public void clearQueryCache() {
        logger.info("清理查询缓存");
        entityManager.getEntityManagerFactory().getCache().evictAll();
    }

    /**
     * 获取查询执行计划（仅用于调试）
     * 
     * @param sql SQL语句
     * @return 执行计划
     */
    public String getQueryExecutionPlan(String sql) {
        try {
            Query query = entityManager.createNativeQuery("EXPLAIN " + sql);
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();
            
            StringBuilder plan = new StringBuilder();
            for (Object[] row : results) {
                for (Object col : row) {
                    plan.append(col).append(" ");
                }
                plan.append("\n");
            }
            
            return plan.toString();
        } catch (Exception e) {
            logger.warn("获取查询执行计划失败", e);
            return "无法获取执行计划";
        }
    }
}
