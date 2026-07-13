package sev.amorlov.plant_nursery.dto;

public record AuthResponseDto(
        String token,
        String email,
        String role
) {}