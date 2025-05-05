package com.agriculture.resource_turnover.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OrderRequestDto {
    private Long resourceId;
    private double quantity;
    private LocalDate deliveryDate;
}