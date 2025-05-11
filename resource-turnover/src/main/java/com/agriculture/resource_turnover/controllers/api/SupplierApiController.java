package com.agriculture.resource_turnover.controllers.api;

import com.agriculture.resource_turnover.models.Supplier;
import com.agriculture.resource_turnover.services.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "Suppliers API", description = "Управління постачальниками")
public class SupplierApiController {
    private final SupplierService supplierService;

    public SupplierApiController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @Operation(summary = "Отримати всіх постачальників")
    @GetMapping
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.findAll());
    }

    @Operation(summary = "Отримати активних постачальників")
    @GetMapping("/active")
    public ResponseEntity<List<Supplier>> getActiveSuppliers() {
        return ResponseEntity.ok(supplierService.findAllActive());
    }

    @Operation(summary = "Отримати постачальника за ID")
    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.findById(id));
    }

    @Operation(summary = "Створити нового постачальника")
    @PostMapping
    public ResponseEntity<Supplier> createSupplier(@RequestBody Supplier supplier) {
        return ResponseEntity.ok(supplierService.save(supplier));
    }

    @Operation(summary = "Оновити інформацію про постачальника")
    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        supplier.setId(id);
        return ResponseEntity.ok(supplierService.save(supplier));
    }

    @Operation(summary = "Видалити постачальника")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Відновити віддаленого постачальника")
    @PostMapping("/{id}/restore")
    public ResponseEntity<Supplier> restoreSupplier(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.restoreSupplier(id));
    }
}
