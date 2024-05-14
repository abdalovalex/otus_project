package org.example.purchaseservice.mapper;

import org.example.purchaseservice.dto.WinnerDto;
import org.example.purchaseservice.entity.Demand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WinnerMapper {
    @Mapping(target = "demandId", source = "id")
    @Mapping(target = "price", source = "amount")
    WinnerDto map(Demand demand);
}
