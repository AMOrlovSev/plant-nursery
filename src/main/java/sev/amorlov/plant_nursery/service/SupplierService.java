package sev.amorlov.plant_nursery.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sev.amorlov.plant_nursery.dto.SupplierMapper;
import sev.amorlov.plant_nursery.dto.SupplierRequestDto;
import sev.amorlov.plant_nursery.dto.SupplierResponseDto;
import sev.amorlov.plant_nursery.model.SupplierEntity;
import sev.amorlov.plant_nursery.repository.SupplierRepository;

import java.util.List;

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
        SupplierEntity supplier = supplierMapper.toEntity(dto);
        SupplierEntity savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toResponseDto(savedSupplier);
    }

    @Transactional
    public SupplierResponseDto updateSupplier(Long id, SupplierRequestDto dto) {
        SupplierEntity supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + id));

        supplierMapper.updateEntityFromDto(dto, supplier);
        return supplierMapper.toResponseDto(supplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new IllegalArgumentException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }
}