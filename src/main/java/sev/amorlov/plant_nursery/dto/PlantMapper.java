package sev.amorlov.plant_nursery.dto;

import sev.amorlov.plant_nursery.model.PlantEntity;

public class PlantMapper {

    public static PlantResponseDto toResponseDto(PlantEntity entity) {
        return new PlantResponseDto(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getPrice(),
                entity.getQuantity()
        );
    }

    public static PlantEntity toEntity(PlantRequestDto dto) {
        PlantEntity entity = new PlantEntity();
        entity.setName(dto.name());
        entity.setType(dto.type());
        entity.setPrice(dto.price());
        entity.setQuantity(dto.quantity());
        return entity;
    }
}
