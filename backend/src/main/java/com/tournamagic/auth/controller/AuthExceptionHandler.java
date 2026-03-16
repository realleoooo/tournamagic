package com.tournamagic.auth.controller;

import com.tournamagic.auth.dto.AuthDtos;
import com.tournamagic.auth.service.AuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<AuthDtos.ErrorResponse> handleAuth(AuthException ex) {
        HttpStatus status = "Unauthorized".equals(ex.getMessage()) || "Session expired".equals(ex.getMessage())
                ? HttpStatus.UNAUTHORIZED
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(new AuthDtos.ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AuthDtos.ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Invalid request")
                .orElse("Invalid request");
        return ResponseEntity.badRequest().body(new AuthDtos.ErrorResponse(message));
    }
}
