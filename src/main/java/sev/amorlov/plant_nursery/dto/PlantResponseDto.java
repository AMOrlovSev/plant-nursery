package sev.amorlov.plant_nursery.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PlantResponseDto(
        Long id,
        String name,
        String type,
        BigDecimal price,
        Integer quantity,
        Long supplierId,
        String supplierName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}