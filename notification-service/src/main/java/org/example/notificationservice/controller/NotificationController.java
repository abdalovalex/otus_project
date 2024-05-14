package org.example.notificationservice.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.example.notificationservice.entity.Notification;
import org.example.notificationservice.exception.NotificationException;
import org.example.notificationservice.service.NotificationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;

    @GetMapping("{userId}")
    @PreAuthorize("authentication.principal.id == #userId")
    public ResponseEntity<List<Notification>> get(@PathVariable Integer userId) {
        try {
            return ResponseEntity.ok(service.get(userId));
        } catch (NotificationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
