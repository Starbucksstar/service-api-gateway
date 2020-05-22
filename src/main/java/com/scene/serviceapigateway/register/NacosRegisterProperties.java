package com.scene.serviceapigateway.register;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
class NacosRegisterProperties {
    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${server.port}")
    private int serverPort;
    @Value("${nacos.application.group.name}")
    private String groupName;
}
