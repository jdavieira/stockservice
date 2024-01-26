package com.critical.stockservice.service;

import com.critical.stockservice.data.entity.Stock;
import com.critical.stockservice.data.repository.StockRepository;
import com.critical.stockservice.dtos.BookRequest;
import com.critical.stockservice.dtos.StockDto;
import com.critical.stockservice.dtos.StockRequestDto;
import com.critical.stockservice.dtos.books.BookDto;
import com.critical.stockservice.message.kafka.CreateStockRequestProducer;
import com.critical.stockservice.message.kafka.StockUpdatedProducer;
import com.critical.stockservice.service.mapper.StockMapper;
import com.critical.stockservice.util.exception.AuthServiceException;
import com.critical.stockservice.util.exception.SaveEntityException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class StockService {

    private static final String catalogServiceUrl = "http://localhost:8880/";

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    private final StockRepository repository;

    private final AuthService authService;

    private final StockRequestService stockRequestService;


    private final JobScheduler jobScheduler;

    private final WebClient webClient;

    public StockService(StockRepository repository,
                        AuthService authService,
                        StockRequestService stockRequestService,
                        JobScheduler jobScheduler,
                        WebClient.Builder webClientBuilder) {

        this.repository = repository;
        this.authService = authService;
        this.stockRequestService = stockRequestService;
        this.jobScheduler = jobScheduler;
        this.webClient = webClientBuilder.baseUrl(catalogServiceUrl).build();
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

    public void buyBook(BookRequest request) {
        try{
            this.buyBookAsync(request);
        }catch (Exception ex){
            jobScheduler.enqueue(() -> this.buyBookAsync(request));
        }
    }

    @Async
    public void buyBookAsync(BookRequest request) throws Exception {

        CompletableFuture<BookDto> bookFuture = this.getBookInformation(request.bookId);
        CompletableFuture.completedFuture(bookFuture);

        var book = bookFuture.get();

        if (book.stockAvailable < request.stockRequest){

            logger.warn("No stock available to complete the request. Stock Request will be created");

            CompletableFuture.completedFuture(this.createStockRequest(request));
            return;
        }

        logger.warn("Book sold with success");
        CompletableFuture.completedFuture(this.sendStockRequest(request));
    }

    @Async
    protected CompletableFuture<Void> createStockRequest(BookRequest request) throws ExecutionException, JsonProcessingException, InterruptedException {

        var stockRequest = new StockRequestDto();

        stockRequest.setStockRequest(request.stockRequest);
        stockRequest.setBookId(request.bookId);
        stockRequest.setUserEmail(request.getUserEmail());

        stockRequestService.createStockRequest(stockRequest);

        return CompletableFuture.completedFuture(null);
    }

    @Async
    protected  CompletableFuture<Void> sendStockRequest(BookRequest request) throws Exception {

        String accessToken = this.authService.getAccessToken();
        if (accessToken == null) {
            throw new AuthServiceException("It was not possible to get the access token from auth service.");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);


        ResponseEntity getBookByIdResponse = webClient.put()
                .uri("v1/api/book/" + request.bookId+"/"+request.stockRequest)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve().toBodilessEntity().block();


        if (getBookByIdResponse.getStatusCode() != HttpStatus.NO_CONTENT) {
            throw new Exception("Status code from Catalog Service's response didn't indicate success: " + getBookByIdResponse.getStatusCode());
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async
    protected CompletableFuture<BookDto> getBookInformation(int bookId) throws Exception {

        String accessToken = this.authService.getAccessToken();
        if (accessToken == null) {
            throw new AuthServiceException("It was not possible to get the access token from auth service.");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        ResponseEntity<BookDto> getBookByIdResponse = webClient.get()
                .uri("v1/api/book/" + bookId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve().toEntity(BookDto.class).block();

        if (getBookByIdResponse.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Status code from Catalog Service's response didn't indicate success: " + getBookByIdResponse.getStatusCode());
        }

        return CompletableFuture.completedFuture(getBookByIdResponse.getBody());
    }
}