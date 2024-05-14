package org.example.purchaseservice.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class PurchaseEvent {
    private Integer purchaseId;
    private Integer demandId;
}
