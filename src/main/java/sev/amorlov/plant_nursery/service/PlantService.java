package sev.amorlov.plant_nursery.service;

import org.springframework.stereotype.Service;
import sev.amorlov.plant_nursery.dto.PlantMapper;
import sev.amorlov.plant_nursery.dto.PlantRequestDto;
import sev.amorlov.plant_nursery.dto.PlantResponseDto;
import sev.amorlov.plant_nursery.model.PlantEntity;
import sev.amorlov.plant_nursery.repository.PlantRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PlantService {
    private final PlantRepository plantRepository;

    public PlantService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    public List<PlantResponseDto> getAllPlants() {
        return plantRepository.findAll().stream()
                .map(PlantMapper::toResponseDto)
                .toList();
    }

    public PlantResponseDto getPlantById(Long id) {
        return plantRepository.findById(id)
                .map(PlantMapper::toResponseDto)
                .orElseThrow(() -> new IllegalArgumentException("Plant not found with id: " + id));
    }

    public PlantResponseDto savePlant(PlantRequestDto dto) {
        PlantEntity entity = PlantMapper.toEntity(dto);
        PlantEntity savedEntity = plantRepository.save(entity);
        return PlantMapper.toResponseDto(savedEntity);
    }

    public PlantResponseDto updatePlant(Long id, PlantRequestDto dto) {
        return plantRepository.findById(id)
                .map(existingPlant -> {
                    existingPlant.setName(dto.name());
                    existingPlant.setType(dto.type());
                    existingPlant.setPrice(dto.price());
                    existingPlant.setQuantity(dto.quantity());

                    PlantEntity updatedEntity = plantRepository.save(existingPlant);
                    return PlantMapper.toResponseDto(updatedEntity);
                })
                .orElseThrow(() -> new IllegalArgumentException("Cannot update. Plant not found with id: " + id));
    }

    public void deletePlantById(Long id) {
        if (!plantRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete. Plant not found with id: " + id);
        }
        plantRepository.deleteById(id);
    }

}
