package sev.amorlov.plant_nursery.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sev.amorlov.plant_nursery.dto.PlantRequestDto;
import sev.amorlov.plant_nursery.dto.PlantResponseDto;
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
    public List<PlantResponseDto> getPlants() {
        return plantService.getAllPlants();
    }

    @GetMapping("/{id}")
    public PlantResponseDto getPlantById(@PathVariable Long id) {
        return plantService.getPlantById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlantResponseDto createPlant(@Valid @RequestBody PlantRequestDto plant) {
        return plantService.savePlant(plant);
    }

    @PutMapping("/{id}")
    public PlantResponseDto updatePlant(@PathVariable Long id, @Valid @RequestBody PlantRequestDto plant) {
        return plantService.updatePlant(id, plant);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlant(@PathVariable Long id) {
        plantService.deletePlantById(id);
    }

}
