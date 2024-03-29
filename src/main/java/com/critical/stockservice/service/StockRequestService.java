package com.critical.stockservice.service;

import com.critical.stockservice.data.entity.StockRequest;
import com.critical.stockservice.data.entity.StockRequestHistory;
import com.critical.stockservice.data.event.BookStockRequestEvent;
import com.critical.stockservice.data.event.StockUpdatedEvent;
import com.critical.stockservice.data.repository.StockRepository;
import com.critical.stockservice.data.repository.StockRequestHistoryRepository;
import com.critical.stockservice.data.repository.StockRequestRepository;
import com.critical.stockservice.dtos.StockRequestDto;
import com.critical.stockservice.dtos.StockRequestFulfilled;
import com.critical.stockservice.message.kafka.CreateStockRequestProducer;
import com.critical.stockservice.message.kafka.StockUpdatedProducer;
import com.critical.stockservice.service.mapper.StockRequestMapper;
import com.critical.stockservice.util.exception.AuthServiceException;
import com.critical.stockservice.util.exception.SaveEntityException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class StockRequestService {

    private static final String notificationServiceUrl = "http://localhost:8884/";

    private static final Logger logger = LoggerFactory.getLogger(StockRequestService.class);

    private final StockRequestRepository repository;

    private final StockRepository stockRepository;

    private final AuthService authService;

    private final StockRequestHistoryRepository stockRequestHistoryRepository;

    private final CreateStockRequestProducer producer;

    private final StockUpdatedProducer stockUpdatedProducer;

    private final JobScheduler jobScheduler;

    private final WebClient webClient;

    public StockRequestService(
            StockRequestRepository repository,
            StockRepository stockRepository,
            AuthService authService,
            StockRequestHistoryRepository stockRequestHistoryRepository,
            CreateStockRequestProducer producer,
            StockUpdatedProducer stockUpdatedProducer,
            JobScheduler jobScheduler,
            WebClient.Builder webClientBuilder) {

        this.repository = repository;
        this.stockRepository = stockRepository;
        this.authService = authService;
        this.stockRequestHistoryRepository = stockRequestHistoryRepository;
        this.producer = producer;
        this.stockUpdatedProducer = stockUpdatedProducer;
        this.jobScheduler = jobScheduler;
        this.webClient = webClientBuilder.baseUrl(notificationServiceUrl).build();
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
            stockRequest.setStockRequest(request.stockRequest);
            stockRequest.setStock(stock);
            stockRequest.setUserEmail(request.userEmail);
            this.repository.save(stockRequest);
        } catch (Exception exception) {
            logger.error("Error occurred while creating the stock request information", exception);
            throw new SaveEntityException(exception.getMessage());
        }
        producer.sendStockRequestEvent(new BookStockRequestEvent(request.bookId, request.stockRequest, request.userEmail));
    }

    @Transactional
    public void createStockRequestFulfilled(StockRequestFulfilled request) {

        var stockRequests = this.repository.findStockRequestByBookIdAndUserEmail(request.bookId, request.userEmail);
        if (null == stockRequests || stockRequests.isEmpty()) {
            var message = "Stock book request not found with the book id: " + request.bookId;
            logger.warn(message);
            return;
        }
        try {
            for (var stockRequest : stockRequests) {
                var stockRequestHistory = new StockRequestHistory(stockRequest.getStockRequest(), stockRequest.getUserEmail(), stockRequest.getStock());
                this.stockRequestHistoryRepository.save(stockRequestHistory);
                this.repository.deleteById(stockRequest.getId());
            }
        } catch (Exception exception) {
            logger.error("Error occurred while creating the stock request history information", exception);
            throw new SaveEntityException(exception.getMessage());
        }
    }

    public void sendNotificationRequest(StockUpdatedEvent stockUpdatedEvent) throws Exception {

        String accessToken = this.authService.getAccessToken();
        if (accessToken == null) {
            throw new AuthServiceException("It was not possible to get the access token from auth service.");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        var request = new StockRequestFulfilled(stockUpdatedEvent.getUserEmail(), stockUpdatedEvent.getBookId());

        ResponseEntity response = webClient.post()
                .uri("v1/api/notification/requestFulfilled")
                .header("Authorization", "Bearer " + accessToken)
                .body(BodyInserters.fromValue(request)).retrieve().toBodilessEntity().block();

        if (response == null || response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("error occurred while executing request fulfilled");
        }
    }

    public void sendNotification(int bookId, int stock) {

        var stockRequests = this.repository.findStockRequestByBookId(bookId);
        if (null == stockRequests || stockRequests.isEmpty()) {
            var message = "Stock book request not found with the book id: " + bookId;
            logger.warn(message);
            return;
        }
        try {
            this.sendNotificationForAllRequests(stockRequests, stock);
        } catch (Exception ex) {
            logger.warn("Error sending kafka message");
            this.jobScheduler.enqueue(() -> this.sendNotificationForAllRequests(stockRequests, stock));
        }
    }

    private void sendNotificationForAllRequests(List<StockRequest> stockRequests, int stock) throws Exception {

        for (var stockRequest : stockRequests) {
            var event = new StockUpdatedEvent(stockRequest.getStock().getBookId(), stock, stockRequest.getUserEmail());
            this.callNotificationService(event);
        }
    }

    private void callNotificationService(StockUpdatedEvent event) throws Exception {

        try {
            stockUpdatedProducer.sendNotificationStockEvent(event);
        } catch (Exception ex) {
            this.sendNotificationRequest(event);
        }
    }
}