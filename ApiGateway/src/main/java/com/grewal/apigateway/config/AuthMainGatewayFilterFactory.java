package com.grewal.apigateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RefreshScope
@RequiredArgsConstructor
public class AuthMainGatewayFilterFactory implements GatewayFilter {


    private final WebClient.Builder webClientBuilder;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        String apiKey = exchange.getRequest().getHeaders().getFirst("x-api-key");
        log.warn("Authorization in side authmainfilterfactory  {}",token);
        log.warn("apikey in side authmainfilterfactory  {}",apiKey);


        return webClientBuilder.build()
                .post()
                .uri("http://localhost:9128/user/client/validate")
                .header("Authorization", token)
                .header("x-api-key", apiKey)
                .exchange()
                .flatMap(response -> {
                    if (response.statusCode() == HttpStatus.OK) {
                        System.out.println("It is working$$$$$");
                        Mono<String> responseBodyMono = response.bodyToMono(String.class);
                        return responseBodyMono.flatMap(app -> {
                            System.out.println("this is returned :::" + app);
                            exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.add("id", app));
                            return chain.filter(exchange);
                        });
                    } else {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        log.error("Authentication failed with status code: {}", response.statusCode());
                        return exchange.getResponse().setComplete();
                    }
                });
    }
}
