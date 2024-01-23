package com.critical.stockservice.message.kafka;

import com.critical.stockservice.data.event.StockUpdatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.annotation.Retry;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class StockUpdatedProducer {

    private static final Logger logger = LoggerFactory.getLogger(StockUpdatedProducer.class);

    private final MessageProducer producer;

    private final JobScheduler jobScheduler;

    @Value("${kafka.producer.topic.stock-updated-request}")
    private String stockUpdateRequestTopic;

    public StockUpdatedProducer(MessageProducer producer, JobScheduler jobScheduler) {

        this.producer = producer;
        this.jobScheduler = jobScheduler;
    }

    @Retry(name = "unstableKafkaService", fallbackMethod = "retrySendStockUpdatedEvent")
    public void sendStockUpdatedEvent(StockUpdatedEvent stockUpdatedEvent) throws JsonProcessingException, ExecutionException, InterruptedException {

        ObjectMapper objectMapper = new ObjectMapper();
        producer.sendMessage(stockUpdateRequestTopic, objectMapper.writeValueAsString(stockUpdatedEvent));
        logger.info("Stock request event message produced.");
    }

    public void retrySendStockUpdatedEvent(StockUpdatedEvent stockUpdatedEvent, Exception t) {

        logger.warn("Enqueuing StockUpdatedEvent message");
        try {
            this.createStockUpdateRequest(stockUpdatedEvent);
        } catch (Exception ex) {
            this.jobScheduler.enqueue(() -> this.createStockUpdateRequest(stockUpdatedEvent));
        }
    }

    private void createStockUpdateRequest(StockUpdatedEvent stockUpdatedEvent) {
        //TODO:: Add http request
    }
}