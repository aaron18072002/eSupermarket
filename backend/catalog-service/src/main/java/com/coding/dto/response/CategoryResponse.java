package com.coding.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        UUID parentId,
        String parentName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
