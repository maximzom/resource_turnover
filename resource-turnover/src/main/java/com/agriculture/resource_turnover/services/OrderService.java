package com.agriculture.resource_turnover.services;

import com.agriculture.resource_turnover.config.OrderSpecifications;
import com.agriculture.resource_turnover.models.*;
import com.agriculture.resource_turnover.repositories.OrderRepository;
import com.agriculture.resource_turnover.repositories.ResourceRepository;
import com.agriculture.resource_turnover.repositories.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ResourceRepository resourceRepository;
    private final SupplierRepository supplierRepository;

    @Transactional
    public Order createOrder(Long resourceId, double quantity, LocalDate deliveryDate) {
        validateQuantity(quantity);
        Resource resource = getResourceById(resourceId);

        Order order = new Order();
        order.setResource(resource);
        order.setQuantity(quantity);
        order.setDeliveryDate(deliveryDate);

        if (resource.getQuantity().compareTo(BigDecimal.valueOf(quantity)) >= 0) {
            order.setStatus(OrderStatus.PENDING_EXECUTION);
            resource.setQuantity(resource.getQuantity().subtract(BigDecimal.valueOf(quantity)));
            resourceRepository.save(resource);
        } else {
            order.setStatus(OrderStatus.PENDING_SUPPLY);
        }

        return orderRepository.save(order);
    }

    @Transactional
    public Order assignSupplier(Long orderId, Long supplierId) {
        Order order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.PENDING_SUPPLY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only PENDING_SUPPLY orders can be assigned");
        }

        Supplier supplier = getSupplierById(supplierId);
        order.setSupplier(supplier);
        order.setStatus(OrderStatus.IN_DELIVERY);
        return orderRepository.save(order);
    }

    @Transactional
    public Order completeOrder(Long orderId) {
        return changeStatus(orderId, OrderStatus.COMPLETED);
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        return changeStatus(orderId, OrderStatus.CANCELLED);
    }

    @Transactional
    public Order changeStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        OrderStatus currentStatus = order.getStatus();

        validateStatusTransition(currentStatus, newStatus);

        handleSpecialStatusCases(order, currentStatus, newStatus);

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus newStatus) {
        Map<OrderStatus, List<OrderStatus>> allowedTransitions = Map.of(
                OrderStatus.PENDING_EXECUTION, List.of(OrderStatus.IN_DELIVERY, OrderStatus.CANCELLED, OrderStatus.COMPLETED, OrderStatus.PENDING_EXECUTION, OrderStatus.PENDING_SUPPLY),
                OrderStatus.PENDING_SUPPLY, List.of(OrderStatus.IN_DELIVERY, OrderStatus.CANCELLED, OrderStatus.COMPLETED, OrderStatus.PENDING_EXECUTION, OrderStatus.PENDING_SUPPLY),
                OrderStatus.IN_DELIVERY, List.of(OrderStatus.IN_DELIVERY, OrderStatus.CANCELLED, OrderStatus.COMPLETED, OrderStatus.PENDING_EXECUTION, OrderStatus.PENDING_SUPPLY),
                OrderStatus.COMPLETED, List.of(),
                OrderStatus.CANCELLED, List.of()
        );

        if (!allowedTransitions.get(current).contains(newStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Недопустимый переход статуса с %s на %s", current, newStatus));
        }
    }

    private void handleSpecialStatusCases(Order order, OrderStatus current, OrderStatus newStatus) {
        if (current == OrderStatus.PENDING_EXECUTION && newStatus == OrderStatus.CANCELLED) {
            Resource resource = order.getResource();
            resource.setQuantity(resource.getQuantity().add(BigDecimal.valueOf(order.getQuantity())));
            resourceRepository.save(resource);
        }

        if (newStatus == OrderStatus.COMPLETED) {
            order.setCompletionDate(LocalDate.now());
        }
    }

    @Transactional
    public Order addComment(Long orderId, String author, String content) {
        validateComment(author, content);
        Order order = getOrderById(orderId);

        OrderComment comment = new OrderComment();
        comment.setAuthor(author);
        comment.setContent(content);
        comment.setOrder(order);
        order.getComments().add(comment);

        return orderRepository.save(order);
    }

    public List<OrderComment> getCommentsForOrder(Long orderId) {
        Order order = getOrderById(orderId);
        return order.getComments();
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
    }

    public List<Order> findByFilters(LocalDate startDate, LocalDate endDate, OrderStatus status) {
        Specification<Order> spec = Specification.where(OrderSpecifications.creationDateAfterOrEqual(startDate))
                .and(OrderSpecifications.creationDateBeforeOrEqual(endDate))
                .and(OrderSpecifications.hasStatus(status));

        return orderRepository.findAll(spec);
    }

    public Order getFullOrderById(Long id) {
        return orderRepository.findFullOrderById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
    }

    // Helper methods to avoid repetition
    private void validateQuantity(double quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    private Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found with id: " + resourceId));
    }

    private Supplier getSupplierById(Long supplierId) {
        return supplierRepository.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + supplierId));
    }

    private void validateComment(String author, String content) {
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
    }
}
