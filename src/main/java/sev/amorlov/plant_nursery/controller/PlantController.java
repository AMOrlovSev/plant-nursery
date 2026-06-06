package sev.amorlov.plant_nursery.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sev.amorlov.plant_nursery.model.PlantEntity;
import sev.amorlov.plant_nursery.service.PlantService;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @GetMapping
    public List<PlantEntity> getPlants() {
        return plantService.getAllPlants();
    }

    @GetMapping("/{id}")
    public PlantEntity getPlantById(@PathVariable Long id) {
        return plantService.getPlantById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plant not found with id: " + id));
    }
}
