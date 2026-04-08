package com.agriculture.resource_turnover;

import com.agriculture.resource_turnover.models.*;
import com.agriculture.resource_turnover.repositories.OrderRepository;
import com.agriculture.resource_turnover.repositories.ResourceRepository;
import com.agriculture.resource_turnover.repositories.SupplierRepository;
import com.agriculture.resource_turnover.services.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_WhenResourceIsAvailable_ShouldSetPendingExecution() {
        // Arrange
        Resource resource = new Resource();
        resource.setQuantity(new BigDecimal("100.00")); // Явное задание формата
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Order result = orderService.createOrder(1L, 50.0, LocalDate.now()); // 50.0 вместо 50

        // Assert
        assertEquals(OrderStatus.PENDING_EXECUTION, result.getStatus());
        assertTrue(
                new BigDecimal("50.00").compareTo(resource.getQuantity()) == 0,
                "Expected 50.00, but got: " + resource.getQuantity()
        );
        verify(resourceRepository).save(resource);
    }

    @Test
    void createOrder_WhenResourceIsNotAvailable_ShouldSetPendingSupply() {
        Resource resource = new Resource();
        resource.setQuantity(BigDecimal.valueOf(10));
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.createOrder(1L, 50, LocalDate.now());

        assertEquals(OrderStatus.PENDING_SUPPLY, result.getStatus());
        verify(resourceRepository, never()).save(resource);
    }

    @Test
    void assignSupplier_WhenStatusPendingSupply_ShouldAssignSupplier() {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING_SUPPLY);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        Supplier supplier = new Supplier();
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.assignSupplier(1L, 1L);

        assertEquals(OrderStatus.IN_DELIVERY, result.getStatus());
        assertNotNull(result.getSupplier());
    }

    @Test
    void assignSupplier_WhenInvalidStatus_ShouldThrowException() {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING_EXECUTION);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(ResponseStatusException.class, () ->
                orderService.assignSupplier(1L, 1L)
        );
    }

    @Test
    void changeStatus_FromPendingExecutionToCancelled_ShouldRestoreResource() {
        // Arrange
        Resource resource = new Resource();
        resource.setQuantity(new BigDecimal("50.00"));

        Order order = new Order();
        order.setStatus(OrderStatus.PENDING_EXECUTION);
        order.setResource(resource);
        order.setQuantity(30.0); // Явное указание double значения

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Order result = orderService.changeStatus(1L, OrderStatus.CANCELLED);

        // Assert
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        assertTrue(
                new BigDecimal("80.00").compareTo(resource.getQuantity()) == 0,
                "Expected 80.00, but got: " + resource.getQuantity()
        );
        verify(resourceRepository).save(resource);
    }

    @Test
    void addComment_WithValidData_ShouldAddComment() {
        Order order = new Order();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.addComment(1L, "Author", "Content");

        assertEquals(1, result.getComments().size());
        assertEquals("Author", result.getComments().get(0).getAuthor());
    }

    @Test
    void addComment_WithEmptyAuthor_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                orderService.addComment(1L, "", "Content")
        );
    }
}