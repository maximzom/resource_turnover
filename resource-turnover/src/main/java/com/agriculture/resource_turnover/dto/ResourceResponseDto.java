package com.agriculture.resource_turnover.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ResourceResponseDto {
    private Long id;
    private String name;
    private String unit;
    private String type;
    private BigDecimal quantity;
    private BigDecimal price;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
