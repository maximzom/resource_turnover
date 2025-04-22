package com.agriculture.resource_turnover.models;

import lombok.Data;
import java.time.LocalDate;

@Data
public class OrderComment {
    private Long id;
    private String author;
    private String content;
    private LocalDate date = LocalDate.now();
}