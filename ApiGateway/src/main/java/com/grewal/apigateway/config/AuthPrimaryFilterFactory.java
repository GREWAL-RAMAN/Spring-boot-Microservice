package com.grewal.apigateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RefreshScope
@RequiredArgsConstructor
public class AuthPrimaryFilterFactory implements GatewayFilter {

    @Value("${primary.api.key}")
    private String api_key;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {
       String givenApi= exchange.getRequest().getHeaders().getFirst("x-api-key");

       log.info("It is inside the AuthPrimary Filter");
        log.info(givenApi);

        return chain.filter(exchange);


    }
}
