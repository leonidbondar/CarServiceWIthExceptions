package com.carserviceapp.ui;

import com.carserviceapp.model.Customer;
import com.carserviceapp.model.ServiceRequest;
import com.carserviceapp.service.CarService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

/**
 * All Scanner I/O here; delegates to CarService.
 */
public class ConsoleUI {
    private static final Logger log = LogManager.getLogger(ConsoleUI.class);
    private final Scanner scanner = new Scanner(System.in);
    private final CarService svc = new CarService();

    public void run() {
        log.info("=== CarServiceApp started ===");
        boolean exit = false;
        while (!exit) {
            showMenu();
            switch (readInt("Option: ")) {
                case 1 -> addCar();
                case 2 -> removeCar();
                case 3 -> createRequest();
                case 4 -> listCars();
                case 5 -> listRequests();
                case 6 -> updateRequestStatus();
                case 7 -> removeRequest();
                case 8 -> exit = true;
                default -> log.warn("Unknown option");
            }
        }
        log.info("=== Goodbye ===");
    }

    private void showMenu() {
        log.info("""
            1) Add Car
            2) Remove Car
            3) Create Service Request
            4) List All Cars
            5) List Requests for Car
            6) Update Request Status
            7) Remove Service Request
            8) Exit
            """);
    }

    private void addCar() {
        try {
            String make  = readString("Make: ");
            String model = readString("Model: ");
            int year     = readInt("Year: ");
            String plate = readString("License Plate: ");
            String vin   = readString("VIN: ");
            svc.addCar(make, model, year, plate, vin);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }

    private void removeCar() {
        try {
            svc.removeCar(readString("License Plate: "));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }

    private void createRequest() {
        try {
            String fn    = readString("Customer first name: ");
            String ln    = readString("Customer last name: ");
            String email = readString("Customer email: ");
            String phone = readString("Customer phone: ");
            Customer cust= new Customer(fn, ln, email, phone);

            String plate = readString("License Plate: ");
            LocalDate date = LocalDate.parse(
                    readString("Request Date (YYYY-MM-DD): ")
            );
            String problem = readString("Problem description: ");
            svc.createServiceRequest(cust, plate, date, problem);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }

    private void listCars() {
        Collection<?> cars = svc.listAllCars();
        if (cars.isEmpty()) {
            log.info("No cars registered.");
        } else {
            cars.forEach(c -> log.info("{}", c));
        }
    }

    private void listRequests() {
        try {
            String plate = readString("License Plate: ");
            List<ServiceRequest> reqs = svc.listServiceRequests(plate);
            if (reqs.isEmpty()) {
                log.info("No requests for {}", plate);
            } else {
                reqs.forEach(r -> log.info("{}", r.getDisplayInfo()));
            }
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }

    private void updateRequestStatus() {
        try {
            String id = readString("Request ID: ");
            String st = readString(
                    "New status (PENDING|IN_PROGRESS|COMPLETED|CANCELLED): "
            );
            svc.updateServiceRequestStatus(id,
                    ServiceRequest.Status.valueOf(st));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }

    private void removeRequest() {
        try {
            svc.removeServiceRequest(readString("Request ID: "));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }

    private String readString(String prompt) {
        log.info(prompt);
        return scanner.nextLine().trim();
    }
    private int readInt(String prompt) {
        while (true) {
            try {
                log.info(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                log.error("Please enter a valid integer.");
            }
        }
    }
}
