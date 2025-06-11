#!/usr/bin/env node

/**
 * 同步后端配置文件中的端口号到前端环境变量
 * 使用方法: node scripts/sync-backend-config.js
 */

import fs from 'fs';
import path from 'path';
import yaml from 'js-yaml';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// 文件路径配置
const BACKEND_CONFIG_PATH = '../../campus-management-backend/src/main/resources/application.yml';
const FRONTEND_ENV_PATH = '.env';

/**
 * 从后端配置文件中提取端口号
 */
function extractPortFromBackendConfig() {
  try {
    const backendConfigPath = path.resolve(__dirname, BACKEND_CONFIG_PATH);
    
    if (!fs.existsSync(backendConfigPath)) {
      console.error(`❌ 后端配置文件不存在: ${backendConfigPath}`);
      return null;
    }

    const configContent = fs.readFileSync(backendConfigPath, 'utf8');
    const config = yaml.load(configContent);
    
    const port = config?.server?.port;
    if (!port) {
      console.error('❌ 无法从后端配置文件中找到端口号');
      return null;
    }

    console.log(`✅ 从后端配置中提取端口号: ${port}`);
    return port;
  } catch (error) {
    console.error('❌ 读取后端配置文件失败:', error.message);
    return null;
  }
}

/**
 * 更新前端环境变量文件
 */
function updateFrontendEnv(port) {
  try {
    const envPath = path.resolve(__dirname, '..', FRONTEND_ENV_PATH);
    
    let envContent = '';
    if (fs.existsSync(envPath)) {
      envContent = fs.readFileSync(envPath, 'utf8');
    }

    // 更新或添加环境变量
    const baseUrl = `http://localhost:${port}`;
    const apiBaseUrl = `${baseUrl}/api`;
    const adminBaseUrl = `${baseUrl}/admin`;

    const envVars = {
      'VITE_API_BASE_URL': apiBaseUrl,
      'VITE_ADMIN_BASE_URL': adminBaseUrl
    };

    let updatedContent = envContent;
    let hasChanges = false;

    Object.entries(envVars).forEach(([key, value]) => {
      const regex = new RegExp(`^${key}=.*$`, 'm');
      const newLine = `${key}=${value}`;
      
      if (regex.test(updatedContent)) {
        const oldValue = updatedContent.match(regex)?.[0];
        if (oldValue !== newLine) {
          updatedContent = updatedContent.replace(regex, newLine);
          hasChanges = true;
          console.log(`🔄 更新 ${key}: ${oldValue.split('=')[1]} → ${value}`);
        }
      } else {
        // 如果变量不存在，添加到文件开头的配置部分
        if (!updatedContent.includes('\n# 系统配置\n')) {
          updatedContent = updatedContent.replace(
            /^# 开发环境配置$/m,
            `# 开发环境配置\n${newLine}`
          );
        } else {
          updatedContent = updatedContent.replace(
            /^# 系统配置$/m,
            `${newLine}\n\n# 系统配置`
          );
        }
        hasChanges = true;
        console.log(`➕ 添加 ${key}: ${value}`);
      }
    });

    if (hasChanges) {
      fs.writeFileSync(envPath, updatedContent);
      console.log(`✅ 前端环境变量文件已更新: ${envPath}`);
    } else {
      console.log('ℹ️  环境变量无需更新');
    }

    return true;
  } catch (error) {
    console.error('❌ 更新前端环境变量失败:', error.message);
    return false;
  }
}

/**
 * 主函数
 */
function main() {
  console.log('🚀 开始同步后端配置到前端环境变量...\n');

  const port = extractPortFromBackendConfig();
  if (!port) {
    process.exit(1);
  }

  const success = updateFrontendEnv(port);
  if (!success) {
    process.exit(1);
  }

  console.log('\n✅ 配置同步完成！');
  console.log('\n💡 提示:');
  console.log('  - 如果开发服务器正在运行，请重启以使配置生效');
  console.log('  - 确保后端服务器在指定端口运行');
}

// 直接执行主函数
main();

export {
  extractPortFromBackendConfig,
  updateFrontendEnv
};