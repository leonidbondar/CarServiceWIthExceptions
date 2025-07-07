package com.carserviceapp.service;

import com.carserviceapp.model.ServiceRequest;

/**
 * Interface for strategies to estimate the total time for a service request.
 */
public interface TimeEstimationStrategy {
    double estimate(ServiceRequest request);
}