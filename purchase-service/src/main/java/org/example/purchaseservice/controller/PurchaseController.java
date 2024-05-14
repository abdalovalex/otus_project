package org.example.purchaseservice.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.example.purchaseservice.dto.PurchaseDto;
import org.example.purchaseservice.dto.UserDto;
import org.example.purchaseservice.dto.WinnerDto;
import org.example.purchaseservice.exception.PurchaseException;
import org.example.purchaseservice.service.PurchaseService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/purchase")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    @PostMapping("/create")
    @PreAuthorize("authentication.principal.id == #purchaseDto.userId")
    public ResponseEntity<Integer> create(@RequestBody PurchaseDto purchaseDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(purchaseService.create(purchaseDto));
        } catch (PurchaseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/{purchaseId}")
    public ResponseEntity<PurchaseDto> get(@PathVariable Integer purchaseId) {
        try {
            return ResponseEntity.ok(purchaseService.get(purchaseId));
        } catch (PurchaseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/suppliers/{purchaseId}")
    public ResponseEntity<List<UserDto>> suppliers(@PathVariable Integer purchaseId) {
        try {
            return ResponseEntity.ok(purchaseService.suppliers(purchaseId));
        } catch (PurchaseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/winner/{purchaseId}")
    public ResponseEntity<WinnerDto> getWinner(@PathVariable Integer purchaseId) {
        try {
            return ResponseEntity.ok().body(purchaseService.getWinner(purchaseId));
        } catch (PurchaseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
