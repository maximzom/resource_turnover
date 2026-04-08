package com.agriculture.resource_turnover;

import com.agriculture.resource_turnover.models.Resource;
import com.agriculture.resource_turnover.repositories.ResourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceRepositoryMockTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Test
    void findByActive_ShouldFilterResources() {
        Resource activeResource = new Resource(/*...*/);
        activeResource.setActive(true);

        when(resourceRepository.findByActive(true))
                .thenReturn(List.of(activeResource));

        List<Resource> result = resourceRepository.findByActive(true);

        assertThat(result)
                .hasSize(1)
                .allMatch(Resource::isActive);
    }
}