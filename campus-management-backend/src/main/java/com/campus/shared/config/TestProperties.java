package com.campus.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 测试配置属性
 * 用于绑定application.yml中的test配置
 *
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-01-15
 */
@Component
@ConfigurationProperties(prefix = "test")
public class TestProperties {

    private Data data = new Data();
    private Security security = new Security();

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    /**
     * 测试数据配置
     */
    public static class Data {
        private boolean init = true;

        public boolean isInit() {
            return init;
        }

        public void setInit(boolean init) {
            this.init = init;
        }
    }

    /**
     * 测试安全配置
     */
    public static class Security {
        private boolean enabled = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
