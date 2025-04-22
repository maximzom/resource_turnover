package com.agriculture.resource_turnover.models;

import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class Order {
    private Long id;
    private Resource resource;
    private double quantity;
    private LocalDate deliveryDate;
    private OrderStatus status;
    private Supplier supplier;
    private LocalDate creationDate = LocalDate.now();
    private LocalDate completionDate;
    private List<OrderComment> comments = new ArrayList<>();
}
