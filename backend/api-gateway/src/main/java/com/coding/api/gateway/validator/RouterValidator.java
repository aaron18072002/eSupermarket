package com.coding.api.gateway.validator;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouterValidator {

    // Endpoints that are unconditionally public for all HTTP methods
    private static final List<String> OPEN_API_ENDPOINTS = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh-token",
            "/actuator"
    );

    // Endpoints that are public ONLY for HTTP GET (storefront catalog browsing)
    private static final List<String> PUBLIC_GET_PREFIXES = List.of(
            "/api/v1/products",
            "/api/v1/categories",
            "/api/v1/brands",
            "/api/v1/suppliers",
            "/api/v1/tags",
            "/api/v1/product-groups"
    );

    public Predicate<ServerHttpRequest> isSecured = request -> {
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        // Check unconditionally open endpoints
        if (OPEN_API_ENDPOINTS.stream().anyMatch(path::startsWith)) {
            return false;
        }

        // Allow public GET for catalog browsing
        if (HttpMethod.GET.equals(method) && PUBLIC_GET_PREFIXES.stream().anyMatch(path::startsWith)) {
            return false;
        }

        // All other requests require authentication
        return true;
    };
}
