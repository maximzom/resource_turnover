package com.agriculture.resource_turnover.controllers.api;

import com.agriculture.resource_turnover.models.Order;
import com.agriculture.resource_turnover.models.OrderComment;
import com.agriculture.resource_turnover.models.OrderStatus;
import com.agriculture.resource_turnover.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders API", description = "Управління замовленнями")
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderService orderService;

    @Operation(summary = "Отримати всі замовлення")
    @ApiResponse(responseCode = "200", description = "Список замовлень успішно отримано")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();

        orders.forEach(order -> {
            if (Hibernate.isInitialized(order.getComments())) {
                order.getComments().size();
            }
        });

        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Створити нове замовлення")
    @ApiResponse(responseCode = "200", description = "Замовлення успішно створено")
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @Parameter(description = "ID ресурсу") @RequestParam Long resourceId,
            @Parameter(description = "Кількість") @RequestParam double quantity,
            @Parameter(description = "Дата доставлення (yyyy-MM-dd)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deliveryDate) {
        return ResponseEntity.ok(orderService.createOrder(resourceId, quantity, deliveryDate));
    }

    @Operation(summary = "Призначити постачальника замовленню")
    @PostMapping("/{orderId}/assign")
    public ResponseEntity<Order> assignSupplier(
            @PathVariable Long orderId,
            @RequestParam Long supplierId) {
        return ResponseEntity.ok(orderService.assignSupplier(orderId, supplierId));
    }

    @Operation(summary = "Завершити замовлення")
    @PostMapping("/{orderId}/complete")
    public ResponseEntity<Order> completeOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.completeOrder(orderId));
    }

    @Operation(summary = "Скасувати замовлення")
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @Operation(summary = "Отримати коментарі до замовлення")
    @GetMapping("/{orderId}/comments")
    public ResponseEntity<List<OrderComment>> getOrderComments(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getCommentsForOrder(orderId));
    }

    @Operation(summary = "Додати коментар до замовлення")
    @PostMapping("/{orderId}/comments")
    public ResponseEntity<Order> addCommentToOrder(
            @PathVariable Long orderId,
            @RequestParam String author,
            @RequestParam String content) {
        return ResponseEntity.ok(orderService.addComment(orderId, author, content));
    }

    @Operation(summary = "Змінити статус замовлення")
    @ApiResponse(responseCode = "200", description = "Статус замовлення успішно змінено")
    @ApiResponse(responseCode = "400", description = "Неприпустимий перехід статусів")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<Order> changeOrderStatus(
            @PathVariable Long orderId,
            @Parameter(description = "Новий статус замовлення") @RequestParam OrderStatus newStatus) {
        return ResponseEntity.ok(orderService.changeStatus(orderId, newStatus));
    }

    @Operation(summary = "Фільтрувати замовлення за датою та/або статусом")
    @ApiResponse(responseCode = "200", description = "Фільтрацію успішно виконано")
    @ApiResponse(responseCode = "400", description = "Некоректні параметри фільтрації")
    @GetMapping("/filter")
    public ResponseEntity<List<Order>> filterOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) OrderStatus status) {

        try {
            List<Order> orders = orderService.findByFilters(startDate, endDate, status);

            orders.forEach(order -> {
                if (Hibernate.isInitialized(order.getComments())) {
                    order.getComments().size();
                }
            });

            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
