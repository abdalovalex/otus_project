package org.example.purchaseservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "demand", uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_purchase", columnNames = {"userId", "purchase"})
})
@Getter
@Setter
@NoArgsConstructor
public class Demand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;
    private String account;
    private BigDecimal amount;
    private LocalDateTime publicationDateTime;
    private StateDemand state = StateDemand.PENDING;
    private Boolean win = false;

    @ManyToOne
    private Purchase purchase;
}

