package com.grewal.apigateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@Slf4j
@Component
@RefreshScope
@RequiredArgsConstructor
public class AuthGatewayFilterFactory implements GatewayFilter {

    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        String apiKey = exchange.getRequest().getHeaders().getFirst("x-api-key");


        log.info(apiKey);
        log.info(token);
        log.info("AuthGatewayFilterFactory Filter trigger");

        log.info("Inside AuthGateWayFilterFactory");

       return webClientBuilder.build()
                .post()
                .uri("http://localhost:9128/user/secondary/validate")
                .header("Authorization", token)
                .header("x-api-key", apiKey)
                .exchange()
                .flatMap(response -> {
                    if (response.statusCode() == HttpStatus.OK) {
                        System.out.println("It is working$$$$$");
                        Mono<String> responseBodyMono = response.bodyToMono(String.class);
                        return responseBodyMono.flatMap(app -> {
                            System.out.println("this is returned :::" + app);
                            System.out.println("inside the authGateway filter factory");
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

