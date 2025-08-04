package com.store.store.service;

import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

@Component
@Scope(value = WebApplicationContext.SCOPE_APPLICATION)
public class AppMetrics {

    private AtomicInteger totalRequests = new AtomicInteger(0);

    public void increment() {
        totalRequests.incrementAndGet();
    }

    public int getCount() {
        return totalRequests.get();
    }

    public void reset() {
        totalRequests.set(0);
    }

}
