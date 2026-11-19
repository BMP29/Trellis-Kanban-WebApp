package com.example.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.factory.TokenRelayGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfiguration {
    private final String secureResourceUrl;
    private final String authServerInternalUrl;
    private final String feAppBaseUrl;
    private final TokenRelayGatewayFilterFactory tokenRelay;


    public RouteConfiguration(TokenRelayGatewayFilterFactory tokenRelay,
                              @Value("${oauth2.secure-resource-url}") String secureResourceUrl,
                              @Value("${oauth2.auth-server-internal-url}") String authServerInternalUrl,
                              @Value("${fe-app.base-uri}") String feAppBaseUrl) {
        this.tokenRelay = tokenRelay;
        this.secureResourceUrl = secureResourceUrl;
        this.authServerInternalUrl = authServerInternalUrl;
        this.feAppBaseUrl = feAppBaseUrl;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("root", r -> r
                        .path("/")
                        .filters(f -> f.redirect(302, feAppBaseUrl))
                        .uri(feAppBaseUrl))
                .route("auth-public", r -> r
                        .path("/api/register", "/api/verify")
                        .filters(f -> f.removeRequestHeader("Cookie"))
                        .uri(authServerInternalUrl))
                .route("auth-protected", r -> r
                        .path("/auth/password/**", "/auth/username/**", "/auth/email/**", "/auth/password", "/auth/username", "/auth/email")
                        .filters(f -> f
                                .filters(tokenRelay.apply())
                                .removeRequestHeader("Cookie"))
                        .uri(authServerInternalUrl))
                .build();
    }

}
