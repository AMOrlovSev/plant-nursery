package sev.amorlov.plant_nursery.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequestDto(
        @NotNull(message = "Plant ID cannot be null")
        Long plantId,

        @NotNull(message = "Quantity cannot be null")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {}