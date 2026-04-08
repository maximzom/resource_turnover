package com.agriculture.resource_turnover.dto.mapper;

import com.agriculture.resource_turnover.dto.*;
import com.agriculture.resource_turnover.models.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponseDto toResponseDto(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());

        if (order.getResource() != null) {
            dto.setResourceInfo(toResourceInfoDto(order.getResource()));
        }

        dto.setQuantity(order.getQuantity());
        dto.setDeliveryDate(order.getDeliveryDate());
        dto.setStatus(order.getStatus());
        dto.setCreationDate(order.getCreationDate());
        dto.setCompletionDate(order.getCompletionDate());

        if (order.getSupplier() != null) {
            dto.setSupplierInfo(toSupplierInfoDto(order.getSupplier()));
        }

        if (order.getComments() != null && !order.getComments().isEmpty()) {
            dto.setComments(toCommentDtos(order.getComments()));
        }

        return dto;
    }

    private ResourceInfoDto toResourceInfoDto(Resource resource) {
        if (resource == null) {
            return null;
        }

        ResourceInfoDto dto = new ResourceInfoDto();
        dto.setId(resource.getId());
        dto.setName(resource.getName());
        dto.setUnit(resource.getUnit());
        dto.setType(resource.getType());
        dto.setPrice(resource.getPrice());
        return dto;
    }

    private SupplierInfoDto toSupplierInfoDto(Supplier supplier) {
        if (supplier == null) {
            return null;
        }

        SupplierInfoDto dto = new SupplierInfoDto();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setContactInfo(supplier.getContactInfo());
        return dto;
    }

    private List<OrderCommentDto> toCommentDtos(List<OrderComment> comments) {
        return comments.stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }

    public OrderCommentDto toCommentDto(OrderComment comment) {
        if (comment == null) {
            return null;
        }

        OrderCommentDto dto = new OrderCommentDto();
        dto.setId(comment.getId());
        dto.setAuthor(comment.getAuthor());
        dto.setContent(comment.getContent());
        dto.setDate(comment.getDate());

        if (comment.getOrder() != null) {
            dto.setOrderId(comment.getOrder().getId());
        }

        return dto;
    }

    public Order toEntity(OrderRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Order order = new Order();
        order.setQuantity(dto.getQuantity());
        order.setDeliveryDate(dto.getDeliveryDate());
        return order;
    }
}