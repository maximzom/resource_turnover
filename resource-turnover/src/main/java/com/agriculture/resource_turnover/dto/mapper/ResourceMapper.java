package com.agriculture.resource_turnover.dto.mapper;

import com.agriculture.resource_turnover.dto.ResourceRequestDto;
import com.agriculture.resource_turnover.dto.ResourceResponseDto;
import com.agriculture.resource_turnover.models.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {
    
    ResourceResponseDto toResponseDto(Resource resource);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Resource toEntity(ResourceRequestDto dto);
}
