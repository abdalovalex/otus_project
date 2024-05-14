package org.example.billingservice.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.billingservice.entity.State;

@Builder
@Getter
@Setter
public class DemandEvent {
    private Integer demandId;
    private State state;
}
