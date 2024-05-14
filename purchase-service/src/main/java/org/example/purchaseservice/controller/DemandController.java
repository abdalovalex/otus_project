package org.example.purchaseservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.purchaseservice.dto.DemandDto;
import org.example.purchaseservice.exception.PurchaseException;
import org.example.purchaseservice.service.DemandService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/demand")
@RequiredArgsConstructor
public class DemandController {
    private final DemandService demandService;

    @PostMapping("/create")
    @PreAuthorize("authentication.principal.id == #demandDto.userId")
    public ResponseEntity<Integer> create(@RequestBody DemandDto demandDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(demandService.create(demandDto));
        } catch (PurchaseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
