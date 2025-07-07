package com.carserviceapp.service;

import com.carserviceapp.model.AbstractServiceOperation;
import com.carserviceapp.model.ServiceRequest;

/**
 * Concrete strategy for simple time estimation (sum of estimated durations).
 */
public class SimpleTimeEstimationStrategy implements TimeEstimationStrategy {
    @Override
    public double estimate(ServiceRequest request) {
        return request.getOperations().stream()
                .mapToDouble(AbstractServiceOperation::estimateTime)
                .sum();
    }
}