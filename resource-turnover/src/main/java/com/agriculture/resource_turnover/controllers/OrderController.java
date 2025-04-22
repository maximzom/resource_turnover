package com.agriculture.resource_turnover.controllers;

import com.agriculture.resource_turnover.models.*;
import com.agriculture.resource_turnover.services.OrderService;
import com.agriculture.resource_turnover.services.ResourceService;
import com.agriculture.resource_turnover.services.SupplierService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final ResourceService resourceService;
    private final SupplierService supplierService;

    public OrderController(OrderService orderService,
                           ResourceService resourceService,
                           SupplierService supplierService) {
        this.orderService = orderService;
        this.resourceService = resourceService;
        this.supplierService = supplierService;
    }

    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "order_list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("resources", resourceService.findAllActive());
        return "order_form";
    }

    @PostMapping
    public String createOrder(@RequestParam Long resourceId,
                              @RequestParam double quantity,
                              @RequestParam String deliveryDate) {
        orderService.createOrder(resourceId, quantity, LocalDate.parse(deliveryDate));
        return "redirect:/orders";
    }

    @GetMapping("/{id}/assign")
    public String showAssignForm(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getOrderById(id));
        model.addAttribute("suppliers", supplierService.findAll());
        return "order_assign";
    }

    @PostMapping("/{id}/assign")
    public String assignSupplier(@PathVariable Long id,
                                 @RequestParam Long supplierId) {
        orderService.assignSupplier(id, supplierId);
        return "redirect:/orders";
    }

    @PostMapping("/{id}/complete")
    public String completeOrder(@PathVariable Long id) {
        orderService.completeOrder(id);
        return "redirect:/orders";
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return "redirect:/orders";
    }

    @GetMapping("/{id}/comments")
    public String showComments(@PathVariable Long id, Model model) {
        model.addAttribute("order", orderService.getOrderById(id));
        return "order_comments";
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @RequestParam String author,
                             @RequestParam String content) {
        orderService.addComment(id, author, content);
        return "redirect:/orders/" + id + "/comments";
    }
}