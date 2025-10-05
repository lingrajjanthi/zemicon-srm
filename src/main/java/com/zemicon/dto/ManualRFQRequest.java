package com.zemicon.dto;

import com.zemicon.model.RequirementStatus;

public class ManualRFQRequest {
    private String customerName;
    private String manufacturer;
    private String mpn;
    private Integer quantity;
    private Double targetPrice;
    private String remarks;
    private RequirementStatus status;
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getMpn() {
		return mpn;
	}
	public void setMpn(String mpn) {
		this.mpn = mpn;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Double getTargetPrice() {
		return targetPrice;
	}
	public void setTargetPrice(Double targetPrice) {
		this.targetPrice = targetPrice;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public RequirementStatus getStatus() {
		return status;
	}
	public void setStatus(RequirementStatus status) {
		this.status = status;
	}
    
    
}
