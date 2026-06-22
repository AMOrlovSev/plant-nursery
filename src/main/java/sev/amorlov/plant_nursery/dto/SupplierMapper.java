package sev.amorlov.plant_nursery.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import sev.amorlov.plant_nursery.model.SupplierEntity;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SupplierMapper {

    SupplierResponseDto toResponseDto(SupplierEntity supplier);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "plants", ignore = true)
    SupplierEntity toEntity(SupplierRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "plants", ignore = true)
    void updateEntityFromDto(SupplierRequestDto dto, @MappingTarget SupplierEntity supplier);
}