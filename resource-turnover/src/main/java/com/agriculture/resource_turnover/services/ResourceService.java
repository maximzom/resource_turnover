package com.agriculture.resource_turnover.services;

import com.agriculture.resource_turnover.models.Resource;
import com.agriculture.resource_turnover.repositories.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;

    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }

    public List<Resource> findAllActive() {
        return resourceRepository.findByActive(true);
    }

    public Resource findById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource not found with id: " + id));
    }

    public Resource save(Resource resource) {
        return resourceRepository.save(resource);
    }

    public void deleteById(Long id) {
        resourceRepository.deleteById(id);
    }

    public Resource archiveResource(Long id) {
        Resource resource = findById(id);
        resource.setActive(!resource.isActive());
        return resourceRepository.save(resource);
    }
}