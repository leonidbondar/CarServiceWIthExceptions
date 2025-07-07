package com.carserviceapp.service;

import com.carserviceapp.model.AbstractServiceOperation;
import com.carserviceapp.model.ServiceRequest;

/**
 * Concrete strategy for calculating cost based on hourly rates of labor operations.
 * For demonstration, this only calculates labor costs. A more complex strategy would combine parts and labor.
 */
public class HourlyCostStrategy implements CostCalculationStrategy {
    @Override
    public double calculate(ServiceRequest request) {
        // Sums up the cost of all operations (which include labor and parts)
        return request.getOperations().stream()
                .mapToDouble(AbstractServiceOperation::calculateCost)
                .sum();
    }
}