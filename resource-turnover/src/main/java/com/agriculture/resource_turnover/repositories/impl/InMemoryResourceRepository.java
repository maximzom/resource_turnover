package com.agriculture.resource_turnover.repositories.impl;

import com.agriculture.resource_turnover.models.Resource;
import com.agriculture.resource_turnover.repositories.ResourceRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryResourceRepository implements ResourceRepository {
    private final List<Resource> resources = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public List<Resource> findAll() {
        return new ArrayList<>(resources);
    }

    @Override
    public Optional<Resource> findById(Long id) {
        return resources.stream()
                .filter(resource -> resource.getId().equals(id))
                .findFirst();
    }

    @Override
    public Resource save(Resource resource) {
        if (resource.getId() == null) {
            resource.setId(idGenerator.getAndIncrement());
        }
        resources.removeIf(r -> r.getId().equals(resource.getId()));
        resources.add(resource);
        return resource;
    }

    @Override
    public void deleteById(Long id) {
        resources.removeIf(resource -> resource.getId().equals(id));
    }

    @Override
    public List<Resource> findByActive(boolean active) {
        return resources.stream()
                .filter(resource -> resource.isActive() == active)
                .toList();
    }
}