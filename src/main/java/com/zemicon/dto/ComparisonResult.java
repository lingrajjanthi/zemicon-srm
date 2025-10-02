package com.zemicon.dto;

import java.util.List;

public class ComparisonResult {
    private Long customerId;
    private String customerName;
    private List<ComparisonRow> rows;

    // Getters & Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public List<ComparisonRow> getRows() { return rows; }
    public void setRows(List<ComparisonRow> rows) { this.rows = rows; }
}
