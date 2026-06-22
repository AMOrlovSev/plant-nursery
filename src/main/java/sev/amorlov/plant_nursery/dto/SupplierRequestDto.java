package sev.amorlov.plant_nursery.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SupplierRequestDto(
        @NotBlank(message = "Название поставщика не должно быть пустым")
        String name,

        @Email(message = "Некорректный формат email")
        String contactEmail,

        String phoneNumber
) {}