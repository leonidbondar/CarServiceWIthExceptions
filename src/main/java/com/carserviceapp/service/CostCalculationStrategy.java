package com.carserviceapp.service;

import com.carserviceapp.model.ServiceRequest;

/**
 * Interface for strategies to calculate the total cost of a service request.
 */
public interface CostCalculationStrategy {
    double calculate(ServiceRequest request);
}