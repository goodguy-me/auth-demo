package org.example.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.ApplicationListener;

import java.util.Map;
@Slf4j
@SpringBootApplication()
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GatewayApplication.class);
        app.addListeners(new ApplicationListener<ApplicationReadyEvent>() {
            @Override
            public void onApplicationEvent(ApplicationReadyEvent event) {
                log.info("=== 检查注册的GlobalFilters ===");
                Map<String, GlobalFilter> filters = event.getApplicationContext()
                        .getBeansOfType(GlobalFilter.class);
                filters.forEach((name, filter) -> {
                    log.info("已注册GlobalFilter: {} -> {}", name, filter.getClass().getName());
                });
                log.info("总共注册了 {} 个GlobalFilter", filters.size());
            }
        });
        app.run(args);
    }
//        SpringApplication.run(GatewayApplication.class, args);

}
