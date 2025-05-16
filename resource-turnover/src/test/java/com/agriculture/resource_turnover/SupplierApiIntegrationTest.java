package com.agriculture.resource_turnover;

import com.agriculture.resource_turnover.models.Supplier;
import com.agriculture.resource_turnover.repositories.SupplierRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@Testcontainers
@SpringBootTest(classes = ResourceTurnoverApplication.class)
@AutoConfigureMockMvc
@Transactional
public class SupplierApiIntegrationTest {

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
    private SupplierRepository supplierRepository;

    private Supplier createTestSupplier(boolean active) {
        Supplier supplier = new Supplier();
        supplier.setName("Test Supplier");
        supplier.setContactInfo("test@example.com");
        supplier.setActive(active);
        return supplierRepository.save(supplier);
    }

    // Позитивные тесты
    @Test
    @WithMockUser(roles = "ADMIN")
    void createSupplier_WithValidData_ShouldReturnSupplier() throws Exception {
        String jsonBody = """
            {
                "name": "New Supplier",
                "contactInfo": "new@supplier.com"
            }
            """;

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Supplier"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateSupplier_ShouldModifyData() throws Exception {
        Supplier supplier = createTestSupplier(true);
        String jsonBody = """
            {
                "name": "Updated Name",
                "contactInfo": "updated@test.com"
            }
            """;

        mockMvc.perform(put("/api/suppliers/{id}", supplier.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(jsonPath("$.contactInfo").value("updated@test.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAndRestoreSupplier_ShouldChangeStatus() throws Exception {
        Supplier supplier = createTestSupplier(true);

        // Удаление
        mockMvc.perform(delete("/api/suppliers/{id}", supplier.getId()))
                .andExpect(status().isNoContent());

        // Восстановление
        mockMvc.perform(post("/api/suppliers/{id}/restore", supplier.getId()))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser
    void getActiveSuppliers_ShouldReturnOnlyActive() throws Exception {
        createTestSupplier(true);
        createTestSupplier(false);

        mockMvc.perform(get("/api/suppliers/active"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    // Негативные тесты
    @Test
    @WithMockUser(roles = "MANAGER")
    void createSupplier_WithoutAdminRole_ShouldForbid() throws Exception {
        String validSupplierJson = """
        {
            "name": "Valid Supplier",
            "contactInfo": "valid@test.com"
        }
        """;

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validSupplierJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateNonExistingSupplier_ShouldReturnNotFound() throws Exception {
        String validSupplierJson = """
        {
            "name": "Non-existent Supplier",
            "contactInfo": "invalid@test.com"
        }
        """;

        mockMvc.perform(put("/api/suppliers/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validSupplierJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void restoreNonExistingSupplier_ShouldFail() throws Exception {
        mockMvc.perform(post("/api/suppliers/999/restore"))
                .andExpect(status().isNotFound());
    }
}