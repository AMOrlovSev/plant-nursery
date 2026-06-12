package sev.amorlov.plant_nursery.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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
    public Page<PlantResponseDto> getPlants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return plantService.getAllPlants(page, size, sortBy, direction);
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

    @PostMapping("/{id}/sell")
    public PlantResponseDto sellPlant(
            @PathVariable Long id,
            @RequestParam Integer quantity
    ) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество продаваемого товара должно быть больше нуля");
        }
        return plantService.sellPlant(id, quantity);
    }

}
