import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'

import App from './App.vue'
import router from './router'

// 导入全局样式
import './styles/global.css'

// 导入安全模块
import { initializeSecurity, reportSecurityEvent } from '@/utils/securityInit.js'
import { SECURITY_CONFIG } from '@/config/security.js'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用插件
app.use(createPinia())
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
  size: 'default'
})

// 增强的全局错误处理
app.config.errorHandler = (err, vm, info) => {
  console.error('全局错误:', err)
  console.error('错误信息:', info)
  console.error('组件实例:', vm)

  // 上报安全事件
  reportSecurityEvent('application_error', {
    message: err.message,
    stack: err.stack,
    info: info,
    component: vm?.$options.name || 'Unknown'
  })
}

// 异步初始化应用
async function initializeApp() {
  try {
    // 1. 初始化安全模块
    console.log('🚀 正在启动智慧校园管理系统...')
    await initializeSecurity()

    // 2. 挂载应用
    app.mount('#app')

    console.log('✅ 应用启动完成')

    // 3. 上报启动成功事件
    reportSecurityEvent('application_started', {
      version: '1.0.0',
      environment: import.meta.env.MODE,
      timestamp: Date.now()
    })

  } catch (error) {
    console.error('❌ 应用启动失败:', error)

    // 显示错误信息给用户
    document.body.innerHTML = `
      <div style="
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100vh;
        font-family: Arial, sans-serif;
        background: #f5f5f5;
      ">
        <div style="
          text-align: center;
          padding: 40px;
          background: white;
          border-radius: 10px;
          box-shadow: 0 4px 12px rgba(0,0,0,0.1);
          max-width: 500px;
        ">
          <h2 style="color: #e74c3c; margin-bottom: 20px;">🚫 系统启动失败</h2>
          <p style="color: #666; margin-bottom: 20px;">
            智慧校园管理系统无法正常启动，请联系系统管理员。
          </p>
          <p style="color: #999; font-size: 14px;">
            错误信息: ${error.message}
          </p>
          <button
            onclick="window.location.reload()"
            style="
              margin-top: 20px;
              padding: 10px 20px;
              background: #3498db;
              color: white;
              border: none;
              border-radius: 5px;
              cursor: pointer;
            "
          >
            重新加载
          </button>
        </div>
      </div>
    `

    // 上报启动失败事件
    reportSecurityEvent('application_start_failed', {
      error: error.message,
      stack: error.stack,
      timestamp: Date.now()
    })
  }
}

// 启动应用
initializeApp()