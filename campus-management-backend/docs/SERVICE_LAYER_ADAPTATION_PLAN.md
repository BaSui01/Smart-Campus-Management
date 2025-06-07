# Smart Campus Management Service层适配计划

## 🎯 适配目标

将现有的Service层从MyBatis Plus迁移到新创建的JPA Repository接口，确保业务逻辑与数据访问层的完美配合。

## 📋 适配范围

### **需要适配的Service类**
1. **UserService** - 用户服务
2. **StudentService** - 学生服务  
3. **CourseService** - 课程服务
4. **CourseScheduleService** - 课程安排服务
5. **CourseSelectionService** - 选课服务
6. **AssignmentService** - 作业服务
7. **ExamService** - 考试服务
8. **GradeService** - 成绩服务
9. **RoleService** - 角色服务
10. **PermissionService** - 权限服务
11. **NotificationService** - 通知服务
12. **PaymentRecordService** - 缴费记录服务
13. **SystemConfigService** - 系统配置服务
14. **SchoolClassService** - 班级服务
15. **DepartmentService** - 院系服务
16. **FeeItemService** - 收费项目服务

## 🔄 适配策略

### **阶段1：核心用户服务适配（优先级：高）**
- ✅ **UserService** - 用户管理核心服务
- ✅ **StudentService** - 学生管理服务
- ✅ **RoleService** - 角色管理服务
- ✅ **PermissionService** - 权限管理服务

### **阶段2：教学核心服务适配（优先级：高）**
- 🔄 **CourseService** - 课程管理服务
- 🔄 **CourseScheduleService** - 课程安排服务
- 🔄 **CourseSelectionService** - 选课服务
- 🔄 **GradeService** - 成绩管理服务

### **阶段3：教学辅助服务适配（优先级：中）**
- 🔄 **AssignmentService** - 作业管理服务
- 🔄 **ExamService** - 考试管理服务
- 🔄 **NotificationService** - 通知服务

### **阶段4：系统功能服务适配（优先级：中）**
- 🔄 **PaymentRecordService** - 缴费记录服务
- 🔄 **SystemConfigService** - 系统配置服务
- 🔄 **FeeItemService** - 收费项目服务

### **阶段5：基础数据服务适配（优先级：低）**
- 🔄 **SchoolClassService** - 班级服务
- 🔄 **DepartmentService** - 院系服务

## 🛠️ 适配步骤

### **1. 依赖注入适配**
```java
// 旧方式 - MyBatis Plus
@Autowired
private UserMapper userMapper;

// 新方式 - JPA Repository
@Autowired
private UserRepository userRepository;
```

### **2. 查询方法适配**
```java
// 旧方式 - MyBatis Plus
QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper.eq("username", username);
wrapper.eq("status", 1);
User user = userMapper.selectOne(wrapper);

// 新方式 - JPA Repository
Optional<User> userOpt = userRepository.findByUsernameAndStatus(username, 1);
User user = userOpt.orElse(null);
```

### **3. 分页查询适配**
```java
// 旧方式 - MyBatis Plus
Page<User> page = new Page<>(current, size);
QueryWrapper<User> wrapper = new QueryWrapper<>();
wrapper.like("real_name", keyword);
IPage<User> result = userMapper.selectPage(page, wrapper);

// 新方式 - JPA Repository
Pageable pageable = PageRequest.of(current - 1, size);
Page<User> result = userRepository.findByRealNameContaining(keyword, pageable);
```

### **4. 批量操作适配**
```java
// 旧方式 - MyBatis Plus
userMapper.deleteBatchIds(Arrays.asList(1L, 2L, 3L));

// 新方式 - JPA Repository
userRepository.batchSoftDelete(Arrays.asList(1L, 2L, 3L));
```

## 📝 适配检查清单

### **每个Service适配完成后需要检查：**
- [ ] 所有MyBatis Plus相关导入已移除
- [ ] 所有Mapper依赖已替换为Repository
- [ ] 所有查询方法已适配到Repository方法
- [ ] 分页查询已适配到Spring Data分页
- [ ] 批量操作已适配到Repository批量方法
- [ ] 事务注解保持不变
- [ ] 异常处理逻辑保持不变
- [ ] 业务逻辑保持不变

## 🧪 测试计划

### **单元测试**
1. **Repository层测试** - 测试所有Repository方法
2. **Service层测试** - 测试适配后的Service方法
3. **集成测试** - 测试Service与Repository的集成

### **功能测试**
1. **用户管理功能** - 登录、注册、权限验证
2. **课程管理功能** - 课程CRUD、选课流程
3. **教学管理功能** - 作业、考试、成绩管理
4. **系统功能** - 通知、缴费等功能

### **性能测试**
1. **查询性能** - 对比适配前后的查询性能
2. **批量操作性能** - 测试批量操作的性能
3. **并发性能** - 测试高并发场景下的性能

## 📊 适配进度跟踪

### **当前进度**
- ✅ **Repository层创建** - 35个Repository接口 (100%)
- 🔄 **Service层适配** - 0个Service类 (0%)
- ⏳ **单元测试** - 待开始
- ⏳ **功能测试** - 待开始
- ⏳ **性能测试** - 待开始

### **预计时间**
- **Service层适配** - 2-3天
- **单元测试** - 1-2天  
- **功能测试** - 1-2天
- **性能测试** - 1天
- **总计** - 5-8天

## 🚀 开始适配

让我们从最核心的UserService开始适配，这是系统的基础服务，影响登录、权限验证等核心功能。

### **第一步：UserService适配**
1. 检查当前UserService的实现
2. 识别需要适配的方法
3. 逐个方法进行适配
4. 测试适配结果

准备开始UserService的适配工作！🎯
