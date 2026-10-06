package com.mercado.altiplano.exception;

import com.mercado.altiplano.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<LoginResponse> handleAuthenticationException(
            AuthenticationException exception
    ) {

        LoginResponse response = new LoginResponse(
                false,
                exception.getMessage(),
                null,
                null
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }
}