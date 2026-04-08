package com.agriculture.resource_turnover;

import com.agriculture.resource_turnover.models.Order;
import com.agriculture.resource_turnover.models.OrderStatus;
import com.agriculture.resource_turnover.models.Resource;
import com.agriculture.resource_turnover.models.Supplier;
import com.agriculture.resource_turnover.repositories.ResourceRepository;
import com.agriculture.resource_turnover.repositories.SupplierRepository;
import com.agriculture.resource_turnover.services.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@Testcontainers
@SpringBootTest(classes = ResourceTurnoverApplication.class)
@AutoConfigureMockMvc
@Transactional
public class OrderApiIntegrationTest {

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
    private ObjectMapper objectMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    private Resource createTestResource() {
        Resource resource = new Resource("Test Resource", "kg", "FERTILIZER",
                BigDecimal.valueOf(100), BigDecimal.valueOf(50));
        return resourceRepository.save(resource);
    }

    private Supplier createTestSupplier() {
        Supplier supplier = new Supplier();
        supplier.setName("Test Supplier");
        supplier.setContactInfo("test@supplier.com");
        return supplierRepository.save(supplier);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void createOrder_WithValidData_ShouldReturnOrder() throws Exception {
        Resource resource = createTestResource();

        mockMvc.perform(post("/api/orders")
                        .param("resourceId", resource.getId().toString())
                        .param("quantity", "50.0")
                        .param("deliveryDate", "2024-12-31"))
                .andExpect(status().isOk())
                // Изменяем проверку: вместо вложенного объекта проверяем ID ресурса напрямую
                .andExpect(jsonPath("$.resource").value(resource.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignSupplier_ShouldUpdateOrder() throws Exception {
        Resource resource = createTestResource();
        Order order = orderService.createOrder(resource.getId(), 100, LocalDate.now());

        orderService.changeStatus(order.getId(), OrderStatus.PENDING_SUPPLY);

        Supplier supplier = createTestSupplier();

        mockMvc.perform(post("/api/orders/{orderId}/assign", order.getId())
                        .param("supplierId", supplier.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supplier").value(supplier.getId())); // Измененная проверка
    }

    @Test
    @WithMockUser(roles = "SUPPLIER")
    void completeOrder_ShouldChangeStatus() throws Exception {
        Order order = orderService.createOrder(createTestResource().getId(), 100, LocalDate.now());

        mockMvc.perform(post("/api/orders/{orderId}/complete", order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cancelOrder_ShouldChangeStatus() throws Exception {
        Resource resource = createTestResource();
        Order order = orderService.createOrder(resource.getId(), 100, LocalDate.now());

        mockMvc.perform(post("/api/orders/{id}/cancel", order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @WithMockUser(roles = "SUPPLIER")
    void addComment_ShouldCreateNewComment() throws Exception {
        Order order = orderService.createOrder(createTestResource().getId(), 100, LocalDate.now());

        mockMvc.perform(post("/api/orders/{orderId}/comments", order.getId())
                        .param("author", "supplier")
                        .param("content", "test comment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments[0].content").value("test comment"));
    }

    @Test
    @WithMockUser
    void filterOrders_ByStatus_ShouldReturnFiltered() throws Exception {
        orderService.createOrder(createTestResource().getId(), 100, LocalDate.now());

        mockMvc.perform(get("/api/orders/filter")
                        .param("status", "PENDING_EXECUTION"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING_EXECUTION"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void createOrder_WithInvalidQuantity_ShouldFail() throws Exception {
        Resource resource = createTestResource();

        mockMvc.perform(post("/api/orders")
                        .param("resourceId", resource.getId().toString())
                        .param("quantity", "-5")
                        .param("deliveryDate", "2024-12-31"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignSupplier_ToNonExistingOrder_ShouldFail() throws Exception {
        mockMvc.perform(post("/api/orders/999/assign")
                        .param("supplierId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "SUPPLIER")
    void createOrder_WithoutPermission_ShouldForbid() throws Exception {
        // 1. Создаем реальный ресурс для теста
        Resource resource = createTestResource();

        // 2. Выполняем запрос с корректным ID ресурса
        mockMvc.perform(post("/api/orders")
                        .param("resourceId", resource.getId().toString()) // Используем существующий ID
                        .param("quantity", "100")
                        .param("deliveryDate", "2024-12-31"))
                .andExpect(status().isForbidden());
    }
}