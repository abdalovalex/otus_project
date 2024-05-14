package org.example.billingservice.mapper;

import org.example.billingservice.entity.Operation;
import org.example.billingservice.event.BankTransactionEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OperationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "state", ignore = true)
    Operation map(BankTransactionEvent dto);
}
