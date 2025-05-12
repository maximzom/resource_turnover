package com.agriculture.resource_turnover;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.agriculture.resource_turnover.controllers.api.SupplierApiController;
import com.agriculture.resource_turnover.models.Supplier;
import com.agriculture.resource_turnover.services.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class SupplierApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SupplierService supplierService;

    @InjectMocks
    private SupplierApiController supplierApiController;

    private Supplier testSupplier;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(supplierApiController).build();

        testSupplier = new Supplier();
        testSupplier.setId(1L);
        testSupplier.setName("Test Supplier");
        testSupplier.setActive(true);
    }

    @Test
    @WithMockUser
    void getAllSuppliers_ShouldReturnSuppliers() throws Exception {
        when(supplierService.findAll()).thenReturn(Collections.singletonList(testSupplier));

        mockMvc.perform(get("/api/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Supplier"));
    }

    @Test
    @WithMockUser
    void getActiveSuppliers_ShouldReturnActiveSuppliers() throws Exception {
        when(supplierService.findAllActive()).thenReturn(Collections.singletonList(testSupplier));

        mockMvc.perform(get("/api/suppliers/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    @WithMockUser
    void getSupplierById_WhenExists_ShouldReturnSupplier() throws Exception {
        when(supplierService.findById(1L)).thenReturn(testSupplier);

        mockMvc.perform(get("/api/suppliers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getSupplierById_WhenNotExists_ShouldThrowException() throws Exception {
        when(supplierService.findById(999L)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/api/suppliers/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createSupplier_WithAdminRole_ShouldCreateSupplier() throws Exception {
        when(supplierService.save(any(Supplier.class))).thenReturn(testSupplier);

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Supplier\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createSupplier_WithoutAdminRole_ShouldForbid() throws Exception {
        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateSupplier_ShouldUpdateSupplier() throws Exception {
        testSupplier.setName("Updated Supplier");
        when(supplierService.save(any(Supplier.class))).thenReturn(testSupplier);

        mockMvc.perform(put("/api/suppliers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Supplier\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Supplier"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteSupplier_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/suppliers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void restoreSupplier_ShouldActivateSupplier() throws Exception {
        testSupplier.setActive(true);
        when(supplierService.restoreSupplier(1L)).thenReturn(testSupplier);

        mockMvc.perform(post("/api/suppliers/1/restore"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }
}