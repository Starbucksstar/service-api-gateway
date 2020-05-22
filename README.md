# service-api-gateway
基于Zuul+Nacos+Jwt+Springboot 静态&amp;动态路由配置和JwtToken校验网关服务

![](https://github.com/Starbucksstar/service-api-gateway/blob/master/nacos.png)

![](https://github.com/Starbucksstar/service-api-gateway/blob/master/nacos-route-detail.png)

## 静态路由
application.properties中zuul.routes...相关配置，静态路由

## 动态路由
Nacos配置中心新建对应dataId,groupId的路由配置json文件，格式如下：
```json
[
    {
        "enabled":true,
        "id":"baidu",
        "path":"/api/baidu",
        "retryable":false,
        "stripPrefix":true,
        "url":"https://www.baidu.com/"
    }, {
        "enabled":true,
        "id":"github11",
        "path":"/api/taobao",
        "retryable":false,
        "stripPrefix":true,
        "url":"http://www.taobao.com/"
    }
]
```
- NacosDynamicRouteListener @NacosConfigListener --监听远程Nacos 路由配置更新
- NacosDynamicRouteLocator implements RefreshableRouteLocator -- 实现路由可刷新定位器
- RoutesRefreshedEvent -- zuul 路由更新事件，publish后实现zuul更新路由

## JwtToken校验
JwtAuthenticationFilter extends ZuulFilter -- 实现api-gateway 对所有请求token的校验
