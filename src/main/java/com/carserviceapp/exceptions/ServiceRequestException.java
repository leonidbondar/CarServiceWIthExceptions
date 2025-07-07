package com.carserviceapp.exceptions;

public class ServiceRequestException extends RuntimeException {
    public ServiceRequestException(String msg) {
        super("Failed to create service request: " + msg);
    }
}