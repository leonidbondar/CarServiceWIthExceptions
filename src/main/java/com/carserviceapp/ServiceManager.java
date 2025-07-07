package com.carserviceapp;

import com.carserviceapp.factory.CarFactory;
import com.carserviceapp.factory.LaborOperationFactory;
import com.carserviceapp.factory.PartInstallationFactory;
import com.carserviceapp.factory.ServiceOperationFactory;
import com.carserviceapp.factory.VehicleFactory;
import com.carserviceapp.interfaces.ReportGenerator;
import com.carserviceapp.model.*;
import com.carserviceapp.service.FinancialReport;
import com.carserviceapp.service.ServicePerformanceReport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages all aspects of the car service application,
 * including customers, vehicles, service requests, invoices, payments,
 * appointments, and insurance claims.
 */
public class ServiceManager {
    private Map<String, Customer> customers;
    private Map<String, AbstractVehicle> vehicles;
    private Map<String, Technician> technicians;
    private Map<String, Part> parts;
    private Map<String, ServiceRequest> serviceRequests;
    private Map<String, Invoice> invoices;
    private Map<String, Payment> payments;
    private Map<String, Appointment> appointments; // New: Manage appointments
    private Map<String, InsuranceClaim> insuranceClaims; // New: Manage insurance claims
    private Map<String, List<AbstractVehicle>> customerVehicles;

    // Factory instances
    private VehicleFactory carFactory;
    private ServiceOperationFactory laborFactory;
    private ServiceOperationFactory partInstallationFactory;

    public ServiceManager() {
        this.customers = new HashMap<>();
        this.vehicles = new HashMap<>();
        this.technicians = new HashMap<>();
        this.parts = new HashMap<>();
        this.serviceRequests = new HashMap<>();
        this.invoices = new HashMap<>();
        this.payments = new HashMap<>();
        this.appointments = new HashMap<>(); // Initialize
        this.insuranceClaims = new HashMap<>(); // Initialize
        this.customerVehicles = new HashMap<>();

        // Initialize factories
        this.carFactory = new CarFactory();
        this.laborFactory = new LaborOperationFactory();
        this.partInstallationFactory = new PartInstallationFactory();

        // Seed initial data
        seedData();
    }

    private void seedData() {
        // Seed Customers
        Customer cust1 = new Customer("Alice", "Smith", "alice@example.com", "123-456-7890");
        Customer cust2 = new Customer("Bob", "Johnson", "bob@example.com", "098-765-4321");
        addCustomer(cust1);
        addCustomer(cust2);

        // Seed Vehicles and associate with customers
        AbstractVehicle car1 = carFactory.createVehicle("Toyota", "Camry", 2018, "ABC-123", "VIN1234567890");
        addVehicle(car1, cust1);
        AbstractVehicle car2 = carFactory.createVehicle("Honda", "Civic", 2020, "XYZ-789", "VIN0987654321");
        addVehicle(car2, cust2);
        AbstractVehicle car3 = carFactory.createVehicle("Ford", "Focus", 2015, "DEF-456", "VIN9876543210");
        addVehicle(car3, cust1);

        // Seed Technicians
        Technician tech1 = new Technician("Jane", "Doe", "jane@example.com", "555-1111", "Certified Mechanic", 50.0);
        Technician tech2 = new Technician("Mike", "Brown", "mike@example.com", "555-2222", "Master Technician", 75.0);
        addTechnician(tech1);
        addTechnician(tech2);

        // Seed Parts
        Part oilFilter = new Part("Oil Filter", 15.0, 50);
        Part brakePads = new Part("Brake Pads", 75.0, 20);
        Part sparkPlug = new Part("Spark Plug", 10.0, 100);
        addPart(oilFilter);
        addPart(brakePads);
        addPart(sparkPlug);

        // Seed a sample Service Request, Operations, Invoice, Payment, Appointment, Claim
        ServiceRequest req1 = createServiceRequest(cust1, car1, "Engine noise and oil change");
        addServiceOperationToRequest(req1.getId(), createLaborOperation("Diagnostic", 1.0, tech1, 1.0));
        addServiceOperationToRequest(req1.getId(), createPartInstallation("Oil Filter Replacement", 0.5, oilFilter, 1));
        addServiceOperationToRequest(req1.getId(), createLaborOperation("Oil Change Labor", 0.5, tech1, 0.5));
        updateServiceRequestStatus(req1.getId(), ServiceRequest.Status.COMPLETED);

        Invoice inv1 = generateInvoice(req1);
        recordPayment(inv1, inv1.getAmount(), Payment.PaymentMethod.CASH);

        scheduleAppointment(cust1, car1, req1, LocalDateTime.now().plusDays(2).withHour(10).withMinute(0), "Post-service checkup");

        fileInsuranceClaim(req1, "AutoInsure Inc.", "POL987654", inv1.getAmount() * 0.8);
    }

    // --- Customer Management ---
    public void addCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
        customerVehicles.putIfAbsent(customer.getId(), new ArrayList<>());
        System.out.println("Added customer: " + customer.getDisplayInfo());
    }

    public Customer getCustomer(String customerId) {
        return customers.get(customerId);
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    // --- Vehicle Management ---
    public void addVehicle(AbstractVehicle vehicle, Customer owner) {
        vehicles.put(vehicle.getId(), vehicle);
        if (owner != null) {
            customerVehicles.computeIfAbsent(owner.getId(), k -> new ArrayList<>()).add(vehicle);
        }
        System.out.println("Added vehicle: " + vehicle.getDisplayInfo() + (owner != null ? " for customer: " + owner.getFirstName() + " " + owner.getLastName() : ""));
    }

    public AbstractVehicle getVehicle(String vehicleId) {
        return vehicles.get(vehicleId);
    }

    public List<AbstractVehicle> getAllVehicles() {
        return new ArrayList<>(vehicles.values());
    }

    public List<AbstractVehicle> getVehiclesByCustomer(Customer customer) {
        if (customer == null) {
            return new ArrayList<>();
        }
        return customerVehicles.getOrDefault(customer.getId(), new ArrayList<>());
    }

    // --- Technician Management ---
    public void addTechnician(Technician technician) {
        technicians.put(technician.getId(), technician);
        System.out.println("Added technician: " + technician.getDisplayInfo());
    }

    public Technician getTechnician(String technicianId) {
        return technicians.get(technicianId);
    }

    public List<Technician> getAllTechnicians() {
        return new ArrayList<>(technicians.values());
    }

    // --- Part Management ---
    public void addPart(Part part) {
        parts.put(part.getId(), part);
        System.out.println("Added part: " + part.getDisplayInfo());
    }

    public Part getPart(String partId) {
        return parts.get(partId);
    }

    public List<Part> getAllParts() {
        return new ArrayList<>(parts.values());
    }

    // --- Service Request Management ---
    public ServiceRequest createServiceRequest(Customer customer, AbstractVehicle vehicle, String problemDescription) {
        ServiceRequest request = new ServiceRequest(customer, vehicle, LocalDate.now(), problemDescription);
        serviceRequests.put(request.getId(), request);
        System.out.println("Created service request: " + request.getId());
        return request;
    }

    public ServiceRequest getServiceRequest(String requestId) {
        return serviceRequests.get(requestId);
    }

    public List<ServiceRequest> getAllServiceRequests() {
        return new ArrayList<>(serviceRequests.values());
    }

    public void updateServiceRequestStatus(String requestId, ServiceRequest.Status newStatus) {
        ServiceRequest request = serviceRequests.get(requestId);
        if (request!= null) {
            request.setStatus(newStatus);
            System.out.println("Service request " + requestId + " status updated to: " + newStatus);
        } else {
            System.out.println("Service request with ID " + requestId + " not found.");
        }
    }

    public void addServiceOperationToRequest(String requestId, AbstractServiceOperation operation) {
        ServiceRequest request = serviceRequests.get(requestId);
        if (request!= null) {
            request.addOperation(operation);
            System.out.println("Added operation to request " + requestId + ": " + operation.getDescription());
        } else {
            System.out.println("Service request with ID " + requestId + " not found.");
        }
    }

    // --- Invoice Management ---
    public Invoice generateInvoice(ServiceRequest request) {
        Invoice invoice = new Invoice(request);
        invoices.put(invoice.getId(), invoice);
        System.out.println("Generated invoice: " + invoice.getId() + " for request " + request.getId());
        return invoice;
    }

    public Invoice getInvoice(String invoiceId) {
        return invoices.get(invoiceId);
    }

    public List<Invoice> getAllInvoices() {
        return new ArrayList<>(invoices.values());
    }

    // --- Payment Management ---
    public Payment recordPayment(Invoice invoice, double amount, Payment.PaymentMethod method) {
        Payment payment = new Payment(invoice, amount, LocalDate.now(), method);
        payments.put(payment.getId(), payment);
        System.out.println("Recorded payment: " + payment.getId() + " for invoice " + invoice.getId());
        return payment;
    }

    public List<Payment> getAllPayments() {
        return new ArrayList<>(payments.values());
    }

    // --- Appointment Management ---
    public Appointment scheduleAppointment(Customer customer, AbstractVehicle vehicle, LocalDateTime scheduledTime, String purpose) {
        Appointment appointment = new Appointment(customer, vehicle, scheduledTime, purpose);
        appointments.put(appointment.getId(), appointment);
        System.out.println("Scheduled appointment: " + appointment.getId());
        return appointment;
    }

    public Appointment scheduleAppointment(Customer customer, AbstractVehicle vehicle, ServiceRequest serviceRequest, LocalDateTime scheduledTime, String purpose) {
        Appointment appointment = new Appointment(customer, vehicle, serviceRequest, scheduledTime, purpose);
        appointments.put(appointment.getId(), appointment);
        System.out.println("Scheduled appointment: " + appointment.getId() + " for Service Request " + serviceRequest.getId());
        return appointment;
    }

    public Appointment getAppointment(String appointmentId) {
        return appointments.get(appointmentId);
    }

    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments.values());
    }

    // --- Insurance Claim Management ---
    public InsuranceClaim fileInsuranceClaim(ServiceRequest request, String insuranceCompany, String policyNumber, double claimAmount) {
        InsuranceClaim claim = new InsuranceClaim(request, insuranceCompany, policyNumber, claimAmount);
        insuranceClaims.put(claim.getId(), claim);
        System.out.println("Filed insurance claim: " + claim.getId() + " for Service Request " + request.getId());
        return claim;
    }

    public InsuranceClaim getInsuranceClaim(String claimId) {
        return insuranceClaims.get(claimId);
    }

    public List<InsuranceClaim> getAllInsuranceClaims() {
        return new ArrayList<>(insuranceClaims.values());
    }

    // --- Reporting ---
    public ReportGenerator generateFinancialReport() {
        return new FinancialReport(new ArrayList<>(invoices.values()), new ArrayList<>(payments.values()));
    }

    public ReportGenerator generateServicePerformanceReport() {
        List<ServiceRequest> completed = serviceRequests.values().stream()
                .filter(req -> req.getStatus() == ServiceRequest.Status.COMPLETED)
                .collect(Collectors.toList());
        return new ServicePerformanceReport(completed);
    }

    // Factory methods for convenience
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