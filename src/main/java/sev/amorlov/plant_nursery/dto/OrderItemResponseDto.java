package sev.amorlov.plant_nursery.dto;

import java.math.BigDecimal;

public record OrderItemResponseDto(
        Long id,
        Long plantId,
        String plantName,
        Integer quantity,
        BigDecimal priceAtPurchase
) {}