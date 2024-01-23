package com.critical.stockservice.util.exception;

public class EntityNullException extends RuntimeException
{
    public EntityNullException(String message){
        super(message);
    }
}