package com.campus.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 自定义配置属性类
 * 用于处理应用程序的自定义配置属性，避免YAML配置警告
 */
@Component
@ConfigurationProperties(prefix = "")
public class CustomConfigurationProperties {

    private JwtProperties jwt = new JwtProperties();
    private AppProperties app = new AppProperties();
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
        private String secret;
        private long expiration;
        private long refreshExpiration;
        private String header;
        private String prefix;

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
        private JwtConfig jwt = new JwtConfig();

        public JwtConfig getJwt() {
            return jwt;
        }

        public void setJwt(JwtConfig jwt) {
            this.jwt = jwt;
        }

        public static class JwtConfig {
            private String secret;
            private int expiration;

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
        private DataConfig data = new DataConfig();
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
            private boolean init;

            public boolean isInit() {
                return init;
            }

            public void setInit(boolean init) {
                this.init = init;
            }
        }

        public static class SecurityConfig {
            private boolean enabled;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
        }
    }
}
