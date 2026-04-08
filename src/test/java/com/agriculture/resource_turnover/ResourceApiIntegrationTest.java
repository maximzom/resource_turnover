package com.agriculture.resource_turnover;

import com.agriculture.resource_turnover.repositories.ResourceRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.agriculture.resource_turnover.models.Resource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@Testcontainers
@SpringBootTest(classes = ResourceTurnoverApplication.class)
@AutoConfigureMockMvc
@Transactional
public class ResourceApiIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("agriculture_db")
            .withUsername("agri_admin")
            .withPassword("agri_root123")
            .withExposedPorts(5432);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResourceRepository resourceRepository;

    // Позитивные тесты
    @Test
    @WithMockUser(roles = "ADMIN")
    void createResource_WithValidData_ShouldReturnCreated() throws Exception {
        String requestBody = """
            {
                "name": "New Resource",
                "unit": "kg",
                "type": "FERTILIZER",
                "quantity": 100.0,
                "price": 50.0
            }
            """;

        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Resource"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateResource_ShouldUpdateFields() throws Exception {
        Resource resource = createTestResource();
        String requestBody = """
            {
                "name": "Updated Name",
                "unit": "l",
                "type": "FUEL",
                "quantity": 200.0,
                "price": 75.0
            }
            """;

        mockMvc.perform(put("/api/resources/{id}", resource.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void archiveResource_ShouldSetInactive() throws Exception {
        Resource resource = createTestResource();

        mockMvc.perform(post("/api/resources/{id}/archive", resource.getId()))
                .andExpect(jsonPath("$.active").value(false));
    }

    // Негативные тесты
    @Test
    @WithMockUser(roles = "MANAGER")
    void createResource_WithoutAdminRole_ShouldForbid() throws Exception {
        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateNonExistingResource_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(put("/api/resources/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createResource_WithInvalidData_ShouldFail() throws Exception {
        String invalidBody = """
            {
                "name": "",
                "unit": "kg",
                "type": "",
                "quantity": -100,
                "price": -50
            }
            """;

        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }

    private Resource createTestResource() {
        Resource resource = new Resource();
        resource.setName("Test Resource");
        resource.setUnit("kg");
        resource.setType("FERTILIZER");
        resource.setQuantity(BigDecimal.valueOf(100));
        resource.setPrice(BigDecimal.valueOf(50));
        return resourceRepository.save(resource);
    }
}