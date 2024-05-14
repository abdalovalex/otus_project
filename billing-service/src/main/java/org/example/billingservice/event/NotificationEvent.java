package org.example.billingservice.event;

import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
public class NotificationEvent {
    private Set<Integer> users;
    private String text;
}
