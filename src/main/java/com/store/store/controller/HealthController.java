package com.store.store.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
public class HealthController {

    @GetMapping("/pong")
    public String ping() {
        return "PING- PONG monday 14th July 2025 last deployment date";
    }
}