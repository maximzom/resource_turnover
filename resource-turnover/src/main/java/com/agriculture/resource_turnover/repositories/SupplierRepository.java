package com.agriculture.resource_turnover.repositories;

import com.agriculture.resource_turnover.models.Supplier;
import java.util.List;
import java.util.Optional;

public interface SupplierRepository {
    List<Supplier> findAll();
    Optional<Supplier> findById(Long id);
    Supplier save(Supplier supplier);
    boolean existsById(Long id);
    void deleteById(Long id);
}