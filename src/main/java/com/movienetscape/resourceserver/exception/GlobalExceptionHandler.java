package com.movienetscape.resourceserver.exception;

import com.movienetscape.resourceserver.dto.AuthorisationDeniedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseStatusExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(TokenRevokedException.class)
    public Mono<ResponseEntity<AuthorisationDeniedResponse>> handleTokenRevokedException(TokenRevokedException ex) {
        logger.error("Token Revoked: {}", ex.getMessage(), ex);
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthorisationDeniedResponse.builder().message(ex.getMessage()).build()));
    }

}

