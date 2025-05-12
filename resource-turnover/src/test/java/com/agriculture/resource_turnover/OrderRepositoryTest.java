package com.agriculture.resource_turnover;

import com.agriculture.resource_turnover.models.Order;
import com.agriculture.resource_turnover.models.OrderStatus;
import com.agriculture.resource_turnover.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderRepositoryTest {

    @Mock
    private OrderRepository orderRepository;

    @Captor
    private ArgumentCaptor<LocalDate> startDateCaptor;

    @Captor
    private ArgumentCaptor<LocalDate> endDateCaptor;

    @Captor
    private ArgumentCaptor<OrderStatus> statusCaptor;

    @Test
    void findByStatus_ShouldDelegateToRepository() {
        // Arrange
        Order testOrder = new Order();
        when(orderRepository.findByStatus(OrderStatus.PENDING_EXECUTION))
                .thenReturn(List.of(testOrder));

        // Act
        List<Order> result = orderRepository.findByStatus(OrderStatus.PENDING_EXECUTION);

        // Assert
        assertEquals(1, result.size());
        assertSame(testOrder, result.get(0));
    }

    @Test
    void findByFilters_ShouldPassCorrectParameters() {
        // Arrange
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 12, 31);
        OrderStatus status = OrderStatus.COMPLETED;

        when(orderRepository.findByFilters(any(), any(), any()))
                .thenReturn(List.of(new Order()));

        // Act
        orderRepository.findByFilters(start, end, status);

        // Assert
        verify(orderRepository).findByFilters(
                startDateCaptor.capture(),
                endDateCaptor.capture(),
                statusCaptor.capture()
        );

        assertEquals(start, startDateCaptor.getValue());
        assertEquals(end, endDateCaptor.getValue());
        assertEquals(status, statusCaptor.getValue());
    }

    @Test
    void existsByIdAndStatus_ShouldVerifyMethodCall() {
        // Arrange
        Long orderId = 1L;
        when(orderRepository.existsByIdAndStatus(orderId, OrderStatus.IN_DELIVERY))
                .thenReturn(true);

        // Act
        boolean exists = orderRepository.existsByIdAndStatus(orderId, OrderStatus.IN_DELIVERY);

        // Assert
        assertTrue(exists);
        verify(orderRepository).existsByIdAndStatus(orderId, OrderStatus.IN_DELIVERY);
    }
}