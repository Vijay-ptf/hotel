package com.hotel.admin_service.security.exception;

public class HotelTypeNotFoundException extends RuntimeException{
    public HotelTypeNotFoundException(String message){
        super(message);
    }
}
