package org.example.tradeservice.mapper;

import org.example.tradeservice.dto.TradeDto;
import org.example.tradeservice.entity.Trade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TradeMapper {
    @Mapping(target = ".", source = "trade")
    TradeDto map(Trade trade);
}
