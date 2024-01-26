package com.critical.stockservice.data.repository;

import com.critical.stockservice.data.entity.StockRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRequestRepository extends JpaRepository<StockRequest, Integer> {

    @Query(value = "SELECT DISTINCT s FROM StockRequest s WHERE s.stock.bookId = :bookId AND s.userEmail = :email")
    List<StockRequest> findStockRequestByBookIdAndUserEmail(@Param("bookId") int bookId, @Param("email") String email);

    @Query(value = "SELECT s FROM StockRequest s WHERE s.stock.bookId = :bookId")
    List<StockRequest> findStockRequestByBookId(@Param("bookId") int bookId);
}