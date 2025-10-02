package com.zemicon.dto;

import java.util.List;
import java.util.Map;
import com.zemicon.model.Vendor;

public class ComparisonResult {
    private Long customerId;
    private String customerName;
    private List<Vendor> vendors;
    private List<ComparisonRow> rows;
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public List<Vendor> getVendors() {
		return vendors;
	}
	public void setVendors(List<Vendor> vendors) {
		this.vendors = vendors;
	}
	public List<ComparisonRow> getRows() {
		return rows;
	}
	public void setRows(List<ComparisonRow> rows) {
		this.rows = rows;
	}
}
