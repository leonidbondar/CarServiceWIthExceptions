package com.carserviceapp.exceptions;

public class DuplicateServiceRequestException extends RuntimeException {
    public DuplicateServiceRequestException(String requestId) {
        super("Service request with ID '" + requestId + "' already exists.");
    }
}