package com.hotel.admin_service.security.exception;

public class FileUploadFailedException extends RuntimeException{

    public FileUploadFailedException(String message){
        super(message);
    }
}
