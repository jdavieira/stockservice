package com.critical.stockservice.controller;

import com.critical.stockservice.dtos.StockRequestDto;
import com.critical.stockservice.dtos.StockRequestFulfilled;
import com.critical.stockservice.dtos.error.ErrorResponse;
import com.critical.stockservice.service.StockRequestService;
import com.critical.stockservice.util.exception.EntityNullException;
import com.critical.stockservice.util.exception.SaveEntityDataIntegrityViolationException;
import com.critical.stockservice.util.exception.SaveEntityException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Tag(name = "StockRequest", description = "Stock Request management API")
@RestController
@RequestMapping("/v1/api")
public class StockRequestController {

    private static final Logger logger = LoggerFactory.getLogger(StockRequestHistoryController.class);

    private final StockRequestService service;

    public StockRequestController(StockRequestService service) {

        this.service = service;
    }

    @Operation(summary = "Retrieve a Stock Request by book id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(array =
            @ArraySchema(schema = @Schema(implementation = StockRequestDto.class)), mediaType = "application/json") }),
            @ApiResponse(responseCode = "403", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}) })
    @GetMapping("/stockRequest/{bookId}")
    public ResponseEntity getStockRequestByBookId(@PathVariable("bookId") int bookId) {
        try {
            return ResponseEntity.ok(this.service.getStockRequestByBookId(bookId));
        }catch (EntityNotFoundException exception){
            logger.warn(exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage()));
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_read')")
    @Operation(summary = "Retrieve all stock requests")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = { @Content(array =
            @ArraySchema(schema = @Schema(implementation = StockRequestDto.class)), mediaType = "application/json") }),
            @ApiResponse(responseCode = "403", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})})
    @GetMapping("/stockRequests")
    public ResponseEntity<List<StockRequestDto>> getAllStockRequests() {
        return ResponseEntity.ok(this.service.getAllStockRequests());
    }

    @PreAuthorize("hasAuthority('SCOPE_write')")
    @Operation(summary = "Create stock request")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})})
    @PostMapping("/stockRequest")
    public ResponseEntity createStockRequests(@Valid @RequestBody StockRequestDto request) {
        try {
            this.service.createStockRequest(request);
            return ResponseEntity.ok().build();
        } catch (SaveEntityException | EntityNullException exception){
            logger.warn(exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
        } catch (SaveEntityDataIntegrityViolationException
                 | JsonProcessingException | ExecutionException | InterruptedException exception){
            logger.warn(exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
        }
    }

    @PreAuthorize("hasAuthority('SCOPE_write')")
    @Operation(summary = "Stock Request fulfilled request")
    @ApiResponses({
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "403", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", content =
                    {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))})})
    @PostMapping("/stockRequestFulfilled")
    public ResponseEntity createStockRequestFulfilled(@Valid @RequestBody StockRequestFulfilled request) {
        try {
            this.service.createStockRequestFulfilled(request);
            return ResponseEntity.ok().build();
        } catch (SaveEntityException | EntityNullException exception){
            logger.warn(exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
        } catch (SaveEntityDataIntegrityViolationException exception){
            logger.warn(exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
        }
    }
}