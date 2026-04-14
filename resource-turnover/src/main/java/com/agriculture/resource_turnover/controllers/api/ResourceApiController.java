package com.agriculture.resource_turnover.controllers.api;

import com.agriculture.resource_turnover.dto.ResourceRequestDto;
import com.agriculture.resource_turnover.dto.ResourceResponseDto;
import com.agriculture.resource_turnover.dto.mapper.ResourceMapper;
import com.agriculture.resource_turnover.models.Resource;
import com.agriculture.resource_turnover.services.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resources")
public class ResourceApiController {

    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<ResourceResponseDto>> getAllResources() {
        return ResponseEntity.ok(resourceService.findAll().stream()
                .map(resourceMapper::toResponseDto)
                .collect(Collectors.toList()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/active")
    public ResponseEntity<List<ResourceResponseDto>> getActiveResources() {
        return ResponseEntity.ok(resourceService.findAllActive().stream()
                .map(resourceMapper::toResponseDto)
                .collect(Collectors.toList()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponseDto> getResourceById(@PathVariable Long id) {
        return ResponseEntity.ok(resourceMapper.toResponseDto(resourceService.findById(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ResourceResponseDto> createResource(
            @Valid @RequestBody ResourceRequestDto dto) {
        Resource created = resourceService.save(resourceMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceMapper.toResponseDto(created));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ResourceResponseDto> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceRequestDto dto) {
        Resource resource = resourceMapper.toEntity(dto);
        resource.setId(id);
        
        // Retain original version if dealing with concurrent edits properly
        // Ideally handled via service finding existing first, but for now just pass to save
        return ResponseEntity.ok(resourceMapper.toResponseDto(resourceService.save(resource)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/archive")
    public ResponseEntity<ResourceResponseDto> archiveResource(@PathVariable Long id) {
        return ResponseEntity.ok(resourceMapper.toResponseDto(resourceService.archiveResource(id)));
    }
}