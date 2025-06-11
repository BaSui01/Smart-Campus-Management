# 前后端配置同步指南

## 概述

为了解决前端 Vite 配置中硬编码后端端口号的问题，我们实现了一个自动配置同步机制。

## 问题背景

原来的 `vite.config.js` 中存在硬编码的端口号：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',  // 硬编码端口号
    changeOrigin: true,
    secure: false
  }
}
```

## 解决方案

### 1. 环境变量配置

在 `.env` 文件中配置后端 API 地址：

```env
# 开发环境配置
VITE_API_BASE_URL=http://localhost:8082/api
VITE_ADMIN_BASE_URL=http://localhost:8082/admin
```

### 2. Vite 配置动态读取

修改后的 `vite.config.js`：

```javascript
proxy: {
  '/api': {
    target: process.env.VITE_API_BASE_URL?.replace('/api', '') || 'http://localhost:8082',
    changeOrigin: true,
    secure: false
  }
}
```

### 3. 自动同步脚本

创建了 `scripts/sync-backend-config.js` 脚本，用于：

- 从后端 `application.yml` 文件中读取端口配置
- 自动更新前端 `.env` 文件中的相关环境变量
- 确保前后端端口配置保持一致

## 使用方法

### 手动同步配置

```bash
# 在前端项目根目录执行
npm run sync-config
```

### 自动同步（推荐）

每次启动开发服务器时自动同步：

```bash
npm run dev
```

脚本会在开发服务器启动前自动执行配置同步。

## 配置文件说明

### 后端配置文件

路径：`campus-management-backend/src/main/resources/application.yml`

关键配置：
```yaml
server:
  port: 8082  # 后端服务端口
```

### 前端环境变量文件

路径：`campus-management-frontend/.env`

自动同步的变量：
- `VITE_API_BASE_URL`: API 基础地址
- `VITE_ADMIN_BASE_URL`: 管理后台基础地址

## 脚本功能特性

1. **智能端口检测**: 自动从后端 YAML 配置中提取端口号
2. **环境变量更新**: 自动更新或创建必要的环境变量
3. **变更提示**: 显示详细的配置变更信息
4. **错误处理**: 完善的错误处理和提示信息

## 新增的 NPM 脚本

在 `package.json` 中新增的脚本：

```json
{
  "scripts": {
    "sync-config": "node scripts/sync-backend-config.js",
    "predev": "npm run sync-config"
  }
}
```

- `sync-config`: 手动执行配置同步
- `predev`: 在 `npm run dev` 前自动执行配置同步

## 依赖项

新增依赖项：
- `js-yaml`: 用于解析 YAML 配置文件

## 工作流程

1. 开发者修改后端 `application.yml` 中的端口配置
2. 运行 `npm run dev` 启动前端开发服务器
3. `predev` 脚本自动执行配置同步
4. 同步脚本读取后端配置并更新前端环境变量
5. Vite 使用更新后的环境变量配置代理

## 注意事项

1. **项目结构**: 脚本假设前后端项目在同一个父目录下
2. **权限要求**: 需要读写权限来访问配置文件
3. **Node.js 版本**: 需要 Node.js 18+ 支持
4. **开发服务器重启**: 配置更改后需要重启开发服务器才能生效

## 故障排除

### 常见问题

1. **找不到后端配置文件**
   - 检查项目目录结构是否正确
   - 确保后端项目路径配置正确

2. **权限错误**
   - 确保有读写配置文件的权限
   - 在 Windows 上可能需要管理员权限

3. **YAML 解析错误**
   - 检查后端 `application.yml` 文件格式是否正确
   - 确保端口配置存在且格式正确

### 调试方法

运行同步脚本查看详细信息：

```bash
node scripts/sync-backend-config.js
```

## 未来改进

1. **支持多环境配置**: 根据不同环境（dev/test/prod）读取不同配置
2. **配置验证**: 增加配置有效性验证
3. **热重载**: 监听配置文件变化并自动更新