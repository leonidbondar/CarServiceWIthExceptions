package com.carserviceapp.factory;

import com.carserviceapp.model.AbstractVehicle;

/**
 * Abstract factory for creating different types of vehicles.
 */
public interface VehicleFactory {
    AbstractVehicle createVehicle(String make, String model, int year, String licensePlate, String vin);
}