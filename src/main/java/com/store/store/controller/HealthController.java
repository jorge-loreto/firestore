package com.store.store.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
public class HealthController {

    @GetMapping("/pong")
    public String ping() {
        return "PING- PONG Sunday 17 May 2026 last deployment date";
    }
}