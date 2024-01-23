package com.critical.stockservice.message.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class MessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public SendResult<String, String> sendMessage(String topic, String message) throws ExecutionException, InterruptedException {

       CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);
        var completableFutureResult = future.whenCompleteAsync((result, ex) -> {
            if (ex == null) {
                logger.info("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                logger.error("Unable to send message=[" + message + "] due to : " + ex.getMessage());
            }
        });
        return completableFutureResult.get();
    }
}