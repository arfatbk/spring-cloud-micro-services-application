package com.arfat.APIGateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

/**
 * @author Arfat Bin Kileb
 * Created at 08-09-2020 07:41 AM
 */
@Configuration
@RestController
public class Router {

    @Autowired
    AccessTokenJwtRelayFilter tokenRelayFilter;

    @Bean
    RouteLocator routes(RouteLocatorBuilder builder) {

        return builder.routes()
                .route(r -> r.path("/get")
                        .filters(f -> f
                                .addResponseHeader("X-Spring-Gateway", "API Gateway")
                                .setPath("/fallback"))
                        .uri("http://localhost:8080"))
                .route(r -> r.path("/fallback")
                        .uri("localhost:8080"))
                .route(r -> r.path("/customers*")
                        .filters(f -> f.setPath("/")
                                .filter(tokenRelayFilter.apply("customer-service")))
                        .uri("lb://customer-service"))
                .build();
    }

    @Bean
    AccessTokenJwtRelayFilter getRequestBean() {
        return new AccessTokenJwtRelayFilter();
    }

    @Bean
    WebClient webClient() {
        //TODO: Add default header for OAuth server
        return WebClient.builder().build();
    }

    @RequestMapping("/fallback")
    public String fallback(@RequestHeader("Authorization") Optional<String> jwt) {
        System.out.println("jwt Token = " + jwt.get());
        return "fallback";
    }
}
