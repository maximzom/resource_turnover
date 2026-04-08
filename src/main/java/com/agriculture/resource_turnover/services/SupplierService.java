package com.agriculture.resource_turnover.services;

import com.agriculture.resource_turnover.models.Supplier;
import com.agriculture.resource_turnover.repositories.SupplierRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public List<Supplier> findAll() {
        return supplierRepository.findAll();
    }

    public List<Supplier> findAllActive() {
        return supplierRepository.findByActive(true);
    }

    public Supplier findById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + id));
    }

    @Transactional
    public Supplier save(Supplier supplier) {
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier name cannot be empty");
        }
        if (supplier.getContactInfo() == null || supplier.getContactInfo().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact info cannot be empty");
        }
        return supplierRepository.save(supplier);
    }

    @Transactional
    public void deleteById(Long id) {
        if (supplierRepository.existsById(id)) {
            supplierRepository.setActiveStatus(id, false);
        }
    }

    @Transactional
    public Supplier restoreSupplier(Long id) {
        supplierRepository.setActiveStatus(id, true);
        return supplierRepository.findById(id).orElseThrow();
    }
}