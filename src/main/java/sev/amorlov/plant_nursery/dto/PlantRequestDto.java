package sev.amorlov.plant_nursery.dto;

import java.math.BigDecimal;

public record PlantRequestDto(
        String name,
        String type,
        BigDecimal price,
        Integer quantity
) {}