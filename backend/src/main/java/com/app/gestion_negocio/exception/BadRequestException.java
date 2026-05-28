package com.app.gestion_negocio.exception;

public class BadRequestException extends  RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
