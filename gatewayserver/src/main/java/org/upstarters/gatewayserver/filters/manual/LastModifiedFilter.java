package org.upstarters.gatewayserver.filters.manual;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@Component
public class LastModifiedFilter implements GlobalFilter{
    private static final Logger logger = LoggerFactory.getLogger(ResponseTraceFilter.class);

    @Autowired
    private FilterUtility filterUtility;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        HttpMethod method = exchange.getRequest().getMethod();

        if (path.contains("/students") && method.equals(HttpMethod.GET)) {
            logger.info("LastModifiedFilter executed before downstream service");

            return chain.filter(exchange).then(
                    Mono.fromRunnable(() -> {
                        try {
                            String lastModified = ZonedDateTime.now()
                                    .format(DateTimeFormatter.RFC_1123_DATE_TIME);

                            exchange.getResponse().getHeaders()
                                    .set(HttpHeaders.LAST_MODIFIED, lastModified);

                            logger.info("Last-Modified header added: {}", lastModified);
                        } catch (Exception e) {
                            logger.error("Error setting Last-Modified header", e);
                        }
                    })
            );
        }

        return chain.filter(exchange);
    }
}