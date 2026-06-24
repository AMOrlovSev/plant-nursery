package sev.amorlov.plant_nursery.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sev.amorlov.plant_nursery.dto.SupplierMapper;
import sev.amorlov.plant_nursery.dto.SupplierRequestDto;
import sev.amorlov.plant_nursery.dto.SupplierResponseDto;
import sev.amorlov.plant_nursery.model.SupplierEntity;
import sev.amorlov.plant_nursery.repository.SupplierRepository;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public SupplierService(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    public List<SupplierResponseDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(supplierMapper::toResponseDto)
                .toList();
    }

    public SupplierResponseDto getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .map(supplierMapper::toResponseDto)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + id));
    }

    @Transactional
    public SupplierResponseDto createSupplier(SupplierRequestDto dto) {
        log.info("Creating a new supplier: '{}'", dto.name());
        SupplierEntity supplier = supplierMapper.toEntity(dto);
        SupplierEntity savedSupplier = supplierRepository.save(supplier);
        log.info("Supplier '{}' successfully created with id: {}", savedSupplier.getName(), savedSupplier.getId());
        return supplierMapper.toResponseDto(savedSupplier);
    }

    @Transactional
    public SupplierResponseDto updateSupplier(Long id, SupplierRequestDto dto) {
        log.info("Request to update supplier with id: {}", id);
        SupplierEntity supplier = supplierRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Failed to update supplier: id {} not found", id);
                    return new IllegalArgumentException("Supplier not found with id: " + id);
                });

        supplierMapper.updateEntityFromDto(dto, supplier);
        log.info("Supplier with id: {} successfully updated", id);
        return supplierMapper.toResponseDto(supplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        log.info("Request to delete supplier with id: {}", id);
        if (!supplierRepository.existsById(id)) {
            log.warn("Supplier not found with id: {}", id);
            throw new IllegalArgumentException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
        log.info("Supplier with id: {} successfully deleted from database", id);
    }
}