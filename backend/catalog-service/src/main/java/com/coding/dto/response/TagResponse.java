package com.coding.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TagResponse(
        UUID id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
