package com.agriculture.resource_turnover.dto;

import lombok.Data;

@Data
public class SupplierRequestDto {
    private String name;
    private String contactInfo;
    private boolean active = true;
}
