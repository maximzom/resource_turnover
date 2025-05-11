package com.agriculture.resource_turnover.repositories;

import com.agriculture.resource_turnover.models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    List<Supplier> findByActive(boolean active);

    @Transactional
    @Modifying
    @Query("UPDATE Supplier s SET s.active = :active WHERE s.id = :id")
    void setActiveStatus(Long id, boolean active);
}