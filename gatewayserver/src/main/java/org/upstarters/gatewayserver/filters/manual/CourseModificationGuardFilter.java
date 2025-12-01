package org.upstarters.gatewayserver.filters.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Order(2)
public class CourseModificationGuardFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(CourseModificationGuardFilter.class);

    @Value("${gateway.security.admin-key}")
    private String adminKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        if (path.contains("/courses")) {

            if (method == HttpMethod.POST ||
                    method == HttpMethod.PUT ||
                    method == HttpMethod.PATCH ||
                    method == HttpMethod.DELETE) {

                List<String> authHeaders = exchange.getRequest().getHeaders().get("X-Admin-Key");

                if (authHeaders == null || authHeaders.isEmpty() || !authHeaders.get(0).equals(adminKey)) {

                    logger.warn("Security Alert: Unauthorized attempt to modify courses. Path: {}", path);

                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                logger.info("Authorized access to course modification. Key verified.");
            }
        }

        return chain.filter(exchange);
    }
}