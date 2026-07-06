package sev.amorlov.plant_nursery.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderRequestDto(
        @NotBlank(message = "Customer email cannot be blank")
        @Email(message = "Invalid email format")
        String customerEmail,

        @NotEmpty(message = "Order must contain at least one item")
        List<OrderItemRequestDto> items
) {}