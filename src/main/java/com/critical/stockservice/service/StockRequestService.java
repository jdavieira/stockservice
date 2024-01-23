package com.critical.stockservice.service;

import com.critical.stockservice.data.entity.StockRequest;
import com.critical.stockservice.data.event.BookStockRequestEvent;
import com.critical.stockservice.data.repository.StockRepository;
import com.critical.stockservice.data.repository.StockRequestHistoryRepository;
import com.critical.stockservice.data.repository.StockRequestRepository;
import com.critical.stockservice.dtos.StockRequestDto;
import com.critical.stockservice.dtos.StockRequestFulfilled;
import com.critical.stockservice.message.kafka.CreateStockRequestProducer;
import com.critical.stockservice.service.mapper.StockRequestHistoryMapper;
import com.critical.stockservice.service.mapper.StockRequestMapper;
import com.critical.stockservice.util.exception.SaveEntityException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class StockRequestService {

    private static final Logger logger = LoggerFactory.getLogger(StockRequestService.class);

    private final StockRequestRepository repository;

    private final StockRepository stockRepository;

    private final StockRequestHistoryRepository stockRequestHistoryRepository;

    private final CreateStockRequestProducer producer;

    public StockRequestService(
            StockRequestRepository repository, StockRepository stockRepository, StockRequestHistoryRepository stockRequestHistoryRepository, CreateStockRequestProducer producer) {

        this.repository = repository;
        this.stockRepository = stockRepository;
        this.stockRequestHistoryRepository = stockRequestHistoryRepository;
        this.producer = producer;
    }

    public List<StockRequestDto> getAllStockRequests() {

        var stockRequest = this.repository.findAll();
        return StockRequestMapper.MAPPER.mapStockRequestsToStockRequestsDto(stockRequest);
    }

    public List<StockRequestDto> getStockRequestByBookId(int bookId) {

        var stockRequest = this.repository.findStockRequestByBookId(bookId);
        if (null == stockRequest) {
            var message = "Stock book request not found with the book id: " + bookId;
            logger.warn(message);
            throw new EntityNotFoundException(message);
        }
        return StockRequestMapper.MAPPER.mapStockRequestsToStockRequestsDto(stockRequest);
    }

    public void createStockRequest(StockRequestDto request) throws ExecutionException, JsonProcessingException, InterruptedException {

        var stock = this.stockRepository.findStockByBookId(request.bookId);
        if (null == stock) {
            var message = "Stock book not found with the book id: " + request.bookId;
            logger.warn(message);
            throw new EntityNotFoundException(message);
        }
        try {
            var stockRequest = new StockRequest();
            stockRequest.setStockRequested(request.stockRequest);
            stockRequest.setStock(stock);
            stockRequest.setUserEmail(request.userEmail);
            this.repository.save(stockRequest);
        } catch (Exception exception) {
            logger.error("Error occurred while creating the stock request information", exception);
            throw new SaveEntityException(exception.getMessage());
        }
        producer.sendStockRequestEvent(new BookStockRequestEvent(request.bookId, request.stockRequest, request.userEmail, UUID.randomUUID()));
    }

    @Transactional
    public void createStockRequestFulfilled(StockRequestFulfilled request) {

        var stockRequests = this.repository.findStockRequestByBookId(request.bookId);
        if (null == stockRequests || stockRequests.isEmpty()) {
            var message = "Stock book request not found with the book id: " + request.bookId;
            logger.warn(message);
            return;
        }
        try {
            for (var stockRequest : stockRequests) {
                var stockRequestHistory = StockRequestHistoryMapper.MAPPER.mapStockRequestToStockRequestHistory(stockRequest);
                this.stockRequestHistoryRepository.save(stockRequestHistory);
                this.repository.deleteById(stockRequest.getId());
            }
        } catch (Exception exception) {
            logger.error("Error occurred while creating the stock request history information", exception);
            throw new SaveEntityException(exception.getMessage());
        }
    }
}