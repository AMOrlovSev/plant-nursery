package sev.amorlov.plant_nursery.service;

import lombok.extern.slf4j.Slf4j;
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
import sev.amorlov.plant_nursery.model.SupplierEntity;
import sev.amorlov.plant_nursery.repository.PlantRepository;
import sev.amorlov.plant_nursery.repository.SupplierRepository;
import sev.amorlov.plant_nursery.repository.specification.PlantSpecifications;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class PlantService {
    private final PlantRepository plantRepository;
    private final PlantMapper plantMapper;
    private final SupplierRepository supplierRepository;

    public PlantService(PlantRepository plantRepository, PlantMapper plantMapper, SupplierRepository supplierRepository) {
        this.plantRepository = plantRepository;
        this.plantMapper = plantMapper;
        this.supplierRepository = supplierRepository;
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
                .orElseThrow(() -> {
                    log.warn("Plant not found with id: {}", id);
                    return new IllegalArgumentException("Plant not found with id: " + id);
                });
    }

    public PlantResponseDto savePlant(PlantRequestDto dto) {
        log.info("Request to save a new plant: '{}', type: '{}'", dto.name(), dto.type());
        PlantEntity entity = plantMapper.toEntity(dto);

        if (dto.supplierId() != null) {
            SupplierEntity supplier = supplierRepository.findById(dto.supplierId())
                    .orElseThrow(() -> {
                        log.warn("Failed to save plant: Supplier with id {} not found", dto.supplierId());
                        return new IllegalArgumentException("Supplier not found with id: " + dto.supplierId());
                    });
            entity.setSupplier(supplier);
        }

        PlantEntity savedEntity = plantRepository.save(entity);
        log.info("Plant successfully saved with id: {}", savedEntity.getId());
        return plantMapper.toResponseDto(savedEntity);
    }

    public PlantResponseDto updatePlant(Long id, PlantRequestDto dto) {
        log.info("Request to update plant with id: {}", id);
        return plantRepository.findById(id)
                .map(existingPlant -> {
                    existingPlant.setName(dto.name());
                    existingPlant.setType(dto.type());
                    existingPlant.setPrice(dto.price());
                    existingPlant.setQuantity(dto.quantity());

                    PlantEntity updatedEntity = plantRepository.save(existingPlant);
                    log.info("Plant with id: {} successfully updated", id);
                    return plantMapper.toResponseDto(updatedEntity);
                })
                .orElseThrow(() -> {
                    log.warn("Cannot update. Plant not found with id: {}", id);
                    return new IllegalArgumentException("Cannot update. Plant not found with id: " + id);
                });
    }

    @Transactional
    public void deletePlantById(Long id) {
        log.info("Request to delete plant with id: {}", id);
        if (!plantRepository.existsById(id)) {
            log.warn("Cannot delete. Plant not found with id: {}", id);
            throw new IllegalArgumentException("Cannot delete. Plant not found with id: " + id);
        }
        plantRepository.deleteById(id);
        log.info("Plant with id: {} successfully deleted", id);
    }

    @Transactional
    public PlantResponseDto sellPlant(Long id, Integer quantityToSell) {
        log.info("Request to sell {} pcs of plant with id: {}", quantityToSell, id);
        PlantEntity plant = plantRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Plant not found with id: {}", id);
                    return new IllegalArgumentException("Plant not found with id: " + id);
                });

        if (plant.getQuantity() < quantityToSell) {
            log.error("Conflict on selling plant id {}: Requested {}, but only {} available",
                    id, quantityToSell, plant.getQuantity());
            throw new InsufficientStockException(
                    String.format("Недостаточно товара на складе. Запрошено: %d, в наличии: %d",
                            quantityToSell, plant.getQuantity())
            );
        }

        plant.setQuantity(plant.getQuantity() - quantityToSell);

        PlantEntity updatedPlant = plantRepository.save(plant);

        log.info("Successfully sold {} pcs of plant id {}. Remaining quantity: {}",
                quantityToSell, id, updatedPlant.getQuantity());
        return plantMapper.toResponseDto(updatedPlant);
    }

}
