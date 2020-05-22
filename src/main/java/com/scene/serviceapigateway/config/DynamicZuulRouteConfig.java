package com.scene.serviceapigateway.config;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.scene.serviceapigateway.route.nacos.NacosDynamicRouteLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "nacos.gateway.dynamic.route", name = "enabled", havingValue = "true")
public class DynamicZuulRouteConfig {
    @Autowired
    private ZuulProperties zuulProperties;

    @Autowired
    private DispatcherServletPath dispatcherServletPath;

    @Configuration
    @ConditionalOnProperty(prefix = "nacos.gateway.dynamic.route", name = "dataType", havingValue = "nacos", matchIfMissing = true)
    public class NacosZuulRoute {
        @NacosInjected
        private ConfigService nacosConfigService;
        @Value("${nacos.gateway.dynamic.routeDataId}")
        private String routeDataId;
        @Value("${nacos.gateway.dynamic.routeGroupId}")
        private String routeGroupId;

        @Bean
        public NacosDynamicRouteLocator nacosDynRouteLocator() {
            return new NacosDynamicRouteLocator(nacosConfigService, dispatcherServletPath.getPrefix(), zuulProperties, routeDataId, routeGroupId);
        }
    }
}
