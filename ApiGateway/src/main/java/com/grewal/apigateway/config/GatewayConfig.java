package com.grewal.apigateway.config;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RefreshScope
@RequiredArgsConstructor
public class GatewayConfig {
   private final AuthPrimaryFilterFactory authPrimaryFilter;
    private final AuthGatewayFilterFactory authGatewayFilterFactory;

    private final AuthMainGatewayFilterFactory authMainGatewayFilterFactory;


    @Value("${product.service.uri}")
    String product_service;
    @Value("${order.service.uri}")
    String order_service;
    @Value("${inventory.service.uri}")
    String inventory_service;

    @Value("${client.service.uri}")
    String client_service;

    @Bean
    public RouteLocator apiRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("discovery-server", p -> p
                        .path("/eureka/web")
                        .filters(f->f.setPath("/"))
                        .uri("http://localhost:8761")

                )
                .route("discovery-server-static", p -> p
                        .path("/eureka/**")
                        .uri("http://localhost:8761")

                )
                .route("ClientAuthSystemPrimary", p -> p
                        .path("/primary/**")
                        .filters(f->
                                f.filter(authPrimaryFilter)
                        )
                        .uri(client_service)

                )
                .route("ClientAuthSystem", p -> p
                        .path("/secondary/**","/user/**")

                        .uri(client_service)

                )

                .route("product-service-sec", p -> p
                        .path("/api/product/**")

                        .filters(f -> f
                                        .filter(authGatewayFilterFactory)

                        )
                        .uri(product_service)
                )
                .route("product-service", p -> p
                        .path("/api/user/product/**")
                        .filters(f -> f
                                .filter(authMainGatewayFilterFactory)

                        )
                        .uri(product_service)
                )
                .route("order-service", p -> p
                        .path("/api/user/order/**")

                        .filters(f -> f
                                .filter(authMainGatewayFilterFactory)
                        )
                        .uri(order_service)
                )
                .route("order-service-sec", p -> p
                        .path("/api/order/**")

                        .filters(f -> f
                                .filter(authGatewayFilterFactory)
                        )
                        .uri(order_service)
                )
                .route("inventory-service", p -> p
                        .path("/api/user/inventory/**")
                        .filters(f -> f
                                .filter(authMainGatewayFilterFactory)

                        )
                        .uri(inventory_service)
                )
                .route("inventory-service-sec", p -> p
                        .path("/api/inventory/**")
                        .filters(f -> f
                                .filter(authGatewayFilterFactory)

                        )
                        .uri(inventory_service)
                )
                .build();

    }
    }


