package com.arfat.APIGateway;

import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Arfat Bin Kileb
 * Created at 08-09-2020 07:41 AM
 */
@Configuration
@RestController
public class Router {

    @Bean
    RouteLocator routes(RouteLocatorBuilder builder) {

        return builder.routes()
                .route(r -> r.path("/get")
                        .filters(f -> f.addResponseHeader("X-Spring-Gateway", "API Gateway"))
                        .uri("forward:/fallback "))
                .build();
    }

    @RequestMapping("/fallback")
    public String fallback(){
        return "fallback";
    }
}
