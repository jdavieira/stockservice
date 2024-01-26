package com.critical.stockservice.data.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "stock_request_history")
@Getter
@Setter
@NoArgsConstructor
public class StockRequestHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_request_history_id")
    private int id;

    @Column(name = "stock_requested")
    private int stockRequest;

    @Column(name = "user_email")
    private String userEmail;

    @OneToOne
    @JoinColumn(name = "stock_id", referencedColumnName = "stock_id")
    private Stock stock;

    @Column(name = "Created_On", nullable = false)
    @CreationTimestamp
    @Setter(AccessLevel.PROTECTED)
    private Instant createdOn;

    public StockRequestHistory(int stockRequest, String userEmail, Stock stock) {

        this.stockRequest = stockRequest;
        this.userEmail = userEmail;
        this.stock = stock;
    }
}