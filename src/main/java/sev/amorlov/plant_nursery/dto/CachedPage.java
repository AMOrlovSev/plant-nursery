package sev.amorlov.plant_nursery.dto;

import java.util.List;

public record CachedPage<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {}