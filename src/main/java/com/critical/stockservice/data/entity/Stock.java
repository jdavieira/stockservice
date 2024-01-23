package com.critical.stockservice.data.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "stock")
@Getter
@Setter
@NoArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private int id;

    @Column(name = "book_id", unique = true)
    private int bookId;

    @Column(name = "stock")
    private int stock;

    @Column(name = "Created_On", nullable = false)
    @CreationTimestamp
    @Setter(AccessLevel.PROTECTED)
    private Instant createdOn;

    @Column(name = "Updated_On")
    @Setter(AccessLevel.PROTECTED)
    @UpdateTimestamp
    private Instant updatedOn;
}