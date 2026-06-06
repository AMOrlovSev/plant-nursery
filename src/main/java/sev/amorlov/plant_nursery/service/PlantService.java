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
}
