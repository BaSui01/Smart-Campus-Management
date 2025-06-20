package com.campus.shared.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * HTTPS重定向配置
 * 在生产环境中强制将HTTP请求重定向到HTTPS
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-18
 */
@Configuration
@Profile("prod")
public class HttpsRedirectConfig {

    @Value("${server.port:8443}")
    private int httpsPort;

    @Value("${server.http.port:8080}")
    private int httpPort;

    /**
     * 配置Tomcat服务器工厂，添加HTTP到HTTPS重定向
     */
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                // 配置安全约束，强制HTTPS
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                
                context.addConstraint(securityConstraint);
            }
        };
        
        // 添加HTTP连接器，用于重定向到HTTPS
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }

    /**
     * 创建HTTP连接器，将HTTP请求重定向到HTTPS
     */
    private Connector redirectConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(httpPort);
        connector.setSecure(false);
        connector.setRedirectPort(httpsPort);
        return connector;
    }
}
