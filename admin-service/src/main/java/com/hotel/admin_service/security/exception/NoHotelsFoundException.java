package com.hotel.admin_service.security.exception;

public class NoHotelsFoundException extends RuntimeException{

    public NoHotelsFoundException(String message){
        super(message);
    }
}
