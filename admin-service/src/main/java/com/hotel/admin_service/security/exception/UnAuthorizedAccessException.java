package com.hotel.admin_service.security.exception;

public class UnAuthorizedAccessException extends RuntimeException{

    public UnAuthorizedAccessException(String message){
        super(message);
    }
}
