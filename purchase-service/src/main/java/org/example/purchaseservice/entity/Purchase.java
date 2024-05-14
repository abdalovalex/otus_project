package org.example.purchaseservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "purchase")
@Getter
@Setter
@NoArgsConstructor
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;
    private String title;
    private LocalDateTime publicationDateTime;
    private LocalDateTime demandEndDateTime;
    private BigDecimal price;
    private StatePurchase state = StatePurchase.DEMAND_START;

    @OneToMany(mappedBy = "purchase")
    private Set<Demand> demands;
}

