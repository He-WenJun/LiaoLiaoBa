package com.hwj.tieba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class TBEurekaService {
    public static void main(String[] args) {
        SpringApplication.run(TBEurekaService.class,args);
    }
}
