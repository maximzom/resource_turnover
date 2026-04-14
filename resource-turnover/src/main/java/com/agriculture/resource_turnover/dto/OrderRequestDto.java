package com.agriculture.resource_turnover.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OrderRequestDto {
    private Long resourceId;
    private BigDecimal quantity;
    private LocalDate deliveryDate;
}