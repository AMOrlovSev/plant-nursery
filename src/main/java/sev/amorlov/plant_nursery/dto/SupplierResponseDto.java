package sev.amorlov.plant_nursery.dto;

import java.time.LocalDateTime;

public record SupplierResponseDto(
        Long id,
        String name,
        String contactEmail,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}