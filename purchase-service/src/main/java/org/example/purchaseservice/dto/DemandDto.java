package org.example.purchaseservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DemandDto {
    private Integer userId;
    private Integer purchaseId;
    private String account;
}
