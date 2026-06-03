package sev.amorlov.plant_nursery.model;

import java.math.BigDecimal;

public record Plant(Long id, String name, String type, BigDecimal price, Integer quantity) {
}
