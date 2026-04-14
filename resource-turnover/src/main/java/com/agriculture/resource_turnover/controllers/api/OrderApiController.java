package com.agriculture.resource_turnover.controllers.api;

import com.agriculture.resource_turnover.dto.OrderCommentDto;
import com.agriculture.resource_turnover.dto.OrderResponseDto;
import com.agriculture.resource_turnover.dto.mapper.OrderMapper;
import com.agriculture.resource_turnover.models.Order;
import com.agriculture.resource_turnover.models.OrderStatus;
import com.agriculture.resource_turnover.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders().stream()
                .map(orderMapper::toResponseDto)
                .collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestParam Long resourceId,
            @RequestParam BigDecimal quantity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deliveryDate) {
        Order order = orderService.createOrder(resourceId, quantity, deliveryDate);
        return ResponseEntity.ok(orderMapper.toResponseDto(order));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{orderId}/assign")
    public ResponseEntity<OrderResponseDto> assignSupplier(
            @PathVariable Long orderId,
            @RequestParam Long supplierId) {
        return ResponseEntity.ok(orderMapper.toResponseDto(orderService.assignSupplier(orderId, supplierId)));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLIER')")
    @PostMapping("/{orderId}/complete")
    public ResponseEntity<OrderResponseDto> completeOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderMapper.toResponseDto(orderService.completeOrder(orderId)));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLIER')")
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderMapper.toResponseDto(orderService.cancelOrder(orderId)));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{orderId}/comments")
    public ResponseEntity<List<OrderCommentDto>> getOrderComments(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getCommentsForOrder(orderId).stream()
                .map(orderMapper::toCommentDto)
                .collect(Collectors.toList()));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPPLIER')")
    @PostMapping("/{orderId}/comments")
    public ResponseEntity<OrderResponseDto> addCommentToOrder(
            @PathVariable Long orderId,
            @RequestParam String author,
            @RequestParam String content) {
        return ResponseEntity.ok(orderMapper.toResponseDto(orderService.addComment(orderId, author, content)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> changeOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus newStatus) {
        return ResponseEntity.ok(orderMapper.toResponseDto(orderService.changeStatus(orderId, newStatus)));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/filter")
    public ResponseEntity<List<OrderResponseDto>> filterOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) OrderStatus status) {

        try {
            return ResponseEntity.ok(orderService.findByFilters(startDate, endDate, status).stream()
                    .map(orderMapper::toResponseDto)
                    .collect(Collectors.toList()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
