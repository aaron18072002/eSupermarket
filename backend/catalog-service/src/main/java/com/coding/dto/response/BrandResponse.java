package com.coding.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record BrandResponse(
        UUID id,
        String name,
        String country,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
