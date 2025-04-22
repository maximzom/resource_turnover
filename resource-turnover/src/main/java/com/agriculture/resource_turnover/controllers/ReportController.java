package com.agriculture.resource_turnover.controllers;

import com.agriculture.resource_turnover.models.OrderStatus;
import com.agriculture.resource_turnover.services.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/reports")
public class ReportController {
    private final OrderService orderService;

    public ReportController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String showReportForm(Model model) {
        model.addAttribute("allStatuses", OrderStatus.values());
        return "report_form";
    }

    @PostMapping
    public String generateReport(@RequestParam(required = false) LocalDate startDate,
                                 @RequestParam(required = false) LocalDate endDate,
                                 @RequestParam(required = false) OrderStatus status,
                                 Model model) {
        model.addAttribute("orders", orderService.findByFilters(startDate, endDate, status));
        return "report_view";
    }
}