# 智慧校园管理平台 - 前端项目

基于 Vue 3 + Element Plus 的智慧校园管理平台前端应用，支持学生端、教师端和家长端多角色访问。

## 技术栈

- **核心框架**: Vue 3 (Composition API)
- **构建工具**: Vite
- **UI组件库**: Element Plus
- **状态管理**: Pinia
- **路由管理**: Vue Router
- **HTTP客户端**: Axios
- **日期处理**: Day.js
- **开发语言**: JavaScript

## 项目结构

```
campus-management-frontend/
├── public/                     # 静态资源
├── src/
│   ├── api/                   # API接口定义
│   │   ├── auth.js           # 认证相关API
│   │   ├── course.js         # 课程相关API
│   │   ├── grade.js          # 成绩相关API
│   │   ├── student.js        # 学生相关API
│   │   ├── payment.js        # 支付相关API
│   │   └── request.js        # Axios封装
│   ├── components/            # 公共组件
│   ├── router/               # 路由配置
│   │   └── index.js          # 路由定义和守卫
│   ├── stores/               # Pinia状态管理
│   │   └── auth.js           # 认证状态管理
│   ├── views/                # 页面组件
│   │   ├── Login.vue         # 登录页面
│   │   ├── Layout.vue        # 主布局组件
│   │   ├── student/          # 学生端页面
│   │   │   ├── Dashboard.vue # 学生首页
│   │   │   ├── Courses.vue   # 我的课程
│   │   │   ├── CourseSelection.vue # 选课系统
│   │   │   ├── Grades.vue    # 成绩查询
│   │   │   ├── Schedule.vue  # 课程表
│   │   │   └── Profile.vue   # 个人信息
│   │   ├── teacher/          # 教师端页面
│   │   └── parent/           # 家长端页面
│   ├── App.vue               # 根组件
│   └── main.js               # 入口文件
├── index.html                # HTML模板
├── package.json              # 项目配置
├── vite.config.js            # Vite配置
└── README.md                 # 项目文档
```

## 功能模块

### 🎓 学生端
- **首页仪表盘**: 课程统计、今日课程、最新成绩、通知公告
- **我的课程**: 已选课程管理、课程详情查看
- **选课系统**: 课程浏览、在线选课、退课功能
- **成绩查询**: 各科成绩查看、成绩统计分析
- **课程表**: 周视图/列表视图切换、课程安排查看
- **个人信息**: 基本信息管理、密码修改

### 👨‍🏫 教师端
- **首页仪表盘**: 教学统计、课程管理概览
- **课程管理**: 课程信息维护、学生名单管理
- **成绩录入**: 学生成绩录入、批量导入导出
- **学生管理**: 班级学生信息查看、学习情况跟踪
- **课程安排**: 教学计划制定、时间安排
- **个人信息**: 教师信息管理

### 👨‍👩‍👧‍👦 家长端
- **首页仪表盘**: 子女学习情况概览
- **子女信息**: 多个子女信息管理
- **成绩查看**: 子女各科成绩查询
- **缴费记录**: 学费缴纳记录、在线缴费
- **个人信息**: 家长信息管理

## 开始使用

### 环境要求

- Node.js >= 16.0.0
- npm >= 7.0.0 或 yarn >= 1.22.0

### 安装依赖

```bash
# 进入前端项目目录
cd campus-management-frontend

# 使用 npm 安装依赖
npm install

# 或使用 yarn 安装依赖
yarn install
```

### 开发环境运行

```bash
# 启动开发服务器
npm run dev

# 或使用 yarn
yarn dev
```

项目将在 `http://localhost:3000` 启动，支持热重载。

### 生产构建

```bash
# 构建生产版本
npm run build

# 或使用 yarn
yarn build
```

构建产物将输出到 `dist` 目录。

### 预览生产构建

```bash
# 预览生产构建
npm run preview

# 或使用 yarn
yarn preview
```

## API 接口

前端通过 Axios 与后端 Spring Boot 服务进行通信，主要接口包括：

### 认证相关
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/userinfo` - 获取用户信息
- `POST /api/auth/validate` - 验证token
- `POST /api/auth/change-password` - 修改密码

### 学生相关
- `GET /api/student/dashboard` - 学生仪表盘数据
- `GET /api/student/courses` - 学生课程列表
- `GET /api/student/grades` - 学生成绩列表
- `GET /api/student/schedule` - 学生课程表
- `POST /api/student/course-selection` - 选课
- `DELETE /api/student/course-selection/{id}` - 退课

### 教师相关
- `GET /api/teacher/courses` - 教师课程列表
- `GET /api/teacher/students` - 学生列表
- `POST /api/teacher/grades` - 录入成绩
- `PUT /api/teacher/grades/{id}` - 更新成绩

### 家长相关
- `GET /api/parent/children` - 子女列表
- `GET /api/parent/children/{id}/grades` - 子女成绩
- `GET /api/parent/children/{id}/payments` - 缴费记录

## 开发规范

### 代码结构
- 使用 Vue 3 Composition API
- 采用单文件组件 (.vue) 开发
- API 调用统一放在 `src/api/` 目录
- 页面组件放在 `src/views/` 目录
- 公共组件放在 `src/components/` 目录

### 命名规范
- 文件名：PascalCase (如: `UserProfile.vue`)
- 组件名：PascalCase
- 变量名：camelCase
- 常量：UPPER_SNAKE_CASE

### 状态管理
使用 Pinia 进行状态管理，主要包括：
- `auth` store: 用户认证状态
- 其他业务相关状态按模块划分

### 路由配置
- 使用 Vue Router 进行路由管理
- 实现路由守卫进行权限控制
- 按用户角色分组路由

## 项目特性

### 🔐 权限控制
- 基于 JWT 的身份认证
- 路由级别的权限控制
- 角色分离的功能模块

### 📱 响应式设计
- 支持桌面端和移动端访问
- Element Plus 组件库保证UI一致性
- 灵活的布局适配

### 🚀 性能优化
- Vite 构建工具提供快速的开发体验
- 路由懒加载减少初始包大小
- API 请求统一管理和错误处理

### 🎨 用户体验
- 直观的操作界面
- 丰富的交互反馈
- 完善的表单验证

## 部署说明

### 开发环境
1. 确保后端服务已启动 (默认端口: 8080)
2. 前端开发服务器会自动代理 API 请求到后端

### 生产环境
1. 构建生产版本: `npm run build`
2. 将 `dist` 目录部署到 Web 服务器
3. 配置 Nginx 反向代理 API 请求到后端服务

### Docker 部署
```bash
# 构建Docker镜像
docker build -t campus-frontend .

# 运行容器
docker run -p 80:80 campus-frontend
```

## 常见问题

### Q: 登录后页面空白？
A: 检查后端服务是否正常运行，确认API接口地址配置正确。

### Q: 开发环境API请求失败？
A: 确认 `vite.config.js` 中的代理配置与后端服务地址一致。

### Q: 构建时出现内存不足？
A: 增加 Node.js 内存限制：`node --max-old-space-size=4096 node_modules/.bin/vite build`

## 贡献指南

1. Fork 项目仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 联系方式

如有问题或建议，请通过以下方式联系：

- 项目Issues: [GitHub Issues](https://github.com/your-repo/issues)
- 邮箱: your-email@example.com

---

**智慧校园管理平台** - 让教育管理更简单、更高效！