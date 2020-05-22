package com.scene.serviceapigateway.route.nacos;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.scene.serviceapigateway.nacos.ZuulRouteEntity;
import com.scene.serviceapigateway.route.AbstractDynamicRouteLocator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class NacosDynamicRouteLocator extends AbstractDynamicRouteLocator {

    private ConfigService nacosConfigService;
    private String dataId;
    private String groupId;

    @Setter
    private List<ZuulRouteEntity> zuulRouteEntities;

    public NacosDynamicRouteLocator(ConfigService nacosConfigService,
                                    String servletPath, ZuulProperties properties,
                                    String dataId, String groupId) {
        super(servletPath, properties);
        this.nacosConfigService = nacosConfigService;
        this.dataId = dataId;
        this.groupId = groupId;
    }

    /**
     * 服务启动时动态加载Nacos配置
     */
    @Override
    protected Map<String, ZuulRoute> loadDynamicRoute() {
        Map<String, ZuulRoute> routes = new LinkedHashMap<>();
        if (zuulRouteEntities == null) {
            zuulRouteEntities = pullNacosConfig();
        }
        try {
            for (ZuulRouteEntity result : zuulRouteEntities) {
                if (StringUtils.isBlank(result.getPath()) || !result.isEnabled()) {
                    continue;
                }
                ZuulRoute zuulRoute = new ZuulRoute();
                BeanUtils.copyProperties(zuulRoute, result);
                routes.put(zuulRoute.getPath(), zuulRoute);
            }
            log.info("Load dynamic route size={}", routes.size());
        } catch (Exception e) {
            log.error("Load dynamic route error ,info={}", e.getMessage(), e);
        }
        return routes;
    }

    /**
     * 查询Nacos-zuul的路由配置
     */
    private List<ZuulRouteEntity> pullNacosConfig() {
        try {
            String content = nacosConfigService.getConfig(dataId, groupId, 5000);
            log.info("Pull remote Nacos route info={}", content);
            return parseRouteJsonToEntity(content);
        } catch (NacosException e) {
            log.error("Listener Nacos properties occur error,error info={}", e.getMessage(), e);
        }
        return new ArrayList<>(0);
    }

    public List<ZuulRouteEntity> parseRouteJsonToEntity(String content) {
        if (StringUtils.isNotEmpty(content)) {
            return JSONObject.parseArray(content, ZuulRouteEntity.class);
        }
        return new ArrayList<>(0);
    }
}
