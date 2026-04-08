package com.agriculture.resource_turnover.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Unit is mandatory")
    @Column(nullable = false, length = 20)
    private String unit;

    @NotBlank(message = "Type is mandatory")
    @Column(nullable = false, length = 50)
    private String type;

    @PositiveOrZero(message = "Quantity must be positive or zero")
    @Column(nullable = false, columnDefinition = "NUMERIC(19,2)")
    private BigDecimal quantity;

    @PositiveOrZero(message = "Price must be positive or zero")
    @Column(nullable = false, columnDefinition = "NUMERIC(19,2)")
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Resource(String name, String unit, String type,
                    BigDecimal quantity, BigDecimal price) {
        this.name = name;
        this.unit = unit;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
    }
}