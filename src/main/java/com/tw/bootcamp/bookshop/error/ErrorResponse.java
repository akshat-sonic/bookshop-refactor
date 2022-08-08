package com.tw.bootcamp.bookshop.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public class ErrorResponse {
    private final HttpStatus status;
    private final String message;
    private final Map<String, String > errors;

    public ErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
        this.errors = new HashMap<>();
    }
}
