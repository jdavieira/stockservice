package com.critical.stockservice.message.kafka;

import com.critical.stockservice.data.event.BookStockRequestEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.retry.annotation.Retry;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ExecutionException;

@Service
public class CreateStockRequestProducer {

    private static final Logger logger = LoggerFactory.getLogger(CreateStockRequestProducer.class);

    private final MessageProducer producer;

    private final JobScheduler jobScheduler;

    @Value("${kafka.producer.topic.create-stock-request}")
    private String createStockRequestTopic;

    public CreateStockRequestProducer(MessageProducer producer, JobScheduler jobScheduler) {

        this.producer = producer;
        this.jobScheduler = jobScheduler;
    }

    @Retry(name = "unstableKafkaService", fallbackMethod ="retrySendStockRequestEvent")
    @Job(name = "test", retries = 2)
    public void sendStockRequestEvent(BookStockRequestEvent bookStockRequestEvent) throws JsonProcessingException, ExecutionException, InterruptedException {

        ObjectMapper objectMapper = null;
        producer.sendMessage(createStockRequestTopic, objectMapper.writeValueAsString(bookStockRequestEvent));
        logger.info("Stock request event message produced.");
    }

    public void retrySendStockRequestEvent(BookStockRequestEvent bookStockRequestEvent, Exception t){
        logger.warn("Enqueuing BookStockRequestEvent message");

        jobScheduler.schedule(bookStockRequestEvent.messageId, Instant.now().plusSeconds(20), () -> this.sendStockRequestEvent(bookStockRequestEvent));
    }
}