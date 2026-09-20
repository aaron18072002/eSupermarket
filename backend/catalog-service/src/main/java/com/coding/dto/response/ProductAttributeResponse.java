package com.coding.dto.response;

import java.util.UUID;

public record ProductAttributeResponse(
        UUID id,
        String attrKey,
        String attrValue
) {}
