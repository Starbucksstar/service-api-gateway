package com.scene.serviceapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy
public class ServiceApiGatewayApplication{

    public static void main(String[] args) {
        SpringApplication.run(ServiceApiGatewayApplication.class, args);
    }

}
