package com.agriculture.resource_turnover.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ResourceRequestDto {
    private String name;
    private String unit;
    private String type;
    private BigDecimal quantity;
    private BigDecimal price;
    private boolean active = true;
}
