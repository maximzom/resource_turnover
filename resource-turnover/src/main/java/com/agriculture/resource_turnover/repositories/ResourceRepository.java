package com.agriculture.resource_turnover.repositories;

import com.agriculture.resource_turnover.models.Resource;
import java.util.List;
import java.util.Optional;

public interface ResourceRepository {
    List<Resource> findAll();
    Optional<Resource> findById(Long id);
    Resource save(Resource resource);
    void deleteById(Long id);
    List<Resource> findByActive(boolean active);
}