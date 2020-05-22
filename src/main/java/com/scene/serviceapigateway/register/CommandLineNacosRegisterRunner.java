package com.scene.serviceapigateway.register;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CommandLineNacosRegisterRunner implements CommandLineRunner {
    @NacosInjected
    private NamingService namingService;

    @Resource
    private NacosRegisterProperties nacosProperties;

    @Override
    public void run(String... args) throws Exception {
        namingService.registerInstance(nacosProperties.getApplicationName(), nacosProperties.getGroupName(), "127.0.0.1", nacosProperties.getServerPort());
    }
}
