package org.upstarters.gatewayserver.filters.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class CacheControlHeaderFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(CacheControlHeaderFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        if (method == HttpMethod.GET && path.contains("/enrollments")) {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                HttpHeaders headers = exchange.getResponse().getHeaders();
                if (!headers.containsKey(HttpHeaders.CACHE_CONTROL)) {
                    logger.debug("Adding Cache-Control header for path: {}", path);
                    headers.add(HttpHeaders.CACHE_CONTROL, "public, max-age=3600");
                }
            }));
        }

        return chain.filter(exchange);
    }
}