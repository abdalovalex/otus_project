package org.example.billingservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.billingservice.dto.CreateAccountDTO;
import org.example.billingservice.dto.IncomeMoneyDTO;
import org.example.billingservice.example.AccountException;
import org.example.billingservice.service.BillingService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final BillingService service;

    @PostMapping("/create")
    @PreAuthorize("authentication.principal.id == #createAccountDTO.userId")
    public ResponseEntity<Void> create(@RequestBody CreateAccountDTO createAccountDTO) {
        try {
            service.create(createAccountDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (AccountException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PatchMapping("/put")
    @PreAuthorize("authentication.principal.id == #incomeMoneyDTO.userId")
    public ResponseEntity<Void> put(@RequestBody IncomeMoneyDTO incomeMoneyDTO) {
        try {
            service.income(incomeMoneyDTO);
            return ResponseEntity.ok().build();
        } catch (AccountException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
