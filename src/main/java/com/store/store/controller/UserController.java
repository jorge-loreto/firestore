package com.store.store.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.store.model.User;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        logger.info("Login attempt for user: " + user.getName());
        Map<String, Object> response = new HashMap<>();

        if ("jorge".equalsIgnoreCase(user.getName()) && "admin123".equals(user.getPasswdString())) {
            response.put("success", true);
            response.put("message", "Login successful");
            return ResponseEntity.ok(response); // ✅ 200 OK
        } else {
            response.put("success", false);
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // ✅ 401
        }
    }
}