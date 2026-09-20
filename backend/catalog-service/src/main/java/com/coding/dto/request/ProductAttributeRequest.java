package com.coding.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ProductAttributeRequest(

        @NotBlank(message = "Attribute key is required")
        String attrKey,

        @NotBlank(message = "Attribute value is required")
        String attrValue

) {}
