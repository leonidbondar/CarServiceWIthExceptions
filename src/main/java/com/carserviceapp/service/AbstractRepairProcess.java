package com.carserviceapp.service;

import com.carserviceapp.model.ServiceRequest;

/**
 * Abstract class defining a template method for a car repair process.
 * (Template Method Design Pattern)
 */
public abstract class AbstractRepairProcess {

    /**
     * The template method that defines the skeleton of the repair process.
     * Subclasses implement the specific steps.
     * @param request The ServiceRequest to execute the repair process on.
     */
    public final void executeRepair(ServiceRequest request) {
        System.out.println("Initiating repair process for Service Request " + request.getId() + "...");
        request.setStatus(ServiceRequest.Status.IN_PROGRESS);

        diagnose(request);
        orderParts(request);
        performActualRepair(request);
        qualityCheck(request);

        request.setStatus(ServiceRequest.Status.COMPLETED);
        System.out.println("Repair process for Service Request " + request.getId() + " completed.");
    }

    protected abstract void diagnose(ServiceRequest request);
    protected abstract void orderParts(ServiceRequest request);
    protected abstract void performActualRepair(ServiceRequest request);
    protected abstract void qualityCheck(ServiceRequest request);
}