package com.movienetscape.resourceserver.exception;


public class InternalServerErrorException extends  RuntimeException {

    private final String message;
    public InternalServerErrorException(String message) {
        this.message = message;
    }
}

