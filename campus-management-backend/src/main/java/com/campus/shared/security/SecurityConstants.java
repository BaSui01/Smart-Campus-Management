package com.campus.shared.security;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 安全常量类
 * 存储安全配置相关的常量，避免硬编码
 */
public class SecurityConstants {
    // API路径
    public static final String API_PREFIX = "/api/";
    public static final String API_PATH_PATTERN = "/api/v1/**";

    // 管理后台路径
    public static final String ADMIN_PREFIX = "/admin/";
    public static final String ADMIN_PATH_PATTERN = "/admin/**";

    // 登录相关
    public static final String LOGIN_PAGE = "/admin/login";
    public static final String LOGOUT_URL = "/admin/logout";
    public static final String LOGOUT_SUCCESS_URL = "/?logout=true";
    public static final String ACCESS_DENIED_PAGE = "/admin/access-denied";
    public static final String HOME_PAGE = "/";

    /**
     * 获取公开端点列表
     */
    public static AntPathRequestMatcher[] getPublicEndpoints() {
        return new AntPathRequestMatcher[] {
            new AntPathRequestMatcher("/"),                    // 首页
            new AntPathRequestMatcher("/index"),               // 首页别名
            new AntPathRequestMatcher("/home"),                // 首页别名
            new AntPathRequestMatcher("/about"),               // 关于我们
            new AntPathRequestMatcher("/contact"),             // 联系我们
            new AntPathRequestMatcher("/help"),                // 帮助页面
            new AntPathRequestMatcher("/privacy"),             // 隐私政策
            new AntPathRequestMatcher("/terms"),               // 服务条款
            new AntPathRequestMatcher("/admin/login"),         // 登录页面
            new AntPathRequestMatcher("/admin/captcha"),       // 验证码接口
            new AntPathRequestMatcher("/admin/check-login"),   // 登录状态检查
            new AntPathRequestMatcher("/static/**"),           // 静态资源
            new AntPathRequestMatcher("/css/**"),              // CSS文件
            new AntPathRequestMatcher("/js/**"),               // JavaScript文件
            new AntPathRequestMatcher("/images/**"),           // 图片文件
            new AntPathRequestMatcher("/fonts/**"),            // 字体文件
            new AntPathRequestMatcher("/favicon.ico"),         // 网站图标
            new AntPathRequestMatcher("/error")                // 错误页面
        };
    }

    /**
     * 获取API文档端点列表
     */
    public static AntPathRequestMatcher[] getApiDocEndpoints() {
        return new AntPathRequestMatcher[] {
            new AntPathRequestMatcher("/swagger-ui/**"),       // Swagger UI
            new AntPathRequestMatcher("/swagger-ui.html"),     // Swagger UI首页
            new AntPathRequestMatcher("/v3/api-docs/**"),      // OpenAPI 3.0文档
            new AntPathRequestMatcher("/v2/api-docs/**"),      // OpenAPI 2.0文档
            new AntPathRequestMatcher("/swagger-resources/**"), // Swagger资源
            new AntPathRequestMatcher("/webjars/**"),          // Web JAR资源
            new AntPathRequestMatcher("/doc.html"),            // Knife4j文档
            new AntPathRequestMatcher("/favicon.ico")          // 图标
        };
    }
}
