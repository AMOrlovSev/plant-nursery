package sev.amorlov.plant_nursery.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(
        Long id,
        String customerEmail,
        String status,
        BigDecimal totalPrice,
        List<OrderItemResponseDto> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}