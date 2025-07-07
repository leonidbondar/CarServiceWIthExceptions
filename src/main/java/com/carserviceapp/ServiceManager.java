package com.carserviceapp;

import com.carserviceapp.factory.*;
import com.carserviceapp.interfaces.ReportGenerator;
import com.carserviceapp.model.*;
import com.carserviceapp.service.ComplexEngineRepairProcess;
import com.carserviceapp.service.FinancialReport;
import com.carserviceapp.service.ServicePerformanceReport;
import com.carserviceapp.service.StandardCarRepairProcess;
import com.carserviceapp.util.ReportWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages all aspects of the car service application,
 * including customers, vehicles, service requests, invoices, payments,
 * appointments, and insurance claims.
 */
public class ServiceManager {
    private static final Logger logger = LogManager.getLogger(ServiceManager.class);

    private final Map<String, Customer> customers = new HashMap<>();

    public Map<String, AbstractVehicle> getVehicles() {
        return vehicles;
    }

    private final Map<String, AbstractVehicle> vehicles = new HashMap<>();
    private final Map<String, Technician> technicians = new HashMap<>();
    private final Map<String, Part> parts = new HashMap<>();

    public Map<String, ServiceRequest> getServiceRequests() {
        return serviceRequests;
    }

    private final Map<String, ServiceRequest> serviceRequests = new HashMap<>();
    private final Map<String, Invoice> invoices = new HashMap<>();
    private final Map<String, Payment> payments = new HashMap<>();
    private final Map<String, Appointment> appointments = new HashMap<>();
    private final Map<String, InsuranceClaim> insuranceClaims = new HashMap<>();
    private final Map<String, List<AbstractVehicle>> customerVehicles = new HashMap<>();

    private final VehicleFactory carFactory = new CarFactory();
    private final ServiceOperationFactory laborFactory = new LaborOperationFactory();
    private final ServiceOperationFactory partInstallationFactory = new PartInstallationFactory();

    public ServiceManager() {
        seedData();
    }

    private void seedData() {
        Customer cust1 = new Customer("Alice", "Smith", "alice@example.com", "123-456-7890");
        Customer cust2 = new Customer("Bob", "Johnson", "bob@example.com", "098-765-4321");
        addCustomer(cust1);
        addCustomer(cust2);

        AbstractVehicle car1 = carFactory.createVehicle("Toyota", "Camry", 2018, "ABC-123", "VIN1234567890");
        AbstractVehicle car2 = carFactory.createVehicle("Honda", "Civic", 2020, "XYZ-789", "VIN0987654321");
        AbstractVehicle car3 = carFactory.createVehicle("Ford", "Focus", 2015, "DEF-456", "VIN9876543210");

        addVehicle(car1, cust1);
        addVehicle(car2, cust2);
        addVehicle(car3, cust1);

        Technician tech1 = new Technician("Jane", "Doe", "jane@example.com", "555-1111", "Certified Mechanic", 50.0);
        Technician tech2 = new Technician("Mike", "Brown", "mike@example.com", "555-2222", "Master Technician", 75.0);
        addTechnician(tech1);
        addTechnician(tech2);

        Part oilFilter = new Part("Oil Filter", 15.0, 50);
        Part brakePads = new Part("Brake Pads", 75.0, 20);
        Part sparkPlug = new Part("Spark Plug", 10.0, 100);
        addPart(oilFilter);
        addPart(brakePads);
        addPart(sparkPlug);

        ServiceRequest req1 = createServiceRequest(cust1, car1, "Engine noise and oil change");
        addServiceOperationToRequest(req1.getId(), createLaborOperation("Diagnostic", 1.0, tech1, 1.0));
        addServiceOperationToRequest(req1.getId(), createPartInstallation("Oil Filter Replacement", 0.5, oilFilter, 1));
        addServiceOperationToRequest(req1.getId(), createLaborOperation("Oil Change Labor", 0.5, tech1, 0.5));
        updateServiceRequestStatus(req1.getId(), ServiceRequest.Status.COMPLETED);

        // Execute a repair process for this service
        ComplexEngineRepairProcess repairProcess = new ComplexEngineRepairProcess();
        repairProcess.runFullProcess(req1);

        StandardCarRepairProcess standardRepair = new StandardCarRepairProcess(this);
        standardRepair.executeRepair(req1);

        Invoice inv1 = generateInvoice(req1);
        recordPayment(inv1, inv1.getAmount(), Payment.PaymentMethod.CASH);

        scheduleAppointment(cust1, car1, req1, LocalDateTime.now().plusDays(2).withHour(10).withMinute(0), "Post-service checkup");
        fileInsuranceClaim(req1, "AutoInsure Inc.", "POL987654", inv1.getAmount() * 0.8);
    }

    public void addCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
        customerVehicles.putIfAbsent(customer.getId(), new ArrayList<>());
        logger.info("Added customer: {}", customer.getDisplayInfo());
    }

    public void addVehicle(AbstractVehicle vehicle, Customer owner) {
        vehicles.put(vehicle.getId(), vehicle);
        if (owner != null) {
            customerVehicles.computeIfAbsent(owner.getId(), k -> new ArrayList<>()).add(vehicle);
        }
        logger.info("Added vehicle: {} for customer: {}", vehicle.getDisplayInfo(), owner.getDisplayInfo());
    }

    public void addTechnician(Technician technician) {
        technicians.put(technician.getId(), technician);
        logger.info("Added technician: {}", technician.getDisplayInfo());
    }

    public void addPart(Part part) {
        parts.put(part.getId(), part);
        logger.info("Added part: {}", part.getDisplayInfo());
    }

    public ServiceRequest createServiceRequest(Customer customer, AbstractVehicle vehicle, String problemDescription) {
        ServiceRequest request = new ServiceRequest(customer, vehicle, LocalDate.now(), problemDescription);
        serviceRequests.put(request.getId(), request);
        logger.info("Created service request: {}", request.getId());
        return request;
    }

    public void updateServiceRequestStatus(String requestId, ServiceRequest.Status newStatus) {
        ServiceRequest request = serviceRequests.get(requestId);
        if (request != null) {
            request.setStatus(newStatus);
            logger.info("Updated service request {} status to: {}", requestId, newStatus);
        } else {
            logger.error("Service request not found: {}", requestId);
        }
    }

    public void addServiceOperationToRequest(String requestId, AbstractServiceOperation operation) {
        ServiceRequest request = serviceRequests.get(requestId);
        if (request != null) {
            request.addOperation(operation);
            logger.info("Added operation to request {}: {}", requestId, operation.getDescription());
        } else {
            logger.warn("Request not found: {}", requestId);
        }
    }

    public Invoice generateInvoice(ServiceRequest request) {
        Invoice invoice = new Invoice(request);
        invoices.put(invoice.getId(), invoice);
        logger.info("Generated invoice {} for request {}", invoice.getId(), request.getId());
        return invoice;
    }

    public Payment recordPayment(Invoice invoice, double amount, Payment.PaymentMethod method) {
        Payment payment = new Payment(invoice, amount, LocalDate.now(), method);
        payments.put(payment.getId(), payment);
        logger.info("Recorded payment {} for invoice {}", payment.getId(), invoice.getId());
        return payment;
    }

    public Appointment scheduleAppointment(Customer customer, AbstractVehicle vehicle, ServiceRequest serviceRequest, LocalDateTime scheduledTime, String purpose) {
        Appointment appointment = new Appointment(customer, vehicle, serviceRequest, scheduledTime, purpose);
        appointments.put(appointment.getId(), appointment);
        logger.info("Scheduled appointment {} for request {}", appointment.getId(), serviceRequest.getId());
        return appointment;
    }

    public InsuranceClaim fileInsuranceClaim(ServiceRequest request, String insuranceCompany, String policyNumber, double claimAmount) {
        InsuranceClaim claim = new InsuranceClaim(request, insuranceCompany, policyNumber, claimAmount);
        insuranceClaims.put(claim.getId(), claim);
        logger.info("Filed insurance claim {} for request {}", claim.getId(), request.getId());
        return claim;
    }

    public ReportGenerator generateFinancialReport() {
        ReportWrapper<Invoice> invoiceReport = new ReportWrapper<>(new ArrayList<>(invoices.values()));
        ReportWrapper<Payment> paymentReport = new ReportWrapper<>(new ArrayList<>(payments.values()));
        return new FinancialReport(invoiceReport.getItems(), paymentReport.getItems());
    }

    public ReportGenerator generateServicePerformanceReport() {
        List<ServiceRequest> completed = serviceRequests.values().stream()
                .filter(req -> req.getStatus() == ServiceRequest.Status.COMPLETED)
                .collect(Collectors.toList());
        return new ServicePerformanceReport(completed);
    }

    public AbstractVehicle createCar(String make, String model, int year, String licensePlate, String vin) {
        return carFactory.createVehicle(make, model, year, licensePlate, vin);
    }

    public AbstractServiceOperation createLaborOperation(String description, double estimatedDurationHours, Technician technician, double hoursWorked) {
        return laborFactory.createServiceOperation(description, estimatedDurationHours, technician, hoursWorked);
    }

    public AbstractServiceOperation createPartInstallation(String description, double estimatedDurationHours, Part part, int quantityUsed) {
        return partInstallationFactory.createServiceOperation(description, estimatedDurationHours, part, quantityUsed);
    }
}
