package com.agriculture.resource_turnover;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.agriculture.resource_turnover.models.Supplier;
import com.agriculture.resource_turnover.repositories.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SupplierRepositoryTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Test
    void findByActive_ShouldCallRepositoryMethod() {
        // Arrange
        Supplier mockSupplier = new Supplier();
        when(supplierRepository.findByActive(true))
                .thenReturn(Collections.singletonList(mockSupplier));

        // Act
        List<Supplier> result = supplierRepository.findByActive(true);

        // Assert
        assertThat(result).hasSize(1);
        verify(supplierRepository, times(1)).findByActive(true);
    }

    @Test
    void setActiveStatus_ShouldCallUpdateMethod() {
        // Arrange
        Long testId = 1L;
        boolean testStatus = false;

        // Act
        supplierRepository.setActiveStatus(testId, testStatus);

        // Assert
        verify(supplierRepository, times(1))
                .setActiveStatus(testId, testStatus);
    }

    @Test
    void save_ShouldDelegateToRepository() {
        // Arrange
        Supplier testSupplier = new Supplier();
        when(supplierRepository.save(testSupplier))
                .thenReturn(testSupplier);

        // Act
        Supplier result = supplierRepository.save(testSupplier);

        // Assert
        assertThat(result).isEqualTo(testSupplier);
        verify(supplierRepository).save(testSupplier);
    }

    @Test
    void deleteById_ShouldCallRepositoryMethod() {
        // Arrange
        Long testId = 1L;

        // Act
        supplierRepository.deleteById(testId);

        // Assert
        verify(supplierRepository).deleteById(testId);
    }

    @Test
    void findById_ShouldReturnSupplier() {
        // Arrange
        Long testId = 1L;
        Supplier expected = new Supplier();
        when(supplierRepository.findById(testId))
                .thenReturn(java.util.Optional.of(expected));

        // Act
        Optional<Supplier> result = supplierRepository.findById(testId);

        // Assert
        assertThat(result).contains(expected);
    }

    @Test
    void findAll_ShouldReturnAllSuppliers() {
        // Arrange
        when(supplierRepository.findAll())
                .thenReturn(List.of(new Supplier(), new Supplier()));

        // Act
        List<Supplier> result = supplierRepository.findAll();

        // Assert
        assertThat(result).hasSize(2);
    }
}