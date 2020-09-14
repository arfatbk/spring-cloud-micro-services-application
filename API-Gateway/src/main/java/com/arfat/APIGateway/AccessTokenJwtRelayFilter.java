package com.arfat.APIGateway;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Log4j2
public class AccessTokenJwtRelayFilter extends AbstractGatewayFilterFactory<Object> {
    @Autowired
    WebClient webClient;

    public AccessTokenJwtRelayFilter() {
    }

    public GatewayFilter apply() {
        return this.apply((Object) null);
    }

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {

            String accessToken = exchange.getRequest().getHeaders().getFirst("auth");
            //TODO: Derive resource from URI pattern
            String resource = (String) config;
            log.info("Token Relay===========================" + accessToken);


            //TODO:check if token is null
            String uri = "http://localhost:8282/oauth/check_token?token=" +
                    accessToken + "&resource=" + resource;

            return webClient.get().uri(uri)
                    .headers(httpHeaders -> httpHeaders.setBasicAuth("bW9iaWxlOnBpbg=="))
                    .exchange()
                    .flatMap(clientResponse -> {
                        if (clientResponse.statusCode().is2xxSuccessful()) {

                            return clientResponse
                                    .toEntity(Map.class)
                                    .flatMap(s -> {
                                        String jwtToken = (String) s.getBody().getOrDefault("jwt", null);
                                        ServerWebExchange ex = this.withBearerAuth(exchange, jwtToken);
                                        return chain.filter(ex);
                                    });
                        } else {
                            ServerHttpResponse response = exchange.getResponse();
                            response.setStatusCode(clientResponse.statusCode());
                            response.getHeaders().add("X-intercepted", "true");
                            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                            return exchange.getResponse().writeWith(
                                    clientResponse
                                            .toEntity(String.class)
                                            .flatMap(s -> Mono.just(response.bufferFactory().wrap(s.getBody().getBytes())))
                            );
                        }
                    });
        };
    }

    private ServerWebExchange withBearerAuth(ServerWebExchange exchange, String token) {
        return exchange.mutate().request((r) -> {
            r.headers((headers) -> {
                headers.setBearerAuth(token);
            });
        }).build();
    }
}