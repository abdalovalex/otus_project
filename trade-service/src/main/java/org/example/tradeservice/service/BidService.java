package org.example.tradeservice.service;

import java.time.LocalDateTime;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tradeservice.dto.BidDto;
import org.example.tradeservice.entity.Bid;
import org.example.tradeservice.entity.StateBid;
import org.example.tradeservice.entity.Trade;
import org.example.tradeservice.event.BidEvent;
import org.example.tradeservice.exception.TradeException;
import org.example.tradeservice.repository.BidRepository;
import org.example.tradeservice.repository.TradeRepository;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidService {
    private final StreamBridge streamBridge;
    private final TradeRepository tradeRepository;
    private final BidRepository bidRepository;
    private final TransactionTemplate transactionTemplate;

    @Transactional
    public void bidProcess(BidEvent bidEvent) {
        Bid bid;
        try {
            bid = bidRepository.findById(bidEvent.getId())
                    .orElseThrow(() -> new TradeException("ЦП не найдено"));
        } catch (TradeException e) {
            log.error(e.getMessage(), e);
            return;
        }

        if (bid.getDateTime().isAfter(bid.getTrade().getEndDate())) {
            bid.setState(StateBid.FAILED);
        } else {
            bid.setState(StateBid.COMPLETED);
        }
        bidRepository.save(bid);
    }

    public Integer createBid(BidDto bidDto) throws TradeException {
        LocalDateTime now = LocalDateTime.now();

        Trade trade = tradeRepository.findById(bidDto.getTradeId())
                .orElseThrow(() -> new TradeException("Торги не найдены"));

        if (now.isAfter(trade.getEndDate())) {
            throw new TradeException("Торги окончены");
        }

        Bid bid = transactionTemplate.execute(status -> {
            Bid newBid = new Bid();
            newBid.setTrade(trade);
            newBid.setUserId(bidDto.getUserId());
            newBid.setDemandId(bidDto.getDemandId());
            newBid.setPrice(bidDto.getPrice());
            newBid.setDateTime(now);

            return bidRepository.save(newBid);
        });

        if (bid != null) {
            BidEvent bidEvent = BidEvent.builder()
                    .id(bid.getId())
                    .userId(bid.getUserId())
                    .demandId(bid.getDemandId())
                    .tradeId(bid.getTrade().getId())
                    .price(bid.getPrice())
                    .build();
            streamBridge.send("bids", MessageBuilder.withPayload(bidEvent).build());
        } else {
            throw new TradeException("Не удалось сохранить ЦП");
        }

        return bid.getId();
    }
}
