package com.campus.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * 自定义配置属性类
 * 用于处理应用程序的自定义配置属性
 */
@Component
@ConfigurationProperties(prefix = "campus")
@Validated
public class CustomConfigurationProperties {

    @Valid
    private JwtProperties jwt = new JwtProperties();

    @Valid
    private AppProperties app = new AppProperties();

    @Valid
    private TestProperties test = new TestProperties();

    public JwtProperties getJwt() {
        return jwt;
    }

    public void setJwt(JwtProperties jwt) {
        this.jwt = jwt;
    }

    public AppProperties getApp() {
        return app;
    }

    public void setApp(AppProperties app) {
        this.app = app;
    }

    public TestProperties getTest() {
        return test;
    }

    public void setTest(TestProperties test) {
        this.test = test;
    }

    /**
     * JWT配置属性
     */
    public static class JwtProperties {
        @NotBlank(message = "JWT密钥不能为空")
        private String secret;

        @Min(value = 60, message = "JWT过期时间不能小于60秒")
        private long expiration = 3600; // 默认1小时

        @Min(value = 60, message = "JWT刷新令牌过期时间不能小于60秒")
        private long refreshExpiration = 86400; // 默认24小时

        @NotBlank(message = "JWT请求头名称不能为空")
        private String header = "Authorization";

        @NotBlank(message = "JWT令牌前缀不能为空")
        private String prefix = "Bearer ";

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public long getExpiration() {
            return expiration;
        }

        public void setExpiration(long expiration) {
            this.expiration = expiration;
        }

        public long getRefreshExpiration() {
            return refreshExpiration;
        }

        public void setRefreshExpiration(long refreshExpiration) {
            this.refreshExpiration = refreshExpiration;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }

    /**
     * 应用配置属性
     */
    public static class AppProperties {
        @Valid
        private JwtConfig jwt = new JwtConfig();

        @NotBlank(message = "应用名称不能为空")
        private String name = "智慧校园管理系统";

        @NotBlank(message = "应用版本不能为空")
        private String version = "1.0.0";

        public JwtConfig getJwt() {
            return jwt;
        }

        public void setJwt(JwtConfig jwt) {
            this.jwt = jwt;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public static class JwtConfig {
            @NotBlank(message = "JWT密钥不能为空")
            private String secret;

            @Min(value = 60, message = "JWT过期时间不能小于60秒")
            private int expiration = 3600;

            public String getSecret() {
                return secret;
            }

            public void setSecret(String secret) {
                this.secret = secret;
            }

            public int getExpiration() {
                return expiration;
            }

            public void setExpiration(int expiration) {
                this.expiration = expiration;
            }
        }
    }

    /**
     * 测试配置属性
     */
    public static class TestProperties {
        @Valid
        private DataConfig data = new DataConfig();

        @Valid
        private SecurityConfig security = new SecurityConfig();

        public DataConfig getData() {
            return data;
        }

        public void setData(DataConfig data) {
            this.data = data;
        }

        public SecurityConfig getSecurity() {
            return security;
        }

        public void setSecurity(SecurityConfig security) {
            this.security = security;
        }

        public static class DataConfig {
            private boolean init = false;

            public boolean isInit() {
                return init;
            }

            public void setInit(boolean init) {
                this.init = init;
            }
        }

        public static class SecurityConfig {
            private boolean enabled = true;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }
    }
}
