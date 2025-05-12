package com.agriculture.resource_turnover.dto;

import com.agriculture.resource_turnover.models.OrderStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class OrderResponseDto {
    private Long id;
    private Long resourceId;
    private String resourceName;
    private ResourceInfoDto resourceInfo;
    private double quantity;
    private LocalDate deliveryDate;
    private OrderStatus status;
    private Long supplierId;
    private String supplierName;
    private SupplierInfoDto supplierInfo;
    private LocalDate creationDate;
    private LocalDate completionDate;
    private List<OrderCommentDto> comments;
}