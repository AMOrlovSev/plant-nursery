package sev.amorlov.plant_nursery.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sev.amorlov.plant_nursery.dto.SupplierMapper;
import sev.amorlov.plant_nursery.dto.SupplierRequestDto;
import sev.amorlov.plant_nursery.dto.SupplierResponseDto;
import sev.amorlov.plant_nursery.model.SupplierEntity;
import sev.amorlov.plant_nursery.repository.SupplierRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private SupplierMapper supplierMapper;

    @InjectMocks
    private SupplierService supplierService;

    private SupplierEntity supplierEntity;
    private SupplierRequestDto supplierRequestDto;
    private SupplierResponseDto supplierResponseDto;

    @BeforeEach
    void setUp() {
        supplierEntity = new SupplierEntity();
        supplierEntity.setId(1L);
        supplierEntity.setName("ЭкоСад Питомник");
        supplierEntity.setContactEmail("info@ecosad.ru");
        supplierEntity.setPhoneNumber("+7 (812) 111-22-33");

        supplierRequestDto = new SupplierRequestDto("ЭкоСад Питомник", "info@ecosad.ru", "+7 (812) 111-22-33");
        supplierResponseDto = new SupplierResponseDto(1L, "ЭкоСад Питомник", "info@ecosad.ru", "+7 (812) 111-22-33", LocalDateTime.now());
    }

    @Test
    void getAllSuppliers_ShouldReturnListOfSuppliers() {
        // Arrange
        when(supplierRepository.findAll()).thenReturn(List.of(supplierEntity));
        when(supplierMapper.toResponseDto(supplierEntity)).thenReturn(supplierResponseDto);

        // Act
        List<SupplierResponseDto> result = supplierService.getAllSuppliers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ЭкоСад Питомник", result.get(0).name());
        verify(supplierRepository, times(1)).findAll();
    }

    @Test
    void getSupplierById_ShouldReturnSupplier_WhenSupplierExists() {
        // Arrange
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplierEntity));
        when(supplierMapper.toResponseDto(supplierEntity)).thenReturn(supplierResponseDto);

        // Act
        SupplierResponseDto result = supplierService.getSupplierById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(supplierRepository, times(1)).findById(1L);
    }

    @Test
    void getSupplierById_ShouldThrowException_WhenSupplierDoesNotExist() {
        // Arrange
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                supplierService.getSupplierById(1L)
        );

        assertTrue(exception.getMessage().contains("Supplier not found with id: 1"));
        verify(supplierMapper, never()).toResponseDto(any());
    }

    @Test
    void createSupplier_ShouldSaveAndReturnSupplier() {
        // Arrange
        when(supplierMapper.toEntity(supplierRequestDto)).thenReturn(supplierEntity);
        when(supplierRepository.save(supplierEntity)).thenReturn(supplierEntity);
        when(supplierMapper.toResponseDto(supplierEntity)).thenReturn(supplierResponseDto);

        // Act
        SupplierResponseDto result = supplierService.createSupplier(supplierRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("ЭкоСад Питомник", result.name());
        verify(supplierRepository, times(1)).save(supplierEntity);
    }

    @Test
    void updateSupplier_ShouldUpdateAndReturnSupplier_WhenSupplierExists() {
        // Arrange
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplierEntity));
        doNothing().when(supplierMapper).updateEntityFromDto(supplierRequestDto, supplierEntity);
        when(supplierMapper.toResponseDto(supplierEntity)).thenReturn(supplierResponseDto);

        // Act
        SupplierResponseDto result = supplierService.updateSupplier(1L, supplierRequestDto);

        // Assert
        assertNotNull(result);
        verify(supplierRepository, times(1)).findById(1L);
        verify(supplierMapper, times(1)).updateEntityFromDto(supplierRequestDto, supplierEntity);
    }

    @Test
    void deleteSupplier_ShouldDelete_WhenSupplierExists() {
        // Arrange
        when(supplierRepository.existsById(1L)).thenReturn(true);
        doNothing().when(supplierRepository).deleteById(1L);

        // Act
        assertDoesNotThrow(() -> supplierService.deleteSupplier(1L));

        // Assert
        verify(supplierRepository, times(1)).existsById(1L);
        verify(supplierRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteSupplier_ShouldThrowException_WhenSupplierDoesNotExist() {
        // Arrange
        when(supplierRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                supplierService.deleteSupplier(1L)
        );

        assertTrue(exception.getMessage().contains("Supplier not found with id: 1"));
        verify(supplierRepository, never()).deleteById(anyLong());
    }
}