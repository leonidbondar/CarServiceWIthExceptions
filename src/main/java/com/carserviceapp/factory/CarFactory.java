package com.carserviceapp.factory;

import com.carserviceapp.model.AbstractVehicle;
import com.carserviceapp.model.Car;

/**
 * Concrete factory for creating Car objects.
 */
public class CarFactory implements VehicleFactory {
    @Override
    public AbstractVehicle createVehicle(String make, String model, int year, String licensePlate, String vin) {
        return new Car(make, model, year, licensePlate, vin);
    }
}