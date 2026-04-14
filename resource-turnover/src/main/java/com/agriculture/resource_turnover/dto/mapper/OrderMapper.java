package com.agriculture.resource_turnover.dto.mapper;

import com.agriculture.resource_turnover.dto.*;
import com.agriculture.resource_turnover.models.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "resourceName", source = "resource.name")
    @Mapping(target = "resourceInfo", source = "resource")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "supplierInfo", source = "supplier")
    OrderResponseDto toResponseDto(Order order);

    ResourceInfoDto toResourceInfoDto(Resource resource);
    SupplierInfoDto toSupplierInfoDto(Supplier supplier);
    OrderCommentDto toCommentDto(OrderComment comment);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "completionDate", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "resource", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    Order toEntity(OrderRequestDto dto);
}