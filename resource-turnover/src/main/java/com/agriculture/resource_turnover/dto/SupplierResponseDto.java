package com.agriculture.resource_turnover.dto;

import lombok.Data;

@Data
public class SupplierResponseDto {
    private Long id;
    private String name;
    private String contactInfo;
    private boolean active;
}
