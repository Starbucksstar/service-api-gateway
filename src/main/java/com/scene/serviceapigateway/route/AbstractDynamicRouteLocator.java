package com.scene.serviceapigateway.route;

import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractDynamicRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator {
    private ZuulProperties properties;

    public AbstractDynamicRouteLocator(String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
    }

    @Override
    public void refresh() {
        doRefresh();
    }

    @Override
    protected Map<String, ZuulRoute> locateRoutes() {
        LinkedHashMap<String, ZuulRoute> routesMap = new LinkedHashMap<>();
        // 从application.properties中加载静态路由信息
        routesMap.putAll(super.locateRoutes());
        // 从Nacos中加载动态路由信息
        routesMap.putAll(loadDynamicRoute());
        // 优化配置
        LinkedHashMap<String, ZuulRoute> values = new LinkedHashMap<>();
        for (Map.Entry<String, ZuulRoute> entry : routesMap.entrySet()) {
            String path = entry.getKey();
            if (StringUtils.isEmpty(path)) {
                continue;
            }
            values.put(path, entry.getValue());
        }
        return values;
    }

    protected abstract Map<String, ZuulRoute> loadDynamicRoute();
}
