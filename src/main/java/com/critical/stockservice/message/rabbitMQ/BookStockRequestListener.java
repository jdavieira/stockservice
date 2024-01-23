package com.critical.stockservice.message.rabbitMQ;

import com.critical.stockservice.data.event.UpdateBookStockEvent;
import com.critical.stockservice.service.StockService;
import com.critical.stockservice.util.exception.SaveEntityException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Slf4j
public class BookStockRequestListener {

    @Autowired
    private StockService service;

    @Autowired
    private JobScheduler jobScheduler;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "catalog.queue-book-stock-request", durable = "true"),
            exchange = @Exchange(value = "catalog.queue.exchange", ignoreDeclarationExceptions = "true"),
            key = "catalog.queue.routing.key"))
    @Job(name = "Update Book Stock", retries = 5)
    public void onUpdateBookStock(UpdateBookStockEvent event) {
        log.info("Update Book Stock Event Received: " + event.bookId + " - " + event.stock);

        if(null == event){
            log.error("Update Book Stock Event Received is null");
            return;
        }

        try{
            service.upsertStock(event.bookId, event.stock);
        }catch (EntityNotFoundException | SaveEntityException exception){
            log.error("Error updating the stock to the book with the book id:"+ event.bookId + " and stock:" + event.stock );
            jobScheduler.schedule(
                    Instant.now().plusSeconds(60),
                    () -> service.upsertStock(event.bookId, event.stock));
        }

        log.info("Update Book Stock Event finished: " + event.bookId + " - " + event.stock);
    }
}