package sev.amorlov.plant_nursery.service;

import org.springframework.stereotype.Service;
import sev.amorlov.plant_nursery.model.Plant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlantService {
    private final List<Plant> plants = new ArrayList<>();

    public PlantService() {
        plants.add(new Plant(1L, "Голубая Ель", "Хвойные", new BigDecimal("5500.00"), 10));
        plants.add(new Plant(2L, "Дуб Красный", "Лиственные", new BigDecimal("4200.00"), 5));
    }

    public List<Plant> getAllPlants() {
        return plants;
    }

    public Optional<Plant> getPlantById(Long id) {
        return plants.stream()
                .filter(plant -> plant.id().equals(id))
                .findFirst();
    }
}
