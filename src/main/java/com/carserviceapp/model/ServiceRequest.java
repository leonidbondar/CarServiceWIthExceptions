package com.carserviceapp.model;

import com.carserviceapp.interfaces.CostCalculable;
import com.carserviceapp.interfaces.Displayable;
import com.carserviceapp.interfaces.Identifiable;
import com.carserviceapp.interfaces.TimeEstimable;
import com.carserviceapp.util.UniqueIdGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a service request submitted by a customer for a vehicle.
 * This class uses a list of AbstractServiceOperation to represent the work to be done.
 */
public class ServiceRequest implements Identifiable, Displayable, CostCalculable, TimeEstimable {
    private final String requestId;
    private Customer customer;
    private AbstractVehicle vehicle;
    private LocalDate requestDate;
    private String problemDescription;
    private Status status;
    private List<AbstractServiceOperation> operations;
    private double estimatedTotalCost;
    private double estimatedTotalTime;

    public enum Status {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    }

    public ServiceRequest(Customer customer, AbstractVehicle vehicle, LocalDate requestDate, String problemDescription) {
        this.requestId = UniqueIdGenerator.generateId("REQ");
        this.customer = customer;
        this.vehicle = vehicle;
        this.requestDate = requestDate;
        this.problemDescription = problemDescription;
        this.status = Status.PENDING;
        this.operations = new ArrayList<>();
        this.estimatedTotalCost = 0.0;
        this.estimatedTotalTime = 0.0;
    }

    @Override
    public String getId() {
        return requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public AbstractVehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(AbstractVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<AbstractServiceOperation> getOperations() {
        return new ArrayList<>(operations); // Return a copy to prevent external modification
    }

    public void addOperation(AbstractServiceOperation operation) {
        this.operations.add(operation);
        recalculateEstimates();
    }

    public void removeOperation(AbstractServiceOperation operation) {
        if (this.operations.remove(operation)) {
            recalculateEstimates();
        }
    }

    // Recalculate estimates whenever operations change
    private void recalculateEstimates() {
        this.estimatedTotalCost = operations.stream()
                .mapToDouble(AbstractServiceOperation::calculateCost)
                .sum();
        this.estimatedTotalTime = operations.stream()
                .mapToDouble(AbstractServiceOperation::estimateTime)
                .sum();
    }

    @Override
    public double calculateCost() {
        return estimatedTotalCost;
    }

    @Override
    public double estimateTime() {
        return estimatedTotalTime;
    }

    @Override
    public String getDisplayInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Service Request ID: ").append(requestId).append("\n");
        sb.append("  Customer: ").append(customer.getDisplayInfo()).append("\n");
        sb.append("  Vehicle: ").append(vehicle.getDisplayInfo()).append("\n");
        sb.append("  Request Date: ").append(requestDate).append("\n");
        sb.append("  Problem: ").append(problemDescription).append("\n");
        sb.append("  Status: ").append(status).append("\n");
        sb.append("  Estimated Total Cost: $").append(String.format("%.2f", estimatedTotalCost)).append("\n");
        sb.append("  Estimated Total Time: ").append(String.format("%.1f", estimatedTotalTime)).append(" hours\n");
        if (!operations.isEmpty()) {
            sb.append("  Operations:\n");
            for (AbstractServiceOperation op : operations) {
                sb.append("    - ").append(op.getDisplayInfo()).append("\n");
            }
        } else {
            sb.append("  No operations added yet.\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getDisplayInfo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceRequest that = (ServiceRequest) o;
        return Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }
}