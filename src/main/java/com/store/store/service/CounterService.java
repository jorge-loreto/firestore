package com.store.store.service;

import com.store.store.model.Counter;
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

        logger.info("RESET COUNTER VISITANTE");

        try {
            Counter newCounter = counterRepository.findById("global_counter");
            if (newCounter == null) {
                newCounter = new Counter();
                newCounter.setId("global_counter");
                newCounter.setValue(0L);
                counterRepository.save(newCounter);
                logger.info("Counter created and set to 0");
            } else {
                newCounter.setValue(newCounter.getValue() + 1);
                counterRepository.save(newCounter);
                logger.info("Counter increasing to 1");
            }
            return Mono.just(newCounter.getValue());
        } catch (Exception e) {
            logger.error("Thread interrupted: " + e.getMessage());
            return Mono.error(e);
        }
    }

    public Boolean resetCounter() {
        logger.info("RESET COUNTER VISITANTE");

        try {
            Counter newCounter = counterRepository.findById("global_counter");
            if (newCounter == null) {
                newCounter = new Counter();
                newCounter.setId("global_counter");
                newCounter.setValue(0L);
                counterRepository.save(newCounter);
                logger.info("Counter created and set to 0");
            } else {
                newCounter.setValue(0L);
                counterRepository.save(newCounter);
                logger.info("Counter reset to 0");
            }
            return true;
        } catch (Exception e) {
            logger.error("Thread interrupted: " + e.getMessage());
            return false;
        }

    }

    public Mono<Long> getCurrentValue() {
        logger.info("GET CURRENT VALUE OF COUNTER");
        Counter counter = counterRepository.findById("global_counter");
        if (counter == null)
            return Mono.just(0L);
        return Mono.just(counter.getValue());
    }
}
