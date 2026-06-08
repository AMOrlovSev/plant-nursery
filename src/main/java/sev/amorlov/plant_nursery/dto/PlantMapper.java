package sev.amorlov.plant_nursery.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import sev.amorlov.plant_nursery.model.PlantEntity;

// componentModel = MappingConstants.ComponentModel.SPRING делает маппер Spring-компонентом (@Component)
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlantMapper {

    PlantResponseDto toResponseDto(PlantEntity entity);

    @Mapping(target = "id", ignore = true)
    PlantEntity toEntity(PlantRequestDto dto);
}
