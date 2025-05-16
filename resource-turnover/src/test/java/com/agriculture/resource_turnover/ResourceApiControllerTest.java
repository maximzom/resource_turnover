package com.agriculture.resource_turnover;

import com.agriculture.resource_turnover.controllers.api.ResourceApiController;
import com.agriculture.resource_turnover.models.Resource;
import com.agriculture.resource_turnover.services.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ResourceApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ResourceService resourceService;

    @InjectMocks
    private ResourceApiController resourceApiController;

    private Resource testResource;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(resourceApiController).build();

        testResource = new Resource();
        testResource.setId(1L);
        testResource.setName("Test Resource");
        testResource.setActive(true);
    }

    @Test
    @WithMockUser
    void getAllResources_ShouldReturnResources() throws Exception {
        when(resourceService.findAll()).thenReturn(Collections.singletonList(testResource));

        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Resource"));

        verify(resourceService).findAll();
    }

    @Test
    @WithMockUser
    void getActiveResources_ShouldReturnActiveResources() throws Exception {
        when(resourceService.findAllActive()).thenReturn(Collections.singletonList(testResource));

        mockMvc.perform(get("/api/resources/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active").value(true));

        verify(resourceService).findAllActive();
    }

    @Test
    @WithMockUser
    void getResourceById_WhenResourceExists_ShouldReturnResource() throws Exception {
        when(resourceService.findById(1L)).thenReturn(testResource);

        mockMvc.perform(get("/api/resources/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(resourceService).findById(1L);
    }

    @Test
    @WithMockUser
    void getResourceById_WhenResourceNotExists_ShouldReturnNotFound() throws Exception {
        when(resourceService.findById(anyLong())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/resources/999"))
                .andExpect(status().isNotFound());

        verify(resourceService).findById(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createResource_ShouldReturnCreatedResource() throws Exception {
        when(resourceService.save(any(Resource.class))).thenReturn(testResource);

        String requestBody = """
        {
            "name": "Test Resource",
            "unit": "kg",
            "type": "FERTILIZER",
            "quantity": 100.0,
            "price": 99.99,
            "active": true
        }
        """;

        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        verify(resourceService).save(any(Resource.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateResource_ShouldUpdateAndReturnResource() throws Exception {
        when(resourceService.save(any(Resource.class))).thenReturn(testResource);

        String requestBody = """
        {
            "name": "Updated Resource",
            "unit": "kg",
            "type": "FERTILIZER",
            "quantity": 150.0,
            "price": 149.99,
            "active": true
        }
        """;

        mockMvc.perform(put("/api/resources/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(resourceService).save(any(Resource.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteResource_ShouldReturnNoContent() throws Exception {
        doNothing().when(resourceService).deleteById(1L);

        mockMvc.perform(delete("/api/resources/1"))
                .andExpect(status().isNoContent());

        verify(resourceService).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void archiveResource_ShouldArchiveAndReturnResource() throws Exception {
        Resource archived = new Resource();
        archived.setId(1L);
        archived.setActive(false);

        when(resourceService.archiveResource(1L)).thenReturn(archived);

        mockMvc.perform(post("/api/resources/1/archive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        verify(resourceService).archiveResource(1L);
    }

    // Тесты на валидацию и безопасность
    @Test
    @WithMockUser(roles = "MANAGER")
    void createResource_WithoutAdminRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createResource_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}