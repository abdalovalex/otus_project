package org.example.purchaseservice.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WinnerDto {
    private Integer demandId;
    private BigDecimal price;
}
