package com.agriculture.resource_turnover.repositories;

import com.agriculture.resource_turnover.models.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByActive(boolean active);
}