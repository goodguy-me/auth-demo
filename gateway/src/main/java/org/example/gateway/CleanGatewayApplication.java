//package org.example.gateway;
//
///**
// * @author ZSZ
// * @date 2026/2/11 8:48
// * @description
// */
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.core.env.Environment;
//
//@Slf4j
//@SpringBootApplication(
//        scanBasePackages = "org.example.gateway.filter",
//        exclude = {
//                // 排除所有可能引入WebMvc的自动配置
//                org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.class,
//                org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration.class,
//                org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration.class
//        }
//)
//@EnableDiscoveryClient
//public class CleanGatewayApplication {
//    public static void main(String[] args) {
//        ConfigurableApplicationContext context = SpringApplication.run(CleanGatewayApplication.class, args);
//
//        // 验证Filter是否注册
//        String[] filterBeans = context.getBeanNamesForType(org.springframework.cloud.gateway.filter.GlobalFilter.class);
//        log.info("==========================================");
//        log.info("已注册的GlobalFilter数量: {}", filterBeans.length);
//        for (String beanName : filterBeans) {
//            log.info("  - {}", beanName);
//        }
//        log.info("==========================================");
//    }
//}
