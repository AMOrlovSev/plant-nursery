package sev.amorlov.plant_nursery.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import sev.amorlov.plant_nursery.model.PlantEntity;

import java.math.BigDecimal;

public class PlantSpecifications {
    public static Specification<PlantEntity> hasType(String type) {
        return (root, query, cb) -> type == null || type.isBlank()
                ? cb.conjunction()
                : cb.equal(root.get("type"), type);
    }

    public static Specification<PlantEntity> hasPriceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null
                ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<PlantEntity> hasPriceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null
                ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<PlantEntity> isAvailable(Boolean onlyAvailable) {
        return (root, query, cb) -> (onlyAvailable == null || !onlyAvailable)
                ? cb.conjunction()
                : cb.greaterThan(root.get("quantity"), 0);
    }
}
