
package com.agriculture.resource_turnover.models;

import lombok.Data;

@Data
public class Resource {
    private Long id;
    private String name;
    private String unit;
    private String type;
    private double quantity;
    private double price;
    private boolean active = true;
}