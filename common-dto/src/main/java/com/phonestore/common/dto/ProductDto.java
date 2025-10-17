package com.phonestore.common.dto;


import jakarta.validation.constraints.*;
import java.math.BigDecimal;


public record ProductDto(
        Long id,
        @NotBlank String sku,
        @NotBlank String name,
        String brand,
        @Positive BigDecimal price,
        String currency,
        String description
) {}