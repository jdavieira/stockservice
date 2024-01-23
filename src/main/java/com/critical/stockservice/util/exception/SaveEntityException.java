package com.critical.stockservice.util.exception;

public class SaveEntityException extends RuntimeException
{
    public SaveEntityException(String message){
        super(message);
    }
}