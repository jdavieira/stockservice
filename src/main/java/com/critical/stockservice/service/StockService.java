package com.critical.stockservice.service;

import com.critical.stockservice.data.entity.Stock;
import com.critical.stockservice.data.repository.StockRepository;
import com.critical.stockservice.dtos.StockDto;
import com.critical.stockservice.service.mapper.StockMapper;
import com.critical.stockservice.util.exception.SaveEntityException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    private final StockRepository repository;

    public StockService(StockRepository repository) {

        this.repository = repository;
    }

    public List<StockDto> getAllStocks() {

        var stockBooks = this.repository.findAll();
        return StockMapper.MAPPER.mapStocksToStocksDto(stockBooks);
    }

    public StockDto getStockByBookId(int bookId) {

        var stockBook = this.repository.findStockByBookId(bookId);
        if (null == stockBook) {
            var message = "Stock book not found with the book id: " + bookId;
            logger.warn(message);
            throw new EntityNotFoundException(message);
        }
        return StockMapper.MAPPER.mapStockToStockDto(stockBook);
    }

    public void upsertStock(int bookId, int stock) {

        var stockBook = this.repository.findStockByBookId(bookId);

        if (null == stockBook) {
            stockBook = new Stock();
            stockBook.setBookId(bookId);
        }

        stockBook.setStock(stock);

        try {
            this.repository.save(stockBook);
            logger.info("stock saved with success.");
        } catch (Exception exception) {
            logger.error("Error occurred while upserting the book information", exception);
            throw new SaveEntityException(exception.getMessage());
        }
    }
}