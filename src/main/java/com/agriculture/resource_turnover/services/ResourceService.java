package com.agriculture.resource_turnover.services;

import com.agriculture.resource_turnover.models.Resource;
import com.agriculture.resource_turnover.repositories.ResourceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public List<Resource> findAll() {
        log.info("Fetching all resources");
        return resourceRepository.findAll();
    }

    public List<Resource> findAllActive() {
        log.info("Fetching all active resources");
        return resourceRepository.findByActive(true);
    }

    public Resource findById(Long id) {
        log.info("Fetching resource by id: {}", id);
        return resourceRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMsg = "Resource not found with id: " + id;
                    log.error(errorMsg);
                    return new IllegalArgumentException(errorMsg);
                });
    }

    @Transactional
    public Resource save(Resource resource) {
        log.info("Attempting to save resource: {}", resource);

        validateResource(resource);

        Resource savedResource = resourceRepository.save(resource);
        log.info("Successfully saved resource with id: {}", savedResource.getId());

        return savedResource;
    }

    @Transactional
    public void deleteById(Long id) {
        log.info("Attempting to delete resource with id: {}", id);

        if (!resourceRepository.existsById(id)) {
            String errorMsg = "Resource not found with id: " + id;
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        resourceRepository.deleteById(id);
        log.info("Successfully deleted resource with id: {}", id);
    }

    @Transactional
    public Resource archiveResource(Long id) {
        log.info("Attempting to archive resource with id: {}", id);

        Resource resource = findById(id);
        resource.setActive(!resource.isActive());

        Resource updatedResource = resourceRepository.save(resource);
        log.info("Resource with id: {} archive status set to: {}",
                id, updatedResource.isActive());

        return updatedResource;
    }

    private void validateResource(Resource resource) {
        if (resource.getName() == null || resource.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Resource name cannot be empty");
        }
        if (resource.getUnit() == null || resource.getUnit().trim().isEmpty()) {
            throw new IllegalArgumentException("Unit cannot be empty");
        }
        if (resource.getType() == null || resource.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Type cannot be empty");
        }
        if (resource.getQuantity() == null || resource.getQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity must be positive or zero");
        }
        if (resource.getPrice() == null || resource.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be positive or zero");
        }
    }
}