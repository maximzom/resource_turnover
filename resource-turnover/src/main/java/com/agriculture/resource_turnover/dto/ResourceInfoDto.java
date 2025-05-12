package com.agriculture.resource_turnover.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ResourceInfoDto {
    private Long id;
    private String name;
    private String unit;
    private String type;
    private BigDecimal price;
}

