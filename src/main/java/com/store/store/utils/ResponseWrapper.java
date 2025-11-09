package com.store.store.utils;

import java.util.HashMap;
import java.util.Map;

public class ResponseWrapper {
    public static Map<String, Object> error(Exception e, String methodError, Map<String, Object> payload) {
        Map<String, Object> parsedJson = new HashMap<>();
        parsedJson.put("status", "error");
        parsedJson.put("message", "An error occurred while processing the request.");
        parsedJson.put("data", e.getMessage());
        parsedJson.put("error", methodError);
        Map<String, Object> error = new HashMap<>();
        error.put("code", 500);
        error.put("message", "Internal Server Error");
        error.put("details", "An unexpected error occurred.");

        error.put("payload", payload);

        return error;
    }

    public static Map<String, Object> successNoMessages() {
        Map<String, Object> parsedJson = new HashMap<>();
        parsedJson.put("status", "Ok");

        return parsedJson;
    }

    public static Map<String, Object> success(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    public static Map<String, Object> error(Exception e, String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        response.put("error", e.getMessage());
        response.put("data", data);
        return response;
    }
}