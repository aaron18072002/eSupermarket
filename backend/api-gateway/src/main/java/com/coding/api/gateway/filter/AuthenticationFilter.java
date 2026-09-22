package com.coding.api.gateway.filter;

import com.coding.api.gateway.util.JwtUtil;
import com.coding.api.gateway.validator.RouterValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private final RouterValidator routerValidator;
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // If route is public, bypass authentication filter
        if (!routerValidator.isSecured.test(request)) {
            return chain.filter(exchange);
        }

        // Verify Authorization header presence
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Invalid Authorization Header format. Expected 'Bearer <token>'", HttpStatus.UNAUTHORIZED);
        }

        // Extract and parse JWT token
        String token = authHeader.substring(7);
        try {
            Claims claims = jwtUtil.extractAllClaims(token);
            String userId = claims.getSubject();
            String roles = jwtUtil.extractRoles(token);

            // 4. Inject clean user context headers downstream to internal services
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId != null ? userId : "")
                    .header("X-User-Roles", roles != null ? roles : "")
                    .build();

            log.debug("Authenticated request for user: {} with roles: {} on path: {}", userId, roles, request.getURI().getPath());

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (ExpiredJwtException e) {
            log.warn("JWT Token expired for request to {}: {}", request.getURI().getPath(), e.getMessage());
            return onError(exchange, "JWT Token has expired", HttpStatus.UNAUTHORIZED);
        } catch (SignatureException | SecurityException e) {
            log.warn("Invalid JWT Signature for request to {}: {}", request.getURI().getPath(), e.getMessage());
            return onError(exchange, "Invalid JWT token signature", HttpStatus.UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token for request to {}: {}", request.getURI().getPath(), e.getMessage());
            return onError(exchange, "Malformed JWT token", HttpStatus.UNAUTHORIZED);
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token for request to {}: {}", request.getURI().getPath(), e.getMessage());
            return onError(exchange, "Unsupported JWT token", HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty for request to {}: {}", request.getURI().getPath(), e.getMessage());
            return onError(exchange, "JWT claims string is empty", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("Authentication error for request to {}: {}", request.getURI().getPath(), e.getMessage(), e);
            return onError(exchange, "Authentication failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Constructs a reactive JSON error response without throwing unhandled exceptions.
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String path = exchange.getRequest().getURI().getPath();
        String jsonError = String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                Instant.now().toString(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                message,
                path
        );

        DataBuffer buffer = response.bufferFactory().wrap(jsonError.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        // High priority: Run before general routing
        return -1;
    }
}
