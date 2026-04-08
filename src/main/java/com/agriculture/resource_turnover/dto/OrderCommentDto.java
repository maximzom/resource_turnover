package com.agriculture.resource_turnover.dto;


import lombok.Data;
import java.time.LocalDate;

@Data
public class OrderCommentDto {
    private Long id;
    private String author;
    private String content;
    private LocalDate date;
    private Long orderId;
}
