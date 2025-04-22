package com.agriculture.resource_turnover.services;

import com.agriculture.resource_turnover.models.Supplier;
import com.agriculture.resource_turnover.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    public Supplier findById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
    }

    public Supplier save(Supplier supplier) {
        validateSupplier(supplier);
        return supplierRepository.save(supplier);
    }

    public void deleteById(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }

    private void validateSupplier(Supplier supplier) {
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier name cannot be empty");
        }
        if (supplier.getContactInfo() == null || supplier.getContactInfo().trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier contact info cannot be empty");
        }
    }
}