package org.upstarters.gatewayserver.filters.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(3)
public class EnrollmentsRoleAccessFilter implements GlobalFilter {
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentsRoleAccessFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (path.contains("/enrollments")) {
            String userRole = exchange.getRequest().getHeaders().getFirst("X-User-Role");
            HttpMethod method = exchange.getRequest().getMethod();

            if (userRole == null || userRole.isEmpty()) {
                logger.warn("Access denied: Missing X-User-Role header for path: {}", path);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            if (userRole.equalsIgnoreCase("STUDENT")) {
                if (method != HttpMethod.GET && method != HttpMethod.POST && method != HttpMethod.PUT) {
                    logger.warn("Access denied for STUDENT: Method {} not allowed for path: {}", method, path);
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
            } else if (!userRole.equalsIgnoreCase("ADMIN")) {
                logger.warn("Access denied: Invalid role '{}' for enrollment service", userRole);
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            logger.info("Role-based access granted for {}. Path: {}, Method: {}", userRole, path, method);
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("X-Verified-Role", userRole)
                            .build())
                    .build();
            return chain.filter(modifiedExchange);
        }

        return chain.filter(exchange);
    }
}
