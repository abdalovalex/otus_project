package org.example.notificationservice.service;

import java.util.List;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.entity.Notification;
import org.example.notificationservice.event.NotificationEvent;
import org.example.notificationservice.exception.NotificationException;
import org.example.notificationservice.mapper.NotificationMapper;
import org.example.notificationservice.repository.NotificationRepository;

import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationMapper notificationMapper;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void send(NotificationEvent notificationEvent) {
        notificationEvent.getUsers().forEach(userId -> {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setText(notificationEvent.getText());
            notificationRepository.save(notification);
        });
    }

    public List<Notification> get(Integer useId) throws NotificationException {
        List<Notification> notifications = notificationRepository.findByUserId(useId);
        return notifications;
    }
}
