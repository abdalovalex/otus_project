package org.example.tradeservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.tradeservice.dto.BidDto;
import org.example.tradeservice.dto.TradeDto;
import org.example.tradeservice.exception.TradeException;
import org.example.tradeservice.service.BidService;
import org.example.tradeservice.service.TradeService;

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
@RequiredArgsConstructor
@RequestMapping("/trade")
public class TradeController {
    private final TradeService tradeService;
    private final BidService bidService;

    @GetMapping("/{purchaseId}")
    public ResponseEntity<TradeDto> get(@PathVariable Integer purchaseId) {
        try {
            return ResponseEntity.ok().body(tradeService.getTradeByPurchase(purchaseId));
        } catch (TradeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping("/bid")
    @PreAuthorize("authentication.principal.id == #bidDto.userId")
    public ResponseEntity<Integer> createBid(@RequestBody BidDto bidDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(bidService.createBid(bidDto));
        } catch (TradeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
