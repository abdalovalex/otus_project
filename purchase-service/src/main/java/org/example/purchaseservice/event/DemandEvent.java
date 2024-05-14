package org.example.purchaseservice.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class DemandEvent {
    private Integer demandId;
    private State state;
}
