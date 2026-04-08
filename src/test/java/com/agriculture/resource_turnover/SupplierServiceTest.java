package com.agriculture.resource_turnover;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.agriculture.resource_turnover.models.Supplier;
import com.agriculture.resource_turnover.repositories.SupplierRepository;
import com.agriculture.resource_turnover.services.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class SupplierServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private SupplierService supplierService;

    private Supplier testSupplier;

    @BeforeEach
    void setUp() {
        testSupplier = new Supplier();
        testSupplier.setId(1L);
        testSupplier.setName("Test Supplier");
        testSupplier.setContactInfo("test@example.com");
        testSupplier.setActive(true);
    }

    @Test
    void findAll_ShouldReturnAllSuppliers() {
        // Arrange
        when(supplierRepository.findAll()).thenReturn(List.of(testSupplier));

        // Act
        List<Supplier> result = supplierService.findAll();

        // Assert
        assertThat(result).hasSize(1);
        verify(supplierRepository).findAll();
    }

    @Test
    void findAllActive_ShouldReturnActiveSuppliers() {
        // Arrange
        when(supplierRepository.findByActive(true)).thenReturn(List.of(testSupplier));

        // Act
        List<Supplier> result = supplierService.findAllActive();

        // Assert
        assertThat(result)
                .hasSize(1)
                .allMatch(Supplier::isActive);
    }

    @Test
    void findById_WhenExists_ShouldReturnSupplier() {
        // Arrange
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));

        // Act
        Supplier result = supplierService.findById(1L);

        // Assert
        assertThat(result).isEqualTo(testSupplier);
    }

    @Test
    void findById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> supplierService.findById(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Supplier not found with id: 999");
    }

    @Test
    void save_WithValidData_ShouldReturnSavedSupplier() {
        // Arrange
        when(supplierRepository.save(testSupplier)).thenReturn(testSupplier);

        // Act
        Supplier result = supplierService.save(testSupplier);

        // Assert
        assertThat(result).isEqualTo(testSupplier);
        verify(supplierRepository).save(testSupplier);
    }

    @Test
    void save_WithInvalidName_ShouldThrowException() {
        // Arrange
        Supplier invalidSupplier = new Supplier();
        invalidSupplier.setContactInfo("valid@contact.com");

        // Act & Assert
        assertThatThrownBy(() -> supplierService.save(invalidSupplier))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Supplier name cannot be empty");
    }

    @Test
    void save_WithInvalidContact_ShouldThrowException() {
        // Arrange
        Supplier invalidSupplier = new Supplier();
        invalidSupplier.setName("Valid Name");

        // Act & Assert
        assertThatThrownBy(() -> supplierService.save(invalidSupplier))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Contact info cannot be empty");
    }

    @Test
    void deleteById_ShouldSetInactiveStatus() {
        when(supplierRepository.existsById(1L)).thenReturn(true); // Добавляем мок для existsById
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Boolean> statusCaptor = ArgumentCaptor.forClass(Boolean.class);

        supplierService.deleteById(1L);

        verify(supplierRepository).existsById(1L);
        verify(supplierRepository).setActiveStatus(
                idCaptor.capture(),
                statusCaptor.capture()
        );

        assertThat(idCaptor.getValue()).isEqualTo(1L);
        assertThat(statusCaptor.getValue()).isFalse();
    }

    @Test
    void restoreSupplier_ShouldSetActiveStatusAndReturnSupplier() {
        // Arrange
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Boolean> statusCaptor = ArgumentCaptor.forClass(Boolean.class);

        // Act
        Supplier result = supplierService.restoreSupplier(1L);

        // Assert
        verify(supplierRepository).setActiveStatus(
                idCaptor.capture(),
                statusCaptor.capture()
        );
        assertThat(idCaptor.getValue()).isEqualTo(1L);
        assertThat(statusCaptor.getValue()).isTrue();
        assertThat(result).isEqualTo(testSupplier);
    }

    @Test
    void restoreSupplier_WhenNotFound_ShouldThrowException() {
        // Arrange
        when(supplierRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> supplierService.restoreSupplier(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No value present");
    }
}