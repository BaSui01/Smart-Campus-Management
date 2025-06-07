# Smart Campus Management Repository接口创建计划

## 📋 Repository接口架构设计

### 🎯 设计目标
1. **统一继承BaseRepository** - 所有Repository继承BaseRepository获得通用功能
2. **性能优化** - 使用@Query注解优化查询，避免N+1问题
3. **分页支持** - 所有查询都提供分页版本
4. **业务场景覆盖** - 覆盖所有常用业务查询场景
5. **批量操作** - 提供批量更新、删除等高效操作

### 🏗️ Repository接口分类

#### **1. 基础管理Repository（5个）**
1. ✅ **BaseRepository** - 基础Repository接口（已完成）
2. ✅ **UserRepository** - 用户Repository（已优化）
3. 🔄 **StudentRepository** - 学生Repository（需优化）
4. 📝 **CourseRepository** - 课程Repository（需创建）
5. 📝 **GradeRepository** - 成绩Repository（需优化）

#### **2. 权限管理Repository（4个）**
6. 📝 **RoleRepository** - 角色Repository（需优化）
7. 📝 **PermissionRepository** - 权限Repository（需优化）
8. 📝 **RolePermissionRepository** - 角色权限关联Repository（需优化）
9. 📝 **UserRoleRepository** - 用户角色关联Repository（需优化）

#### **3. 教学管理Repository（5个）**
10. 📝 **AssignmentRepository** - 作业管理Repository（需创建）
11. 📝 **AssignmentSubmissionRepository** - 作业提交Repository（需创建）
12. 📝 **ExamRepository** - 考试管理Repository（需创建）
13. 📝 **ExamQuestionRepository** - 考试题目Repository（需创建）
14. 📝 **ExamRecordRepository** - 考试记录Repository（需创建）

#### **4. 课程管理Repository（5个）**
15. 📝 **CourseScheduleRepository** - 课程安排Repository（需优化）
16. 📝 **CourseSelectionRepository** - 选课记录Repository（需优化）
17. 📝 **ScheduleRepository** - 课程表Repository（需优化）
18. 📝 **TimeSlotRepository** - 时间段Repository（需优化）
19. 📝 **ClassroomRepository** - 教室管理Repository（需优化）

#### **5. 系统功能Repository（5个）**
20. 📝 **NotificationRepository** - 通知Repository（需优化）
21. 📝 **PaymentRecordRepository** - 缴费记录Repository（需优化）
22. 📝 **SystemConfigRepository** - 系统配置Repository（需创建）
23. 📝 **NotificationTemplateRepository** - 通知模板Repository（需创建）
24. 📝 **CourseSelectionPeriodRepository** - 选课时间Repository（需创建）

#### **6. 资源管理Repository（3个）**
25. 📝 **CourseResourceRepository** - 课程资源Repository（需创建）
26. 📝 **ResourceAccessLogRepository** - 资源访问日志Repository（需创建）
27. 📝 **StudentEvaluationRepository** - 学生评价Repository（需创建）

#### **7. 系统管理Repository（5个）**
28. 📝 **ActivityLogRepository** - 活动日志Repository（需优化）
29. 📝 **MessageRepository** - 消息管理Repository（需创建）
30. 📝 **FeeItemRepository** - 收费项目Repository（需优化）
31. 📝 **ParentStudentRelationRepository** - 家长学生关系Repository（需创建）
32. 📝 **TeacherCoursePermissionRepository** - 教师课程权限Repository（需创建）

#### **8. 基础数据Repository（3个）**
33. 📝 **SchoolClassRepository** - 班级管理Repository（需优化）
34. 📝 **DepartmentRepository** - 院系管理Repository（需优化）
35. 📝 **AttendanceRepository** - 考勤管理Repository（需创建）

## 🚀 Repository接口标准模板

### **基础查询方法模板**
```java
// 基础查询
Optional<T> findByXxx(@Param("xxx") String xxx);
List<T> findByXxx(@Param("xxx") String xxx);
Page<T> findByXxx(@Param("xxx") String xxx, Pageable pageable);

// 复合查询
@Query("SELECT e FROM EntityName e WHERE e.field1 = :field1 AND e.field2 = :field2 AND e.deleted = 0")
List<T> findByMultipleConditions(@Param("field1") String field1, @Param("field2") String field2);

// 模糊查询
@Query("SELECT e FROM EntityName e WHERE e.name LIKE %:keyword% AND e.deleted = 0")
List<T> findByNameContaining(@Param("keyword") String keyword);

// 关联查询
@Query("SELECT DISTINCT e FROM EntityName e LEFT JOIN FETCH e.relatedEntity WHERE e.deleted = 0")
List<T> findAllWithRelations();
```

### **统计查询方法模板**
```java
// 基础统计
@Query("SELECT COUNT(e) FROM EntityName e WHERE e.field = :value AND e.deleted = 0")
long countByField(@Param("value") String value);

// 分组统计
@Query("SELECT e.field, COUNT(e) FROM EntityName e WHERE e.deleted = 0 GROUP BY e.field")
List<Object[]> countByFieldGrouped();

// 时间范围统计
@Query("SELECT COUNT(e) FROM EntityName e WHERE e.createdAt BETWEEN :start AND :end AND e.deleted = 0")
long countByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
```

### **更新操作方法模板**
```java
// 单字段更新
@Modifying
@Query("UPDATE EntityName e SET e.field = :value, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
int updateField(@Param("id") Long id, @Param("value") String value);

// 批量更新
@Modifying
@Query("UPDATE EntityName e SET e.field = :value, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id IN :ids")
int batchUpdateField(@Param("ids") List<Long> ids, @Param("value") String value);

// 状态更新
@Modifying
@Query("UPDATE EntityName e SET e.status = :status, e.updatedAt = CURRENT_TIMESTAMP WHERE e.id = :id")
int updateStatus(@Param("id") Long id, @Param("status") Integer status);
```

### **存在性检查方法模板**
```java
// 基础存在性检查
@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EntityName e WHERE e.field = :value AND e.deleted = 0")
boolean existsByField(@Param("value") String value);

// 排除指定ID的存在性检查
@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EntityName e WHERE e.field = :value AND e.id != :excludeId AND e.deleted = 0")
boolean existsByFieldAndIdNot(@Param("value") String value, @Param("excludeId") Long excludeId);
```

## 📊 性能优化策略

### **1. 查询优化**
- 使用@Query注解编写优化的JPQL查询
- 避免N+1查询问题，使用JOIN FETCH预加载关联数据
- 合理使用分页查询，避免一次性加载大量数据
- 使用索引优化查询性能

### **2. 缓存策略**
- 对频繁查询的数据使用@Cacheable注解
- 合理设置缓存过期时间
- 使用@CacheEvict清理过期缓存

### **3. 批量操作**
- 提供批量插入、更新、删除方法
- 使用@Modifying注解优化更新操作
- 合理使用事务控制批量操作

### **4. 连接池优化**
- 配置合适的数据库连接池大小
- 设置合理的连接超时时间
- 监控连接池使用情况

## 🔄 实施计划

### **第一阶段：基础Repository优化（优先级：高）**
1. 完善StudentRepository
2. 优化CourseRepository
3. 优化GradeRepository
4. 优化权限管理相关Repository

### **第二阶段：教学管理Repository创建（优先级：高）**
1. 创建AssignmentRepository
2. 创建AssignmentSubmissionRepository
3. 创建ExamRepository
4. 创建ExamQuestionRepository
5. 创建ExamRecordRepository

### **第三阶段：课程管理Repository优化（优先级：中）**
1. 优化CourseScheduleRepository
2. 优化CourseSelectionRepository
3. 优化ScheduleRepository
4. 优化TimeSlotRepository
5. 优化ClassroomRepository

### **第四阶段：系统功能Repository完善（优先级：中）**
1. 优化NotificationRepository
2. 优化PaymentRecordRepository
3. 创建SystemConfigRepository
4. 创建NotificationTemplateRepository
5. 创建CourseSelectionPeriodRepository

### **第五阶段：其他Repository完善（优先级：低）**
1. 创建资源管理相关Repository
2. 创建系统管理相关Repository
3. 优化基础数据Repository

## 🎯 预期成果

完成后将拥有：
- ✅ **35个完整的Repository接口**
- ✅ **统一的查询方法规范**
- ✅ **完善的性能优化**
- ✅ **全面的业务场景覆盖**
- ✅ **高效的批量操作支持**
- ✅ **完整的分页查询功能**

这将为Smart Campus Management项目提供强大、高效、易维护的数据访问层基础！🚀
