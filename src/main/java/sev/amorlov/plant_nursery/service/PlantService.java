package sev.amorlov.plant_nursery.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sev.amorlov.plant_nursery.dto.PlantMapper;
import sev.amorlov.plant_nursery.dto.PlantRequestDto;
import sev.amorlov.plant_nursery.dto.PlantResponseDto;
import sev.amorlov.plant_nursery.exception.InsufficientStockException;
import sev.amorlov.plant_nursery.model.PlantEntity;
import sev.amorlov.plant_nursery.repository.PlantRepository;
import sev.amorlov.plant_nursery.repository.specification.PlantSpecifications;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PlantService {
    private final PlantRepository plantRepository;
    private final PlantMapper plantMapper;

    public PlantService(PlantRepository plantRepository, PlantMapper plantMapper) {
        this.plantRepository = plantRepository;
        this.plantMapper = plantMapper;
    }

    public Page<PlantResponseDto> getAllPlants(
            String type, BigDecimal minPrice, BigDecimal maxPrice, Boolean onlyAvailable,
            int page, int size, String sortBy, String direction
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.DESC.name())
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<PlantEntity> spec = Specification.allOf(
                PlantSpecifications.hasType(type),
                PlantSpecifications.hasPriceGreaterThanOrEqual(minPrice),
                PlantSpecifications.hasPriceLessThanOrEqual(maxPrice),
                PlantSpecifications.isAvailable(onlyAvailable)
        );

        return plantRepository.findAll(spec, pageable)
                .map(plantMapper::toResponseDto);
    }

    public PlantResponseDto getPlantById(Long id) {
        return plantRepository.findById(id)
                .map(plantMapper::toResponseDto)
                .orElseThrow(() -> new IllegalArgumentException("Plant not found with id: " + id));
    }

    public PlantResponseDto savePlant(PlantRequestDto dto) {
        PlantEntity entity = plantMapper.toEntity(dto);
        PlantEntity savedEntity = plantRepository.save(entity);
        return plantMapper.toResponseDto(savedEntity);
    }

    public PlantResponseDto updatePlant(Long id, PlantRequestDto dto) {
        return plantRepository.findById(id)
                .map(existingPlant -> {
                    existingPlant.setName(dto.name());
                    existingPlant.setType(dto.type());
                    existingPlant.setPrice(dto.price());
                    existingPlant.setQuantity(dto.quantity());

                    PlantEntity updatedEntity = plantRepository.save(existingPlant);
                    return plantMapper.toResponseDto(updatedEntity);
                })
                .orElseThrow(() -> new IllegalArgumentException("Cannot update. Plant not found with id: " + id));
    }

    public void deletePlantById(Long id) {
        if (!plantRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete. Plant not found with id: " + id);
        }
        plantRepository.deleteById(id);
    }

    @Transactional
    public PlantResponseDto sellPlant(Long id, Integer quantityToSell) {
        PlantEntity plant = plantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plant not found with id: " + id));

        if (plant.getQuantity() < quantityToSell) {
            throw new InsufficientStockException(
                    String.format("Недостаточно товара на складе. Запрошено: %d, в наличии: %d",
                            quantityToSell, plant.getQuantity())
            );
        }

        plant.setQuantity(plant.getQuantity() - quantityToSell);

        PlantEntity updatedPlant = plantRepository.save(plant);
        return plantMapper.toResponseDto(updatedPlant);
    }

}
