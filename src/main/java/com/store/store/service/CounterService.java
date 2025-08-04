package com.store.store.service;

import com.google.cloud.firestore.FieldValue;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import com.store.store.model.Counter;
import com.store.store.repository.CategoryRepository;
import com.store.store.repository.CounterRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CounterService {
    @Autowired
    CounterRepository counterRepository;

    private static final Logger logger = LogManager.getLogger(CounterService.class);

    public Mono<Long> incrementCounter() {
        logger.info("INCREMENTAR CONTADOR VISITANTE");

        return counterRepository.findById("global_counter")
                .switchIfEmpty(
                        // If not found, create a new counter
                        Mono.defer(() -> {
                            Counter newCounter = new Counter();
                            newCounter.setId("global_counter");
                            newCounter.setValue(0L);
                            return counterRepository.save(newCounter);
                        }))
                .flatMap(counter -> {
                    counter.setValue(counter.getValue() + 1);
                    return counterRepository.save(counter);
                })
                .map(counter -> {
                    logger.info("Counter incremented successfully: {}", counter.getValue());
                    return counter.getValue();
                })
                .doOnError(e -> logger.error("Error incrementing counter: {}", e.getMessage()));
    }

    public Mono<Void> resetCounter() {
        logger.info("RESET COUNTER VISITANTE");

        return counterRepository.findById("global_counter")
                .flatMap(counter -> {
                    counter.setValue(0L);
                    return counterRepository.save(counter);
                })
                .doOnSuccess(saved -> logger.info("Counter reset successfully to 0"))
                .doOnError(e -> logger.error("Error resetting counter: " + e.getMessage()))
                .then();
    }

    public Mono<Long> getCurrentValue() {
        return counterRepository.findById("global_counter")
                .map(counter -> {
                    logger.info("Current counter value: {}", counter.getValue());
                    return counter.getValue();
                })
                .doOnError(e -> logger.error("Error retrieving current counter value: " + e.getMessage()));
    }
}
