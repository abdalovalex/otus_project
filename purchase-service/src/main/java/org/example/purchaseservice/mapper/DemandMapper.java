package org.example.purchaseservice.mapper;

import org.example.purchaseservice.dto.DemandDto;
import org.example.purchaseservice.entity.Demand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DemandMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "publicationDateTime", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "purchase", ignore = true)
    @Mapping(target = "win", ignore = true)
    Demand map(DemandDto demandDto);
}
