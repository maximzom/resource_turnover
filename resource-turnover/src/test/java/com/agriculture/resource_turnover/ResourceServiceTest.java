package com.agriculture.resource_turnover;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.agriculture.resource_turnover.models.Resource;
import com.agriculture.resource_turnover.repositories.ResourceRepository;
import com.agriculture.resource_turnover.services.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ResourceService resourceService;

    private Resource testResource;

    @BeforeEach
    void setUp() {
        testResource = new Resource(
                "Test Resource",
                "kg",
                "FERTILIZER",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(50)
        );
        testResource.setId(1L);
    }

    @Test
    void findAll_ShouldReturnAllResources() {
        // Arrange
        when(resourceRepository.findAll()).thenReturn(Arrays.asList(testResource));

        // Act
        List<Resource> result = resourceService.findAll();

        // Assert
        assertEquals(1, result.size());
        verify(resourceRepository).findAll();
    }

    @Test
    void findAllActive_ShouldReturnActiveResources() {
        // Arrange
        when(resourceRepository.findByActive(true)).thenReturn(Arrays.asList(testResource));

        // Act
        List<Resource> result = resourceService.findAllActive();

        // Assert
        assertEquals(1, result.size());
        verify(resourceRepository).findByActive(true);
    }

    @Test
    void findById_WhenResourceExists_ShouldReturnResource() {
        // Arrange
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(testResource));

        // Act
        Resource result = resourceService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_WhenResourceNotExists_ShouldThrowException() {
        // Arrange
        when(resourceRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> resourceService.findById(999L));
    }

    @Test
    void save_WithValidResource_ShouldReturnSavedResource() {
        // Arrange
        when(resourceRepository.save(testResource)).thenReturn(testResource);

        // Act
        Resource result = resourceService.save(testResource);

        // Assert
        assertNotNull(result);
        verify(resourceRepository).save(testResource);
    }

    @Test
    void save_WithInvalidData_ShouldThrowException() {
        // Arrange
        Resource invalidResource = new Resource();

        // Act & Assert
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> resourceService.save(invalidResource)),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> resourceService.save(new Resource("", "kg", "TYPE",
                                BigDecimal.ONE, BigDecimal.ONE))),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> resourceService.save(new Resource("Name", "", "TYPE",
                                BigDecimal.ONE, BigDecimal.ONE))),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> resourceService.save(new Resource("Name", "kg", "",
                                BigDecimal.ONE, BigDecimal.ONE))),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> resourceService.save(new Resource("Name", "kg", "TYPE",
                                BigDecimal.valueOf(-1), BigDecimal.ONE))),

                () -> assertThrows(IllegalArgumentException.class,
                        () -> resourceService.save(new Resource("Name", "kg", "TYPE",
                                BigDecimal.ONE, BigDecimal.valueOf(-1)))
                )
        );
    }

    @Test
    void deleteById_WhenResourceExists_ShouldDeleteResource() {
        // Arrange
        when(resourceRepository.existsById(1L)).thenReturn(true);

        // Act
        resourceService.deleteById(1L);

        // Assert
        verify(resourceRepository).deleteById(1L);
    }

    @Test
    void deleteById_WhenResourceNotExists_ShouldThrowException() {
        // Arrange
        when(resourceRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> resourceService.deleteById(999L));
    }

    @Test
    void archiveResource_ShouldToggleActiveStatus() {
        // Arrange
        Resource originalResource = new Resource();
        originalResource.setId(1L);
        originalResource.setActive(true);

        when(resourceRepository.findById(1L)).thenReturn(Optional.of(originalResource));
        when(resourceRepository.save(any(Resource.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Resource result = resourceService.archiveResource(1L);

        // Assert
        assertFalse(result.isActive());
        verify(resourceRepository).save(originalResource);
    }
}