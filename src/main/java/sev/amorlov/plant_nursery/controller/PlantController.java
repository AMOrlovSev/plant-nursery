package sev.amorlov.plant_nursery.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
        return plantService.getPlantById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlantEntity createPlant(@RequestBody PlantEntity plant) {
        return plantService.savePlant(plant);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlant(@PathVariable Long id) {
        plantService.deletePlantById(id);
    }

    @PutMapping("/{id}")
    public PlantEntity updatePlant(@PathVariable Long id, @RequestBody PlantEntity plant) {
        return plantService.updatePlant(id, plant);
    }
}
