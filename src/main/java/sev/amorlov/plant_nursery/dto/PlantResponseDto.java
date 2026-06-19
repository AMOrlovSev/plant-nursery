package sev.amorlov.plant_nursery.dto;

import java.math.BigDecimal;

public record PlantResponseDto(
        Long id,
        String name,
        String type,
        BigDecimal price,
        Integer quantity,
        Long supplierId
) {}