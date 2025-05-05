package com.agriculture.resource_turnover.controllers.api;

import com.agriculture.resource_turnover.models.Order;
import com.agriculture.resource_turnover.models.OrderStatus;
import com.agriculture.resource_turnover.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Report API", description = "Генерація звітів по замовленнях і виручці")
public class ReportApiController {
    private final ReportService reportService;

    public ReportApiController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "Отримати звіт за замовленнями", description = "Фільтрація замовлень за датою і статусом")
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getOrderReport(
            @Parameter(description = "Дата початку періоду (ГГГГ-ММ-ДД)", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Дата кінця періоду (ГГГГ-ММ-ДД)", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(description = "Статус замовлення (наприклад, NEW, COMPLETED, CANCELED)")
            @RequestParam(required = false) OrderStatus status) {

        return ResponseEntity.ok(
                reportService.generateOrderReport(startDate, endDate, status)
        );
    }

    @Operation(summary = "Отримати звіт по виручці", description = "Розрахунок загальної виручки за вказаний період")
    @GetMapping("/revenue")
    public ResponseEntity<Double> getRevenueReport(
            @Parameter(description = "Дата початку періоду (ГГГГ-ММ-ДД)", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Дата кінця періоду (ГГГГ-ММ-ДД)", example = "2026-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(
                reportService.calculateTotalRevenue(startDate, endDate)
        );
    }
}
