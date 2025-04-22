package com.agriculture.resource_turnover.services;

import com.agriculture.resource_turnover.models.*;
import com.agriculture.resource_turnover.repositories.OrderRepository;
import com.agriculture.resource_turnover.repositories.ResourceRepository;
import com.agriculture.resource_turnover.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ResourceRepository resourceRepository;
    private final SupplierRepository supplierRepository;

    public Order createOrder(Long resourceId, double quantity, LocalDate deliveryDate) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        Order order = new Order();
        order.setResource(resource);
        order.setQuantity(quantity);
        order.setDeliveryDate(deliveryDate);

        if (resource.getQuantity() >= quantity) {
            order.setStatus(OrderStatus.PENDING_EXECUTION);
            resource.setQuantity(resource.getQuantity() - quantity);
            resourceRepository.save(resource);
        } else {
            order.setStatus(OrderStatus.PENDING_SUPPLY);
        }

        return orderRepository.save(order);
    }

    public Order assignSupplier(Long orderId, Long supplierId) {
        Order order = getOrderById(orderId);
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        order.setSupplier(supplier);
        order.setStatus(OrderStatus.IN_DELIVERY);
        return orderRepository.save(order);
    }

    public Order completeOrder(Long orderId) {
        Order order = getOrderById(orderId);
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletionDate(LocalDate.now());
        return orderRepository.save(order);
    }

    public Order cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.PENDING_SUPPLY) {
            order.setStatus(OrderStatus.CANCELLED);
            return orderRepository.save(order);
        }
        throw new IllegalStateException("Only PENDING_SUPPLY orders can be cancelled");
    }

    public Order addComment(Long orderId, String author, String content) {
        Order order = getOrderById(orderId);
        OrderComment comment = new OrderComment();
        comment.setAuthor(author);
        comment.setContent(content);
        order.getComments().add(comment);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> findByFilters(LocalDate startDate, LocalDate endDate, OrderStatus status) {
        if (startDate == null && endDate == null && status == null) {
            return orderRepository.findAll();
        }

        return orderRepository.findAll().stream()
                .filter(order -> startDate == null || !order.getCreationDate().isBefore(startDate))
                .filter(order -> endDate == null || !order.getCreationDate().isAfter(endDate))
                .filter(order -> status == null || order.getStatus() == status)
                .collect(Collectors.toList());
    }
}