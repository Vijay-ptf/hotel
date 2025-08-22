package com.hotel.admin_service.security.exception;

public class AccountExistException extends RuntimeException{

    public AccountExistException(String message){
        super(message);
    }
}
