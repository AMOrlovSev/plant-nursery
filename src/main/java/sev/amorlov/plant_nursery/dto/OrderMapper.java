package sev.amorlov.plant_nursery.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sev.amorlov.plant_nursery.model.OrderEntity;
import sev.amorlov.plant_nursery.model.OrderItemEntity;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponseDto toResponseDto(OrderEntity entity);

    @Mapping(target = "plantId", source = "plant.id")
    @Mapping(target = "plantName", source = "plant.name")
    OrderItemResponseDto toItemResponseDto(OrderItemEntity entity);
}