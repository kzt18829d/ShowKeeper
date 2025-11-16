package com.klabs.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class GatewayConfiguration {

    @Value("${app.routes.account-service.uri}")
    private String accountServiceUri;

    @Value("${app.routes.end-error-service.uri}")
    private String errorServiceUri;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("route_v1_account_service", r -> r
                        .path("/api/v1/auth/**", "/api/v1/account/**", "/api/v1/verify")
                        .uri(accountServiceUri))

                .build();
    }
}
