/*
package com.agriculture.resource_turnover.repositories.impl;

import com.agriculture.resource_turnover.models.Supplier;
import com.agriculture.resource_turnover.repositories.SupplierRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemorySupplierRepository implements SupplierRepository {
    private final List<Supplier> suppliers = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Supplier> findAll() {
        return new ArrayList<>(suppliers);
    }

    @Override
    public Optional<Supplier> findById(Long id) {
        return suppliers.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    @Override
    public Supplier save(Supplier supplier) {
        if (supplier.getId() == null) {
            supplier.setId(idGenerator.getAndIncrement());
            suppliers.add(supplier);
        } else {
            deleteById(supplier.getId());
            suppliers.add(supplier);
        }
        return supplier;
    }

    @Override
    public boolean existsById(Long id) {
        return suppliers.stream().anyMatch(s -> s.getId().equals(id));
    }

    @Override
    public void deleteById(Long id) {
        suppliers.removeIf(s -> s.getId().equals(id));
    }
}

 */