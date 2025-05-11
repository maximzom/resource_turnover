package com.agriculture.resource_turnover.services;

import com.agriculture.resource_turnover.models.Order;
import com.agriculture.resource_turnover.models.OrderStatus;
import com.agriculture.resource_turnover.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {
    private final OrderRepository orderRepository;

    public ReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> generateOrderReport(LocalDate startDate, LocalDate endDate, OrderStatus status) {
        LocalDate defaultStartDate = startDate != null ? startDate : LocalDate.MIN;
        LocalDate defaultEndDate = endDate != null ? endDate : LocalDate.MAX;

        if (status == null) {
            return orderRepository.findByFilters(defaultStartDate, defaultEndDate, null);
        }

        return orderRepository.findByFilters(defaultStartDate, defaultEndDate, status);
    }

    public Double calculateTotalRevenue(LocalDate startDate, LocalDate endDate) {
        List<Order> orders = generateOrderReport(startDate, endDate, OrderStatus.COMPLETED);
        return orders.stream()
                .mapToDouble(order -> order.getResource().getPrice().multiply(BigDecimal.valueOf(order.getQuantity())).doubleValue())
                .sum();
    }

}
