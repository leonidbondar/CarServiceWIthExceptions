package com.carserviceapp.service;

import com.carserviceapp.exceptions.*;
import com.carserviceapp.model.Car;
import com.carserviceapp.model.Customer;
import com.carserviceapp.model.ServiceRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.*;

/**
 * Core operations: manage cars and service requests.
 */
public class CarService {
    private static final Logger log = LogManager.getLogger(CarService.class);

    // vehicles by licensePlate
    private final Map<String, Car> vehicles = new HashMap<>();
    // all requests by ID
    private final Map<String, ServiceRequest> requests = new HashMap<>();

    public void addCar(String make,
                       String model,
                       int year,
                       String licensePlate,
                       String vin) {
        if (vehicles.containsKey(licensePlate)) {
            throw new DuplicateVehicleException(licensePlate);
        }
        Car car = new Car(make, model, year, licensePlate, vin);
        vehicles.put(licensePlate, car);
        log.info("Added car: {}", car);
    }

    public void removeCar(String licensePlate) {
        Car removed = vehicles.remove(licensePlate);
        if (removed == null) {
            throw new VehicleNotFoundException(licensePlate);
        }
        // remove all associated requests
        removed.getServiceHistory()
                .forEach(req -> requests.remove(req.getRequestId()));
        log.info("Removed car: {}", removed);
    }

    public ServiceRequest createServiceRequest(Customer customer,
                                               String licensePlate,
                                               LocalDate date,
                                               String problemDesc) {
        if (customer == null) {
            throw new InvalidServiceRequestException("customer is null");
        }
        if (date == null || date.isAfter(LocalDate.now())) {
            throw new InvalidServiceRequestException("invalid date: " + date);
        }
        if (problemDesc == null || problemDesc.isBlank()) {
            throw new InvalidServiceRequestException("empty problem description");
        }

        Car car = vehicles.get(licensePlate);
        if (car == null) {
            throw new VehicleNotFoundException(licensePlate);
        }

        ServiceRequest req = new ServiceRequest(customer, car, date, problemDesc);
        String id = req.getRequestId();
        if (requests.containsKey(id)) {
            throw new DuplicateServiceRequestException(id);
        }
        requests.put(id, req);
        car.addServiceRequest(req);
        log.info("Created service request {} for {}", id, licensePlate);
        return req;
    }

    public List<ServiceRequest> listServiceRequests(String licensePlate) {
        Car car = vehicles.get(licensePlate);
        if (car == null) {
            throw new VehicleNotFoundException(licensePlate);
        }
        return car.getServiceHistory();
    }

    public void updateServiceRequestStatus(String requestId,
                                           ServiceRequest.Status newStatus) {
        ServiceRequest req = requests.get(requestId);
        if (req == null) {
            throw new ServiceRequestNotFoundException(requestId);
        }
        req.setStatus(newStatus);
        log.info("Request {} status updated to {}", requestId, newStatus);
    }

    public void removeServiceRequest(String requestId) {
        ServiceRequest req = requests.remove(requestId);
        if (req == null) {
            throw new ServiceRequestNotFoundException(requestId);
        }
        // ← now works on AbstractVehicle
        req.getVehicle().removeServiceRequest(req);
        log.info("Removed service request {}", requestId);
    }

    public Collection<Car> listAllCars() {
        return Collections.unmodifiableCollection(vehicles.values());
    }
}
