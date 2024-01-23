package com.critical.stockservice.service;

import com.critical.stockservice.data.repository.StockRequestHistoryRepository;
import com.critical.stockservice.dtos.StockRequestHistoryDto;
import com.critical.stockservice.service.mapper.StockRequestHistoryMapper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockRequestHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(StockRequestHistoryService.class);

    private final StockRequestHistoryRepository repository;

    public StockRequestHistoryService(StockRequestHistoryRepository repository) {

        this.repository = repository;
    }

    public List<StockRequestHistoryDto> getAllStockRequests() {

        var stockRequest = this.repository.findAll();
        return StockRequestHistoryMapper.MAPPER.mapStockRequestsToStockRequestsDto(stockRequest);
    }

    public StockRequestHistoryDto getStockRequestByBookId(int bookId) {

        var stockRequest = this.repository.findStockRequestHistoryByBookId(bookId);
        if (null == stockRequest) {
            var message = "Stock book request not found with the book id: " + bookId;
            logger.warn(message);
            throw new EntityNotFoundException(message);
        }
        return StockRequestHistoryMapper.MAPPER.mapStockRequestToStockRequestDto(stockRequest);
    }
}