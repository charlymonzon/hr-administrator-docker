package com.hr.administrator.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, String keyType, String keyValue) {
        super(String.format("%s with %s %s was not found",resource, keyType, keyValue));
    }
}
