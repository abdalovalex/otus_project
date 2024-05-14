package org.example.tradeservice.repository;

import java.util.List;
import java.util.Optional;

import org.example.tradeservice.entity.Trade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Integer> {
    Optional<Trade> findByPurchaseId(Integer purchaseId);

    @Query(value = """
            SELECT trade
            FROM Trade trade
            LEFT JOIN FETCH trade.bids bid
            WHERE trade.stateTrade = org.example.tradeservice.entity.StateTrade.PENDING
            AND trade.startDate <= CURRENT_TIMESTAMP
            """
    )
    List<Trade> findStartTrade();

    @Query(value = """
            SELECT trade
            FROM Trade trade
            LEFT JOIN FETCH trade.bids bid
            WHERE trade.stateTrade = org.example.tradeservice.entity.StateTrade.PROCESS
            AND trade.endDate <= CURRENT_TIMESTAMP
            """
    )
    List<Trade> findEndTrade();
}
