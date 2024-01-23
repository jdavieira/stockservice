package com.critical.stockservice.data.repository;

import com.critical.stockservice.data.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {

    Stock findStockByBookId(int bookId);
}