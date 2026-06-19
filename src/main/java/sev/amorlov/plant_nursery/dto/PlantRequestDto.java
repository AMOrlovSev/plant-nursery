package sev.amorlov.plant_nursery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PlantRequestDto(
        @NotBlank(message = "Название растения не должно быть пустым")
        String name,

        @NotBlank(message = "Тип растения должен быть указан")
        String type,

        @NotNull(message = "Цена должна быть указана")
        @Positive(message = "Цена должна быть больше нуля")
        BigDecimal price,

        @NotNull(message = "Количество должно быть указано")
        @PositiveOrZero(message = "Количество не может быть отрицательным")
        Integer quantity,

        Long supplierId
) {}