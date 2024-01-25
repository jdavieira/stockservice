package com.critical.stockservice.util.exception;

public class AuthServiceException extends RuntimeException
{
    public AuthServiceException(String message){
        super(message);
    }
}