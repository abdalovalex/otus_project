package org.example.notificationservice.event;

import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationEvent {
    private Set<Integer> users;
    private String text;
}
