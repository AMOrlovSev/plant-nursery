package sev.amorlov.plant_nursery.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import sev.amorlov.plant_nursery.dto.PlantMapper;
import sev.amorlov.plant_nursery.dto.PlantRequestDto;
import sev.amorlov.plant_nursery.dto.PlantResponseDto;
import sev.amorlov.plant_nursery.exception.InsufficientStockException;
import sev.amorlov.plant_nursery.model.PlantEntity;
import sev.amorlov.plant_nursery.model.SupplierEntity;
import sev.amorlov.plant_nursery.repository.PlantRepository;
import sev.amorlov.plant_nursery.repository.SupplierRepository;

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

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private PlantService plantService;

    private PlantEntity plantEntity;
    private SupplierEntity supplierEntity;
    private PlantRequestDto plantRequestDto;
    private PlantResponseDto plantResponseDto;

    @BeforeEach
    void setUp() {
        supplierEntity = new SupplierEntity();
        supplierEntity.setId(1L);
        supplierEntity.setName("ЭкоСад Питомник");

        plantEntity = new PlantEntity();
        plantEntity.setId(1L);
        plantEntity.setName("Фикус");
        plantEntity.setType("Комнатные");
        plantEntity.setPrice(BigDecimal.valueOf(1500));
        plantEntity.setQuantity(5);
        plantEntity.setSupplier(supplierEntity);

        plantRequestDto = new PlantRequestDto("Фикус", "Комнатные", BigDecimal.valueOf(1500), 5, 1L);
        plantResponseDto = new PlantResponseDto(1L, "Фикус", "Комнатные", BigDecimal.valueOf(1500), 5, 1L);
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
    void getAllPlants_ShouldReturnFilteredPageOfPlants_WhenFiltersAreApplied() {
        // Arrange
        String type = "Комнатные";
        BigDecimal minPrice = BigDecimal.valueOf(1000);
        BigDecimal maxPrice = BigDecimal.valueOf(2000);
        Boolean onlyAvailable = true;

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<PlantEntity> entityPage = new PageImpl<>(List.of(plantEntity));

        when(plantRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(entityPage);
        when(plantMapper.toResponseDto(plantEntity)).thenReturn(plantResponseDto);

        // Act
        Page<PlantResponseDto> resultPage = plantService.getAllPlants(
                type, minPrice, maxPrice, onlyAvailable, 0, 10, "id", "asc"
        );

        // Assert
        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals("Фикус", resultPage.getContent().get(0).name());

        verify(plantRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void sellPlant_ShouldDecreaseQuantity_WhenStockIsSufficient() {
        // Arrange
        when(plantRepository.findById(1L)).thenReturn(Optional.of(plantEntity));

        PlantResponseDto updatedResponseDto = new PlantResponseDto(1L, "Фикус", "Комнатные", BigDecimal.valueOf(1500), 3, 1L);
        when(plantRepository.save(any(PlantEntity.class))).thenReturn(plantEntity);
        when(plantMapper.toResponseDto(any(PlantEntity.class))).thenReturn(updatedResponseDto);

        // Act
        PlantResponseDto result = plantService.sellPlant(1L, 2);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.quantity());

        assertEquals(3, plantEntity.getQuantity());
        verify(plantRepository, times(1)).save(plantEntity);
    }

    @Test
    void sellPlant_ShouldThrowInsufficientStockException_WhenStockIsNotSufficient() {
        // Arrange
        when(plantRepository.findById(1L)).thenReturn(Optional.of(plantEntity));

        // Act & Assert
        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () -> {
            plantService.sellPlant(1L, 10);
        });

        assertTrue(exception.getMessage().contains("Недостаточно товара на складе"));

        verify(plantRepository, never()).save(any());
    }

    @Test
    void savePlant_ShouldSavePlantWithSupplier_WhenSupplierExists() {
        // Arrange
        when(plantMapper.toEntity(plantRequestDto)).thenReturn(plantEntity);
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplierEntity));
        when(plantRepository.save(plantEntity)).thenReturn(plantEntity);
        when(plantMapper.toResponseDto(plantEntity)).thenReturn(plantResponseDto);

        // Act
        PlantResponseDto result = plantService.savePlant(plantRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.supplierId());
        verify(supplierRepository, times(1)).findById(1L);
        verify(plantRepository, times(1)).save(plantEntity);
    }

    @Test
    void savePlant_ShouldThrowException_WhenSupplierDoesNotExist() {
        // Arrange
        when(plantMapper.toEntity(plantRequestDto)).thenReturn(plantEntity);
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            plantService.savePlant(plantRequestDto);
        });

        assertTrue(exception.getMessage().contains("Supplier not found with id: 1"));
        verify(plantRepository, never()).save(any());
    }
}