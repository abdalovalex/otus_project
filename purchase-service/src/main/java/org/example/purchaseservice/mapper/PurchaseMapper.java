package org.example.purchaseservice.mapper;

import org.example.purchaseservice.dto.PurchaseDto;
import org.example.purchaseservice.entity.Purchase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PurchaseMapper {
    @Mapping(target = ".", source = "purchase")
    PurchaseDto map(Purchase purchase);
}
