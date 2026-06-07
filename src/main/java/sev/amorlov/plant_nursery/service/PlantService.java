package sev.amorlov.plant_nursery.service;

import org.springframework.stereotype.Service;
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

    public List<PlantEntity> getAllPlants() {
        return plantRepository.findAll();
    }

    public Optional<PlantEntity> getPlantById(Long id) {
        return plantRepository.findById(id);
    }

    public PlantEntity savePlant(PlantEntity plant) {
        return plantRepository.save(plant);
    }

    public void deletePlantById(Long id) {
        if (!plantRepository.existsById(id)) {
            throw new IllegalArgumentException("Cannot delete. Plant not found with id: " + id);
        }
        plantRepository.deleteById(id);
    }

    public PlantEntity updatePlant(Long id, PlantEntity updatedPlant) {
        return plantRepository.findById(id)
                .map(existingPlant -> {
                    existingPlant.setName(updatedPlant.getName());
                    existingPlant.setType(updatedPlant.getType());
                    existingPlant.setPrice(updatedPlant.getPrice());
                    existingPlant.setQuantity(updatedPlant.getQuantity());

                    return plantRepository.save(existingPlant);
                })
                .orElseThrow(() -> new IllegalArgumentException("Cannot update. Plant not found with id: " + id));
    }
}
