package com.hwj.tieba;

import com.hwj.tieba.config.common.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableDiscoveryClient
@EnableZuulProxy
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
@SpringBootApplication
public class TBGatewayServer {
    public static void main(String[] args) {
        SpringApplication.run(TBGatewayServer.class,args);
    }
}
