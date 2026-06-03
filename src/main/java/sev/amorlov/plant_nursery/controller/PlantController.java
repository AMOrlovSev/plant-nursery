package sev.amorlov.plant_nursery.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sev.amorlov.plant_nursery.model.Plant;
import sev.amorlov.plant_nursery.service.PlantService;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @GetMapping()
    public List<Plant> getPlants() {
        var plants = plantService.getAllPlants();
        return plants;
    }
}
