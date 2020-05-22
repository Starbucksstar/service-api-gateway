package com.scene.serviceapigateway.listener;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.scene.serviceapigateway.nacos.ZuulRouteEntity;
import com.scene.serviceapigateway.route.nacos.NacosDynamicRouteLocator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.RoutesRefreshedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class NacosDynamicRouteListener {

    @Autowired
    private NacosDynamicRouteLocator locator;
    @Autowired
    private ApplicationEventPublisher publisher;

    @NacosConfigListener(dataId = "${nacos.gateway.dynamic.routeDataId}", groupId = "${nacos.gateway.dynamic.routeGroupId}")
    public void onMessage(String configInfo) {
        log.info("Nacos Gateway route config changed, info={}", configInfo);
        //设置路由信息到定位器
        locator.setZuulRouteEntities(parseRouteJsonToEntity(configInfo));
        //publish zuul route refresh event
        RoutesRefreshedEvent routesRefreshedEvent = new RoutesRefreshedEvent(locator);
        publisher.publishEvent(routesRefreshedEvent);
    }

    private List<ZuulRouteEntity> parseRouteJsonToEntity(String content) {
        if (StringUtils.isNotEmpty(content)) {
            return JSONObject.parseArray(content, ZuulRouteEntity.class);
        }
        return new ArrayList<>(0);
    }

}
