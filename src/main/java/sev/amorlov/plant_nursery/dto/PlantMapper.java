package sev.amorlov.plant_nursery.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import sev.amorlov.plant_nursery.model.PlantEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlantMapper {

    @Mapping(source = "supplier.id", target = "supplierId")
    @Mapping(source = "supplier.name", target = "supplierName")
    PlantResponseDto toResponseDto(PlantEntity entity);

    @Mapping(target = "supplier", ignore = true)
    PlantEntity toEntity(PlantRequestDto dto);
}
