package com.carserviceapp.service;

import com.carserviceapp.model.ServiceRequest;

/**
 * Concrete implementation of a more complex engine repair process.
 */
public class ComplexEngineRepairProcess extends AbstractRepairProcess {
    @Override
    protected void diagnose(ServiceRequest request) {
        System.out.println("  [Complex Engine Repair] Conducting in-depth engine diagnostics for " + request.getVehicle().getMake() + " " + request.getVehicle().getModel());
        System.out.println("    - Running ECU diagnostics, compression test, and visual inspection.");
    }

    @Override
    protected void orderParts(ServiceRequest request) {
        System.out.println("  [Complex Engine Repair] Ordering specialized engine parts if required based on diagnostics.");
        System.out.println("    - Checking availability of pistons, valves, and gaskets.");
    }

    @Override
    protected void performActualRepair(ServiceRequest request) {
        System.out.println("  [Complex Engine Repair] Disassembling and repairing the engine as needed.");
        System.out.println("    - Performing cylinder head work, block repairs, and component replacement.");
    }

    @Override
    protected void qualityCheck(ServiceRequest request) {
        System.out.println("  [Complex Engine Repair] Conducting post-repair engine performance tests and extended road test.");
        System.out.println("    - Monitoring engine parameters for stability and efficiency.");
    }
}