package com.agriculture.resource_turnover.controllers.api;

import com.agriculture.resource_turnover.dto.SupplierRequestDto;
import com.agriculture.resource_turnover.dto.SupplierResponseDto;
import com.agriculture.resource_turnover.dto.mapper.SupplierMapper;
import com.agriculture.resource_turnover.models.Supplier;
import com.agriculture.resource_turnover.services.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierApiController {
    
    private final SupplierService supplierService;
    private final SupplierMapper supplierMapper;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<SupplierResponseDto>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.findAll().stream()
                .map(supplierMapper::toResponseDto)
                .collect(Collectors.toList()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/active")
    public ResponseEntity<List<SupplierResponseDto>> getActiveSuppliers() {
        return ResponseEntity.ok(supplierService.findAllActive().stream()
                .map(supplierMapper::toResponseDto)
                .collect(Collectors.toList()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<SupplierResponseDto> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierMapper.toResponseDto(supplierService.findById(id)));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<SupplierResponseDto> createSupplier(@RequestBody SupplierRequestDto dto) {
        Supplier supplier = supplierService.save(supplierMapper.toEntity(dto));
        return ResponseEntity.ok(supplierMapper.toResponseDto(supplier));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<SupplierResponseDto> updateSupplier(@PathVariable Long id, @RequestBody SupplierRequestDto dto) {
        Supplier supplier = supplierMapper.toEntity(dto);
        supplier.setId(id);
        return ResponseEntity.ok(supplierMapper.toResponseDto(supplierService.save(supplier)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/restore")
    public ResponseEntity<SupplierResponseDto> restoreSupplier(@PathVariable Long id) {
        return ResponseEntity.ok(supplierMapper.toResponseDto(supplierService.restoreSupplier(id)));
    }
}
