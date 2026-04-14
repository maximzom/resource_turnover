package com.agriculture.resource_turnover.dto.mapper;

import com.agriculture.resource_turnover.dto.SupplierRequestDto;
import com.agriculture.resource_turnover.dto.SupplierResponseDto;
import com.agriculture.resource_turnover.models.Supplier;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    
    SupplierResponseDto toResponseDto(Supplier supplier);

    @Mapping(target = "id", ignore = true)
    Supplier toEntity(SupplierRequestDto dto);
}
