package com.critical.stockservice.data.repository;

import com.critical.stockservice.data.entity.StockRequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRequestHistoryRepository extends JpaRepository<StockRequestHistory, Integer> {

    @Query(value = "SELECT DISTINCT s FROM StockRequestHistory s WHERE s.stock.bookId = :bookId")
    List<StockRequestHistory> findStockRequestHistoryByBookId(@Param("bookId") int bookId);
}