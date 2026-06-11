package sev.amorlov.plant_nursery.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import sev.amorlov.plant_nursery.dto.PlantMapper;
import sev.amorlov.plant_nursery.dto.PlantRequestDto;
import sev.amorlov.plant_nursery.dto.PlantResponseDto;
import sev.amorlov.plant_nursery.model.PlantEntity;
import sev.amorlov.plant_nursery.repository.PlantRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlantServiceTest {

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private PlantMapper plantMapper;

    @InjectMocks
    private PlantService plantService;

    private PlantEntity plantEntity;
    private PlantResponseDto plantResponseDto;

    @BeforeEach
    void setUp() {
        plantEntity = new PlantEntity();
        plantEntity.setId(1L);
        plantEntity.setName("Фикус");
        plantEntity.setType("Комнатные");
        plantEntity.setPrice(BigDecimal.valueOf(1500));
        plantEntity.setQuantity(5);

        plantResponseDto = new PlantResponseDto(1L, "Фикус", "Комнатные", BigDecimal.valueOf(1500), 5);
    }

    @Test
    void getPlantById_ShouldReturnPlant_WhenPlantExists() {
        // Arrange (Настройка окружения и моков)
        when(plantRepository.findById(1L)).thenReturn(Optional.of(plantEntity));
        when(plantMapper.toResponseDto(plantEntity)).thenReturn(plantResponseDto);

        // Act (Выполнение целевого метода)
        PlantResponseDto result = plantService.getPlantById(1L);

        // Assert (Проверка результатов)
        assertNotNull(result);
        assertEquals("Фикус", result.name());
        assertEquals(BigDecimal.valueOf(1500), result.price());

        // Проверяем, что методы моков вызывались ровно по 1 разу
        verify(plantRepository, times(1)).findById(1L);
        verify(plantMapper, times(1)).toResponseDto(plantEntity);
    }

    @Test
    void getPlantById_ShouldThrowException_WhenPlantDoesNotExist() {
        // Arrange
        when(plantRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            plantService.getPlantById(1L);
        });

        assertEquals("Plant not found with id: 1", exception.getMessage());

        // Проверяем, что маппер даже не пытался вызываться, так как упали раньше
        verify(plantMapper, never()).toResponseDto(any());
    }

    @Test
    void getAllPlants_ShouldReturnPageOfPlants() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<PlantEntity> entityPage = new PageImpl<>(List.of(plantEntity));

        when(plantRepository.findAll(pageable)).thenReturn(entityPage);
        when(plantMapper.toResponseDto(plantEntity)).thenReturn(plantResponseDto);

        // Act
        Page<PlantResponseDto> resultPage = plantService.getAllPlants(0, 10, "id", "asc");

        // Assert
        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals("Фикус", resultPage.getContent().get(0).name());
    }
}