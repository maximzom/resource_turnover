package com.agriculture.resource_turnover.controllers.api;

import com.agriculture.resource_turnover.models.Resource;
import com.agriculture.resource_turnover.services.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated; // Опционально
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resources")
@Tag(name = "Resource API", description = "Управління ресурсами")
public class ResourceApiController {

    private final ResourceService resourceService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    @Operation(summary = "Отримати всі ресурси")
    public ResponseEntity<List<Resource>> getAllResources() {
        return ResponseEntity.ok(resourceService.findAll());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/active")
    @Operation(summary = "Отримати активні ресурси")
    public ResponseEntity<List<Resource>> getActiveResources() {
        return ResponseEntity.ok(resourceService.findAllActive());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    @Operation(summary = "Отримати ресурс за ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ресурс знайдено"),
            @ApiResponse(responseCode = "404", description = "Ресурс не знайдено")
    })
    public ResponseEntity<Resource> getResourceById(
            @Parameter(description = "ID ресурсу", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(resourceService.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Створити новий ресурс")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ресурс створено"),
            @ApiResponse(responseCode = "400", description = "Неправильні дані")
    })
    public ResponseEntity<Resource> createResource(
            @Parameter(description = "Дані ресурсу")
            @Valid @RequestBody Resource resource) {
        Resource created = resourceService.save(resource);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Оновити ресурс")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ресурс створено"),
            @ApiResponse(responseCode = "400", description = "Неправильні дані"),
            @ApiResponse(responseCode = "404", description = "Ресурс не знайдено")
    })
    public ResponseEntity<Resource> updateResource(
            @Parameter(description = "ID ресурсу", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Оновлені дані")
            @Valid @RequestBody Resource resource) {
        resource.setId(id);
        return ResponseEntity.ok(resourceService.save(resource));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Видалити ресурс")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ресурс видалено"),
            @ApiResponse(responseCode = "404", description = "Ресурс не знайдено")
    })
    public ResponseEntity<Void> deleteResource(
            @Parameter(description = "ID ресурсу", example = "1")
            @PathVariable Long id) {
        resourceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/archive")
    @Operation(summary = "Архівувати ресурс")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ресурс архівований"),
            @ApiResponse(responseCode = "404", description = "Ресурс не знайдено")
    })
    public ResponseEntity<Resource> archiveResource(
            @Parameter(description = "ID ресурсу", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(resourceService.archiveResource(id));
    }
}