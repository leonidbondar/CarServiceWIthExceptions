package com.carserviceapp.service;

import com.carserviceapp.model.ServiceRequest;

/**
 * Concrete implementation of a standard car repair process.
 */
public class StandardCarRepairProcess extends AbstractRepairProcess {
    @Override
    protected void diagnose(ServiceRequest request) {
        System.out.println("  [Standard Repair] Performing basic diagnostic checks for " + request.getVehicle().getMake() + " " + request.getVehicle().getModel());
        // Example: Add a diagnostic labor operation
        // serviceManager.addServiceOperationToRequest(request.getId(), new LaborOperation("Basic Diagnostic", 0.5, null, 0.5));
    }

    @Override
    protected void orderParts(ServiceRequest request) {
        System.out.println("  [Standard Repair] Checking for common parts needed for " + request.getProblemDescription().toLowerCase());
        // No specific parts ordered in this basic example
    }

    @Override
    protected void performActualRepair(ServiceRequest request) {
        System.out.println("  [Standard Repair] Executing basic repairs as described: " + request.getProblemDescription());
        // Example: Add a general repair labor operation
        // serviceManager.addServiceOperationToRequest(request.getId(), new LaborOperation("General Repair", 2.0, null, 2.0));
    }

    @Override
    protected void qualityCheck(ServiceRequest request) {
        System.out.println("  [Standard Repair] Performing final quality inspection and test drive.");
    }
}