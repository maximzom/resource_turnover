package com.agriculture.resource_turnover;

import com.agriculture.resource_turnover.config.SecurityConfig;
import com.agriculture.resource_turnover.controllers.api.OrderApiController;
import com.agriculture.resource_turnover.models.Order;
import com.agriculture.resource_turnover.models.OrderStatus;
import com.agriculture.resource_turnover.services.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderApiController.class)
@Import(SecurityConfig.class)
public class OrderApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    @WithMockUser
    void getAllOrders_ShouldReturn200() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/orders")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "MANAGER"})
    void createOrder_WithValidParams_ShouldReturn200() throws Exception {
        when(orderService.createOrder(anyLong(), anyDouble(), any()))
                .thenReturn(new Order());

        mockMvc.perform(post("/api/orders")
                        .param("resourceId", "1")
                        .param("quantity", "50")
                        .param("deliveryDate", "2024-01-01"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignSupplier_WithValidParams_ShouldReturn200() throws Exception {
        when(orderService.assignSupplier(anyLong(), anyLong()))
                .thenReturn(new Order());

        mockMvc.perform(post("/api/orders/1/assign")
                        .param("supplierId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "SUPPLIER"})
    void completeOrder_ShouldReturn200() throws Exception {
        when(orderService.completeOrder(anyLong()))
                .thenReturn(new Order());

        mockMvc.perform(post("/api/orders/1/complete"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void filterOrders_WithValidParams_ShouldReturn200() throws Exception {
        when(orderService.findByFilters(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/orders/filter")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-12-31")
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk());
    }
}