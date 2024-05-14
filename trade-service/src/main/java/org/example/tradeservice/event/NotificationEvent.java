package org.example.tradeservice.event;

import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationEvent {
    private Set<Integer> users;
    private String text;
}
