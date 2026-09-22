package com.coding.api.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final String START_TIME_ATTR = "startTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        exchange.getAttributes().put(START_TIME_ATTR, System.currentTimeMillis());

        log.info("[GATEWAY-IN]  --> {} {}", request.getMethod(), request.getURI().getPath());

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(START_TIME_ATTR);
            long executeTime = (startTime != null) ? (System.currentTimeMillis() - startTime) : 0;
            ServerHttpResponse response = exchange.getResponse();

            log.info("[GATEWAY-OUT] <-- {} {} [Status: {}] ({}ms)",
                    request.getMethod(),
                    request.getURI().getPath(),
                    response.getStatusCode(),
                    executeTime);
        }));
    }

    @Override
    public int getOrder() {
        // High priority so this filter wraps the entire execution lifecycle
        return Ordered.LOWEST_PRECEDENCE;
    }
}
