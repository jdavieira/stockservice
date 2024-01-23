package com.critical.stockservice.util.exception;

public class SaveEntityDataIntegrityViolationException extends RuntimeException
{
    public SaveEntityDataIntegrityViolationException(String message)
    {
        super(message);
    }
}