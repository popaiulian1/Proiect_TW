package org.upstarters.gatewayserver.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator universityRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/Proiect_TW/courses/**")
                        .filters(f -> f
                                .addRequestHeader("X-Service", "courses-service")
                                .addResponseHeader("X-Service", "courses-service")
                                .rewritePath("/Proiect_TW/courses/(?<segment>.*)", "/${segment}")
                        )
                        .uri("lb://courses"))
                .route(p -> p
                        .path("/Proiect_TW/students/**")
                        .filters(f -> f
                                .addRequestHeader("X-Service", "students-service")
                                .addResponseHeader("X-Service", "students-service")
                                .rewritePath("/Proiect_TW/students/(?<segment>.*)", "/${segment}")
                        )
                        .uri("lb://students"))
                .route(p -> p
                        .path("/Proiect_TW/enrollments/**")
                        .filters(f -> f
                                .addRequestHeader("X-Service", "enrollments-service")
                                .addResponseHeader("X-Service", "enrollments-service")
                                .rewritePath("/Proiect_TW/enrollments/(?<segment>.*)", "/${segment}")
                        )
                        .uri("lb://enrollments"))
                .build();
    }
}
